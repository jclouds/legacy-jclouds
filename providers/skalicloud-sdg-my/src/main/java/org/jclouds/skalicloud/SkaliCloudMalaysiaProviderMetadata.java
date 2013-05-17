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
package org.jclouds.skalicloud;

import java.net.URI;
import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for SkaliCloud Malaysia.
 * 
 * @author Adrian Cole
 */
public class SkaliCloudMalaysiaProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public SkaliCloudMalaysiaProviderMetadata() {
      super(builder());
   }

   public SkaliCloudMalaysiaProviderMetadata(Builder builder) {
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
         id("skalicloud-sdg-my")
         .name("SkaliCloud Malaysia")
         .apiMetadata(new ElasticStackApiMetadata())
         .homepage(URI.create("https://sdg-my.skalicloud.com"))
         .console(URI.create("https://api.sdg-my.skalicloud.com/accounts"))
         .iso3166Codes("MY-10")
         .endpoint("https://api.sdg-my.skalicloud.com")
         .defaultProperties(SkaliCloudMalaysiaProviderMetadata.defaultProperties());
      }

      @Override
      public SkaliCloudMalaysiaProviderMetadata build() {
         return new SkaliCloudMalaysiaProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
