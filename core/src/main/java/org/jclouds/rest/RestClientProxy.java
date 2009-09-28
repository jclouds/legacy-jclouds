/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rest;

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

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

@Singleton
public class RestClientProxy<T> implements InvocationHandler {
   private final JaxrsAnnotationProcessor<T> util;
   private final Class<T> declaring;
   private final TransformingHttpCommand.Factory commandFactory;

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
   public RestClientProxy(TransformingHttpCommand.Factory factory,
           JaxrsAnnotationProcessor<T> util, TypeLiteral<T> typeLiteral) {
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
      } else if (util.getDelegateOrNull(method) != null) {
         method = util.getDelegateOrNull(method);
         logger.trace("%s - converting method to request", method);
         Function<Exception, ?> exceptionParser = util
                  .createExceptionParserOrNullIfNotFound(method);
         HttpRequest request;
         try {
            request = util.createRequest(method, args);
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

         logger.trace("%s - converted method to request %s", method, request);

         Function<HttpResponse, ?> transformer = util.createResponseParser(method, request, args);

         logger.trace("%s - creating command for request %s, transformer %s, exceptionParser %s",
                  method, request, transformer, exceptionParser);
         Future<?> result = commandFactory.create(request, transformer, exceptionParser).execute();

         if (exceptionParser != null) {
            logger.trace("%s - wrapping future for request %s in exceptionParser %s", method,
                     request, exceptionParser);
            result = new FutureExceptionParser(result, exceptionParser);
         }

         if (method.getReturnType().isAssignableFrom(Future.class)) {
            return result;
         } else {
            logger
                     .trace("%s - invoking request synchronously %s", method, request,
                              exceptionParser);
            return result.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         }
      } else {
         throw new RuntimeException("method is intended solely to set constants: " + method);
      }
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