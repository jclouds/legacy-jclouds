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
package org.jclouds.bluelock.vcloud.zone01;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.vcloud.VCloudApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Bluelock vCloud Zone 1.
 * 
 * @author Adrian Cole
 */
public class BluelockVCloudZone01ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public BluelockVCloudZone01ProviderMetadata() {
      super(builder());
   }

   public BluelockVCloudZone01ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_NETWORK, "internet01-.*");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("bluelock-vcloud-zone01")
         .name("Bluelock vCloud Zone 1")
               .apiMetadata(
                     new VCloudApiMetadata().toBuilder().buildVersion("1.5.0.464915").build())
         .homepage(URI.create("http://www.bluelock.com/bluelock-cloud-hosting"))
         .console(URI.create("https://zone01.bluelock.com/cloud/org/YOUR_ORG_HERE"))
         .iso3166Codes("US-IN")
         .endpoint("https://zone01.bluelock.com/api")
         .defaultProperties(BluelockVCloudZone01ProviderMetadata.defaultProperties());
      }

      @Override
      public BluelockVCloudZone01ProviderMetadata build() {
         return new BluelockVCloudZone01ProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
