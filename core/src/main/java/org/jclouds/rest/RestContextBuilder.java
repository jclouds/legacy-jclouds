/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.any;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.util.Types.newParameterizedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.events.config.ConfiguresEventBus;
import org.jclouds.events.config.EventBusModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.lifecycle.config.LifeCycleModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.config.BindPropertiesToAnnotations;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestContextImpl;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;

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
public class RestContextBuilder<S, A> {
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
      addAll(this.modules, modules);
      return this;
   }

   public Injector buildInjector() {
      modules.add(Rocoto.expandVariables(new ConfigurationModule(){

         @Override
         protected void bindConfigurations() {
            Properties toBind = new Properties();
            toBind.putAll(checkNotNull(properties, "properties"));
            toBind.putAll(System.getProperties());
            bindProperties(toBind);            
         }
         
      }));
      addContextModule(modules);
      addClientModuleIfNotPresent(modules);
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      ifHttpConfigureRestOtherwiseGuiceClientFactory(modules);
      addExecutorServiceIfNotPresent(modules);
      addEventBusIfNotPresent(modules);
      addCredentialStoreIfNotPresent(modules);
      modules.add(new LifeCycleModule());
      modules.add(new BindPropertiesToAnnotations());
      Injector returnVal = Guice.createInjector(Stage.PRODUCTION, modules);
      returnVal.getInstance(ExecutionList.class).execute();
      return returnVal;
   }

   @VisibleForTesting
   protected void addLoggingModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   protected void addHttpModuleIfNeededAndNotPresent(List<Module> modules) {
      if (defaultOrAtLeastOneModuleRequiresHttp(modules) && nothingConfiguresAnHttpService(modules))
         modules.add(new JavaUrlHttpCommandExecutorServiceModule());
   }

   private boolean nothingConfiguresAnHttpService(List<Module> modules) {
      return (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class);
         }

      }));
   }

   @VisibleForTesting
   protected void addContextModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
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

         @SuppressWarnings( { "unchecked", "rawtypes" })
         @Override
         protected void configure() {
            bind(
                     (TypeLiteral) TypeLiteral.get(newParameterizedType(RestContext.class, syncClientType,
                              asyncClientType))).to(
                     TypeLiteral.get(newParameterizedType(RestContextImpl.class, syncClientType, asyncClientType))).in(
                     SINGLETON);

         }

         public String toString() {
            return String.format("configure rest context %s->%s", syncClientType.getSimpleName(), asyncClientType
                     .getSimpleName());
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
      return any(modules, new Predicate<Module>() {
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
      return any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
         }

      });
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new RestClientModule<S, A>(syncClientType, asyncClientType));
   }
   
   @VisibleForTesting
   protected void addEventBusIfNotPresent(List<Module> modules) {
       if (!any(modules, new Predicate<Module>() {
           public boolean apply(Module input) {
              return input.getClass().isAnnotationPresent(ConfiguresEventBus.class);
           }
        }
       
        )) {
           modules.add(new EventBusModule());
       }
   }

   @VisibleForTesting
   protected void addExecutorServiceIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresExecutorService.class);
         }
      }

      )) {
         if (any(modules, new Predicate<Module>() {
            public boolean apply(Module input) {
               return input.getClass().isAnnotationPresent(SingleThreaded.class);
            }
         })) {
            modules.add(new ExecutorServiceModule(MoreExecutors.sameThreadExecutor(), MoreExecutors
                     .sameThreadExecutor()));
         } else {
            modules.add(new ExecutorServiceModule());
         }
      }
   }

   @VisibleForTesting
   protected void addCredentialStoreIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresCredentialStore.class);
         }
      }

      )) {
         modules.add(new CredentialStoreModule());
      }
   }

   @VisibleForTesting 
   protected Properties getProperties() {
      return properties;
   }

   @SuppressWarnings("unchecked")
   public <T extends RestContext<S, A>> T buildContext() {
      Injector injector = buildInjector();
      return (T) injector
               .getInstance(Key.get(newParameterizedType(RestContext.class, syncClientType, asyncClientType)));
   }
}
