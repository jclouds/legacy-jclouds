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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.command.FutureCommand;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Google App Engine version of {@link HttpFutureCommandClient}
 * 
 * @author Adrian Cole
 */
public class URLFetchServiceClient implements HttpFutureCommandClient {
    private final URL target;
    private List<HttpRequestFilter> requestFilters = Collections.emptyList();
    @Resource
    private Logger logger = Logger.NULL;
    private final URLFetchService urlFetchService;

    public List<HttpRequestFilter> getRequestFilters() {
	return requestFilters;
    }

    @Inject(optional = true)
    public void setRequestFilters(List<HttpRequestFilter> requestFilters) {
	this.requestFilters = requestFilters;
    }

    @Inject
    public URLFetchServiceClient(URL target, URLFetchService urlFetchService)
	    throws MalformedURLException {
	this.urlFetchService = urlFetchService;
	this.target = target;
	this.logger.info("configured to connect to target: %1s", target);
    }

    public <O extends FutureCommand> void submit(O operation) {
	HttpRequest request = (HttpRequest) operation.getRequest();
	HTTPResponse gaeResponse = null;
	try {
	    for (HttpRequestFilter filter : getRequestFilters()) {
		filter.filter(request);
	    }
	    logger.trace("%1s - converting request %2s", target, request);
	    HTTPRequest gaeRequest = convert(request);
	    logger.trace("%1s - submitting request %2s", target, gaeRequest);
	    gaeResponse = this.urlFetchService.fetch(gaeRequest);
	    logger.trace("%1s - received response %2s", target, gaeResponse);
	    HttpResponse response = convert(gaeResponse);
	    operation.getResponseFuture().setResponse(response);
	    operation.getResponseFuture().run();
	} catch (Exception e) {
	    if (gaeResponse != null && gaeResponse.getContent() != null) {
		logger.error(e,
			"error encountered during the execution: %1s%n%2s",
			gaeResponse, new String(gaeResponse.getContent()));
	    }
	    operation.setException(e);
	}
    }

    @VisibleForTesting
    HttpResponse convert(HTTPResponse gaeResponse) {
	HttpResponse response = new HttpResponse();
	response.setStatusCode(gaeResponse.getResponseCode());
	for (HTTPHeader header : gaeResponse.getHeaders()) {
	    response.getHeaders().put(header.getName(), header.getValue());
	}
	if (gaeResponse.getContent() != null) {
	    response.setContent(new ByteArrayInputStream(gaeResponse
		    .getContent()));
	    response.setContentType(response
		    .getFirstHeaderOrNull(HttpConstants.CONTENT_TYPE));
	}
	return response;
    }

    @VisibleForTesting
    HTTPRequest convert(HttpRequest request) throws IOException {
	URL url = new URL(target, request.getUri());
	HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod
		.valueOf(request.getMethod()), disallowTruncate()
		.doNotFollowRedirects());
	for (String header : request.getHeaders().keySet()) {
	    for (String value : request.getHeaders().get(header))
		gaeRequest.addHeader(new HTTPHeader(header, value));
	}
	if (request.getContent() != null) {
	    gaeRequest.addHeader(new HTTPHeader(HttpConstants.CONTENT_TYPE,
		    request.getContentType()));
	    if (request.getContent() instanceof String) {
		String string = (String) request.getContent();
		gaeRequest.setPayload(string.getBytes());
	    } else if (request.getContent() instanceof InputStream) {
		gaeRequest.setPayload(IOUtils.toByteArray((InputStream) request
			.getContent()));
	    } else if (request.getContent() instanceof File) {
		gaeRequest.setPayload(IOUtils.toByteArray(new FileInputStream(
			(File) request.getContent())));
	    } else if (request.getContent() instanceof byte[]) {
		gaeRequest.setPayload((byte[]) request.getContent());
	    } else {
		throw new UnsupportedOperationException(
			"Content not supported "
				+ request.getContent().getClass());
	    }

	}
	return gaeRequest;
    }
}
