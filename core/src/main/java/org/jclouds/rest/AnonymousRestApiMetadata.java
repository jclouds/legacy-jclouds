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
package org.jclouds.rest;

import java.net.URI;

import org.jclouds.apis.ApiType;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.annotations.Beta;

/**
 * Useful in creating arbitrary clients.
 * 
 * @author Adrian Cole
 */
@Beta
public class AnonymousRestApiMetadata<S, A> extends BaseRestApiMetadata<S, A, RestContext<S, A>, AnonymousRestApiMetadata<S, A>> {

   public static <S, A> AnonymousRestApiMetadata<S, A> forClientMappedToAsyncClient(Class<S> client, Class<A> asyncClient) {
      return new AnonymousRestApiMetadata<S, A>(client, asyncClient);
   }

   @Override
   public Builder<S, A> toBuilder() {
      return new Builder<S, A>(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public AnonymousRestApiMetadata(Class<S> client, Class<A> asyncClient) {
      super(new Builder<S, A>(client, asyncClient));
   }

   protected AnonymousRestApiMetadata(Builder<S, A> builder) {
      super(builder);
   }

   public static class Builder<S, A> extends BaseRestApiMetadata.Builder<S, A,RestContext<S, A>, AnonymousRestApiMetadata<S, A>> {

      public Builder(Class<S> client, Class<A> asyncClient) {
         super(client, asyncClient);
         id(client.getSimpleName())
         .type(ApiType.UNRECOGNIZED)
         .name(String.format("%s->%s", client.getSimpleName(), asyncClient.getSimpleName()))
         .identityName("unused")
         .defaultIdentity("foo")
         .version("1")
         .documentation(URI.create("http://jclouds.org/documentation"));
      }

      @Override
      public AnonymousRestApiMetadata<S, A> build() {
         return new AnonymousRestApiMetadata<S, A>(this);
      }
      
      @Override
      public Builder<S, A> fromApiMetadata(AnonymousRestApiMetadata<S, A> in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
