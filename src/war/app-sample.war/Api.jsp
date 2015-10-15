<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
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
%><%@ page session="true" import="
java.util.*,
java.util.regex.*,
java.io.*,
java.text.*,
java.math.*,
java.net.URL,
java.net.URLEncoder,
java.net.MalformedURLException,
java.io.UnsupportedEncodingException,
javax.naming.*,
org.opentdc.base.utils.*,
org.opentdc.base.store.*,
org.opentdc.base.format.*,
org.opentdc.base.generic.*
"%><%!

	public static class ContactModel extends BasicObjectModel {
		
		public ContactModel() {
			
		}
		
		public String getPhotoUrl() {
			return photoUrl;
		}
	
		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}
	
		public String getFn() {
			return fn;
		}
	
		public void setFn(String fn) {
			this.fn = fn;
		}
	
		public String getFirstName() {
			return firstName;
		}
	
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
	
		public String getLastName() {
			return lastName;
		}
	
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
	
		public String getMiddleName() {
			return middleName;
		}
	
		public void setMiddleName(String middleName) {
			this.middleName = middleName;
		}
	
		public String getMaidenName() {
			return maidenName;
		}
	
		public void setMaidenName(String maidenName) {
			this.maidenName = maidenName;
		}
	
		public String getPrefix() {
			return prefix;
		}
	
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
	
		public String getSuffix() {
			return suffix;
		}
	
		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}
	
		public String getNickName() {
			return nickName;
		}
	
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
	
		public String getJobTitle() {
			return jobTitle;
		}
	
		public void setJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
		}
	
		public String getDepartment() {
			return department;
		}
	
		public void setDepartment(String department) {
			this.department = department;
		}
	
		public String getCompany() {
			return company;
		}
	
		public void setCompany(String company) {
			this.company = company;
		}
	
		public Date getBirthday() {
			return birthday;
		}
	
		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}
	
		public String getNote() {
			return note;
		}
	
		public void setNote(String note) {
			this.note = note;
		}
		
		private String photoUrl;
		private String fn;
		private String firstName;
		private String lastName;
		private String middleName;
		private String maidenName;
		private String prefix;
		private String suffix;
		private String nickName;
		private String jobTitle;
		private String department;
		private String company;
		private Date birthday;
		private String note;
		
	}
	
	public static List<ContactModel> findContacts(
		HttpServletRequest request,
		String path,
		ServletUtils.Query query
	) throws Exception {
		List<ContactModel> contacts = StoreManager.find(
			request.getServletContext(), 
			path,
			query.getQuery(),
			query.getPosition(),
			query.getSize(),
			ContactModel.class
		);
		return contacts;
	}

	public static ContactModel getContact(
		HttpServletRequest request,
		String path
	) throws Exception {
		ContactModel contact = StoreManager.get(
			request.getServletContext(), 
			path, 
			ContactModel.class
		);
		return contact;
	}	

	public static ContactModel createContact(
		HttpServletRequest request,
		String path
	) throws Exception {
		ContactModel contact = JsonFormat.toObject(
			request.getInputStream(), 
			ContactModel.class
		);
		contact = BasicObjectModel.forCreate(contact, request);
		StoreManager.create(
			request.getServletContext(),
			path,
			contact.getId(),
			contact
		);
		return contact;
	}

	public static ContactModel updateContact(
		HttpServletRequest request,
		String path
	) throws Exception {
		ContactModel contact = JsonFormat.toObject(
			request.getInputStream(), 
			ContactModel.class
		);
		contact = BasicObjectModel.forUpdate(contact, request);
		StoreManager.update(
			request.getServletContext(), 
			path,
			contact
		);
		return contact;
	}

%><%
	// Sample URLs:
	// - http://localhost:9080/app-sample/api/provider/Opentdc/segment/Test/contact?((@.lastName >= 'A') %26%26 (@.disabled == false))&position=0&size=2
	// - http://localhost:9080/app-sample/api/provider/Opentdc/segment/Test/contact/1
	try {
		com.google.gson.Gson gson = JsonFormat.getGson();
		String path = ServletUtils.getPath(request);
		System.out.println(new Date() + " Api.jsp: " + path);
		ServletUtils.Query query = ServletUtils.getQuery(request);
		boolean isGet = "GET".equals(request.getMethod());
		Object result = null;
		if(ServletUtils.pathMatches(path, "provider/{pid}/segment/{sid}/contact/{cid}")) {
			if(isGet) {
				result = getContact(request, path);
			} else {
				result = updateContact(request, path);
			}
		} else if(ServletUtils.pathMatches(path, "provider/{pid}/segment/{sid}/contact")) {
			if(isGet) {
				result = findContacts(request, path, query);
			} else {
				result = createContact(request, path);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		if(result != null) {
%><%= gson.toJson(result) %>
<%
		}
	} catch(Exception e) {
		e.printStackTrace();
		if(e instanceof FileNotFoundException) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);			
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	} finally {
	}
%>
