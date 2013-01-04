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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.internal.ClassInvokerArgs;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.reflect.Invokable;

/**
 * Represents a request generated from annotations
 * 
 * @author Adrian Cole
 */
// TODO: get rid of all the mock tests so that this can be made final
public class GeneratedHttpRequest extends HttpRequest {
   public static Builder builder() { 
      return new Builder();
   }
   
   public Builder toBuilder() { 
      return new Builder().fromGeneratedHttpRequest(this);
   }

   public static class Builder extends HttpRequest.Builder<Builder>  {
      protected Class<?> declaring;
      protected Method javaMethod;
      protected Invokable<?, ?> invoker;
      // args can be null, so cannot use immutable list
      protected List<Object> args = Lists.newArrayList();
      protected Optional<ClassInvokerArgs> caller = Optional.absent();
      
      /** 
       * @see GeneratedHttpRequest#getDeclaring()
       */
      public Builder declaring(Class<?> declaring) {
         this.declaring = checkNotNull(declaring, "declaring");
         return this;
      }

      /** 
       * @see GeneratedHttpRequest#getJavaMethod()
       */
      @Deprecated
      public Builder javaMethod(Method javaMethod) {
         this.javaMethod = checkNotNull(javaMethod, "javaMethod");
         this.invoker(Invokable.from(javaMethod));
         return this;
      }
      
      /**
       * @see GeneratedHttpRequest#getInvoker()
       */
      public Builder invoker(Invokable<?, ?> invoker) {
         this.invoker = checkNotNull(invoker, "invoker");
         return this;
      }

      /** 
       * @see GeneratedHttpRequest#getArgs()
       */
      public Builder args(Iterable<Object> args) {
         this.args = Lists.newArrayList(checkNotNull(args, "args"));
         return this;
      }
      
      /** 
       * @see GeneratedHttpRequest#getArgs()
       */
      public Builder args(@Nullable Object[] args) {
         return args(Arrays.asList(args != null ? args : new Object[] {}));
      }

      /** 
       * @see GeneratedHttpRequest#getArgs()
       */
      public Builder arg(@Nullable Object arg) {
         this.args.add(arg);
         return this;
      }
      
      /** 
       * @see GeneratedHttpRequest#getCaller()
       */
      public Builder caller(@Nullable ClassInvokerArgs caller) {
         this.caller = Optional.fromNullable(caller);
         return this;
      }
      
      public GeneratedHttpRequest build() {
         return new GeneratedHttpRequest(method, endpoint, headers.build(), payload, declaring, javaMethod, invoker,
               args, filters.build(), caller);
      }

      public Builder fromGeneratedHttpRequest(GeneratedHttpRequest in) {
         return super.fromHttpRequest(in)
                     .declaring(in.getDeclaring())
                     .javaMethod(in.getJavaMethod())
                     .invoker(in.invoker)
                     .args(in.getArgs())
                     .caller(in.getCaller().orNull());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
   
   private final Class<?> declaring;
   private final Method javaMethod;
   private final Invokable<?, ?> invoker;
   private final List<Object> args;
   private final Optional<ClassInvokerArgs> caller;

   protected GeneratedHttpRequest(String method, URI endpoint, Multimap<String, String> headers,
         @Nullable Payload payload, Class<?> declaring, Method javaMethod, Invokable<?, ?> invoker,
         Iterable<Object> args, Iterable<HttpRequestFilter> filters, Optional<ClassInvokerArgs> caller) {
      super(method, endpoint, headers, payload, filters);
      this.declaring = checkNotNull(declaring, "declaring");
      this.javaMethod = checkNotNull(javaMethod, "javaMethod");
      this.invoker = checkNotNull(invoker, "invoker");
      // TODO make immutable. ImmutableList.of() doesn't accept nulls
      this.args = Lists.newArrayList(checkNotNull(args, "args"));
      this.caller = checkNotNull(caller, "caller");
   }

   public Class<?> getDeclaring() {
      return declaring;
   }

   /**
    * @deprecated see {@link #getInvoker()}
    */
   @Deprecated
   public Method getJavaMethod() {
      return javaMethod;
   }

   public Invokable<?,?> getInvoker() {
      return invoker;
   }
   
   public List<Object> getArgs() {
      return Collections.unmodifiableList(args);
   }

   public Optional<ClassInvokerArgs> getCaller() {
      return caller;
   }
}
