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
package org.jclouds.rackspace.clouddns.uk;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace DNS UK.
 * 
 * @author Everett Toews
 */
public class CloudDNSUKProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudDNSUKProviderMetadata() {
      super(builder());
   }

   public CloudDNSUKProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-clouddns-uk")
         .name("Rackspace Cloud DNS UK")
         .apiMetadata(new CloudDNSApiMetadata().toBuilder()
                  .defaultEndpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/public/dns/"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudidentity", "rackspace-cloudservers-uk", "cloudfiles-uk", "rackspace-cloudblockstorage-uk", "rackspace-cloudloadbalancers-uk")
         .iso3166Codes("GB-SLG")
         .endpoint("https://lon.identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudDNSApiMetadata.defaultProperties());
      }

      @Override
      public CloudDNSUKProviderMetadata build() {
         return new CloudDNSUKProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
