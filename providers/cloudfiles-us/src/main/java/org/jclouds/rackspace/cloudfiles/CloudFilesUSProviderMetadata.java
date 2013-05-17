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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.cloudfiles.CloudFilesApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud Files US.
 * 
 * @author Adrian Cole
 */
public class CloudFilesUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudFilesUSProviderMetadata() {
      super(builder());
   }

   public CloudFilesUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "US");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("cloudfiles-us")
         .name("Rackspace Cloud Files US")
         .apiMetadata(new CloudFilesApiMetadata())
         .endpoint("https://auth.api.rackspacecloud.com")
         .homepage(URI.create("http://www.rackspace.com/cloud/cloud_hosting_products/files"))
         .console(URI.create("https://manage.rackspacecloud.com"))
         .linkedServices("cloudfiles-us", "cloudservers-us", "cloudloadbalancers-us")
         .iso3166Codes("US-IL","US-TX");
      }

      @Override
      public CloudFilesUSProviderMetadata build() {
         return new CloudFilesUSProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
