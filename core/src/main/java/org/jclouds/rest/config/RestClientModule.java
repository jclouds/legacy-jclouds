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
package org.jclouds.rest.config;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class RestClientModule<S, A> extends AbstractModule {

   protected final Class<A> asyncClientType;
   protected final Class<S> syncClientType;
   protected final Map<Class<?>, Class<?>> delegates;

   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
            Map<Class<?>, Class<?>> delegates) {
      this.asyncClientType = asyncClientType;
      this.syncClientType = syncClientType;
      this.delegates = delegates;
   }

   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      this(syncClientType, asyncClientType, ImmutableMap.<Class<?>, Class<?>> of(syncClientType,
               asyncClientType));
   }

   @Override
   protected void configure() {
      bindAsyncClient();
      bindClient();
      bindErrorHandlers();
      bindRetryHandlers();
   }

   /**
    * overrides this to change the default retry handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(AWSRedirectionRetryHandler.class);
    * bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AWSClientErrorRetryHandler.class);
    * </pre>
    * 
    */
   protected void bindRetryHandlers() {
   }

   /**
    * overrides this to change the default error handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
    * </pre>
    * 
    * 
    */
   protected void bindErrorHandlers() {
   }

   protected void bindAsyncClient() {
      Provider<A> asyncProvider = new AsyncClientProvider<A>(asyncClientType);
      binder().requestInjection(asyncProvider);
      bind(asyncClientType).toProvider(asyncProvider);
   }

   @Singleton
   static class AsyncClientProvider<A> implements Provider<A> {
      @Inject
      Injector injector;
      private final Class<A> asyncClientType;

      @Inject
      AsyncClientProvider(Class<A> asyncClientType) {
         this.asyncClientType = asyncClientType;
      }

      @Override
      @Singleton
      public A get() {
         return (A) injector.getInstance(AsyncClientFactory.class).create(asyncClientType);
      }

   }

   protected void bindClient() {
      Provider<S> asyncProvider = new ClientProvider<S, A>(asyncClientType, syncClientType,
               delegates);
      binder().requestInjection(asyncProvider);
      bind(syncClientType).toProvider(asyncProvider);
   }

   @Singleton
   static class ClientProvider<S, A> implements Provider<S> {
      @Inject
      Injector injector;
      private final Class<S> syncClientType;
      private final Class<A> asyncClientType;
      private final Map<Class<?>, Class<?>> sync2Async;

      @Inject
      ClientProvider(Class<A> asyncClientType, Class<S> syncClientType,
               Map<Class<?>, Class<?>> sync2Async) {
         this.asyncClientType = asyncClientType;
         this.syncClientType = syncClientType;
         this.sync2Async = sync2Async;
      }

      @Override
      @Singleton
      public S get() {
         A client = (A) injector.getInstance(asyncClientType);
         ConcurrentMap<ClassMethodArgs, Object> delegateMap = injector.getInstance(Key.get(
                  new TypeLiteral<ConcurrentMap<ClassMethodArgs, Object>>() {
                  }, Names.named("sync")));
         try {
            return (S) SyncProxy.proxy(syncClientType, new SyncProxy(syncClientType, client,
                     delegateMap, sync2Async));
         } catch (Exception e) {
            Throwables.propagate(e);
            assert false : "should have propagated";
            return null;
         }
      }
   }

   @Provides
   @Singleton
   @Named("sync")
   ConcurrentMap<ClassMethodArgs, Object> provideSyncDelegateMap(
            CreateClientForCaller createClientForCaller) {
      createClientForCaller.sync2Async = delegates;
      return new MapMaker().makeComputingMap(createClientForCaller);
   }

   static class CreateClientForCaller implements Function<ClassMethodArgs, Object> {
      private final ConcurrentMap<ClassMethodArgs, Object> asyncMap;
      private final Provider<ConcurrentMap<ClassMethodArgs, Object>> delegateMap;
      private Map<Class<?>, Class<?>> sync2Async;

      @Inject
      CreateClientForCaller(@Named("async") ConcurrentMap<ClassMethodArgs, Object> asyncMap,
               @Named("sync") Provider<ConcurrentMap<ClassMethodArgs, Object>> delegateMap) {
         this.asyncMap = asyncMap;
         this.delegateMap = delegateMap;
      }

      @SuppressWarnings("unchecked")
      public Object apply(final ClassMethodArgs from) {
         Class syncClass = from.getMethod().getReturnType();
         Class asyncClass = sync2Async.get(syncClass);
         checkState(asyncClass != null, "configuration error, sync class " + syncClass.getName()
                  + " not mapped to an async class");
         Object asyncClient = asyncMap.get(from);
         checkState(asyncClient != null, "configuration error, sync client for " + from
                  + " not found");
         try {
            return SyncProxy.proxy(syncClass, new SyncProxy(syncClass, asyncClient, delegateMap
                     .get(), sync2Async));
         } catch (Exception e) {
            Throwables.propagate(e);
            assert false : "should have propagated";
            return null;
         }

      }
   }
}