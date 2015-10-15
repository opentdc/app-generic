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
package org.opentdc.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
	
	public static class Query {
		
		public Query() {
			
		}
		
		public Query(
			String query,
			int position,
			int size,
			String order
		) {
			this.query = query;
			this.position = position;
			this.size = size;
			this.order = order;
		}
		
		public String getQuery() {
			return query;
		}
		public void setQuery(String query) {
			this.query = query;
		}
		public int getPosition() {
			return position;
		}
		public void setPosition(int position) {
			this.position = position;
		}
		public int getSize() {
			return size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}

		private String query;
		private int position;
		private int size;
		private String order;		
	}
	
	public static String getPath(
		HttpServletRequest request
	) {
		String pathInfo = request.getPathInfo();
		return pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
	}

	public static Query getQuery(
		HttpServletRequest request
	) {
		int position = 0;
		if(request.getParameter("position") != null) {
			position = Integer.parseInt(request.getParameter("position"));
		}
		int size = 20;
		if(request.getParameter("size") != null) {
			size = Integer.parseInt(request.getParameter("size"));
		}
		String query = request.getParameter("query");
		String order = request.getParameter("order");
		return new Query(query, position, size, order);
	}

	public static boolean pathMatches(
		String path1,
		String path2
	) {
		String[] path1Elements = path1.split("/");
		String[] path2Elements = path2.split("/");
		if(path1Elements.length == path2Elements.length) {
			for(int i = 0; i < path1Elements.length; i++) {
				if(
					!path1Elements[i].equals(path2Elements[i]) && 
					!(path1Elements[i].startsWith("{") && path1Elements[i].endsWith("}")) && 
					!(path2Elements[i].startsWith("{") && path2Elements[i].endsWith("}"))
				) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static String getPathElement(
		String path,
		int index
	) {
		return path.split("/")[index];
	}
	
	public static String getRequestBody(
		HttpServletRequest request
	) throws Exception {
		InputStream is = request.getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int c;
		while((c = is.read()) != -1) {
			os.write(c);
		}
		return os.toString("utf-8");
	}
	
}
