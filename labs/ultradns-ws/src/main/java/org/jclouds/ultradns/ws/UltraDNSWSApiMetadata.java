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
package org.jclouds.ultradns.ws;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.ultradns.ws.config.UltraDNSWSRestClientModule;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Neustar's UltraDNSWS api.
 * 
 * @author Adrian Cole
 */
public class UltraDNSWSApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<UltraDNSWSApi, UltraDNSWSAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<UltraDNSWSApi, UltraDNSWSAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public UltraDNSWSApiMetadata() {
      this(new Builder(UltraDNSWSApi.class, UltraDNSWSAsyncApi.class));
   }

   protected UltraDNSWSApiMetadata(Builder builder) {
      super(Builder.class.cast(builder));
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
         id("ultradns-ws")
         .name("Neustar UltraDNS WS Api")
         .identityName("Username")
         .credentialName("Password")
         .version("v01")
         .documentation(URI.create("https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf"))
         .defaultEndpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .defaultProperties(UltraDNSWSApiMetadata.defaultProperties())
         .defaultModule(UltraDNSWSRestClientModule.class);
      }

      @Override
      public UltraDNSWSApiMetadata build() {
         return new UltraDNSWSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
