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
package org.jclouds.rest;

import java.net.URI;

import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.annotations.Beta;

/**
 * Useful in creating arbitrary http apis.
 * 
 * @author Adrian Cole
 */
@Beta
public class AnonymousHttpApiMetadata<A> extends BaseHttpApiMetadata<A> {

   public static <A> AnonymousHttpApiMetadata<A> forApi(Class<A> httpApi) {
      return new Builder<A>(httpApi).build();
   }

   @Override
   public Builder<A> toBuilder() {
      return new Builder<A>(getApi()).fromApiMetadata(this);
   }

   private AnonymousHttpApiMetadata(Builder<A> builder) {
      super(builder);
   }

   private static final class Builder<A> extends BaseHttpApiMetadata.Builder<A, Builder<A>> {

      private Builder(Class<A> api) {
         super(api);
         id(api.getSimpleName())
         .identityName("unused")
         .defaultIdentity("foo")
         .version("1")
         .documentation(URI.create("http://jclouds.org/documentation"));
      }

      @Override
      public AnonymousHttpApiMetadata<A> build() {
         return new AnonymousHttpApiMetadata<A>(this);
      }

      @Override
      protected Builder<A> self() {
         return this;
      }
   }
}
