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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.Utils;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class JavaUrlHttpFutureCommandClient implements HttpFutureCommandClient {
    private URL target;
    private List<HttpRequestFilter> requestFilters = Collections.emptyList();
    @Resource
    private Logger logger = Logger.NULL;

    public List<HttpRequestFilter> getRequestFilters() {
	return requestFilters;
    }

    @Inject(optional = true)
    public void setRequestFilters(List<HttpRequestFilter> requestFilters) {
	this.requestFilters = requestFilters;
    }

    @Inject
    public JavaUrlHttpFutureCommandClient(URL target)
	    throws MalformedURLException {
	this.target = target;
    }

    public void submit(HttpFutureCommand<?> command) {
	HttpRequest request = (HttpRequest) command.getRequest();
	HttpURLConnection connection = null;
	try {
	    for (HttpRequestFilter filter : getRequestFilters()) {
		filter.filter(request);
	    }
	    HttpResponse response = null;
	    for (;;) {
		try {
		    logger.trace("%1s - converting request %2s", target,
			    request);
		    connection = openJavaConnection(request);
		    logger.trace("%1s - submitting request %2s", target,
			    connection);
		    response = getResponse(connection);
		    logger.trace("%1s - received response %2s", target,
			    response);
		    if (request.isReplayable()
			    && response.getStatusCode() >= 500) {
			logger.info("resubmitting command: %1s", command);
			continue;
		    }
		    break;
		} catch (IOException e) {
		    if (request.isReplayable()
			    && e.getMessage().indexOf(
				    "Server returned HTTP response code: 5") >= 0) {
			logger.info("resubmitting command: %1s", command);
			continue;
		    }
		    throw e;
		}
	    }
	    command.getResponseFuture().setResponse(response);
	    command.getResponseFuture().run();
	} catch (FileNotFoundException e) {
	    HttpResponse response = new HttpResponse();
	    response.setStatusCode(404);
	    command.getResponseFuture().setResponse(response);
	    command.getResponseFuture().run();
	} catch (Exception e) {
	    if (connection != null) {
		StringBuilder errors = new StringBuilder();
		try {
		    for (InputStream in : new InputStream[] {
			    connection.getErrorStream(),
			    connection.getInputStream() }) {
			if (in != null) {
			    errors.append(Utils.toStringAndClose(in)).append(
				    "\n");
			}
		    }
		    logger.error(e,
			    "error encountered during the exception: %1s",
			    errors.toString());
		} catch (IOException e2) {
		}
	    }
	    command.setException(e);
	} finally {
	    // DO NOT disconnect, as it will also close the unconsumed
	    // outputStream from above.
	    if (request.getMethod().equals("HEAD"))
		connection.disconnect();
	}
    }

    private HttpResponse getResponse(HttpURLConnection connection)
	    throws IOException {
	HttpResponse response = new HttpResponse();
	response.setStatusCode(connection.getResponseCode());
	for (String header : connection.getHeaderFields().keySet()) {
	    response.getHeaders().putAll(header,
		    connection.getHeaderFields().get(header));
	}

	response.setMessage(connection.getResponseMessage());
	if (!connection.getRequestMethod().equals("HEAD")) {
	    response.setContent(connection.getInputStream());
	    response.setContentType(connection
		    .getHeaderField(HttpConstants.CONTENT_TYPE));
	}
	return response;
    }

    private HttpURLConnection openJavaConnection(HttpRequest request)
	    throws IOException {
	URL url = new URL(target, request.getUri());
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoOutput(true);
	connection.setAllowUserInteraction(false);
	connection.setInstanceFollowRedirects(false);
	connection.setRequestMethod(request.getMethod());
	for (String header : request.getHeaders().keySet()) {
	    for (String value : request.getHeaders().get(header))
		connection.setRequestProperty(header, value);
	}
	if (request.getContent() != null) {
	    connection.setRequestProperty(HttpConstants.CONTENT_TYPE, request
		    .getContentType());
	    OutputStream out = connection.getOutputStream();
	    try {
		if (request.getContent() instanceof String) {
		    OutputStreamWriter writer = new OutputStreamWriter(out);
		    writer.write((String) request.getContent());
		    writer.close();
		} else if (request.getContent() instanceof InputStream) {
		    IOUtils.copy((InputStream) request.getContent(), out);
		} else if (request.getContent() instanceof File) {
		    IOUtils.copy(new FileInputStream((File) request
			    .getContent()), out);
		} else if (request.getContent() instanceof byte[]) {
		    IOUtils.write((byte[]) request.getContent(), out);
		} else {
		    throw new UnsupportedOperationException(
			    "Content not supported "
				    + request.getContent().getClass());
		}
	    } finally {
		IOUtils.closeQuietly(out);
	    }

	}
	return connection;
    }
}
