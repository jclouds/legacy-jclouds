/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http.apachehc.config;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;

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
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.apachehc.ApacheHCHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.SSLModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.proxy.ProxyConfig;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures {@link ApacheHCHttpCommandExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class ApacheHCHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new SSLModule());
      bindClient();
   }

   @Singleton
   @Provides
   HttpParams newBasicHttpParams(HttpUtils utils) {
      BasicHttpParams params = new BasicHttpParams();

      params.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).setBooleanParameter(
               CoreConnectionPNames.STALE_CONNECTION_CHECK, true).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY,
               true).setParameter(CoreProtocolPNames.ORIGIN_SERVER, "jclouds/1.0");

      if (utils.getConnectionTimeout() > 0) {
         params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, utils.getConnectionTimeout());
      }

      if (utils.getSocketOpenTimeout() > 0) {
         params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, utils.getSocketOpenTimeout());
      }

      if (utils.getMaxConnections() > 0)
         ConnManagerParams.setMaxTotalConnections(params, utils.getMaxConnections());

      if (utils.getMaxConnectionsPerHost() > 0) {
         ConnPerRoute connectionsPerRoute = new ConnPerRouteBean(utils.getMaxConnectionsPerHost());
         ConnManagerParams.setMaxConnectionsPerRoute(params, connectionsPerRoute);
      }
      HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
      return params;
   }

   @Singleton
   @Provides
   X509HostnameVerifier newHostnameVerifier(HttpUtils utils) {
      return utils.relaxHostname() ? SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
               : SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
   }

   @Singleton
   @Provides
   SSLContext newSSLSocketFactory(HttpUtils utils, @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider)
            throws NoSuchAlgorithmException, KeyManagementException {
      if (utils.trustAllCerts())
         return untrustedSSLContextProvider.get();
      SSLContext context = SSLContext.getInstance("TLS");

      context.init(null, null, null);
      return context;
   }

   @Singleton
   @Provides
   ClientConnectionManager newClientConnectionManager(HttpParams params, X509HostnameVerifier verifier,
            SSLContext context, Closer closer) throws NoSuchAlgorithmException, KeyManagementException {

      SchemeRegistry schemeRegistry = new SchemeRegistry();

      Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
      SSLSocketFactory sf = new SSLSocketFactory(context);

      sf.setHostnameVerifier(verifier);

      Scheme https = new Scheme("https", sf, 443);

      SchemeRegistry sr = new SchemeRegistry();
      sr.register(http);
      sr.register(https);

      schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
      schemeRegistry.register(new Scheme("https", sf, 443));
      final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
      closer.addToClose(new Closeable() {
         @Override
         public void close() throws IOException {
            cm.shutdown();
         }
      });
      return cm;
   }

   @Provides
   @Singleton
   HttpClient newDefaultHttpClient(ProxyConfig config, BasicHttpParams params, ClientConnectionManager cm) {
      DefaultHttpClient client = new DefaultHttpClient(cm, params);
      if (config.useSystem()) {
         ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client.getConnectionManager()
                  .getSchemeRegistry(), ProxySelector.getDefault());
         client.setRoutePlanner(routePlanner);
      }
      return client;
   }

   protected void bindClient() {
      bind(HttpCommandExecutorService.class).to(ApacheHCHttpCommandExecutorService.class).in(Scopes.SINGLETON);
   }

}
