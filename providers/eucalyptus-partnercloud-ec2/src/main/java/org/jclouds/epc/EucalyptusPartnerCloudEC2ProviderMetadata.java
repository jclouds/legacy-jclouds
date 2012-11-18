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
package org.jclouds.epc;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.ec2.compute.config.EC2ResolveImagesModule;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.epc.config.EucalyptusPartnerCloudComputeServiceContextModule;
import org.jclouds.eucalyptus.EucalyptusApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for Eucalyptus Partner Cloud EC2.
 * 
 * @author Adrian Cole
 */
public class EucalyptusPartnerCloudEC2ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public EucalyptusPartnerCloudEC2ProviderMetadata() {
      super(builder());
   }

   public EucalyptusPartnerCloudEC2ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "eucalyptus");
      properties.setProperty(PROPERTY_REGION + ".eucalyptus." + ISO3166_CODES, "US-CA");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("eucalyptus-partnercloud-ec2")
         .name("Eucalyptus Partner Cloud (EC2)")
               .apiMetadata(
                     new EucalyptusApiMetadata().toBuilder()
                            .defaultModules(ImmutableSet.<Class<? extends Module>>of(EC2RestClientModule.class, EC2ResolveImagesModule.class, EucalyptusPartnerCloudComputeServiceContextModule.class))
                            .build())
         .homepage(URI.create("http://www.eucalyptus.com/partners"))
         .console(URI.create("https://eucalyptus.partner.eucalyptus.com"))
         .linkedServices("eucalyptus-partnercloud-ec2", "eucalyptus-partnercloud-s3")
         .iso3166Codes("US-CA")
         .endpoint("http://partnercloud.eucalyptus.com:8773/services/Eucalyptus")
         .defaultProperties(EucalyptusPartnerCloudEC2ProviderMetadata.defaultProperties());
      }

      @Override
      public EucalyptusPartnerCloudEC2ProviderMetadata build() {
         return new EucalyptusPartnerCloudEC2ProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
