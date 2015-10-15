/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arbalo AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.opentdc.base.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.opentdc.base.format.JsonFormat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;

public class StoreManager {
	
	/**
	 * JsonResourceEnumeration
	 *
	 */
	public static class JsonResourceEnumeration implements Enumeration<InputStream> {

		public JsonResourceEnumeration(
			ServletContext context,
			String path
		) throws Exception {
			File dir = new File(context.getRealPath(STORE_PREFIX + path));
			if(!dir.exists()) {
				throw new FileNotFoundException(path);
			}
			List<InputStream> resources = new ArrayList<InputStream>();
			// Start list of JSON objects
			try {
				resources.add(new ByteArrayInputStream("{ \"object\": [".getBytes("utf-8")));
			} catch(Exception e) {}
			int count = 0;
			for(String resourcePath: new TreeSet<String>(context.getResourcePaths(STORE_PREFIX + path))) {
				// JSONArray comma separator
				if(count > 0) {
					try {
						resources.add(new ByteArrayInputStream(",".getBytes("utf-8")));
					} catch(Exception e) {}
				}
				if(resourcePath.endsWith(".json")) {
					resources.add(
						context.getResourceAsStream(resourcePath)
					);
				}
				count++;
			}
			// End list of JSON objects
			try {
				resources.add(new ByteArrayInputStream("]}".getBytes("utf-8")));
			} catch(Exception e) {}
			this.iterator = resources.iterator();
		}

		@Override
		public boolean hasMoreElements(
		) {
			return this.iterator.hasNext();
		}

		@Override
		public InputStream nextElement(
		) {
			return this.iterator.next();
		}
		
		private final Iterator<InputStream> iterator;
	}

	/**
	 * Find objects matching the query.
	 * 
	 * @param context
	 * @param path
	 * @param queryType
	 * @param query
	 * @param position
	 * @param size
	 * @param classOfT
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> find(
		ServletContext context,
		String path,
		String query,
		int position,
		int size,
		Comparator<T> comparator,
		Class<T> classOfT
	) throws Exception {
		Object matchingElements = JsonPath.using(jsonProvider).parse(
			new SequenceInputStream(new JsonResourceEnumeration(context, path))
		).read(
			"$.object" + (query == null ? "" : "[?" + query + "]")
		);
		JsonArray objects = null;
		if(matchingElements instanceof JsonArray) {
			objects = (JsonArray)matchingElements;
		} else {
			objects = ((JsonObject)matchingElements).getAsJsonArray("elements");
		}
		List<T> resultSet = new ArrayList<T>();
		for(Iterator<JsonElement> i = objects.iterator(); i.hasNext(); ) {
			JsonObject json = (JsonObject)i.next();
			resultSet.add(
				json.has("members")
					? JsonFormat.toObject(json.get("members"), classOfT)
					: JsonFormat.toObject(json, classOfT)
			);
		}
		if(comparator != null) {
			Collections.sort(resultSet, comparator);
		}
		return position < resultSet.size() 
			? resultSet.subList(position, Math.min(position + size, resultSet.size()))
			: Collections.<T>emptyList();
	}

	/**
	 * Get object at given path.
	 * 
	 * @param context
	 * @param path
	 * @param classOfT
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static <T> T get(
		ServletContext context,
		String path,
		Class<T> classOfT
	) throws Exception {
		if(!path.endsWith(".json")) {
			path += ".json";
		}
		InputStream is = null;
		File file = new File(context.getRealPath(STORE_PREFIX + path));
		if(!file.exists()) {
			throw new FileNotFoundException(path);
		}
		try {
			is = context.getResourceAsStream(STORE_PREFIX + path);
			return (T)JsonFormat.toObject(is, classOfT);
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch(Exception ignore) {}
			}
		}
	}

	/**
	 * Create object at given path.
	 * 
	 * @param context
	 * @param path
	 * @param id
	 * @param object
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void create(
		ServletContext context,
		String path,
		String id,
		Object object
	) throws Exception {
		File dir = new File(context.getRealPath(STORE_PREFIX + path));
		dir.mkdirs();
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(dir, id + ".json")), "utf-8");
		out.write(JsonFormat.toJson(object));
		out.close();
	}

	/**
	 * Update object at given path.
	 * 
	 * @param context
	 * @param path
	 * @param object
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void update(
		ServletContext context,
		String path,
		Object object
	) throws Exception {
		if(!path.endsWith(".json")) {
			path += ".json";
		}
		File file = new File(context.getRealPath(STORE_PREFIX + path));
		if(!file.exists()) {
			throw new FileNotFoundException(path);
		}
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
		out.write(JsonFormat.toJson(object));
		out.close();
	}

	public static final String STORE_PREFIX = "/data/";
	private static final JsonProvider jsonProvider = new GsonJsonProvider();
	
}
