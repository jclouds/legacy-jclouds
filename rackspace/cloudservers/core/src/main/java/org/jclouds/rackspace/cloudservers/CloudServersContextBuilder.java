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
package org.jclouds.rackspace.cloudservers;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.RackspaceContextBuilder;
import org.jclouds.rackspace.cloudservers.config.CloudServersContextModule;
import org.jclouds.rackspace.cloudservers.config.RestCloudServersConnectionModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link CloudServersContext} instances based on the most commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see CloudServersContext
 */
public class CloudServersContextBuilder extends RackspaceContextBuilder<CloudServersConnection> {

   private static final TypeLiteral<CloudServersConnection> connectionType = new TypeLiteral<CloudServersConnection>() {
   };

   public CloudServersContextBuilder(String id, String secret) {
      super(connectionType, id, secret);
   }

   public CloudServersContextBuilder(Properties properties) {
      super(connectionType, properties);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new CloudServersContextModule());
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      super.addConnectionModule(modules);
      modules.add(new RestCloudServersConnectionModule());
   }

   // below is to cast the builder to the correct type so that chained builder methods end correctly

   @Override
   public CloudServersContextBuilder withEndpoint(URI endpoint) {
      return (CloudServersContextBuilder) super.withEndpoint(endpoint);
   }

   @Override
   public CloudServersContext buildContext() {
      Injector injector = buildInjector();
      return injector.getInstance(CloudServersContext.class);
   }

   @Override
   public CloudServersContextBuilder relaxSSLHostname() {
      return (CloudServersContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public CloudServersContextBuilder withExecutorService(ExecutorService service) {
      return (CloudServersContextBuilder) super.withExecutorService(service);
   }

   @Override
   public CloudServersContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (CloudServersContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public CloudServersContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (CloudServersContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public CloudServersContextBuilder withJsonDebug() {
      return (CloudServersContextBuilder) super.withJsonDebug();
   }

   @Override
   public CloudServersContextBuilder withModule(Module module) {
      return (CloudServersContextBuilder) super.withModule(module);
   }

   @Override
   public CloudServersContextBuilder withModules(Module... modules) {
      return (CloudServersContextBuilder) super.withModules(modules);
   }

   @Override
   public CloudServersContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (CloudServersContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public CloudServersContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (CloudServersContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public CloudServersContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (CloudServersContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public CloudServersContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (CloudServersContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public CloudServersContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (CloudServersContextBuilder) super
               .withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @Override
   public CloudServersContextBuilder withSaxDebug() {
      return (CloudServersContextBuilder) (CloudServersContextBuilder) super.withSaxDebug();
   }
}
