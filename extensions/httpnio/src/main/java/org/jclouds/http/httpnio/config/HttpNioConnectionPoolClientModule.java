/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.httpnio.config;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;

import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.config.HttpFutureCommandClientModule;
import org.jclouds.http.httpnio.config.internal.NonSSLHttpNioConnectionPoolClientModule;
import org.jclouds.http.httpnio.config.internal.SSLHttpNioConnectionPoolClientModule;
import org.jclouds.http.httpnio.pool.HttpNioConnectionPoolClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures {@link HttpNioConnectionPoolClient}
 * 
 * @author Adrian Cole
 */
@HttpFutureCommandClientModule
public class HttpNioConnectionPoolClientModule extends AbstractModule {

   @Named(HttpConstants.PROPERTY_HTTP_SECURE)
   boolean isSecure;

   @Override
   protected void configure() {
      requestInjection(this);
      if (isSecure)
         install(new SSLHttpNioConnectionPoolClientModule());
      else
         install(new NonSSLHttpNioConnectionPoolClientModule());
      bind(HttpFutureCommandClient.class).to(HttpNioConnectionPoolClient.class);
   }

   @Singleton
   @Provides
   protected InetSocketAddress provideAddress(URI endPoint) {
      return new InetSocketAddress(endPoint.getHost(), endPoint.getPort());
   }

   @Singleton
   @Provides
   protected URI provideAddress(@Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
            @Named(HttpConstants.PROPERTY_HTTP_PORT) int port,
            @Named(HttpConstants.PROPERTY_HTTP_SECURE) boolean isSecure)
            throws MalformedURLException {

      return URI.create(String.format("%1$s://%2$s:%3$s", isSecure ? "https" : "http", address,
               port));
   }
}
