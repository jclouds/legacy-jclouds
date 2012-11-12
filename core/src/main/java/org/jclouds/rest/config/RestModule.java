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
package org.jclouds.rest.config;

import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.config.LocationModule;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;
import org.jclouds.rest.internal.AsyncRestClientProxy;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rest.internal.RestAnnotationProcessor.MethodKey;
import org.jclouds.rest.internal.SeedAnnotationCache;
import org.jclouds.util.Maps2;
import org.jclouds.util.Predicates2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.sun.jersey.api.uri.UriBuilderImpl;

public class RestModule extends AbstractModule {

   public static final TypeLiteral<Supplier<URI>> URI_SUPPLIER_TYPE = new TypeLiteral<Supplier<URI>>() {
   };
   protected final Map<Class<?>, Class<?>> sync2Async;
   protected final AtomicReference<AuthorizationException> authException = Atomics.newReference();
   
   public RestModule() {
      this(ImmutableMap.<Class<?>, Class<?>> of());
   }
   
   public RestModule(Map<Class<?>, Class<?>> sync2Async) {
      this.sync2Async = sync2Async;
   }

   protected void installLocations() {
      install(new LocationModule());
   }

   @Override
   protected void configure() {
      install(new SaxParserModule());
      install(new GsonModule());
      install(new FactoryModuleBuilder().build(BindToJsonPayloadWrappedWith.Factory.class));
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      bind(UriBuilder.class).to(UriBuilderImpl.class);
      bind(AsyncRestClientProxy.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
      BinderUtils.bindAsyncClient(binder(), HttpAsyncClient.class);
      BinderUtils.bindClient(binder(), HttpClient.class, HttpAsyncClient.class, ImmutableMap.<Class<?>, Class<?>> of(
               HttpClient.class, HttpAsyncClient.class));
      // this will help short circuit scenarios that can otherwise lock out users
      bind(new TypeLiteral<AtomicReference<AuthorizationException>>() {
      }).toInstance(authException);
      bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
      }).to(FilterStringsBoundToInjectorByName.class);
      installLocations();
   }

   /**
    * Shared for all types of rest clients. this is read-only in this class, and
    * currently populated only by {@link SeedAnnotationCache}
    * 
    * @see SeedAnnotationCache
    */
   @Provides
   @Singleton
   protected Cache<MethodKey, Method> delegationMap(){
      return CacheBuilder.newBuilder().build();
   }

   @Provides
   @Singleton
   @Named("TIMEOUTS")
   protected Map<String, Long> timeouts(Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      Map<String, String> stringBoundWithTimeoutPrefix = filterStringsBoundByName.apply(Predicates2.startsWith(PROPERTY_TIMEOUTS_PREFIX));
      Map<String, Long> longsByName = Maps.transformValues(stringBoundWithTimeoutPrefix, new Function<String, Long>() {

         @Override
         public Long apply(String input) {
            return Long.valueOf(String.valueOf(input));
         }

      });
      return Maps2.transformKeys(longsByName, new Function<String, String>() {

         @Override
         public String apply(String input) {
            return input.replaceFirst(PROPERTY_TIMEOUTS_PREFIX, "");
         }

      });

   }

   @Provides
   @Singleton
   protected LoadingCache<Class<?>, Boolean> seedAnnotationCache(SeedAnnotationCache seedAnnotationCache) {
      return CacheBuilder.newBuilder().build(seedAnnotationCache);
   }

   @Provides
   @Singleton
   @Named("async")
   LoadingCache<ClassMethodArgs, Object> provideAsyncDelegateMap(CreateAsyncClientForCaller createAsyncClientForCaller) {
      return CacheBuilder.newBuilder().build(createAsyncClientForCaller);
   }

   static class CreateAsyncClientForCaller extends CacheLoader<ClassMethodArgs, Object> {
      private final Injector injector;
      private final AsyncRestClientProxy.Factory factory;

      @Inject
      CreateAsyncClientForCaller(Injector injector, AsyncRestClientProxy.Factory factory) {
         this.injector = injector;
         this.factory = factory;
      }

      @SuppressWarnings( { "unchecked", "rawtypes" })
      @Override
      public Object load(final ClassMethodArgs from) {
         Class clazz = from.getClazz();
         TypeLiteral typeLiteral = TypeLiteral.get(clazz);
         RestAnnotationProcessor util = (RestAnnotationProcessor) injector.getInstance(Key.get(TypeLiteral.get(Types
                  .newParameterizedType(RestAnnotationProcessor.class, clazz))));
         // cannot use child injectors due to the super coarse guice lock on
         // Singleton
         util.setCaller(from);
         LoadingCache<ClassMethodArgs, Object> delegateMap = injector.getInstance(Key.get(
                  new TypeLiteral<LoadingCache<ClassMethodArgs, Object>>() {
                  }, Names.named("async")));
         AsyncRestClientProxy proxy = new AsyncRestClientProxy(injector, factory, util, typeLiteral, delegateMap);
         injector.injectMembers(proxy);
         return AsyncClientFactory.create(clazz, proxy);
      }
   }

   private static class Factory implements AsyncRestClientProxy.Factory {
      @Inject
      private TransformingHttpCommandExecutorService executorService;

      @SuppressWarnings( { "unchecked", "rawtypes" })
      @Override
      public TransformingHttpCommand<?> create(HttpRequest request, Function<HttpResponse, ?> transformer) {
         return new TransformingHttpCommandImpl(executorService, request, transformer);
      }

   }

   @Provides
   @Singleton
   @Named("sync")
   LoadingCache<ClassMethodArgs, Object> provideSyncDelegateMap(CreateClientForCaller createClientForCaller) {
      createClientForCaller.sync2Async = sync2Async;
      return CacheBuilder.newBuilder().build(createClientForCaller);
   }

}
