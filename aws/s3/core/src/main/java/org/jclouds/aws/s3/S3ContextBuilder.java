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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SESSIONINTERVAL;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.aws.s3.config.RestS3ConnectionModule;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link S3Context} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see S3Context
 */
public class S3ContextBuilder extends
         BlobStoreContextBuilder<S3Connection, BucketMetadata, ObjectMetadata, S3Object> {

   @Override
   public S3Context buildContext() {
      return this.buildInjector().getInstance(S3Context.class);
   }

   public S3ContextBuilder(Properties props) {
      super(new TypeLiteral<S3Connection>() {
      }, new TypeLiteral<BucketMetadata>() {
      }, new TypeLiteral<ObjectMetadata>() {
      }, new TypeLiteral<S3Object>() {
      }, props);
      properties.setProperty(S3Constants.PROPERTY_S3_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-amz-meta-");
      if (!properties.containsKey(PROPERTY_S3_SESSIONINTERVAL))
         this.withTimeStampExpiration(60);
   }

   public S3ContextBuilder(String id, String secret) {
      this(new Properties());
      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(id, "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(secret,
               "awsSecretAccessKey"));
   }

   @Override
   public S3ContextBuilder relaxSSLHostname() {
      return (S3ContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public S3ContextBuilder withRequestTimeout(long milliseconds) {
      return (S3ContextBuilder) super.withRequestTimeout(milliseconds);
   }

   @Override
   public S3ContextBuilder withExecutorService(ExecutorService service) {
      return (S3ContextBuilder) super.withExecutorService(service);
   }

   @Override
   public S3ContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (S3ContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public S3ContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (S3ContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public S3ContextBuilder withModule(Module module) {
      return (S3ContextBuilder) super.withModule(module);
   }

   @Override
   public S3ContextBuilder withModules(Module... modules) {
      return (S3ContextBuilder) super.withModules(modules);
   }

   @Override
   public S3ContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (S3ContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public S3ContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (S3ContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public S3ContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (S3ContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public S3ContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (S3ContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public S3ContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (S3ContextBuilder) super.withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @Override
   public S3ContextBuilder withEndpoint(URI endpoint) {
      properties.setProperty(S3Constants.PROPERTY_S3_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return (S3ContextBuilder) this;
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new S3ContextModule());
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestS3ConnectionModule());
   }

   public S3ContextBuilder withTimeStampExpiration(long seconds) {
      getProperties().setProperty(PROPERTY_S3_SESSIONINTERVAL, seconds + "");
      return this;
   }
}
