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
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_API;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_PROVIDER;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.annotations.Api;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

/**
 * Creates {@link RestContext} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or
 * Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default
 * {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be
 * installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see RestContext
 */
public class RestContextBuilder<S, A> {

   static class BindPropertiesAndPrincipalContext extends AbstractModule {
      protected Properties properties;

      protected BindPropertiesAndPrincipalContext(Properties properties) {
         this.properties = checkNotNull(properties, "properties");
      }

      @Override
      protected void configure() {
         Properties toBind = new Properties();
         toBind.putAll(checkNotNull(properties, "properties"));
         toBind.putAll(System.getProperties());
         Names.bindProperties(binder(), toBind);
         bind(String.class).annotatedWith(Provider.class).toInstance(
               checkNotNull(toBind.getProperty(PROPERTY_PROVIDER), PROPERTY_PROVIDER));
         bind(URI.class).annotatedWith(Provider.class).toInstance(
               URI.create(checkNotNull(toBind.getProperty(PROPERTY_ENDPOINT), PROPERTY_ENDPOINT)));
         if (toBind.containsKey(PROPERTY_API))
            bind(String.class).annotatedWith(Api.class).toInstance(toBind.getProperty(PROPERTY_API));
         if (toBind.containsKey(PROPERTY_API_VERSION))
            bind(String.class).annotatedWith(ApiVersion.class).toInstance(toBind.getProperty(PROPERTY_API_VERSION));
         if (toBind.containsKey(PROPERTY_IDENTITY))
            bind(String.class).annotatedWith(Identity.class).toInstance(
                  checkNotNull(toBind.getProperty(PROPERTY_IDENTITY), PROPERTY_IDENTITY));
         if (toBind.containsKey(PROPERTY_CREDENTIAL))
            bind(String.class).annotatedWith(Credential.class).toInstance(toBind.getProperty(PROPERTY_CREDENTIAL));
      }
   }

   protected Properties properties;
   protected List<Module> modules = new ArrayList<Module>(3);
   protected Class<A> asyncClientType;
   protected Class<S> syncClientType;

   @Inject
   public RestContextBuilder(Class<S> syncClientClass, Class<A> asyncClientClass, Properties properties) {
      this.asyncClientType = checkNotNull(asyncClientClass, "asyncClientType");
      this.syncClientType = checkNotNull(syncClientClass, "syncClientType");
      this.properties = checkNotNull(properties, "properties");
   }

   public RestContextBuilder<S, A> withModules(Iterable<Module> modules) {
      Iterables.addAll(this.modules, modules);
      return this;
   }

   public Injector buildInjector() {
      addContextModule(modules);
      addClientModuleIfNotPresent(modules);
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      ifHttpConfigureRestOtherwiseGuiceClientFactory(modules);
      addExecutorServiceIfNotPresent(modules);
      modules.add(new BindPropertiesAndPrincipalContext(properties));
      return Guice.createInjector(modules);
   }

   @VisibleForTesting
   protected void addLoggingModuleIfNotPresent(List<Module> modules) {
      if (!Iterables.any(modules, Predicates.instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   protected void addHttpModuleIfNeededAndNotPresent(List<Module> modules) {
      if (defaultOrAtLeastOneModuleRequiresHttp(modules) && nothingConfiguresAnHttpService(modules))
         modules.add(new JavaUrlHttpCommandExecutorServiceModule());
   }

   private boolean nothingConfiguresAnHttpService(List<Module> modules) {
      return (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class);
         }

      }));
   }

   @VisibleForTesting
   protected void addContextModuleIfNotPresent(List<Module> modules) {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestContext.class);
         }

      })) {
         addContextModule(modules);
      }
   }

   @VisibleForTesting
   protected void addContextModule(List<Module> modules) {
      modules.add(new AbstractModule() {

         @SuppressWarnings("unchecked")
         @Override
         protected void configure() {
            bind(
                  (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(RestContext.class, syncClientType,
                        asyncClientType))).to(
                  TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class, syncClientType, asyncClientType)))
                  .in(Scopes.SINGLETON);

         }

      });
   }

   @VisibleForTesting
   protected void ifHttpConfigureRestOtherwiseGuiceClientFactory(List<Module> modules) {
      if (defaultOrAtLeastOneModuleRequiresHttp(modules)) {
         modules.add(new RestModule());
      }
   }

   private boolean defaultOrAtLeastOneModuleRequiresHttp(List<Module> modules) {
      return atLeastOneModuleRequiresHttp(modules) || !restClientModulePresent(modules);
   }

   private boolean atLeastOneModuleRequiresHttp(List<Module> modules) {
      return Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(RequiresHttp.class);
         }
      });
   }

   @VisibleForTesting
   protected void addClientModuleIfNotPresent(List<Module> modules) {
      if (!restClientModulePresent(modules)) {
         addClientModule(modules);
      }
   }

   private boolean restClientModulePresent(List<Module> modules) {
      return Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
         }

      });
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new RestClientModule<S, A>(syncClientType, asyncClientType));
   }

   @VisibleForTesting
   protected void addExecutorServiceIfNotPresent(List<Module> modules) {
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
            modules.add(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));
         } else {
            modules.add(new ExecutorServiceModule());
         }
      }
   }

   @VisibleForTesting
   public Properties getProperties() {
      return properties;
   }

   @SuppressWarnings("unchecked")
   public <T extends RestContext<S, A>> T buildContext() {
      Injector injector = buildInjector();
      return (T) injector.getInstance(Key.get(Types.newParameterizedType(RestContext.class, syncClientType,
            asyncClientType)));
   }
}
