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
package org.jclouds.hpcloud.objectstorage;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.REQUIRES_TENANT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for StratoGen VMware hosting
 * 
 * @author Adrian Cole
 */
public class HPCloudObjectStorageProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public HPCloudObjectStorageProviderMetadata() {
      super(builder());
   }

   public HPCloudObjectStorageProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(REQUIRES_TENANT, "true");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("hpcloud-objectstorage")
         .name("HP Cloud Services Object Storage")
         .apiMetadata(new HPCloudObjectStorageApiMetadata())
         .homepage(URI.create("http://hpcloud.com"))
         .console(URI.create("https://manage.hpcloud.com/objects/us-west"))
         .linkedServices("hpcloud-compute", "hpcloud-objectstorage")
         .iso3166Codes("US-NV")
         .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
         .defaultProperties(HPCloudObjectStorageProviderMetadata.defaultProperties());
      }

      @Override
      public HPCloudObjectStorageProviderMetadata build() {
         return new HPCloudObjectStorageProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
