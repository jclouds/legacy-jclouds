/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.methods;

import java.io.Closeable;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.reflect.Invocation;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.internal.DelegatesToInvocationFunction;
import org.jclouds.rest.internal.DelegatesToPotentiallySyncToAsyncInvocationFunction;
import org.jclouds.rest.internal.InvokeAndCallGetOnFutures;
import org.jclouds.rest.internal.InvokeSyncToAsyncHttpMethod;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * supports sync-async mapping
 * 
 * @deprecated will be removed in jclouds 1.7; use {@link HttpApiModule}
 */
@Deprecated
public class SyncToAsyncHttpInvocationModule extends AbstractModule {
   protected final Map<Class<?>, Class<?>> sync2Async;

   public SyncToAsyncHttpInvocationModule() {
      this(ImmutableMap.<Class<?>, Class<?>> of());
   }

   public SyncToAsyncHttpInvocationModule(Map<Class<?>, Class<?>> sync2Async) {
      this.sync2Async = sync2Async;
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Map<Class<?>, Class<?>>>() {
      }).toInstance(sync2Async);
      bind(new TypeLiteral<Function<Invocation, Object>>() {
      }).to(InvokeSyncToAsyncHttpMethod.class);
      BinderUtils.bindSyncToAsyncHttpApi(binder(), HttpClient.class, HttpAsyncClient.class);
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
    * 
    * @see InvokeAndCallGetOnFutures
    * @see InvokeSyncToAsyncHttpMethod
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
         checkArgument(delegatedMethod.getExceptionTypes().equals(invoked.getExceptionTypes())
               || isCloseable(delegatedMethod), "invoked %s has different typed exceptions than target %s", invoked,
               delegatedMethod);
         cache.put(invoked, delegatedMethod);
      }
   }

   /**
    * In JDK7 Closeable.close is declared in AutoCloseable, which throws
    * Exception vs IOException, so we have to be more lenient about exception
    * type declarations.
    * 
    * <h4>note</h4>
    * 
    * This will be refactored out when we delete Async code in jclouds 1.7.
    */
   private static boolean isCloseable(Invokable<?, ?> delegatedMethod) {
      return "close".equals(delegatedMethod.getName())
            && Closeable.class.isAssignableFrom(delegatedMethod.getDeclaringClass());
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
}
