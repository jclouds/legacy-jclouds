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
package org.jclouds.openhosting;

import java.net.URI;
import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for OpenHosting East1.
 * 
 * @author Adrian Cole
 */
public class OpenHostingEast1ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public OpenHostingEast1ProviderMetadata() {
      super(builder());
   }

   public OpenHostingEast1ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }

   public static class Builder
         extends
         BaseProviderMetadata.Builder {

      protected Builder() {
         id("openhosting-east1")
         .name("OpenHosting East1")
         .apiMetadata(new ElasticStackApiMetadata())
         .homepage(URI.create("https://east1.openhosting.com"))
         .console(URI.create("https://east1.openhosting.com/accounts"))
         .iso3166Codes("US-VA")
         .endpoint("https://api.east1.openhosting.com")
         .defaultProperties(OpenHostingEast1ProviderMetadata.defaultProperties());
      }

      @Override
      public OpenHostingEast1ProviderMetadata build() {
         return new OpenHostingEast1ProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
