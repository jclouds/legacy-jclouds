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
package org.jclouds.ninefold.storage;

import java.net.URI;
import java.util.Properties;

import org.jclouds.atmos.AtmosApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Ninefold's
 * Storage provider.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class NinefoldStorageProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public NinefoldStorageProviderMetadata() {
      super(builder());
   }

   public NinefoldStorageProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
          id("ninefold-storage")
         .name("Ninefold Storage")
         .apiMetadata(new AtmosApiMetadata())
         .homepage(URI.create("http://ninefold.com/cloud-storage/"))
         .console(URI.create("https://ninefold.com/portal/"))
         .iso3166Codes("AU-NSW")
         .endpoint("http://onlinestorage.ninefold.com")
         .defaultProperties(NinefoldStorageProviderMetadata.defaultProperties());
      }

      @Override
      public NinefoldStorageProviderMetadata build() {
         return new NinefoldStorageProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
