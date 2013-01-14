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
package org.jclouds.rest.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.Futures.transform;
import static com.google.common.util.concurrent.Futures.withFallback;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.Fallback;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class InvokeHttpMethod<S, A> implements Function<Invocation, Object> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Injector injector;
   private final TypeToken<A> enclosingType;
   private final Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables;
   private final RestAnnotationProcessor<A> annotationProcessor;
   private final HttpCommandExecutorService http;
   private final TransformerForRequest<A> transformerForRequest;
   private final ListeningExecutorService userExecutor;
   private final BlockOnFuture.Factory blocker;

   @SuppressWarnings("unchecked")
   @Inject
   private InvokeHttpMethod(Injector injector, TypeLiteral<A> enclosingType,
         Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables, RestAnnotationProcessor<A> annotationProcessor,
         HttpCommandExecutorService http, TransformerForRequest<A> transformerForRequest,
         @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, BlockOnFuture.Factory blocker) {
      this.injector = injector;
      this.enclosingType = (TypeToken<A>) TypeToken.of(enclosingType.getType());
      this.sync2AsyncInvokables = sync2AsyncInvokables;
      this.annotationProcessor = annotationProcessor;
      this.http = http;
      this.userExecutor = userExecutor;
      this.blocker = blocker;
      this.transformerForRequest = transformerForRequest;
   }

   private final LoadingCache<Invokable<?, ?>, FutureFallback<?>> fallbacks = CacheBuilder.newBuilder().build(
         new CacheLoader<Invokable<?, ?>, FutureFallback<?>>() {

            @Override
            public FutureFallback<?> load(Invokable<?, ?> key) throws Exception {
               Fallback annotation = key.getAnnotation(Fallback.class);
               if (annotation != null) {
                  return injector.getInstance(annotation.value());
               }
               return injector.getInstance(MapHttp4xxCodesToExceptions.class);
            }

         });

   @Override
   public Object apply(Invocation in) {
      if (isFuture(in.getInvokable())) {
         return createFuture(in);
      }
      @SuppressWarnings("rawtypes")
      Invokable async = checkNotNull(sync2AsyncInvokables.getIfPresent(in.getInvokable()), "invokable %s not in %s",
            in.getInvokable(), sync2AsyncInvokables);
      checkState(isFuture(async), "not a future: %s", async);
      return blocker.create(enclosingType, in).apply(createFuture(Invocation.create(async, in.getArgs())));
   }

   private boolean isFuture(Invokable<?, ?> in) {
      return in.getReturnType().getRawType().equals(ListenableFuture.class);
   }

   public ListenableFuture<?> createFuture(Invocation invocation) {
      String name = invocation.getInvokable().toString();
      logger.trace(">> converting %s", name);
      GeneratedHttpRequest<A> request = annotationProcessor.apply(invocation);
      logger.trace("<< converted %s to %s", name, request.getRequestLine());

      Function<HttpResponse, ?> transformer = transformerForRequest.apply(request);
      logger.trace("<< response from %s is parsed by %s", name, transformer.getClass().getSimpleName());

      logger.debug(">> invoking %s", name);
      ListenableFuture<?> result = transform(http.submit(new HttpCommand(request)), transformer, userExecutor);

      FutureFallback<?> fallback = fallbacks.getUnchecked(invocation.getInvokable());
      if (fallback instanceof InvocationContext) {
         InvocationContext.class.cast(fallback).setContext(request);
      }
      logger.trace("<< exceptions from %s are parsed by %s", name, fallback.getClass().getSimpleName());
      return withFallback(result, fallback);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InvokeHttpMethod<?, ?> that = InvokeHttpMethod.class.cast(o);
      return equal(this.annotationProcessor, that.annotationProcessor);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(annotationProcessor);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("annotationParser", annotationProcessor).toString();
   }
}