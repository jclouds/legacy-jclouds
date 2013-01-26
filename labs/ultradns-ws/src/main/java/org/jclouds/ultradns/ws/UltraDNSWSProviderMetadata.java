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

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Neustar
 * UltraDNS.
 * 
 * @author Adrian Cole
 */
public class UltraDNSWSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public UltraDNSWSProviderMetadata() {
      super(builder());
   }

   public UltraDNSWSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("ultradns-ws")
         .name("Neustar UltraDNS WS")
         .apiMetadata(new UltraDNSWSApiMetadata())
         .homepage(URI.create("http://www.neustar.biz/enterprise/dns-services/what-is-external-dns"))
         .console(URI.create("https://www.ultradns.net"))
         .iso3166Codes("US-CA", "US-VA") // TODO
         .endpoint("http://ultra-api.ultradns.com:8008/UltraDNS_WS/v01")
         .defaultProperties(UltraDNSWSProviderMetadata.defaultProperties());
      }

      @Override
      public UltraDNSWSProviderMetadata build() {
         return new UltraDNSWSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
