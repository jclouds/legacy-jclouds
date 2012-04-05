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
package org.jclouds.opsource.servers;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for  API
 * 
 * @author Adrian Cole
 */
public class OpSourceServersApiMetadata
      extends
      BaseRestApiMetadata<OpSourceServersClient, OpSourceServersAsyncClient, RestContext<OpSourceServersClient, OpSourceServersAsyncClient>, OpSourceServersApiMetadata> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public OpSourceServersApiMetadata() {
      this(new Builder());
   }

   protected OpSourceServersApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.Builder.defaultProperties();
      return properties;
   }

   public static class Builder
         extends
         BaseRestApiMetadata.Builder<OpSourceServersClient, OpSourceServersAsyncClient, RestContext<OpSourceServersClient, OpSourceServersAsyncClient>, OpSourceServersApiMetadata> {

      protected Builder() {
         super(OpSourceServersClient.class, OpSourceServersAsyncClient.class);
         id("opsource-servers")
         .name("OpSourceServers API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://www.opsource.net/Services/Cloud-Hosting/Open-API"))
         .version("0.9")
         .defaultEndpoint("https://api.opsourcecloud.net/oec/${jclouds.api-version}")
         .type(ApiType.COMPUTE)
         .contextBuilder(TypeToken.of(OpSourceServersContextBuilder.class));
      }

      @Override
      public OpSourceServersApiMetadata build() {
         return new OpSourceServersApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(OpSourceServersApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
