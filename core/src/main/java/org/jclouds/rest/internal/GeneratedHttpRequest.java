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
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.io.Payload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Represents a request generated from annotations
 * 
 * @author Adrian Cole
 */
public class GeneratedHttpRequest<T> extends HttpRequest {
   public static Builder<?> builder() {
      // empty builder, so can be safely cast to Builder<T> by the caller
      return new Builder<Object>();
   }

   /*
    * Convenience method - cannot have the same signature as builder() - see
    * http://code.google.com/p/jclouds/issues/detail?id=795
    */
   @SuppressWarnings("unchecked")
   public static <T> Builder<T> requestBuilder() {
       return (Builder<T>) builder();
   }

   public static class Builder<T> extends HttpRequest.Builder {
      protected Class<T> declaring;
      protected Method javaMethod;
      protected List<Object> args;

      public Builder<T> declaring(Class<T> declaring) {
         this.declaring = checkNotNull(declaring, "declaring");
         return this;
      }

      public Builder<T> javaMethod(Method javaMethod) {
         this.javaMethod = checkNotNull(javaMethod, "javaMethod");
         return this;
      }

      public Builder<T> args(Object[] args) {
         // TODO make immutable. ImmutableList.of() doesn't accept nulls
         return args((args == null) ? ImmutableList.<Object> of() : Lists.newArrayList(args));
      }

      public Builder<T> args(List<Object> args) {
         this.args = checkNotNull(args, "args");
         return this;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> filters(List<HttpRequestFilter> requestFilters) {
         return (Builder<T>) super.filters(requestFilters);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> method(String method) {
         return (Builder<T>) super.method(method);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> endpoint(URI endpoint) {
         return (Builder<T>) super.endpoint(endpoint);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> skips(char[] skips) {
         return (Builder<T>) super.skips(skips);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> payload(Payload payload) {
         return (Builder<T>) super.payload(payload);
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> headers(Multimap<String, String> headers) {
         return (Builder<T>) super.headers(headers);
      }

      @Override
      public GeneratedHttpRequest<T> build() {
         return new GeneratedHttpRequest<T>(method, endpoint, skips, requestFilters, payload, headers, declaring,
               javaMethod, args);
      }

      public static Builder<?> from(HttpRequest input) {
         /*
          * State added to builder will not conflict with return type so caller can
          * safely cast result to Builder<T>
          */
         return new Builder<Object>().method(input.getMethod()).endpoint(input.getEndpoint()).skips(input.getSkips())
               .filters(input.getFilters()).payload(input.getPayload()).headers(input.getHeaders());
      }

      /*
       * Convenience method - cannot have the same signature as from(HttpRequest) - see
       * http://code.google.com/p/jclouds/issues/detail?id=795
       */
      @SuppressWarnings("unchecked")
      public static <Y> Builder<Y> fromRequest(HttpRequest input) {
          return (Builder<Y>) from(input);
      }

      public static <Y> Builder<Y> from(GeneratedHttpRequest<Y> input) {
         return new Builder<Y>().method(input.getMethod()).endpoint(input.getEndpoint()).skips(input.getSkips())
               .filters(input.getFilters()).payload(input.getPayload()).headers(input.getHeaders())
               .declaring(input.getDeclaring()).javaMethod(input.getJavaMethod()).args(input.getArgs());
      }

   }

   private final Class<T> declaring;
   private final Method javaMethod;
   private final List<Object> args;

   GeneratedHttpRequest(String method, URI endpoint, char[] skips, List<HttpRequestFilter> requestFilters,
         @Nullable Payload payload, Multimap<String, String> headers, Class<T> declaring, Method javaMethod,
         Iterable<Object> args) {
      super(method, endpoint, skips, requestFilters, payload, headers);
      this.declaring = checkNotNull(declaring, "declaring");
      this.javaMethod = checkNotNull(javaMethod, "javaMethod");
      // TODO make immutable. ImmutableList.of() doesn't accept nulls
      this.args = Lists.newArrayList(checkNotNull(args, "args"));
   }

   public Class<T> getDeclaring() {
      return declaring;
   }

   public Method getJavaMethod() {
      return javaMethod;
   }

   public List<Object> getArgs() {
      return args;
   }

   @Override
   public Builder<T> toBuilder() {
      return Builder.from(this);
   }

}
