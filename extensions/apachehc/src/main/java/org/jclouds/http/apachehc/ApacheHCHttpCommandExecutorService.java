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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jclouds.Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Simple implementation of a {@link HttpFutureCommandClient}, Apache Components HttpClient 4.x.
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class ApacheHCHttpCommandExecutorService extends
         BaseHttpCommandExecutorService<HttpEntityEnclosingRequest> {
   @VisibleForTesting
   boolean isOpen = true;
   private final BasicHttpParams params;
   private final ThreadSafeClientConnManager cm;
   private final ApacheHCUtils utils;

   @Inject
   ApacheHCHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler,
            HttpWire wire,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT) final int globalMaxConnections,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST) final int globalMaxConnectionsPerHost,
            ApacheHCUtils utils) {
      super(ioWorkerExecutor, retryHandler, errorHandler, wire);
      this.utils = utils;
      params = new BasicHttpParams();
      // TODO: have this use our executor service, if possible
      if (globalMaxConnections > 0)
         ConnManagerParams.setMaxTotalConnections(params, globalMaxConnections);
      if (globalMaxConnectionsPerHost > 0) {
         ConnPerRoute connectionsPerRoute = new ConnPerRouteBean(globalMaxConnectionsPerHost);
         ConnManagerParams.setMaxConnectionsPerRoute(params, connectionsPerRoute);
      }
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      SchemeRegistry schemeRegistry = new SchemeRegistry();
      schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
      schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
      cm = new ThreadSafeClientConnManager(params, schemeRegistry);
   }

   @Override
   protected HttpEntityEnclosingRequest convert(HttpRequest request) throws IOException {
      return utils.convertToApacheRequest(request);
   }

   @Override
   protected HttpResponse invoke(HttpEntityEnclosingRequest nativeRequest) throws IOException {
      checkState(isOpen, "http executor not open");
      org.apache.http.HttpResponse nativeResponse = executeRequest(nativeRequest);
      return utils.convertToJCloudsResponse(nativeResponse);
   }

   private org.apache.http.HttpResponse executeRequest(HttpEntityEnclosingRequest nativeRequest)
            throws IOException, ClientProtocolException {
      URI endpoint = URI.create(nativeRequest.getRequestLine().getUri());
      HttpClient client = new DefaultHttpClient(cm, params);
      HttpHost host = new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getScheme());
      org.apache.http.HttpResponse nativeResponse = client.execute(host, nativeRequest);
      return nativeResponse;
   }

   @PreDestroy
   public void close() {
      // TODO test
      isOpen = false;
      cm.shutdown();
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         close();
      } finally {
         super.finalize();
      }
   }

   @Override
   protected void cleanup(HttpEntityEnclosingRequest nativeResponse) {
      // No cleanup necessary
   }
}
