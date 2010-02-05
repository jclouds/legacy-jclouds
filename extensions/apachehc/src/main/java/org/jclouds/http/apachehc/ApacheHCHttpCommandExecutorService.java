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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
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
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jclouds.Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

/**
 * Simple implementation of a {@link HttpFutureCommandClient}, Apache Components HttpClient 4.x.
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class ApacheHCHttpCommandExecutorService extends
         BaseHttpCommandExecutorService<HttpEntityEnclosingRequest> {

   private final ConcurrentMap<URI, HttpClient> poolMap;

   @Inject
   ApacheHCHttpCommandExecutorService(
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
            DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler,
            HttpWire wire,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT) final int globalMaxConnections,
            @Named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST) final int globalMaxConnectionsPerHost) {
      super(ioWorkerExecutor, retryHandler, errorHandler, wire);
      checkArgument(globalMaxConnections > 0, Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT
               + " must be greater than zero");
      checkArgument(globalMaxConnectionsPerHost > 0, Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST
               + " must be greater than zero");
      poolMap = new MapMaker().makeComputingMap(new Function<URI, HttpClient>() {
         public HttpClient apply(URI endPoint) {
            checkArgument(endPoint.getHost() != null, String.format(
                     "endPoint.getHost() is null for %s", endPoint));
            HttpParams params = new BasicHttpParams();
            try {
               // TODO: have this use our executor service
               // TODO: implement wire logging
               ConnManagerParams.setMaxTotalConnections(params, globalMaxConnections);
               ConnPerRoute connectionsPerRoute = new ConnPerRouteBean(globalMaxConnectionsPerHost);
               ConnManagerParams.setMaxConnectionsPerRoute(params, connectionsPerRoute);
               HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
               SchemeRegistry schemeRegistry = new SchemeRegistry();
               if (endPoint.getScheme().equals("http"))
                  schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),
                           80));
               else
                  schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(),
                           443));
               ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
               return new DefaultHttpClient(cm, params);
            } catch (RuntimeException e) {
               logger.error(e, "error creating entry for %s", endPoint);
               throw e;
            }
         }
      });
   }

   @Override
   protected HttpEntityEnclosingRequest convert(HttpRequest request) throws IOException {
      return ApacheHCUtils.convertToApacheRequest(request);
   }

   @Override
   protected HttpResponse invoke(HttpEntityEnclosingRequest nativeRequest) throws IOException {
      URI endpoint = URI.create(nativeRequest.getRequestLine().getUri());
      HttpClient client = poolMap.get(HttpUtils.createBaseEndpointFor(endpoint));
      assert (client != null) : "pool for endpoint null " + endpoint;
      HttpHost host = new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getScheme());
      org.apache.http.HttpResponse nativeResponse = client.execute(host, nativeRequest);
      return ApacheHCUtils.convertToJCloudsResponse(nativeResponse);
   }

   @Override
   protected void cleanup(HttpEntityEnclosingRequest nativeResponse) {
      // TODO
   }
}
