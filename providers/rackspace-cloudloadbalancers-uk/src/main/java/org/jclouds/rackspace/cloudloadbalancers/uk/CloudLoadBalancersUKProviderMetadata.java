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
package org.jclouds.rackspace.cloudloadbalancers.uk;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud LoadBalancers UK.
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersUKProviderMetadata extends BaseProviderMetadata {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudLoadBalancersUKProviderMetadata() {
      super(builder());
   }

   public CloudLoadBalancersUKProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ZONES, "LON");
      properties.setProperty(PROPERTY_ZONE + ".LON." + ISO3166_CODES, "GB-SLG");
      return properties;
   }
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudloadbalancers-uk")
         .name("Rackspace Cloud Load Balancers UK")
         .apiMetadata(new CloudLoadBalancersApiMetadata().toBuilder()
                  .defaultEndpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
                  .build())
         .homepage(URI.create("http://www.rackspace.co.uk/cloud-load-balancers/"))
         .console(URI.create("https://mycloud.rackspace.co.uk"))
         .linkedServices("rackspace-cloudservers-uk", "cloudfiles-uk", "rackspace-cloudblockstorage-uk")
         .iso3166Codes("GB-SLG")
         .endpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudLoadBalancersApiMetadata.defaultProperties());
      }

      @Override
      public CloudLoadBalancersUKProviderMetadata build() {
         return new CloudLoadBalancersUKProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
