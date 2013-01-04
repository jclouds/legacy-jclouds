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
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.internal.ClassInvokerArgs;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.config.LocationModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;
import org.jclouds.rest.internal.AsyncRestClientProxy;
import org.jclouds.rest.internal.CreateAsyncClientForCaller;
import org.jclouds.rest.internal.CreateClientForCaller;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Maps2;
import org.jclouds.util.Predicates2;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RestModule extends AbstractModule {

   public static final TypeLiteral<Supplier<URI>> URI_SUPPLIER_TYPE = new TypeLiteral<Supplier<URI>>() {
   };
   private final Map<Class<?>, Class<?>> sync2Async;
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
      bind(new TypeLiteral<Map<Class<?>, Class<?>>>(){}).toInstance(sync2Async);
      install(new SaxParserModule());
      install(new GsonModule());
      install(new FactoryModuleBuilder().build(BindToJsonPayloadWrappedWith.Factory.class));
      install(new FactoryModuleBuilder().build(RestAnnotationProcessor.Factory.class));
      install(new FactoryModuleBuilder().build(AsyncRestClientProxy.Factory.class));
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      install(new FactoryModuleBuilder().build(SyncProxy.Factory.class));
      bindClientAndAsyncClient(binder(), HttpClient.class, HttpAsyncClient.class);
      // this will help short circuit scenarios that can otherwise lock out users
      bind(new TypeLiteral<AtomicReference<AuthorizationException>>() {
      }).toInstance(authException);
      bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
      }).to(FilterStringsBoundToInjectorByName.class);
      installLocations();
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
   @Named("async")
   LoadingCache<ClassInvokerArgs, Object> provideAsyncDelegateMap(CreateAsyncClientForCaller createAsyncClientForCaller) {
      return CacheBuilder.newBuilder().build(createAsyncClientForCaller);
   }

   @Provides
   @Singleton
   @Named("sync")
   LoadingCache<ClassInvokerArgs, Object> provideSyncDelegateMap(CreateClientForCaller createClientForCaller) {
      return CacheBuilder.newBuilder().build(createClientForCaller);
   }

}
