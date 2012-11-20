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
package org.jclouds.rackspace.cloudloadbalancers;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.cloudloadbalancers.reference.Region.DFW;
import static org.jclouds.cloudloadbalancers.reference.Region.ORD;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.common.base.Joiner;

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
   public static final String[] REGIONS = {ORD, DFW};

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ENDPOINT, "https://auth.api.rackspacecloud.com");
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(REGIONS));
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-IL,US-TX");
      
      properties.setProperty(PROPERTY_REGION + "." + ORD + "." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_REGION + "." + ORD + "." + ENDPOINT, String
               .format("https://ord.loadbalancers.api.rackspacecloud.com/v${%s}", PROPERTY_API_VERSION));
      
      properties.setProperty(PROPERTY_REGION + "." + DFW + "." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_REGION + "." + DFW + "." + ENDPOINT, String
               .format("https://dfw.loadbalancers.api.rackspacecloud.com/v${%s}", PROPERTY_API_VERSION));
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("cloudloadbalancers-us")
         .name("Rackspace Cloud Load Balancers US")
         .apiMetadata(new CloudLoadBalancersApiMetadata())
         .homepage(URI.create("http://www.rackspace.com/cloud/cloud_hosting_products/loadbalancers"))
         .console(URI.create("https://manage.rackspacecloud.com"))
         .linkedServices("cloudloadbalancers-us", "cloudservers-us", "cloudfiles-us")
         .iso3166Codes("US-IL","US-TX")
         .endpoint("https://auth.api.rackspacecloud.com");
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
