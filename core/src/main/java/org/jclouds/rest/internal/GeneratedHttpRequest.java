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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.io.Payload;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.reflect.Invocation;

import com.google.common.base.Optional;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public final class GeneratedHttpRequest extends HttpRequest {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGeneratedHttpRequest(this);
   }

   public static final class Builder extends HttpRequest.Builder<Builder> {
      private Invocation invocation;
      private Optional<Invocation> caller = Optional.absent();

      /**
       * @see GeneratedHttpRequest#getInvocation()
       */
      public Builder invocation(Invocation invocation) {
         this.invocation = checkNotNull(invocation, "invocation");
         return this;
      }

      /**
       * @see GeneratedHttpRequest#getCaller()
       */
      public Builder caller(@Nullable Invocation caller) {
         this.caller = Optional.fromNullable(caller);
         return this;
      }

      public GeneratedHttpRequest build() {
         return new GeneratedHttpRequest(method, endpoint, headers.build(), payload, filters.build(), invocation,
               caller);
      }

      public Builder fromGeneratedHttpRequest(GeneratedHttpRequest in) {
         return super.fromHttpRequest(in).invocation(in.invocation).caller(in.getCaller().orNull());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   private final Invocation invocation;
   private final Optional<Invocation> caller;

   protected GeneratedHttpRequest(String method, URI endpoint, Multimap<String, String> headers,
         @Nullable Payload payload, Iterable<HttpRequestFilter> filters, Invocation invocation,
         Optional<Invocation> caller) {
      super(method, endpoint, headers, payload, filters);
      this.invocation = checkNotNull(invocation, "invocation");
      this.caller = checkNotNull(caller, "caller");
   }

   /**
    * what was interpreted to create this request
    */
   public Invocation getInvocation() {
      return invocation;
   }

   public Optional<Invocation> getCaller() {
      return caller;
   }
}
