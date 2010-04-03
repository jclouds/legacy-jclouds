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
package org.jclouds.http.ning.config;

import com.google.inject.Provides;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.ning.NingHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import javax.inject.Singleton;

/**
 * Configures {@link NingHttpCommandExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class NingHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bindClient();
   }

   @Singleton
   @Provides
   AsyncHttpClient provideNingClient() {
       AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().
               setFollowRedirects(true).
               build();
       return new AsyncHttpClient(config);
   }
//
//   @Singleton
//   @Provides
//   HttpParams newBasicHttpParams(HttpUtils utils) {
//      BasicHttpParams params = new BasicHttpParams();
//
//      params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
//               .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, true)
//               .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).setParameter(
//                        CoreProtocolPNames.ORIGIN_SERVER, "jclouds/1.0");
//
//      if (utils.getConnectionTimeout() > 0) {
//         params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, utils
//                  .getConnectionTimeout());
//      }
//
//      if (utils.getSocketOpenTimeout() > 0) {
//         params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, utils.getSocketOpenTimeout());
//      }
//
//      if (utils.getMaxConnections() > 0)
//         ConnManagerParams.setMaxTotalConnections(params, utils.getMaxConnections());
//
//      if (utils.getMaxConnectionsPerHost() > 0) {
//         ConnPerRoute connectionsPerRoute = new ConnPerRouteBean(utils.getMaxConnectionsPerHost());
//         ConnManagerParams.setMaxConnectionsPerRoute(params, connectionsPerRoute);
//      }
//      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//      return params;
//   }
//
//   @Singleton
//   @Provides
//   X509HostnameVerifier newHostnameVerifier(HttpUtils utils) {
//      return utils.relaxHostname() ? SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
//               : SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
//   }
//
//   @Singleton
//   @Provides
//   ClientConnectionManager newClientConnectionManager(HttpParams params,
//            X509HostnameVerifier verifier, Closer closer) throws NoSuchAlgorithmException,
//            KeyManagementException {
//
//      SchemeRegistry schemeRegistry = new SchemeRegistry();
//
//      Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
//      SSLContext context = SSLContext.getInstance("TLS");
//
//      context.init(null, null, null);
//      SSLSocketFactory sf = new SSLSocketFactory(context);
//      sf.setHostnameVerifier(verifier);
//
//      Scheme https = new Scheme("https", sf, 443);
//
//      SchemeRegistry sr = new SchemeRegistry();
//      sr.register(http);
//      sr.register(https);
//
//      schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//      schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//      final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
//      closer.addToClose(new Closeable() {
//         @Override
//         public void close() throws IOException {
//            cm.shutdown();
//         }
//      });
//      return cm;
//   }
//
//   @Provides
//   @Singleton
//   HttpClient newDefaultHttpClient(HttpUtils utils, BasicHttpParams params,
//            ClientConnectionManager cm) {
//      DefaultHttpClient client = new DefaultHttpClient(cm, params);
//      if (utils.useSystemProxies()) {
//         ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client
//                  .getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
//         client.setRoutePlanner(routePlanner);
//      }
//      return client;
//   }

   protected void bindClient() {
      bind(HttpCommandExecutorService.class).to(NingHttpCommandExecutorService.class).in(
               Scopes.SINGLETON);

      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class).in(Scopes.SINGLETON);
   }

}