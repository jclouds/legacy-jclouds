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
import static org.jclouds.command.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS;
import static org.jclouds.command.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS;
import static org.jclouds.command.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE;
import static org.jclouds.command.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES;
import static org.jclouds.command.pool.PoolConstants.PROPERTY_POOL_REQUEST_INVOKER_THREADS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_SECURE;
import static org.jclouds.http.HttpConstants.PROPERTY_SAX_DEBUG;

import java.util.List;
import java.util.Properties;

import org.jclouds.aws.s3.config.LiveS3ConnectionModule;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link S3Context} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpFutureCommandClientModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see S3Context
 */
public class S3ContextBuilder extends CloudContextBuilder<S3Connection, S3Context> {

   public S3ContextBuilder(Properties props) {
      super(props);
   }

   public static S3ContextBuilder newBuilder(String id, String secret) {
      Properties properties = new Properties();

      properties.setProperty(PROPERTY_HTTP_ADDRESS, "s3.amazonaws.com");
      properties.setProperty(PROPERTY_SAX_DEBUG, "false");
      properties.setProperty(PROPERTY_HTTP_SECURE, "true");
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, "5");
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, "5");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, "75");
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, "2");
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, "1");
      properties.setProperty(PROPERTY_POOL_IO_WORKER_THREADS, "2");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, "12");

      S3ContextBuilder builder = new S3ContextBuilder(properties);
      builder.authenticate(id, secret);
      return builder;
   }

   public void authenticate(String id, String secret) {
      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(id, "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(secret,
               "awsSecretAccessKey"));
   }

   public S3Context buildContext() {
      return buildInjector().getInstance(S3Context.class);
   }
   
   protected void addParserModule(List<Module> modules) {
      modules.add(new S3ParserModule());
   }

   protected void addContextModule(List<Module> modules) {
      modules.add(new S3ContextModule());
   }

   protected void addConnectionModule(List<Module> modules) {
      modules.add(new LiveS3ConnectionModule());
   }

}
