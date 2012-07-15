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
package org.jclouds.carrenza.vcloud.director;

import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.PROPERTY_VCLOUD_DIRECTOR_DEFAULT_NETWORK;

import java.net.URI;
import java.util.Properties;

import org.jclouds.carrenza.vcloud.director.config.CarrenzaVCloudDirectorComputeServiceContextModule;
import org.jclouds.carrenza.vcloud.director.config.CarrenzaVCloudDirectorRestClientModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Carrenza vCloud hosting
 * 
 * @author Adrian Cole
 * @author grkvlt@apache.org
 */
@SuppressWarnings("serial")
public class CarrenzaVCloudDirectorProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CarrenzaVCloudDirectorProviderMetadata() {
      super(builder());
   }

   public CarrenzaVCloudDirectorProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_BUILD_VERSION, "1.5.0.464915");
      properties.setProperty(PROPERTY_VCLOUD_DIRECTOR_DEFAULT_NETWORK, "orgNet-.*-External");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("carrenza-vcloud-director")
         .name("Carrenza vCloud Director")
         .apiMetadata(new VCloudDirectorApiMetadata().toBuilder()
                     .buildVersion("1.5.0.464915")
                     .defaultModules(ImmutableSet.<Class<? extends Module>>of(CarrenzaVCloudDirectorRestClientModule.class, CarrenzaVCloudDirectorComputeServiceContextModule.class))
                     .build())
         .homepage(URI.create("http://carrenza.com/"))
         .console(URI.create("https://myvdc.carrenza.net/cloud/org/YOUR_ORG_HERE"))
         .iso3166Codes("GB-LND")
         .endpoint("https://myvdc.carrenza.net/api")
         .defaultProperties(CarrenzaVCloudDirectorProviderMetadata.defaultProperties());
      }

      @Override
      public CarrenzaVCloudDirectorProviderMetadata build() {
         return new CarrenzaVCloudDirectorProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
