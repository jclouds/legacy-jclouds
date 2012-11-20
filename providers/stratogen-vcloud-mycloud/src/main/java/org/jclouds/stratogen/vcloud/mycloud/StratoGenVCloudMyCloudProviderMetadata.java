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
package org.jclouds.stratogen.vcloud.mycloud;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.stratogen.vcloud.mycloud.config.StratoGenVCloudMyCloudComputeServiceContextModule;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.config.VCloudRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for StratoGen VMware hosting
 * 
 * @author Adrian Cole
 */
public class StratoGenVCloudMyCloudProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public StratoGenVCloudMyCloudProviderMetadata() {
      super(builder());
   }

   public StratoGenVCloudMyCloudProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_NETWORK, "Direct Internet");
      properties.setProperty(TEMPLATE, "imageNameMatches=Ubuntu server 11.04 64bit no GUI (base)");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("stratogen-vcloud-mycloud")
         .name("StratoGen VMware hosting")
               .apiMetadata(
                     new VCloudApiMetadata().toBuilder()
                     .buildVersion("1.5.0.464915")
                     .defaultModules(ImmutableSet.<Class<? extends Module>>of(VCloudRestClientModule.class, StratoGenVCloudMyCloudComputeServiceContextModule.class))
                     .build())
         .homepage(URI.create("http://www.stratogen.net"))
         .console(URI.create("https://mycloud.stratogen.net/cloud/org/YOUR_ORG_HERE"))
         .iso3166Codes("GB")
         .endpoint("https://mycloud.greenhousedata.com/api")
         .defaultProperties(StratoGenVCloudMyCloudProviderMetadata.defaultProperties());
      }

      @Override
      public StratoGenVCloudMyCloudProviderMetadata build() {
         return new StratoGenVCloudMyCloudProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
