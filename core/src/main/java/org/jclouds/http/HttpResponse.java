/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
import java.net.URL;


/**
 * Represents a response produced from {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
public class HttpResponse extends HttpMessage {
    private URL requestURL;
    private int statusCode;
    private String message;
    private InputStream content;
    
    public HttpResponse(URL requestURL) {
       this.requestURL = requestURL;
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("HttpResponse");
	sb.append("{statusCode=").append(statusCode);
	sb.append(", headers=").append(headers);
	sb.append(", message='").append(message).append('\'');
	sb.append(", content set=").append(content != null);
	sb.append('}');
	return sb.toString();
    }

    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
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
    
    public URL getRequestURL() {
       return this.requestURL;
    }

}