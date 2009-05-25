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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;
import com.google.appengine.api.urlfetch.*;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.jclouds.http.*;
import org.jclouds.http.internal.BaseHttpFutureCommandClient;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Google App Engine version of {@link HttpFutureCommandClient}
 *
 * @author Adrian Cole
 */
public class URLFetchServiceClient extends BaseHttpFutureCommandClient {
    private final URLFetchService urlFetchService;

    @Inject
    public URLFetchServiceClient(URL target, URLFetchService urlFetchService)
            throws MalformedURLException {
        super(target);
        this.urlFetchService = urlFetchService;
    }

    public void submit(HttpFutureCommand<?> command) {
        HttpRequest request = command.getRequest();
        HTTPResponse gaeResponse = null;
        try {
            for (HttpRequestFilter filter : requestFilters) {
                filter.filter(request);
            }
            HttpResponse response = null;
            for (; ;) {
                logger.trace("%1$s - converting request %2$s", target, request);
                HTTPRequest gaeRequest = convert(request);
                if (logger.isTraceEnabled())
                    logger.trace(
                            "%1$s - submitting request %2$s, headers: %3$s",
                            target, gaeRequest.getURL(),
                            headersAsString(gaeRequest.getHeaders()));
                gaeResponse = this.urlFetchService.fetch(gaeRequest);
                if (logger.isTraceEnabled())
                    logger
                            .trace(
                                    "%1$s - received response code %2$s, headers: %3$s",
                                    target, gaeResponse.getResponseCode(),
                                    headersAsString(gaeResponse.getHeaders()));
                response = convert(gaeResponse);
                int statusCode = response.getStatusCode();
                if (statusCode >= 500 && httpRetryHandler.retryRequest(command, response))
                    continue;
                break;
            }
            handleResponse(command, response);
        } catch (Exception e) {
            if (gaeResponse != null && gaeResponse.getContent() != null) {
                logger.error(e,
                        "error encountered during the execution: %1$s%n%2$s",
                        gaeResponse, new String(gaeResponse.getContent()));
            }
            command.setException(e);
        }
    }

    String headersAsString(List<HTTPHeader> headers) {
        StringBuilder builder = new StringBuilder("");
        for (HTTPHeader header : headers)
            builder.append("[").append(header.getName()).append("=").append(
                    header.getValue()).append("],");
        return builder.toString();
    }

    /**
     * byte [] content is replayable and the only content type supportable by
     * GAE. As such, we convert the original request content to a byte array.
     */
    @VisibleForTesting
    void changeRequestContentToBytes(HttpRequest request) throws IOException {
        Object content = request.getPayload();
        if (content == null || content instanceof byte[]) {
            return;
        } else if (content instanceof String) {
            String string = (String) content;
            request.setPayload(string.getBytes());
        } else if (content instanceof InputStream || content instanceof File) {
            InputStream i = content instanceof InputStream ? (InputStream) content
                    : new FileInputStream((File) content);
            try {
                request.setPayload(IOUtils.toByteArray(i));
            } finally {
                IOUtils.closeQuietly(i);
            }
        } else {
            throw new UnsupportedOperationException("Content not supported "
                    + content.getClass());
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
        }
        return response;
    }

    @VisibleForTesting
    HTTPRequest convert(HttpRequest request) throws IOException {
        URL url = new URL(target, request.getUri());
        HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod
                .valueOf(request.getMethod()), disallowTruncate());
        for (String header : request.getHeaders().keySet()) {
            for (String value : request.getHeaders().get(header))
                gaeRequest.addHeader(new HTTPHeader(header, value));
        }
        if (request.getPayload() != null) {
            changeRequestContentToBytes(request);
            gaeRequest.setPayload((byte[]) request.getPayload());
        }
        return gaeRequest;
    }
}
