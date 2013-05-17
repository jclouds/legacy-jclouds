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
package org.jclouds.ultradns.ws;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.ultradns.ws.config.UltraDNSWSHttpApiModule;

/**
 * Implementation of {@link ApiMetadata} for Neustar's UltraDNSWS api.
 * 
 * @author Adrian Cole
 */
public class UltraDNSWSApiMetadata extends BaseHttpApiMetadata<UltraDNSWSApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public UltraDNSWSApiMetadata() {
      this(new Builder());
   }

   protected UltraDNSWSApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<UltraDNSWSApi, Builder> {

      protected Builder() {
         id("ultradns-ws")
         .name("Neustar UltraDNS WS Api")
         .identityName("Username")
         .credentialName("Password")
         .version("v01")
         .documentation(URI.create("https://portal.ultradns.com/static/docs/NUS_API_XML_SOAP.pdf"))
         .defaultEndpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .defaultProperties(UltraDNSWSApiMetadata.defaultProperties())
         .defaultModule(UltraDNSWSHttpApiModule.class);
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
