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
package org.jclouds.rackspace.cloudservers;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudservers.CloudServersApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud Servers UK.
 * 
 * @author Adrian Cole
 */
public class CloudServersUKProviderMetadata extends BaseProviderMetadata {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudServersUKProviderMetadata() {
      super(builder());
   }

   public CloudServersUKProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "UK");
      return properties;
   }
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("cloudservers-uk")
         .name("Rackspace Cloud Servers UK")
         .apiMetadata(new CloudServersApiMetadata())
         .endpoint("https://lon.auth.api.rackspacecloud.com")
         .homepage(URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-servers"))
         .console(URI.create("https://lon.manage.rackspacecloud.com"))
         .linkedServices("cloudloadbalancers-uk", "cloudservers-uk", "cloudfiles-uk")
         .iso3166Codes("GB-SLG");
      }

      @Override
      public CloudServersUKProviderMetadata build() {
         return new CloudServersUKProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
