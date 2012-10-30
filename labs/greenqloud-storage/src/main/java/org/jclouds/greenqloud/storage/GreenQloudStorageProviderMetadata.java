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
package org.jclouds.greenqloud.storage;

import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.s3.S3ApiMetadata;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for GreenQloud Simple Storage Service (S3).
 * 
 * @author Adrian Cole
 */
public class GreenQloudStorageProviderMetadata extends BaseProviderMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public GreenQloudStorageProviderMetadata() {
      super(builder());
   }

   public GreenQloudStorageProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "is-1");
      properties.setProperty(PROPERTY_REGION + ".is-1." + ISO3166_CODES, "IS-1");
      properties.setProperty(PROPERTY_REGION + "." + "is-1" + "." + ENDPOINT, "https://s.greenqloud.com");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("greenqloud-storage")
         .name("GreenQloud Simple Storage Service (S3)")
         .apiMetadata(new S3ApiMetadata())
         .homepage(URI.create("http://www.greenqloud.com"))
         .console(URI.create("https://manage.greenqloud.com"))
         .linkedServices("greenqloud-compute", "greenqloud-storage")
         .iso3166Codes("IS-1")
         .endpoint("https://s.greenqloud.com")
         .defaultProperties(GreenQloudStorageProviderMetadata.defaultProperties());
      }

      @Override
      public GreenQloudStorageProviderMetadata build() {
         return new GreenQloudStorageProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
