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
import static com.google.common.util.concurrent.Futures.transform;
import static com.google.common.util.concurrent.Futures.withFallback;
import static org.jclouds.concurrent.Futures.makeListenable;

import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invokable;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.Fallback;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class InvokeListenableFutureViaHttp implements Function<Invocation, ListenableFuture<?>> {

   public final static class Caller extends InvokeListenableFutureViaHttp {

      public static interface Factory {
         Caller caller(Invocation caller);
      }

      @Inject
      private Caller(Injector injector, RestAnnotationProcessor annotationProcessor, HttpCommandExecutorService http,
            Function<GeneratedHttpRequest, Function<HttpResponse, ?>> transformerForRequest,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads, @Assisted Invocation caller) {
         super(injector, annotationProcessor.caller(caller), http, transformerForRequest, userThreads);
      }
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Injector injector;
   private final RestAnnotationProcessor annotationProcessor;
   private final HttpCommandExecutorService http;
   private final Function<GeneratedHttpRequest, Function<HttpResponse, ?>> transformerForRequest;
   private final ExecutorService userThreads;

   @Inject
   private InvokeListenableFutureViaHttp(Injector injector, RestAnnotationProcessor annotationProcessor,
         HttpCommandExecutorService http,
         Function<GeneratedHttpRequest, Function<HttpResponse, ?>> transformerForRequest,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userThreads) {
      this.injector = injector;
      this.annotationProcessor = annotationProcessor;
      this.http = http;
      this.userThreads = userThreads;
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
   public ListenableFuture<?> apply(Invocation invocation) {
      String name = invocation.getInvokable().toString();
      logger.trace(">> converting %s", name);
      GeneratedHttpRequest request = annotationProcessor.apply(invocation);
      logger.trace("<< converted %s to %s", name, request.getRequestLine());

      Function<HttpResponse, ?> transformer = transformerForRequest.apply(request);
      logger.trace("<< response from %s is parsed by %s", name, transformer.getClass().getSimpleName());

      logger.debug(">> invoking %s", name);
      ListenableFuture<?> result = transform(makeListenable(http.submit(new HttpCommand(request)), userThreads), transformer);
      
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
      InvokeListenableFutureViaHttp that = InvokeListenableFutureViaHttp.class.cast(o);
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