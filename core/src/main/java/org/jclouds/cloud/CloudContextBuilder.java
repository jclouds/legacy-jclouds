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
package org.jclouds.cloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_PORT;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_RELAX_HOSTNAME;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_SECURE;
import static org.jclouds.http.HttpConstants.PROPERTY_JSON_DEBUG;
import static org.jclouds.http.HttpConstants.PROPERTY_SAX_DEBUG;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_REQUEST_INVOKER_THREADS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.command.ConfiguresResponseTransformer;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
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
import org.jclouds.util.Jsr330;

/**
 * Creates {@link CloudContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see CloudContext
 */
public abstract class CloudContextBuilder<X extends CloudContext<?>> {

   private static final String DEFAULT_SECURE_HTTP_PORT = "443";
   private static final String DEFAULT_NON_SECURE_HTTP_PORT = "80";

   protected final Properties properties;
   private final List<Module> modules = new ArrayList<Module>(3);

   protected CloudContextBuilder(Properties properties) {
      this.properties = properties;
   }

   public CloudContextBuilder<X> withHttpAddress(String httpAddress) {
      properties.setProperty(PROPERTY_HTTP_ADDRESS, httpAddress);
      return this;
   }

   public CloudContextBuilder<X> withSaxDebug() {
      properties.setProperty(PROPERTY_SAX_DEBUG, "true");
      return this;
   }

   public CloudContextBuilder<X> withJsonDebug() {
      properties.setProperty(PROPERTY_JSON_DEBUG, "true");
      return this;
   }
   
   /**
    * allow mismatches between the certificate and the hostname of ssl requests.
    */
   public CloudContextBuilder<X> relaxSSLHostname() {
      properties.setProperty(PROPERTY_HTTP_RELAX_HOSTNAME, "true");
      return this;
   }

   public CloudContextBuilder<X> withHttpMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   public CloudContextBuilder<X> withHttpMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   public CloudContextBuilder<X> withExecutorService(ExecutorService service) {
      modules.add(new ExecutorServiceModule(service));
      return this;
   }

   public CloudContextBuilder<X> withHttpPort(int httpPort) {
      properties.setProperty(PROPERTY_HTTP_PORT, Integer.toString(httpPort));
      return this;
   }

   public CloudContextBuilder<X> withHttpSecure(boolean httpSecure) {
      properties.setProperty(PROPERTY_HTTP_SECURE, Boolean.toString(httpSecure));
      return this;
   }

   public CloudContextBuilder<X> withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, Integer
               .toString(poolMaxConnectionReuse));
      return this;

   }

   public CloudContextBuilder<X> withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, Integer
               .toString(poolMaxSessionFailures));
      return this;

   }

   public CloudContextBuilder<X> withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, Integer
               .toString(poolRequestInvokerThreads));
      return this;

   }

   public CloudContextBuilder<X> withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      properties
               .setProperty(PROPERTY_POOL_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;

   }

   public CloudContextBuilder<X> withPoolMaxConnections(int poolMaxConnections) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, Integer.toString(poolMaxConnections));
      return this;
   }

   public CloudContextBuilder<X> withModule(Module module) {
      modules.add(module);
      return this;
   }

   public CloudContextBuilder<X> withModules(Module... modules) {
      this.modules.addAll(Arrays.asList(modules));
      return this;
   }

   public Injector buildInjector() {

      useDefaultPortIfNotPresent(properties);

      addLoggingModuleIfNotPresent(modules);

      addParserModuleIfNotPresent(modules);

      addConnectionModuleIfNotPresent(modules);

      addHttpModuleIfNeededAndNotPresent(modules);

      addExecutorServiceIfNotPresent(modules);

      modules.add(new AbstractModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), checkNotNull(properties, "properties"));
         }
      });
      addContextModule(modules);

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

   protected void addLoggingModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, Predicates.instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   protected void addHttpModuleIfNeededAndNotPresent(final List<Module> modules) {
      if (Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(RequiresHttp.class);
         }

      }) && (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class);
         }

      })))
         modules.add(new JavaUrlHttpCommandExecutorServiceModule());
   }

   public static void addExecutorServiceIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresExecutorService.class);
         }
      }

      )) {
         if (Iterables.any(modules, new Predicate<Module>() {
            public boolean apply(Module input) {
               return input.getClass().isAnnotationPresent(SingleThreaded.class);
            }
         })) {
            modules.add(new ExecutorServiceModule(new WithinThreadExecutorService()));
         } else {
            modules.add(new ExecutorServiceModule(Executors.newCachedThreadPool()));
         }
      }
   }

   protected void addConnectionModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresCloudConnection.class);
         }

      })) {
         addConnectionModule(modules);
      }
   }

   protected void addParserModuleIfNotPresent(List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresResponseTransformer.class);
         }

      }))
         addParserModule(modules);
   }

   @VisibleForTesting
   public Properties getProperties() {
      return properties;
   }

   public abstract X buildContext();

   public abstract void authenticate(String id, String secret);

   protected abstract void addParserModule(List<Module> modules);

   protected abstract void addContextModule(List<Module> modules);

   protected abstract void addConnectionModule(List<Module> modules);

}
