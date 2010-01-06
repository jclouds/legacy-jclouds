/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static java.util.concurrent.Executors.newCachedThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.concurrent.SingleThreaded;
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
 * Creates {@link RestContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see RestContext
 */
public abstract class RestContextBuilder<A, S> {

   protected final Properties properties;
   protected final List<Module> modules = new ArrayList<Module>(3);
   protected final TypeLiteral<A> asyncClientType;
   protected final TypeLiteral<S> syncClientType;

   protected RestContextBuilder(TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType,
            Properties properties) {
      this.asyncClientType = asyncClientType;
      this.syncClientType = syncClientType;
      this.properties = properties;
   }

   public RestContextBuilder<A, S> withExecutorService(ExecutorService service) {
      modules.add(new ExecutorServiceModule(service));
      return this;
   }

   public RestContextBuilder<A, S> withModules(Module... modules) {
      this.modules.addAll(Arrays.asList(modules));
      return this;
   }

   public Injector buildInjector() {

      addContextModule(modules);
      addClientModuleIfNotPresent(modules);
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
   protected void addClientModuleIfNotPresent(final List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
         }

      })) {
         addClientModule(modules);
      }
   }

   protected abstract void addClientModule(final List<Module> modules);

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
            modules.add(new ExecutorServiceModule(sameThreadExecutor()));
         } else {
            modules.add(new ExecutorServiceModule(newCachedThreadPool()));
         }
      }
   }

   @VisibleForTesting
   public Properties getProperties() {
      return properties;
   }

   @SuppressWarnings("unchecked")
   public RestContext<A, S> buildContext() {
      Injector injector = buildInjector();
      return (RestContext<A, S>) injector.getInstance(Key.get(Types.newParameterizedType(
               RestContext.class, asyncClientType.getType(), syncClientType.getType())));
   }
}
