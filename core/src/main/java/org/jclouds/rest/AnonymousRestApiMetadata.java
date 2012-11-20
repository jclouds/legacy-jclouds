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

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.annotations.Beta;

/**
 * Useful in creating arbitrary clients.
 * 
 * @author Adrian Cole
 */
@Beta
public class AnonymousRestApiMetadata extends BaseRestApiMetadata {

   public static AnonymousRestApiMetadata forClientMappedToAsyncClient(Class<?> client, Class<?> asyncClient) {
      return new AnonymousRestApiMetadata(client, asyncClient);
   }

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public AnonymousRestApiMetadata(Class<?> client, Class<?> asyncClient) {
      super(new Builder(client, asyncClient));
   }

   protected AnonymousRestApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      public Builder(Class<?> client, Class<?> asyncClient) {
         super(client, asyncClient);
         id(client.getSimpleName())
         .identityName("unused")
         .defaultIdentity("foo")
         .version("1")
         .documentation(URI.create("http://jclouds.org/documentation"));
      }

      @Override
      public AnonymousRestApiMetadata build() {
         return new AnonymousRestApiMetadata(this);
      }
      
      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
