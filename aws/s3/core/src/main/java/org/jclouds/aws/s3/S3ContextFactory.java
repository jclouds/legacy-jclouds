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
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_PORT;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_SECURE;
import static org.jclouds.http.HttpConstants.PROPERTY_SAX_DEBUG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jclouds.aws.s3.config.LiveS3ConnectionModule;
import org.jclouds.aws.s3.config.S3ConnectionModule;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.http.config.HttpFutureCommandClientModule;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

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
public class S3ContextFactory {

   private static final String DEFAULT_SECURE_HTTP_PORT = "443";
   private static final String DEFAULT_NON_SECURE_HTTP_PORT = "80";

   private final Properties properties;
   private final List<Module> modules = new ArrayList<Module>(3);

   private S3ContextFactory(Properties properties) {
      this.properties = properties;
   }

   public static S3ContextFactory createContext(String awsAccessKeyId, String awsSecretAccessKey) {
      Properties properties = new Properties();

      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(awsAccessKeyId,
               "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(awsSecretAccessKey,
               "awsSecretAccessKey"));

      properties.setProperty(PROPERTY_SAX_DEBUG, "false");
      properties.setProperty(PROPERTY_HTTP_ADDRESS, "s3.amazonaws.com");
      properties.setProperty(PROPERTY_HTTP_SECURE, "true");
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, "5");
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, "5");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, "75");
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, "2");
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, "1");
      properties.setProperty(PROPERTY_POOL_IO_WORKER_THREADS, "2");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, "12");

      return new S3ContextFactory(properties);
   }

   public S3Context build() {
      return createInjector().getInstance(S3Context.class);
   }

   public static Injector createInjector(String awsAccessKeyId, String awsSecretAccessKey,
            Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules)
               .createInjector();
   }

   public static S3Context createS3Context(String awsAccessKeyId, String awsSecretAccessKey,
            Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).build();

   }

   public static Injector createInjector(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).createInjector();
   }

   public static S3Context createS3Context(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, Module... modules) {

      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).build();
   }

   public static Injector createInjector(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, String server, Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).withHttpAddress(server).createInjector();
   }

   public static S3Context createS3Context(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, String server, Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).withHttpAddress(server).build();
   }

   public static S3Context createS3Context(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, String server, int port, Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).withHttpAddress(server).withHttpPort(port).build();
   }

   public static Injector createInjector(String awsAccessKeyId, String awsSecretAccessKey,
            boolean isSecure, String server, int port, Module... modules) {
      return createContext(awsAccessKeyId, awsSecretAccessKey).withModules(modules).withHttpSecure(
               isSecure).withHttpAddress(server).withHttpPort(port).createInjector();
   }

   public S3ContextFactory withHttpAddress(String httpAddress) {
      properties.setProperty(PROPERTY_HTTP_ADDRESS, httpAddress);
      return this;
   }

   public S3ContextFactory withSaxDebug() {
      properties.setProperty(PROPERTY_SAX_DEBUG, "true");
      return this;
   }

   public S3ContextFactory withHttpMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   public S3ContextFactory withHttpMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   public S3ContextFactory withHttpPort(int httpPort) {
      properties.setProperty(PROPERTY_HTTP_PORT, Integer.toString(httpPort));
      return this;
   }

   public S3ContextFactory withHttpSecure(boolean httpSecure) {
      properties.setProperty(PROPERTY_HTTP_SECURE, Boolean.toString(httpSecure));
      return this;
   }

   public S3ContextFactory withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, Integer
               .toString(poolMaxConnectionReuse));
      return this;

   }

   public S3ContextFactory withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, Integer
               .toString(poolMaxSessionFailures));
      return this;

   }

   public S3ContextFactory withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, Integer
               .toString(poolRequestInvokerThreads));
      return this;

   }

   public S3ContextFactory withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      properties
               .setProperty(PROPERTY_POOL_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;

   }

   public S3ContextFactory withPoolMaxConnections(int poolMaxConnections) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, Integer.toString(poolMaxConnections));
      return this;
   }

   public S3ContextFactory withModule(Module module) {
      modules.add(module);
      return this;
   }

   public S3ContextFactory withModules(Module... modules) {
      this.modules.addAll(Arrays.asList(modules));
      return this;
   }

   private Injector createInjector() {

      useDefaultPortIfNotPresent(properties);

      addLoggingModuleIfNotPresent(modules);

      addS3ParserModuleIfNotPresent(modules);

      addS3ConnectionModuleIfNotPresent(modules);

      addHttpModuleIfNeededAndNotPresent(modules);

      modules.add(new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), checkNotNull(properties, "properties"));
         }
      });
      modules.add(new S3ContextModule());

      return Guice.createInjector(modules);
   }

   private void useDefaultPortIfNotPresent(Properties properties) {
      /* Use 80 or 443 as the default port if one hasn't been set? */
      if (!properties.containsKey(PROPERTY_HTTP_PORT)) {
         if (Boolean.parseBoolean(properties.getProperty(PROPERTY_HTTP_SECURE))) {
            properties.setProperty(PROPERTY_HTTP_PORT, DEFAULT_SECURE_HTTP_PORT);
         } else {
            properties.setProperty(PROPERTY_HTTP_PORT, DEFAULT_NON_SECURE_HTTP_PORT);
         }
      }
   }

   @VisibleForTesting
   static void addS3ParserModuleIfNotPresent(List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input instanceof S3ParserModule;
         }

      }))
         modules.add(new S3ParserModule());
   }

   @VisibleForTesting
   static void addHttpModuleIfNeededAndNotPresent(final List<Module> modules) {
      if (Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input instanceof LiveS3ConnectionModule;
         }

      }) && (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(HttpFutureCommandClientModule.class);
         }

      })))
         modules.add(new JavaUrlHttpFutureCommandClientModule());
   }

   @VisibleForTesting
   static void addS3ConnectionModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(S3ConnectionModule.class);
         }

      })) {
         modules.add(0, new LiveS3ConnectionModule());
      }
   }

   @VisibleForTesting
   static void addLoggingModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, Predicates.instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   Properties getProperties() {
      return properties;
   }
}
