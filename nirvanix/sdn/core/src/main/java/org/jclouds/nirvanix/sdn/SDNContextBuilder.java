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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.nirvanix.sdn.config.RestSDNAuthenticationModule;
import org.jclouds.nirvanix.sdn.config.RestSDNConnectionModule;
import org.jclouds.nirvanix.sdn.config.SDNContextModule;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class SDNContextBuilder extends CloudContextBuilder<SDNConnection> {

   public SDNContextBuilder(String apikey, String appname, String password) {
      this(new Properties());
      authenticate(this, apikey, appname, appname, password);
   }

   public SDNContextBuilder(String apikey, String appname, String username, String password) {
      this(new Properties());
      authenticate(this, apikey, appname, username, password);
   }

   public SDNContextBuilder(Properties props) {
      super(new TypeLiteral<SDNConnection>() {
      }, props);
      initialize(this);
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      addAuthenticationModule(this);
   }

   public static void authenticate(SDNContextBuilder builder, String appkey, String appname,
            String username, String password) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_APPKEY,
               checkNotNull(appkey, "appkey"));
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_APPNAME,
               checkNotNull(appname, "appname"));
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_USERNAME,
               checkNotNull(username, "username"));
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_PASSWORD,
               checkNotNull(password, "password"));
   }

   public static void initialize(SDNContextBuilder builder) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT,
               "http://services.nirvanix.com");
   }

   public static void addAuthenticationModule(SDNContextBuilder builder) {
      builder.withModule(new RestSDNAuthenticationModule());
      builder.withModule(new RestSDNConnectionModule());
   }

   public static SDNContextBuilder withEndpoint(SDNContextBuilder builder, URI endpoint) {
      builder.getProperties().setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return (SDNContextBuilder) builder;
   }

   @Override
   public SDNContextBuilder relaxSSLHostname() {
      return (SDNContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public SDNContextBuilder withExecutorService(ExecutorService service) {
      return (SDNContextBuilder) super.withExecutorService(service);
   }

   @Override
   public SDNContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (SDNContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public SDNContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (SDNContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public SDNContextBuilder withModule(Module module) {
      return (SDNContextBuilder) super.withModule(module);
   }

   @Override
   public SDNContextBuilder withModules(Module... modules) {
      return (SDNContextBuilder) super.withModules(modules);
   }

   @Override
   public SDNContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (SDNContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public SDNContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (SDNContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public SDNContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (SDNContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public SDNContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (SDNContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public SDNContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (SDNContextBuilder) super.withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @Override
   public SDNContextBuilder withEndpoint(URI endpoint) {
      return (SDNContextBuilder) (SDNContextBuilder) withEndpoint(this, endpoint);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new SDNContextModule());
   }

   @Override
   public SDNContext buildContext() {
      Injector injector = buildInjector();
      return injector.getInstance(SDNContext.class);
   }
}
