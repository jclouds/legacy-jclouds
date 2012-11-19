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
package org.jclouds.providers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rest.AnonymousRestApiMetadata;

/**
 * Useful in creating arbitrary clients.
 * 
 * @author Adrian Cole
 */
public class AnonymousProviderMetadata extends BaseProviderMetadata {

   public static ProviderMetadata forClientMappedToAsyncClientOnEndpoint(Class<?> client, Class<?> asyncClient,
            String endpoint) {
      return forApiWithEndpoint(AnonymousRestApiMetadata.forClientMappedToAsyncClient(client, asyncClient), endpoint);
   }

   public static ProviderMetadata forApiWithEndpoint(ApiMetadata md, String endpoint) {
      checkNotNull(md, "api");
      checkNotNull(endpoint, "endpoint (%s)", md.getEndpointName());
      return new AnonymousProviderMetadata(md, endpoint);
   }

   @Override
   public Builder toBuilder() {
      return (Builder) new Builder(getApiMetadata(), getEndpoint()).fromProviderMetadata(this);
   }

   public AnonymousProviderMetadata(ApiMetadata apiMetadata, String endpoint) {
      super(new Builder(apiMetadata, endpoint));
   }

   public AnonymousProviderMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      public Builder(ApiMetadata apiMetadata, String endpoint) {
         id(checkNotNull(apiMetadata, "apiMetadata").getId())
         .name(apiMetadata.getName())
         .apiMetadata(apiMetadata)
         .endpoint(endpoint);
      }

      @Override
      public AnonymousProviderMetadata build() {
         return new AnonymousProviderMetadata(this);
      }

   }

}
