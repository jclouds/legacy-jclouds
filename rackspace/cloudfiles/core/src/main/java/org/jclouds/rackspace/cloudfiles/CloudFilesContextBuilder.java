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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rackspace.RackspaceContextBuilder;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule;
import org.jclouds.rackspace.cloudfiles.config.RestCloudFilesBlobStoreModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link CloudFilesContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see CloudFilesContext
 */
public class CloudFilesContextBuilder
         extends
         BlobStoreContextBuilder<CloudFilesConnection, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

   @Override
   public CloudFilesContext buildContext() {
      return this.buildInjector().getInstance(CloudFilesContext.class);
   }

   @Override
   public CloudFilesContextBuilder relaxSSLHostname() {
      return (CloudFilesContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public CloudFilesContextBuilder withRequestTimeout(long milliseconds) {
      return (CloudFilesContextBuilder) super.withRequestTimeout(milliseconds);
   }

   @Override
   public CloudFilesContextBuilder withExecutorService(ExecutorService service) {
      return (CloudFilesContextBuilder) super.withExecutorService(service);
   }

   @Override
   public CloudFilesContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (CloudFilesContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public CloudFilesContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (CloudFilesContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public CloudFilesContextBuilder withModule(Module module) {
      return (CloudFilesContextBuilder) super.withModule(module);
   }

   @Override
   public CloudFilesContextBuilder withModules(Module... modules) {
      return (CloudFilesContextBuilder) super.withModules(modules);
   }

   @Override
   public CloudFilesContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (CloudFilesContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public CloudFilesContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (CloudFilesContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public CloudFilesContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (CloudFilesContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public CloudFilesContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (CloudFilesContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public CloudFilesContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (CloudFilesContextBuilder) super
               .withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   public CloudFilesContextBuilder(Properties props) {
      super(new TypeLiteral<CloudFilesConnection>() {
      }, new TypeLiteral<ContainerMetadata>() {
      }, new TypeLiteral<BlobMetadata>() {
      }, new TypeLiteral<Blob<BlobMetadata>>() {
      }, props);
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      RackspaceContextBuilder.initialize(this);
   }

   public CloudFilesContextBuilder(String id, String secret) {
      this(new Properties());
      RackspaceContextBuilder.authenticate(this, id, secret);
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestCloudFilesBlobStoreModule());
      RackspaceContextBuilder.addAuthenticationModule(this);
   }

   @Override
   public void addContextModule(List<Module> modules) {
      modules.add(new CloudFilesContextModule());
   }

   @Override
   public CloudFilesContextBuilder withEndpoint(URI endpoint) {
      return (CloudFilesContextBuilder) RackspaceContextBuilder.withEndpoint(this, endpoint);
   }

}
