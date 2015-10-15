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
package org.opentdc.base.format;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.JsonElement;

public class JsonFormat {

	public static <T> T toObject(
		InputStream is,
		Class<T> classOfT
	) throws UnsupportedEncodingException {
		return (T)gson.fromJson(new InputStreamReader(is, "utf-8"), classOfT);		
	}

	public static <T> T toObject(
		String json,
		Class<T> classOfT
	) throws UnsupportedEncodingException {
		return (T)gson.fromJson(json, classOfT);		
	}

	public static <T> T toObject(
		JsonElement json,
		Class<T> classOfT
	) throws UnsupportedEncodingException {
		return (T)gson.fromJson(json, classOfT);		
	}

	public static String toJson(
		Object object
	) {
		return gson.toJson(object);
	}

	public static com.google.gson.Gson getGson(
	) {
		return gson;
	}
	
	private static com.google.gson.Gson gson = null;
	
	static {
		com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		gsonBuilder.setPrettyPrinting();
		gson = gsonBuilder.create();		
	}
	
}
