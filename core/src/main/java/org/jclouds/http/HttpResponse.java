/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http;

import java.io.InputStream;
import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class HttpResponse {
    int statusCode;
    Multimap<String, String> headers = HashMultimap.create();
    String message;
    InputStream content;
    String contentType;

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("HttpResponse");
	sb.append("{statusCode=").append(statusCode);
	sb.append(", headers=").append(headers);
	sb.append(", message='").append(message).append('\'');
	sb.append(", content set=").append(content != null);
	sb.append(", contentType='").append(contentType).append('\'');
	sb.append('}');
	return sb.toString();
    }

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }

    public Multimap<String, String> getHeaders() {
	return headers;
    }

    public void setHeaders(Multimap<String, String> headers) {
	this.headers = headers;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public InputStream getContent() {
	return content;
    }

    public void setContent(InputStream content) {
	this.content = content;
    }

    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }

    public String getFirstHeaderOrNull(String string) {
	Collection<String> values = headers.get(string);
	return (values != null && values.size() >= 1) ? values.iterator()
		.next() : null;
    }
}