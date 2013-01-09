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

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.reflect.Invocation;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author adriancole
 * 
 * @param <A>
 *           enclosing type of the interface parsed to generate this request.
 */
public final class GeneratedHttpRequest<A> extends HttpRequest {
   public static <A> Builder<A> builder(Class<A> enclosingType) {
      return new Builder<A>(TypeToken.of(enclosingType));
   }

   public static <A> Builder<A> builder(TypeToken<A> enclosingType) {
      return new Builder<A>(enclosingType);
   }

   public Builder<A> toBuilder() {
      return new Builder<A>(enclosingType).fromGeneratedHttpRequest(this);
   }

   public final static class Builder<A> extends HttpRequest.Builder<Builder<A>> {
      private final TypeToken<A> enclosingType;

      private Builder(TypeToken<A> enclosingType) {
         this.enclosingType = checkNotNull(enclosingType, "enclosingType");
      }

      private Invocation invocation;
      private Optional<TypeToken<?>> callerEnclosingType = Optional.absent();
      private Optional<Invocation> caller = Optional.absent();

      /**
       * @see GeneratedHttpRequest#getInvocation()
       */
      public Builder<A> invocation(Invocation invocation) {
         this.invocation = checkNotNull(invocation, "invocation");
         return this;
      }

      /**
       * @see GeneratedHttpRequest#getCallerEnclosingType()
       */
      public Builder<A> callerEnclosingType(@Nullable TypeToken<?> callerEnclosingType) {
         this.callerEnclosingType = Optional.<TypeToken<?>> fromNullable(callerEnclosingType);
         return this;
      }

      /**
       * @see GeneratedHttpRequest#getCaller()
       */
      public Builder<A> caller(@Nullable Invocation caller) {
         this.caller = Optional.fromNullable(caller);
         return this;
      }

      public GeneratedHttpRequest<A> build() {
         return new GeneratedHttpRequest<A>(method, endpoint, headers.build(), payload, filters.build(), enclosingType,
               invocation, callerEnclosingType, caller);
      }

      public Builder<A> fromGeneratedHttpRequest(GeneratedHttpRequest<A> in) {
         return super.fromHttpRequest(in).invocation(in.invocation)
               .callerEnclosingType(in.getCallerEnclosingType().orNull()).caller(in.getCaller().orNull());
      }

      @Override
      protected Builder<A> self() {
         return this;
      }
   }

   private final TypeToken<A> enclosingType;
   private final Invocation invocation;
   private final Optional<TypeToken<?>> callerEnclosingType;
   private final Optional<Invocation> caller;

   protected GeneratedHttpRequest(String method, URI endpoint, Multimap<String, String> headers,
         @Nullable Payload payload, Iterable<HttpRequestFilter> filters, TypeToken<A> enclosingType,
         Invocation invocation, Optional<TypeToken<?>> callerEnclosingType, Optional<Invocation> caller) {
      super(method, endpoint, headers, payload, filters);
      this.enclosingType = checkNotNull(enclosingType, "enclosingType");
      this.invocation = checkNotNull(invocation, "invocation");
      this.callerEnclosingType = checkNotNull(callerEnclosingType, "callerEnclosingType");
      this.caller = checkNotNull(caller, "caller");
   }

   /**
    * different than {@link #getDeclaringClass()} when this is a member of a class it was not declared in.
    */
   public TypeToken<?> getEnclosingType() {
      return enclosingType;
   }

   /**
    * what was interpreted to create this request
    */
   public Invocation getInvocation() {
      return invocation;
   }

   /**
    * different than {@link #getDeclaringClass()} when {@link #getCaller()} is a member of a class it was not declared
    * in.
    */
   public Optional<TypeToken<?>> getCallerEnclosingType() {
      return callerEnclosingType;
   }

   public Optional<Invocation> getCaller() {
      return caller;
   }
}
