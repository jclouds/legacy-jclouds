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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Useful in creating arbitrary clients.
 * 
 * @author Adrian Cole
 */
public class AnonymousProviderMetadata<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> extends
      BaseProviderMetadata<S, A, C, M> {

   public static <S, A> ProviderMetadata<S, A, RestContext<S, A>, AnonymousRestApiMetadata<S, A>> forClientMappedToAsyncClientOnEndpoint(Class<S> client, Class<A> asyncClient,
         String endpoint) {
      return forApiWithEndpoint(AnonymousRestApiMetadata.forClientMappedToAsyncClient(client, asyncClient), endpoint);
   }
   
   public static <S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> ProviderMetadata<S, A, C, M> forApiWithEndpoint(M md,
         String endpoint) {
      checkNotNull(md, "api");
      checkNotNull(endpoint, "endpoint (%s)", md.getEndpointName());
      return new AnonymousProviderMetadata<S, A, C, M>(md, endpoint);
   }

   @Override
   public Builder<S, A, C, M> toBuilder() {
      return (Builder<S, A, C, M>) new Builder<S, A, C, M>(getApiMetadata(), getEndpoint()).fromProviderMetadata(this);
   }

   public AnonymousProviderMetadata(M apiMetadata, String endpoint) {
      super(new Builder<S, A, C, M>(apiMetadata, endpoint));
   }

   public AnonymousProviderMetadata(Builder<S, A, C, M> builder) {
      super(builder);
   }

   public static class Builder<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> extends
         BaseProviderMetadata.Builder<S, A, C, M> {

      public Builder(M apiMetadata, String endpoint) {
         id(checkNotNull(apiMetadata, "apiMetadata").getId())
         .name(apiMetadata.getName())
         .apiMetadata(apiMetadata)
         .endpoint(endpoint);
      }

      @Override
      public AnonymousProviderMetadata<S, A, C, M> build() {
         return new AnonymousProviderMetadata<S, A, C, M>(this);
      }

   }

}