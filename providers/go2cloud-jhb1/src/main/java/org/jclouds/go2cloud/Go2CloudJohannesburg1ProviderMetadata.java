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
package org.jclouds.go2cloud;

import java.net.URI;
import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Go2Cloud's Johannesburg1
 * provider.
 * 
 * @author Adrian Cole
 */
public class Go2CloudJohannesburg1ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public Go2CloudJohannesburg1ProviderMetadata() {
      super(builder());
   }

   public Go2CloudJohannesburg1ProviderMetadata(Builder builder) {
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
         id("go2cloud-jhb1")
         .name("Go2Cloud Johannesburg1")
         .apiMetadata(new ElasticStackApiMetadata().toBuilder().version("2.0").build())
         .homepage(URI.create("https://jhb1.go2cloud.co.za"))
         .console(URI.create("https://jhb1.go2cloud.co.za/accounts"))
         .iso3166Codes("ZA-GP")
         .endpoint("https://api.jhb1.go2cloud.co.za")
         .defaultProperties(Go2CloudJohannesburg1ProviderMetadata.defaultProperties());
      }

      @Override
      public Go2CloudJohannesburg1ProviderMetadata build() {
         return new Go2CloudJohannesburg1ProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
