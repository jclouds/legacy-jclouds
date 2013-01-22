/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.http.ning;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.JcloudsVersion;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.client.generators.InputStreamBodyGenerator;

/**
 * Todo Write me
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class NingHttpCommandExecutorService implements HttpCommandExecutorService {

   //TODO: get async version from maven or something
   public static final String USER_AGENT = String.format("jclouds/%s async-http-client/%s", JcloudsVersion.get(), "1.7.4");

   private final AsyncHttpClient client;
   private final ConvertToNingRequest convertToNingRequest;
   private final ConvertToJCloudsResponse convertToJCloudsResponse;
   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;

   @Inject
   public NingHttpCommandExecutorService(AsyncHttpClient client, ConvertToNingRequest convertToNingRequest,
            ConvertToJCloudsResponse convertToJCloudsResponse, DelegatingRetryHandler retryHandler,
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
         for (;;) {
            Future<Response> responseF = client.executeRequest(convertToNingRequest.apply(command.getCurrentRequest()));
            final HttpResponse httpResponse = convertToJCloudsResponse.apply(responseF.get());
            int statusCode = httpResponse.getStatusCode();
            if (statusCode >= 300) {
               if (retryHandler.shouldRetryRequest(command, httpResponse)) {
                  continue;
               } else {
                  errorHandler.handleError(command, httpResponse);
                  return Futures.immediateFuture(httpResponse);
               }
            } else {
               return Futures.immediateFuture(httpResponse);
            }
         }

      } catch (IOException e) {
         throw Throwables.propagate(e);
      } catch (InterruptedException e) {
         throw Throwables.propagate(e);
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }
   }

   @Singleton
   public static class ConvertToNingRequest implements Function<HttpRequest, Request> {

      public Request apply(HttpRequest request) {

         for (HttpRequestFilter filter : request.getFilters()) {
            request = filter.filter(request);
         }

         RequestBuilder builder = new RequestBuilder(request.getMethod());
         builder.setUrl(request.getEndpoint().toASCIIString());
         Payload payload = request.getPayload();
         if (payload != null) {
            boolean chunked = "chunked".equals(request.getFirstHeaderOrNull("Transfer-Encoding"));

            if (request.getPayload().getContentMetadata().getContentMD5() != null)
               builder.addHeader("Content-MD5", CryptoStreams.base64(request.getPayload().getContentMetadata()
                        .getContentMD5()));
            if (request.getPayload().getContentMetadata().getContentType() != null)
               builder.addHeader(HttpHeaders.CONTENT_TYPE, request.getPayload().getContentMetadata().getContentType());
            if (request.getPayload().getContentMetadata().getContentLanguage() != null)
               builder.addHeader(HttpHeaders.CONTENT_LANGUAGE, request.getPayload().getContentMetadata()
                        .getContentLanguage());
            if (request.getPayload().getContentMetadata().getContentEncoding() != null)
               builder.addHeader(HttpHeaders.CONTENT_ENCODING, request.getPayload().getContentMetadata()
                        .getContentEncoding());
            if (request.getPayload().getContentMetadata().getContentDisposition() != null)
               builder.addHeader("Content-Disposition", request.getPayload().getContentMetadata()
                        .getContentDisposition());
            if (!chunked) {
               Long length = checkNotNull(request.getPayload().getContentMetadata().getContentLength(),
                        "payload.getContentLength");
               builder.addHeader(HttpHeaders.CONTENT_LENGTH, length.toString());
            }
            setPayload(builder, payload);
         } else {
            builder.addHeader(HttpHeaders.CONTENT_LENGTH, "0");
         }

         builder.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
         for (Map<String, String> entry : request.getHeaders().entries()) {
            builder.addHeader(entry.getKey(), entry.getValue());
         }

         return builder.build();
      }

      void setPayload(RequestBuilder requestBuilder, Payload payload) {
         if (payload instanceof FilePayload) {
            requestBuilder.setBody(((FilePayload) payload).getRawContent());
         } else {
            requestBuilder.setBody(new InputStreamBodyGenerator(payload.getInput()));
         }
      }
   }

   @Singleton
   public static class ConvertToJCloudsResponse implements Function<Response, HttpResponse> {

      public HttpResponse apply(Response nativeResponse) {

         InputStream in = null;
         try {
            in = BaseHttpCommandExecutorService.consumeOnClose(nativeResponse.getResponseBodyAsStream());
         } catch (IOException e) {
            Closeables.closeQuietly(in);
            throw propagate(e);
         }

         Payload payload = in != null ? Payloads.newInputStreamPayload(in) : null;
         Multimap<String, String> headers = LinkedHashMultimap.create();
         for (Entry<String, List<String>> header : nativeResponse.getHeaders()) {
            headers.putAll(header.getKey(), header.getValue());
         }
         if (payload != null)
            payload.getContentMetadata().setPropertiesFromHttpHeaders(headers);
         return new HttpResponse(nativeResponse.getStatusCode(), nativeResponse.getStatusText(), payload,
                  RestAnnotationProcessor.filterOutContentHeaders(headers));
      }
   }
}
