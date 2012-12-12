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
package org.jclouds.rackspace.cloudloadbalancers.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApiMetadata;
import org.jclouds.rackspace.cloudloadbalancers.config.CloudLoadBalancersRestClientModule;
import org.jclouds.rackspace.cloudloadbalancers.loadbalancer.config.CloudLoadBalancersLoadBalancerContextModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud LoadBalancers US.
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudLoadBalancersUSProviderMetadata() {
      super(builder());
   }

   public CloudLoadBalancersUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ZONES, "ORD,DFW");
      properties.setProperty(PROPERTY_ZONE + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_ZONE + ".DFW." + ISO3166_CODES, "US-TX");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudloadbalancers-us")
         .name("Rackspace Cloud Load Balancers US")
         .apiMetadata(new CloudLoadBalancersApiMetadata().toBuilder()
                  .identityName("${userName}")
                  .credentialName("${apiKey}")
                  .version("1.0")
                  .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
                  .endpointName("Identity service URL ending in /v2.0/")
                  .documentation(URI.create("http://docs.rackspace.com/loadbalancers/api/clb-devguide-latest/index.html"))
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(CloudIdentityAuthenticationModule.class)
                                              .add(ZoneModule.class)
                                              .add(CloudLoadBalancersRestClientModule.class)
                                              .add(CloudLoadBalancersLoadBalancerContextModule.class)
                                              .build())
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/public/loadbalancers/"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudservers-us", "cloudfiles-us", "rackspace-cloudblockstorage-us")
         .iso3166Codes("US-IL", "US-TX")
         .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudLoadBalancersApiMetadata.defaultProperties());
      }

      @Override
      public CloudLoadBalancersUSProviderMetadata build() {
         return new CloudLoadBalancersUSProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
