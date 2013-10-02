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
package org.jclouds.rest.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Throwables.propagate;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.config.InvocationConfig;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * @author Adrian Cole
 */
public class InvokeHttpMethod implements Function<Invocation, Object> {

   @Resource
   private Logger logger = Logger.NULL;

   private final Function<Invocation, HttpRequest> annotationProcessor;
   private final HttpCommandExecutorService http;
   private final TimeLimiter timeLimiter;
   private final Function<HttpRequest, Function<HttpResponse, ?>> transformerForRequest;
   private final InvocationConfig config;

   @Inject
   @VisibleForTesting
   InvokeHttpMethod(Function<Invocation, HttpRequest> annotationProcessor,
         HttpCommandExecutorService http, Function<HttpRequest, Function<HttpResponse, ?>> transformerForRequest,
         TimeLimiter timeLimiter, InvocationConfig config) {
      this.annotationProcessor = annotationProcessor;
      this.http = http;
      this.timeLimiter = timeLimiter;
      this.transformerForRequest = transformerForRequest;
      this.config = config;
   }

   @Override
   public Object apply(Invocation in) {
      Optional<Long> timeoutNanos = config.getTimeoutNanos(in);
      if (timeoutNanos.isPresent()) {
         return invokeWithTimeout(in, timeoutNanos.get());
      }
      return invoke(in);
   }

   /**
    * invokes the {@linkplain HttpCommand} associated with {@code invocation},
    * {@link #getTransformer(String, HttpCommand) parses its response}, and
    * applies a {@link #getFallback(String, Invocation, HttpCommand) fallback}
    * if a {@code Throwable} is encountered.
    */
   public Object invoke(Invocation invocation) {
      String commandName = config.getCommandName(invocation);
      HttpCommand command = toCommand(commandName, invocation);
      Function<HttpResponse, ?> transformer = getTransformer(commandName, command);
      org.jclouds.Fallback<?> fallback = getFallback(commandName, invocation, command);

      logger.debug(">> invoking %s", commandName);
      try {
         return transformer.apply(http.invoke(command));
      } catch (Throwable t) {
         try {
            return fallback.createOrPropagate(t);
         } catch (Exception e) {
            throw propagate(e);
         }
      }
   }

   /**
    * calls {@link #invoke(Invocation)}, timing out after the specified time
    * limit. If the target method call finished before the limit is reached, the
    * return value or exception is propagated to the caller exactly as-is. If,
    * on the other hand, the time limit is reached, we attempt to abort the call
    * to the target, and throw an {@link UncheckedTimeoutException} to the
    * caller.
    * 
    * @param invocation
    *           the Invocation to invoke via {@link #invoke(Invocation)}
    * @param limitNanos
    *           with timeoutUnit, the maximum length of time to wait in
    *           nanoseconds
    * @throws InterruptedException
    *            if our thread is interrupted during execution
    * @throws UncheckedTimeoutException
    *            if the time limit is reached
    * @see TimeLimiter#callWithTimeout(Callable, long, TimeUnit, boolean)
    */
   public Object invokeWithTimeout(final Invocation invocation, final long limitNanos) {
      String commandName = config.getCommandName(invocation);
      HttpCommand command = toCommand(commandName, invocation);
      org.jclouds.Fallback<?> fallback = getFallback(commandName, invocation, command);

      logger.debug(">> blocking on %s for %s", invocation, limitNanos);
      try {
         return timeLimiter
               .callWithTimeout(new InvokeAndTransform(commandName, command), limitNanos, NANOSECONDS, true);
      } catch (Throwable t) {
         try {
            return fallback.createOrPropagate(t);
         } catch (Exception e) {
            throw propagate(e);
         }
      }
   }

   private org.jclouds.Fallback<?> getFallback(String commandName, Invocation invocation, HttpCommand command) {
      HttpRequest request = command.getCurrentRequest();
      org.jclouds.Fallback<?> fallback = config.getFallback(invocation);
      if (fallback instanceof InvocationContext)
         InvocationContext.class.cast(fallback).setContext(request);
      logger.trace("<< exceptions from %s are parsed by %s", commandName, fallback.getClass().getSimpleName());
      return fallback;
   }

   @VisibleForTesting
   final class InvokeAndTransform implements Callable<Object> {
      private final String commandName;
      private final HttpCommand command;
      private final Function<HttpResponse, ?> transformer;

      InvokeAndTransform(String commandName, HttpCommand command) {
         this.commandName = commandName;
         this.command = command;
         this.transformer = getTransformer(commandName, command);
      }

      @Override
      public Object call() throws Exception {
         return transformer.apply(http.invoke(command));
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(commandName, command, transformer);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         InvokeAndTransform that = InvokeAndTransform.class.cast(obj);
         return equal(this.commandName, that.commandName) && equal(this.command, that.command)
               && equal(this.transformer, that.transformer);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("commandName", commandName).add("command", command)
               .add("transformer", transformer).toString();
      }
   }

   private HttpCommand toCommand(String commandName, Invocation invocation) {
      logger.trace(">> converting %s", commandName);
      HttpRequest request = annotationProcessor.apply(invocation);
      logger.trace("<< converted %s to %s", commandName, request.getRequestLine());
      return new HttpCommand(request);
   }

   private Function<HttpResponse, ?> getTransformer(String commandName, HttpCommand command) {
      HttpRequest request = command.getCurrentRequest();
      Function<HttpResponse, ?> transformer = transformerForRequest.apply(request);
      logger.trace("<< response from %s is parsed by %s", commandName, transformer.getClass().getSimpleName());
      return transformer;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InvokeHttpMethod that = InvokeHttpMethod.class.cast(o);
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
