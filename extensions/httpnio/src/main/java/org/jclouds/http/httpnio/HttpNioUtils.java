/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.http.httpnio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpVersion;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.nio.entity.NFileEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

public class HttpNioUtils {
    public static HttpEntityEnclosingRequest convertToApacheRequest(
	    HttpRequest object) {
	BasicHttpEntityEnclosingRequest apacheRequest = new BasicHttpEntityEnclosingRequest(
		object.getMethod(), object.getUri(), HttpVersion.HTTP_1_1);
	for (String header : object.getHeaders().keySet()) {
	    for (String value : object.getHeaders().get(header))
		apacheRequest.addHeader(header, value);
	}
	Object content = object.getContent();
	if (content != null) {
	    addEntityForContent(apacheRequest, content,
		    object.getContentType(), object.getContentLength());
	}
	return apacheRequest;
    }

    public static void addEntityForContent(
	    BasicHttpEntityEnclosingRequest apacheRequest, Object content,
	    String contentType, long length) {
	if (content instanceof InputStream) {
	    InputStream inputStream = (InputStream) content;
	    if (length == -1)
		throw new IllegalArgumentException(
			"you must specify size when content is an InputStream");
	    InputStreamEntity entity = new InputStreamEntity(inputStream,
		    length);
	    entity.setContentType(contentType);
	    apacheRequest.setEntity(entity);
	} else if (content instanceof String) {
	    NStringEntity nStringEntity = null;
	    try {
		nStringEntity = new NStringEntity((String) content);
	    } catch (UnsupportedEncodingException e) {
		throw new UnsupportedOperationException(
			"Encoding not supported", e);
	    }
	    nStringEntity.setContentType(contentType);
	    apacheRequest.setEntity(nStringEntity);
	} else if (content instanceof File) {
	    apacheRequest.setEntity(new NFileEntity((File) content,
		    contentType, true));
	} else if (content instanceof byte[]) {
	    NByteArrayEntity entity = new NByteArrayEntity((byte[]) content);
	    entity.setContentType(contentType);
	    apacheRequest.setEntity(entity);
	} else {
	    throw new UnsupportedOperationException(
		    "Content class not supported: "
			    + content.getClass().getName());
	}
    }

    public static HttpResponse convertToJavaCloudsResponse(
	    org.apache.http.HttpResponse apacheResponse) throws IOException {
	HttpResponse response = new HttpResponse();
	if (apacheResponse.getEntity() != null) {
	    response.setContent(apacheResponse.getEntity().getContent());
	}
	for (Header header : apacheResponse.getAllHeaders()) {
	    response.getHeaders().put(header.getName(), header.getValue());
	}
	response.setStatusCode(apacheResponse.getStatusLine().getStatusCode());
	return response;
    }
}