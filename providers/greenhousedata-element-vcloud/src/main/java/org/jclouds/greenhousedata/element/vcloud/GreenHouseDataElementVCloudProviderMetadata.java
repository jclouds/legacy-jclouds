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
package org.jclouds.greenhousedata.element.vcloud;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.net.URI;
import java.util.Properties;

import org.jclouds.greenhousedata.element.vcloud.config.GreenHouseDataElementVCloudComputeServiceContextModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.config.VCloudRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Green House Data Element vCloud
 * 
 * @author Adrian Cole
 */
public class GreenHouseDataElementVCloudProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public GreenHouseDataElementVCloudProviderMetadata() {
      super(builder());
   }

   public GreenHouseDataElementVCloudProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_NETWORK, "orgNet-.*-External");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("greenhousedata-element-vcloud")
         .name("Green House Data Element vCloud")
               .apiMetadata(
                     new VCloudApiMetadata().toBuilder()
                     .buildVersion("1.5.0.464915")
                     .defaultModules(ImmutableSet.<Class<? extends Module>>of(VCloudRestClientModule.class, GreenHouseDataElementVCloudComputeServiceContextModule.class))
                     .build())
         .homepage(URI.create("http://www.greenhousedata.com/element-cloud-hosting/vcloud-services/"))
         .console(URI.create("https://mycloud.greenhousedata.com/cloud/org/YOUR_ORG_HERE"))
         .iso3166Codes("US-WY")
         .endpoint("https://mycloud.greenhousedata.com/api")
         .defaultProperties(GreenHouseDataElementVCloudProviderMetadata.defaultProperties());
      }

      @Override
      public GreenHouseDataElementVCloudProviderMetadata build() {
         return new GreenHouseDataElementVCloudProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
