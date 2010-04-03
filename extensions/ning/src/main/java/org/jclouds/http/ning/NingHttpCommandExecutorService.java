/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.http.ning;

import java.io.IOException;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.ning.http.client.*;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.ning.http.collection.Pair;
import org.jclouds.http.*;

import com.google.inject.Inject;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.payloads.ByteArrayPayload;
import org.jclouds.http.payloads.FilePayload;
import org.jclouds.http.payloads.InputStreamPayload;
import org.jclouds.http.payloads.StringPayload;
import org.jclouds.util.Utils;

/**
 * Todo Write me
 *
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class NingHttpCommandExecutorService implements HttpCommandExecutorService {

    public static final String USER_AGENT = "jclouds/1.0 ning http/1.0.0";

    private final AsyncHttpClient client;
    private final ConvertToNingRequest convertToNingRequest;
    private final ConvertToJCloudsResponse convertToJCloudsResponse;
    private final DelegatingRetryHandler retryHandler;
    private final DelegatingErrorHandler errorHandler;

    @Inject
    public NingHttpCommandExecutorService(AsyncHttpClient client,
                                          ConvertToNingRequest convertToNingRequest,
                                          ConvertToJCloudsResponse convertToJCloudsResponse,
                                          DelegatingRetryHandler retryHandler,
                                          DelegatingErrorHandler errorHandler) {
        this.client = client;
        this.convertToNingRequest = convertToNingRequest;
        this.convertToJCloudsResponse = convertToJCloudsResponse;
        this.retryHandler = retryHandler;
        this.errorHandler = errorHandler;
    }

    @Override
    public ListenableFuture<HttpResponse> submit(HttpCommand command) {
        try {
            for(;;) {
                Future<Response> responseF = client.executeRequest(convertToNingRequest.apply(command.getRequest()));
                final HttpResponse httpResponse = convertToJCloudsResponse.apply(responseF.get());
                int statusCode = httpResponse.getStatusCode();
                if (statusCode >= 300) {
                    if (retryHandler.shouldRetryRequest(command, httpResponse)) {
                        continue;
                    } else {
                        errorHandler.handleError(command, httpResponse);
                        return wrapAsFuture(httpResponse);
                    }
                } else {
                    return wrapAsFuture(httpResponse);
                }
            }

        } catch(IOException e) {
            throw Throwables.propagate(e);
        } catch(InterruptedException e) {
            throw Throwables.propagate(e);
        } catch(ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private ListenableFuture<HttpResponse> wrapAsFuture(final HttpResponse httpResponse) {
        return Futures.makeListenable(new AbstractFuture<HttpResponse>() {
            @Override
            public HttpResponse get() throws InterruptedException, ExecutionException {
                return httpResponse;
            }
        });
    }

    @Singleton
    public static class ConvertToNingRequest implements Function<HttpRequest, Request> {

        public Request apply(HttpRequest request) {

            for (HttpRequestFilter filter : request.getFilters()) {
                filter.filter(request);
            }

            AsyncHttpClient client = new AsyncHttpClient();
            AsyncHttpClient.BoundRequestBuilder nativeRequestBuilder;
            String endpoint = request.getEndpoint().toASCIIString();

            if (request.getMethod().equals(HttpMethod.HEAD)) {
                nativeRequestBuilder = client.prepareHead(endpoint);
            } else if (request.getMethod().equals(HttpMethod.GET)) {
                nativeRequestBuilder = client.prepareGet(endpoint);
            } else if (request.getMethod().equals(HttpMethod.DELETE)) {
                nativeRequestBuilder = client.prepareDelete(endpoint);
            } else if (request.getMethod().equals(HttpMethod.PUT)) {
                nativeRequestBuilder = client.preparePut(endpoint);
            } else if (request.getMethod().equals(HttpMethod.POST)) {
                nativeRequestBuilder = client.preparePost(endpoint);
            } else {
                throw new UnsupportedOperationException(request.getMethod());
            }
            Payload payload = request.getPayload();
            if(payload != null) {
                //changeRequestContentToBytes(request);
                setPayload(nativeRequestBuilder, payload);
            } else {
                nativeRequestBuilder.addHeader(HttpHeaders.CONTENT_LENGTH, "0");
            }

            nativeRequestBuilder.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
            for (String header : request.getHeaders().keySet()) {
                for (String value : request.getHeaders().get(header)) {
                    nativeRequestBuilder.addHeader(header, value);
                }
            }

            return nativeRequestBuilder.build();
        }

        @VisibleForTesting
        void changeRequestContentToBytes(HttpRequest request) {
            Payload content = request.getPayload();
            if (content == null || content instanceof ByteArrayPayload) {
                //just return
            } else if (content instanceof StringPayload) {
                String string = ((StringPayload) content).getRawContent();
                request.setPayload(string.getBytes());
            } else if (content instanceof InputStreamPayload || content instanceof FilePayload) {
                InputStream i = content.getContent();
                try {
                    try {
                        request.setPayload(ByteStreams.toByteArray(i));
                    } catch (IOException e) {
                        throw Throwables.propagate(e);
                    }
                } finally {
                    Closeables.closeQuietly(i);
                }
            } else {
                throw new UnsupportedOperationException("Content not supported " + content.getClass());
            }
        }

        void setPayload(AsyncHttpClient.BoundRequestBuilder requestBuilder, Payload payload) {
            try {
                requestBuilder.setBody(ByteStreams.toByteArray(payload.getContent()));
            } catch(IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Singleton
    public static class ConvertToJCloudsResponse implements Function<Response, HttpResponse> {
        public HttpResponse apply(Response nativeResponse) {
            HttpResponse response = new HttpResponse();
            response.setStatusCode(nativeResponse.getStatusCode());
            for (Pair<String, String> header : nativeResponse.getHeaders()) {
                response.getHeaders().put(header.getFirst(), header.getSecond());
            }
            try {
                String responseBody = nativeResponse.getResponseBody();
                if(responseBody != null) {
                    response.setContent(Utils.toInputStream(responseBody));
                }
            } catch(IOException e) {
                throw Throwables.propagate(e);
            }
            return response;
        }
    }
}
