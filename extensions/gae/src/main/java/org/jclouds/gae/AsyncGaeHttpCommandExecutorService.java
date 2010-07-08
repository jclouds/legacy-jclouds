/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.repackaged.com.google.common.base.Throwables;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Google App Engine version of {@link HttpCommandExecutorService} using their fetchAsync call
 * 
 * @author Adrian Cole
 */
@SingleThreaded
@Singleton
public class AsyncGaeHttpCommandExecutorService implements HttpCommandExecutorService {
   public static final String USER_AGENT = "jclouds/1.0 urlfetch/1.3.2";

   private final URLFetchService urlFetchService;
   private final ConvertToGaeRequest convertToGaeRequest;
   private final ConvertToJcloudsResponse convertToJcloudsResponse;

   @Inject
   public AsyncGaeHttpCommandExecutorService(URLFetchService urlFetchService,
            ConvertToGaeRequest convertToGaeRequest,
            ConvertToJcloudsResponse convertToJcloudsResponse) {
      this.urlFetchService = urlFetchService;
      this.convertToGaeRequest = convertToGaeRequest;
      this.convertToJcloudsResponse = convertToJcloudsResponse;
   }

   @Override
   public ListenableFuture<HttpResponse> submit(HttpCommand command) {
      // TODO: this needs to handle retrying and filtering
      return Futures.compose(ConcurrentUtils.makeListenable(urlFetchService
               .fetchAsync(convertToGaeRequest.apply(command.getRequest())), sameThreadExecutor()),
               convertToJcloudsResponse);
   }

   @Singleton
   public static class ConvertToJcloudsResponse implements Function<HTTPResponse, HttpResponse> {

      @Override
      public HttpResponse apply(HTTPResponse gaeResponse) {
         HttpResponse response = new HttpResponse();
         response.setStatusCode(gaeResponse.getResponseCode());
         for (HTTPHeader header : gaeResponse.getHeaders()) {
            response.getHeaders().put(header.getName(), header.getValue());
         }
         if (gaeResponse.getContent() != null) {
            response.setContent(new ByteArrayInputStream(gaeResponse.getContent()));
         }
         return response;
      }
   }

   @Singleton
   public static class ConvertToGaeRequest implements Function<HttpRequest, HTTPRequest> {
      /**
       * byte [] content is replayable and the only content type supportable by GAE. As such, we
       * convert the original request content to a byte array.
       */
      @Override
      public HTTPRequest apply(HttpRequest request) {
         URL url = null;
         try {
            url = request.getEndpoint().toURL();
         } catch (MalformedURLException e) {
            Throwables.propagate(e);
         }

         FetchOptions options = disallowTruncate();
         options.followRedirects();

         HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod.valueOf(request.getMethod()
                  .toString()), options);

         for (String header : request.getHeaders().keySet()) {
            for (String value : request.getHeaders().get(header)) {
               gaeRequest.addHeader(new HTTPHeader(header, value));
            }
         }
         gaeRequest.addHeader(new HTTPHeader(HttpHeaders.USER_AGENT, USER_AGENT));

         if (request.getPayload() != null) {
            InputStream input = request.getPayload().getInput();
            try {
               byte[] array = toByteArray(input);
               if (!request.getPayload().isRepeatable())
                  request.setPayload(array);
               gaeRequest.setPayload(array);
            } catch (IOException e) {
               Throwables.propagate(e);
            } finally {
               closeQuietly(input);
            }
         } else {
            gaeRequest.addHeader(new HTTPHeader(HttpHeaders.CONTENT_LENGTH, "0"));
         }
         return gaeRequest;
      }

   }

}
