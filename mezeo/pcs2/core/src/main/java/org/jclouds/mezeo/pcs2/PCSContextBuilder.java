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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.mezeo.pcs2.config.PCSContextModule;
import org.jclouds.mezeo.pcs2.config.RestPCSBlobStoreModule;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class PCSContextBuilder extends
         BlobStoreContextBuilder<PCSConnection, ContainerMetadata, FileMetadata, PCSFile> {

   public PCSContextBuilder(Properties props) {
      super(new TypeLiteral<PCSConnection>() {
      }, new TypeLiteral<ContainerMetadata>() {
      }, new TypeLiteral<FileMetadata>() {
      }, new TypeLiteral<PCSFile>() {
      }, props);
      checkNotNull(properties.getProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT));
   }

   public PCSContextBuilder(URI endpoint, String id, String secret) {
      this(addEndpointTo(endpoint, new Properties()));
      properties.setProperty(PCSConstants.PROPERTY_PCS2_USER, checkNotNull(id, "user"));
      properties.setProperty(PCSConstants.PROPERTY_PCS2_PASSWORD, checkNotNull(secret, "key"));
   }

   private static Properties addEndpointTo(URI endpoint, Properties properties) {
      properties.setProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT,
               checkNotNull(endpoint, "endpoint").toString());
      return properties;
   }

   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestPCSBlobStoreModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new PCSContextModule());
   }

   @Override
   public PCSContextBuilder withEndpoint(URI endpoint) {
      addEndpointTo(endpoint, properties);
      return this;
   }

   // below is to cast the builder to the correct type so that chained builder methods end correctly

   @Override
   public PCSContext buildContext() {
      Injector injector = buildInjector();
      return injector.getInstance(PCSContext.class);
   }

   @Override
   public PCSContextBuilder relaxSSLHostname() {
      return (PCSContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public PCSContextBuilder withExecutorService(ExecutorService service) {
      return (PCSContextBuilder) super.withExecutorService(service);
   }

   @Override
   public PCSContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (PCSContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public PCSContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (PCSContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public PCSContextBuilder withJsonDebug() {
      return (PCSContextBuilder) super.withJsonDebug();
   }

   @Override
   public PCSContextBuilder withModule(Module module) {
      return (PCSContextBuilder) super.withModule(module);
   }

   @Override
   public PCSContextBuilder withRequestTimeout(long milliseconds) {
      return (PCSContextBuilder) super.withRequestTimeout(milliseconds);
   }
   
   @Override
   public PCSContextBuilder withModules(Module... modules) {
      return (PCSContextBuilder) super.withModules(modules);
   }

   @Override
   public PCSContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (PCSContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public PCSContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (PCSContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public PCSContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (PCSContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public PCSContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (PCSContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public PCSContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (PCSContextBuilder) super.withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @Override
   public PCSContextBuilder withSaxDebug() {
      return (PCSContextBuilder) super.withSaxDebug();
   }
}
