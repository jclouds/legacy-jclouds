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
package org.jclouds.trmk.ecloud;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Terremark's Enterprise Cloud.
 * 
 * @author Adrian Cole
 */
public class TerremarkECloudProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public TerremarkECloudProviderMetadata() {
      super(builder());
   }

   public TerremarkECloudProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
            id("trmk-ecloud")
            .name("Terremark Enterprise Cloud v2.8")
            .apiMetadata(new TerremarkECloudApiMetadata())
            .endpoint("https://services.enterprisecloud.terremark.com/api")
            .homepage(URI.create("http://www.terremark.com/services/cloudcomputing/theenterprisecloud.aspx"))
            .console(URI.create("https://icenter.digitalops.net"))
            .iso3166Codes("US-FL", "US-VA", "NL-NH", "BR-SP");
      }

      @Override
      public TerremarkECloudProviderMetadata build() {
         return new TerremarkECloudProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
