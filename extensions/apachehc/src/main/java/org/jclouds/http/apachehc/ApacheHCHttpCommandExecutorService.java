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

package org.jclouds.http.apachehc;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.jclouds.Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;

import com.google.inject.Inject;

/**
 * Simple implementation of a {@link HttpFutureCommandClient}, Apache Components HttpClient 4.x.
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class ApacheHCHttpCommandExecutorService extends
         BaseHttpCommandExecutorService<HttpUriRequest> {
   private final HttpClient client;

   @Inject
   ApacheHCHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler,
            HttpWire wire, HttpClient client) {
      super(ioWorkerExecutor, retryHandler, errorHandler, wire);
      this.client = client;
   }

   @Override
   protected HttpUriRequest convert(HttpRequest request) throws IOException {
      return ApacheHCUtils.convertToApacheRequest(request);
   }

   @Override
   protected HttpResponse invoke(HttpUriRequest nativeRequest) throws IOException {
      org.apache.http.HttpResponse nativeResponse = executeRequest(nativeRequest);
      return ApacheHCUtils.convertToJCloudsResponse(nativeResponse);
   }

   private org.apache.http.HttpResponse executeRequest(HttpUriRequest nativeRequest)
            throws IOException, ClientProtocolException {
      URI endpoint = URI.create(nativeRequest.getRequestLine().getUri());
      HttpHost host = new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getScheme());
      org.apache.http.HttpResponse nativeResponse = client.execute(host, nativeRequest);
      return nativeResponse;
   }

   @Override
   protected void cleanup(HttpUriRequest nativeResponse) {
      // No cleanup necessary
   }
}
