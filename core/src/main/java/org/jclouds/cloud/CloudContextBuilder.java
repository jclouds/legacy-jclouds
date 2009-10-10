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
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_RELAX_HOSTNAME;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_REQUEST_INVOKER_THREADS;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.config.RestModule;
import org.jclouds.util.Jsr330;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

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
public abstract class CloudContextBuilder<C> {

   protected final Properties properties;
   protected final List<Module> modules = new ArrayList<Module>(3);
   protected final TypeLiteral<C> connectionType;

   protected CloudContextBuilder(TypeLiteral<C> connectionTypeLiteral, Properties properties) {
      this.connectionType = connectionTypeLiteral;
      this.properties = properties;
   }

   /**
    * allow mismatches between the certificate and the hostname of ssl requests.
    */
   public CloudContextBuilder<C> relaxSSLHostname() {
      properties.setProperty(PROPERTY_HTTP_RELAX_HOSTNAME, "true");
      return this;
   }

   public CloudContextBuilder<C> withHttpMaxRetries(int httpMaxRetries) {
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, Integer.toString(httpMaxRetries));
      return this;
   }

   public CloudContextBuilder<C> withHttpMaxRedirects(int httpMaxRedirects) {
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, Integer.toString(httpMaxRedirects));
      return this;
   }

   public CloudContextBuilder<C> withExecutorService(ExecutorService service) {
      modules.add(new ExecutorServiceModule(service));
      return this;
   }

   public CloudContextBuilder<C> withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, Integer
               .toString(poolMaxConnectionReuse));
      return this;
   }

   public abstract CloudContextBuilder<C> withEndpoint(URI endpoint);

   public CloudContextBuilder<C> withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, Integer
               .toString(poolMaxSessionFailures));
      return this;

   }

   public CloudContextBuilder<C> withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, Integer
               .toString(poolRequestInvokerThreads));
      return this;

   }

   public CloudContextBuilder<C> withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      properties
               .setProperty(PROPERTY_POOL_IO_WORKER_THREADS, Integer.toString(poolIoWorkerThreads));
      return this;

   }

   public CloudContextBuilder<C> withPoolMaxConnections(int poolMaxConnections) {
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, Integer.toString(poolMaxConnections));
      return this;
   }

   public CloudContextBuilder<C> withModule(Module module) {
      modules.add(module);
      return this;
   }

   public CloudContextBuilder<C> withModules(Module... modules) {
      this.modules.addAll(Arrays.asList(modules));
      return this;
   }

   public Injector buildInjector() {

      addContextModule(modules);
      addConnectionModuleIfNotPresent(modules);
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      ifHttpConfigureRestOtherwiseGuiceClientFactory(modules);
      addExecutorServiceIfNotPresent(modules);
      modules.add(new AbstractModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(binder(), checkNotNull(properties, "properties"));
         }
      });
      return Guice.createInjector(modules);
   }

   @VisibleForTesting
   protected void addLoggingModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, Predicates.instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
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

   @VisibleForTesting
   protected abstract void addContextModule(List<Module> modules);

   @VisibleForTesting
   protected void ifHttpConfigureRestOtherwiseGuiceClientFactory(final List<Module> modules) {
      if (Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(RequiresHttp.class);
         }

      })) {
         modules.add(new RestModule());
      }
   }

   @VisibleForTesting
   protected void addConnectionModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresCloudConnection.class);
         }

      })) {
         addConnectionModule(modules);
      }
   }

   protected abstract void addConnectionModule(final List<Module> modules);

   @VisibleForTesting
   protected void addExecutorServiceIfNotPresent(final List<Module> modules) {
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

   @VisibleForTesting
   public Properties getProperties() {
      return properties;
   }

   @SuppressWarnings("unchecked")
   public CloudContext<C> buildContext() {
      Injector injector = buildInjector();
      return (CloudContext<C>) injector.getInstance(Key.get(Types.newParameterizedType(
               CloudContext.class, connectionType.getType()))); 
   }
}
