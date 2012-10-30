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
import static org.jclouds.cloudloadbalancers.reference.Region.LON;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud LoadBalancers UK.
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersUKProviderMetadata extends BaseProviderMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 5566019798179959163L;
   
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
      properties.setProperty(PROPERTY_REGIONS, LON);
      properties.setProperty(PROPERTY_ENDPOINT, "https://lon.auth.api.rackspacecloud.com");
      properties.setProperty(PROPERTY_ISO3166_CODES, "GB-SLG");
      properties.setProperty(PROPERTY_REGION + "." + LON + "." + ISO3166_CODES, "GB-SLG");
      properties.setProperty(PROPERTY_REGION + "." + LON + "." + ENDPOINT,
            String.format("https://lon.loadbalancers.api.rackspacecloud.com/v${%s}", PROPERTY_API_VERSION));
      return properties;
   }
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("cloudloadbalancers-uk")
         .name("Rackspace Cloud Load Balancers UK")
         .apiMetadata(new CloudLoadBalancersApiMetadata())
         .homepage(URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-load-balancers"))
         .console(URI.create("https://lon.manage.rackspacecloud.com"))
         .linkedServices("cloudloadbalancers-uk", "cloudservers-uk", "cloudfiles-uk")
         .iso3166Codes("GB-SLG")
         .endpoint("https://lon.auth.api.rackspacecloud.com");
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
