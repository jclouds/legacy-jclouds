/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rest.internal;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * 
 * @author Adrian Cole
 */
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.FutureExceptionParser;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.logging.Logger;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;

@Singleton
public class RestClientProxy<T> implements InvocationHandler {
   private final Injector injector;
   private final RestAnnotationProcessor<T> util;
   private final Class<T> declaring;
   private final Factory commandFactory;

   /**
    * maximum duration of an unwrapped http Request
    */
   @Inject(optional = true)
   @Named(HttpConstants.PROPERTY_HTTP_REQUEST_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Resource
   protected Logger logger = Logger.NULL;

   @SuppressWarnings("unchecked")
   @Inject
   public RestClientProxy(Injector injector, Factory factory, RestAnnotationProcessor<T> util,
            TypeLiteral<T> typeLiteral) {
      this.injector = injector;
      this.util = util;
      this.declaring = (Class<T>) typeLiteral.getRawType();
      this.commandFactory = factory;
   }

   @SuppressWarnings("unchecked")
   public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      if (method.getName().equals("equals")) {
         return this.equals(o);
      } else if (method.getName().equals("hashCode")) {
         return this.hashCode();
      } else if (method.getName().startsWith("new")) {
         return injector.getInstance(method.getReturnType());
      } else if (util.getDelegateOrNull(method) != null) {
         method = util.getDelegateOrNull(method);
         logger.trace("Converting %s.%s", declaring.getSimpleName(), method.getName());
         Function<Exception, ?> exceptionParser = util
                  .createExceptionParserOrNullIfNotFound(method);
         // in case there is an exception creating the request, we should at least pass in args
         if (exceptionParser instanceof InvocationContext) {
            ((InvocationContext) exceptionParser).setContext(null);
         }
         GeneratedHttpRequest<T> request;
         try {
            request = util.createRequest(method, args);
            if (exceptionParser instanceof InvocationContext) {
               ((InvocationContext) exceptionParser).setContext(request);
            }
         } catch (RuntimeException e) {
            if (exceptionParser != null) {
               final Object toReturn = exceptionParser.apply(e);
               if (toReturn == null)
                  throw e;
               if (method.getReturnType().isAssignableFrom(Future.class)) {
                  return new Future<Object>() {

                     public boolean cancel(boolean mayInterruptIfRunning) {
                        return false;
                     }

                     public Object get() throws InterruptedException, ExecutionException {
                        return toReturn;
                     }

                     public Object get(long timeout, TimeUnit unit) throws InterruptedException,
                              ExecutionException, TimeoutException {
                        return get();
                     }

                     public boolean isCancelled() {
                        return false;
                     }

                     public boolean isDone() {
                        return true;
                     }

                  };
               } else {
                  return toReturn;
               }
            }
            throw e;
         }
         logger.debug("Converted %s.%s to %s", declaring.getSimpleName(), method.getName(), request
                  .getRequestLine());

         Function<HttpResponse, ?> transformer = util.createResponseParser(method, request);
         logger.trace("Response from %s.%s is parsed by %s", declaring.getSimpleName(), method
                  .getName(), transformer.getClass().getSimpleName());

         logger.debug("Invoking %s.%s", declaring.getSimpleName(), method.getName());
         Future<?> result = commandFactory.create(request, transformer, exceptionParser).execute();

         if (exceptionParser != null) {
            logger.trace("Exceptions from %s.%s are parsed by %s", declaring.getSimpleName(),
                     method.getName(), exceptionParser.getClass().getSimpleName());
            result = new FutureExceptionParser(result, exceptionParser);
         }

         if (method.getReturnType().isAssignableFrom(Future.class)) {
            return result;
         } else {
            logger.debug("Blocking up to %dms for %s.%s to complete", requestTimeoutMilliseconds,
                     declaring.getSimpleName(), method.getName());
            return result.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         }
      } else {
         throw new RuntimeException("method is intended solely to set constants: " + method);
      }
   }

   public static interface Factory {
      public TransformingHttpCommand<?> create(HttpRequest request,
               Function<HttpResponse, ?> transformer,
               @Nullable Function<Exception, ?> exceptionTransformer);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof RestClientProxy<?>))
         return false;
      RestClientProxy<?> other = (RestClientProxy<?>) obj;
      if (other == this)
         return true;
      if (other.declaring != this.declaring)
         return false;
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return declaring.hashCode();
   }

   public String toString() {
      return "Client Proxy for :" + declaring.getName();
   }
}