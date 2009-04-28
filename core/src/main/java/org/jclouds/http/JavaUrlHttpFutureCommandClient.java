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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jclouds.Logger;
import org.jclouds.Utils;
import org.jclouds.command.FutureCommand;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class JavaUrlHttpFutureCommandClient implements HttpFutureCommandClient {
    private URL target;
    private List<HttpRequestFilter> requestFilters = Collections.emptyList();
    private Logger logger;

    public List<HttpRequestFilter> getRequestFilters() {
	return requestFilters;
    }

    @Inject(optional = true)
    public void setRequestFilters(List<HttpRequestFilter> requestFilters) {
	this.requestFilters = requestFilters;
    }

    @Inject
    public JavaUrlHttpFutureCommandClient(java.util.logging.Logger logger,
	    URL target) throws MalformedURLException {
	this.logger = new Logger(logger);
	this.target = target;
	this.logger.info("configured to connect to target: %1s", target);
    }

    public <O extends FutureCommand> void submit(O operation) {
	HttpRequest request = (HttpRequest) operation.getRequest();
	HttpURLConnection connection = null;
	try {
	    for (HttpRequestFilter filter : getRequestFilters()) {
		filter.filter(request);
	    }
	    logger.trace("%1s - submitting request %2s", target, request);
	    connection = openJavaConnection(request);
	    HttpResponse response = getResponse(connection);
	    logger.trace("%1s - received response %2s", target, response);

	    operation.getResponseFuture().setResponse(response);
	    operation.getResponseFuture().run();
	} catch (Exception e) {
	    if (connection != null) {
		InputStream errorStream = connection.getErrorStream();
		if (errorStream != null) {
		    try {
			String errorMessage = Utils.toStringAndClose(connection
				.getErrorStream());
			logger.error(e,
				"error encountered during the exception: %1s",
				errorMessage);
		    } catch (IOException e1) {
		    }
		}

	    }
	    operation.setException(e);
	} finally {
            // DO NOT disconnect, as it will also close the unconsumed outputStream from above.
	    // connection.disconnect();
	}
    }

    public void close() {
	// Nothing to stop;
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
	response.setContent(connection.getInputStream());
	response.setContentType(connection
		.getHeaderField(HttpConstants.CONTENT_TYPE));
	return response;
    }

    private HttpURLConnection openJavaConnection(HttpRequest request)
	    throws IOException {
	URL url = new URL(target, request.getUri());
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.setDoOutput(true);
	connection.setRequestMethod(request.getMethod());
	for (String header : request.getHeaders().keySet()) {
	    for (String value : request.getHeaders().get(header))
		connection.setRequestProperty(header, value);
	}
	connection.setRequestProperty(HttpConstants.CONTENT_TYPE, request
		.getContentType());
	if (request.getContent() != null) {
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
