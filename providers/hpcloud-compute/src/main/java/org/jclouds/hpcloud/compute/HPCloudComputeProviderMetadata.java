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
package org.jclouds.hpcloud.compute;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.REQUIRES_TENANT;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.hpcloud.compute.config.HPCloudComputeServiceContextModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.MappedAuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.config.NovaRestClientModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for HP Cloud Compute Services.
 * 
 * @author Adrian Cole
 */
public class HPCloudComputeProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public HPCloudComputeProviderMetadata() {
      super(builder());
   }

   public HPCloudComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = NovaApiMetadata.defaultProperties();
      // deallocating ip addresses can take a while
      properties.setProperty(TIMEOUT_NODE_TERMINATED, 60 * 1000 + "");

      properties.setProperty(REQUIRES_TENANT, "true");
      properties.setProperty(AUTO_ALLOCATE_FLOATING_IPS, "true");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "true");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,locationId=az-2.region-a.geo-1");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("hpcloud-compute")
         .name("HP Cloud Compute Services")
         .apiMetadata(new NovaApiMetadata().toBuilder()
                  .endpointName("identity service url ending in /v2.0/")
                  .defaultEndpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(MappedAuthenticationApiModule.class)
                                              .add(KeystoneAuthenticationModule.class)
                                              .add(ZoneModule.class)
                                              .add(NovaParserModule.class)
                                              .add(NovaRestClientModule.class)
                                              .add(HPCloudComputeServiceContextModule.class).build())
                  .build())
         .homepage(URI.create("http://hpcloud.com"))
         .console(URI.create("https://manage.hpcloud.com/compute"))
         .linkedServices("hpcloud-compute", "hpcloud-objectstorage")
         .iso3166Codes("US-NV")
         .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
         .defaultProperties(HPCloudComputeProviderMetadata.defaultProperties());
      }

      @Override
      public HPCloudComputeProviderMetadata build() {
         return new HPCloudComputeProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
