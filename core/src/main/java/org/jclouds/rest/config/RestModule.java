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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Atomics.newReference;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.methods;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.net.Proxy;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.config.LocationModule;
import org.jclouds.proxy.ProxyForURI;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;
import org.jclouds.rest.internal.InvokeHttpMethod;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rest.internal.TransformerForRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RestModule extends AbstractModule {

   public static final TypeLiteral<Supplier<URI>> URI_SUPPLIER_TYPE = new TypeLiteral<Supplier<URI>>() {
   };

   protected final Map<Class<?>, Class<?>> sync2Async;
   protected final AtomicReference<AuthorizationException> authException = newReference();

   public RestModule() {
      this(ImmutableMap.<Class<?>, Class<?>> of());
   }

   public RestModule(Map<Class<?>, Class<?>> sync2Async) {
      this.sync2Async = sync2Async;
   }

   /**
    * seeds well-known invokables.
    */
   @Provides
   @Singleton
   protected Cache<Invokable<?, ?>, Invokable<?, ?>> seedKnownSync2AsyncInvokables() {
      return seedKnownSync2AsyncInvokables(sync2Async);
   }

   /**
    * function view of above
    */
   @Provides
   @Singleton
   protected Function<Invocation, Invocation> sync2async(final Cache<Invokable<?, ?>, Invokable<?, ?>> cache) {
      return new Function<Invocation, Invocation>() {
         public Invocation apply(Invocation in) {
            return Invocation.create(
                  checkNotNull(cache.getIfPresent(in.getInvokable()), "invokable %s not in %s", in.getInvokable(),
                        cache), in.getArgs());
         }
      };
   }

   @VisibleForTesting
   static Cache<Invokable<?, ?>, Invokable<?, ?>> seedKnownSync2AsyncInvokables(Map<Class<?>, Class<?>> sync2Async) {
      Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncBuilder = CacheBuilder.newBuilder().build();
      putInvokables(HttpClient.class, HttpAsyncClient.class, sync2AsyncBuilder);
      for (Map.Entry<Class<?>, Class<?>> entry : sync2Async.entrySet()) {
         putInvokables(entry.getKey(), entry.getValue(), sync2AsyncBuilder);
      }
      return sync2AsyncBuilder;
   }

   // accessible for ClientProvider
   public static void putInvokables(Class<?> sync, Class<?> async, Cache<Invokable<?, ?>, Invokable<?, ?>> cache) {
      for (Invokable<?, ?> invoked : methods(sync)) {
         Invokable<?, ?> delegatedMethod = method(async, invoked.getName(), getParameterTypes(invoked));
         checkArgument(delegatedMethod.getExceptionTypes().equals(invoked.getExceptionTypes()),
               "invoked %s has different typed exceptions than target %s", invoked, delegatedMethod);
         cache.put(invoked, delegatedMethod);
      }
   }

   /**
    * for portability with {@link Class#getMethod(String, Class...)}
    */
   private static Class<?>[] getParameterTypes(Invokable<?, ?> in) {
      return toArray(transform(checkNotNull(in, "invokable").getParameters(), new Function<Parameter, Class<?>>() {
         public Class<?> apply(Parameter input) {
            return input.getType().getRawType();
         }
      }), Class.class);
   }

   protected void installLocations() {
      install(new LocationModule());
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Map<Class<?>, Class<?>>>() {
      }).toInstance(sync2Async);
      install(new SaxParserModule());
      install(new GsonModule());
      install(new SetCaller.Module());
      install(new FactoryModuleBuilder().build(BindToJsonPayloadWrappedWith.Factory.class));
      bind(new TypeLiteral<Function<HttpRequest, Function<HttpResponse, ?>>>() {
      }).to(TransformerForRequest.class);
      bind(new TypeLiteral<Function<Invocation, Object>>() {
      }).to(InvokeHttpMethod.class);
      bind(new TypeLiteral<org.jclouds.Fallback<Object>>() {
      }).to(MapHttp4xxCodesToExceptions.class);
      bind(new TypeLiteral<Function<Invocation, HttpRequest>>() {
      }).to(RestAnnotationProcessor.class);
      bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
      bindHttpApi(binder(), HttpClient.class, HttpAsyncClient.class);
      // this will help short circuit scenarios that can otherwise lock out users
      bind(new TypeLiteral<AtomicReference<AuthorizationException>>() {
      }).toInstance(authException);
      bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
      }).to(FilterStringsBoundToInjectorByName.class);
      bind(new TypeLiteral<Function<URI, Proxy>>() {
      }).to(ProxyForURI.class);
      installLocations();
   }
}
