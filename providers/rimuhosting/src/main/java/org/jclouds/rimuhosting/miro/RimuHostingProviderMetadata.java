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
package org.jclouds.rimuhosting.miro;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for RimuHosting.
 * @author Adrian Cole
 */
public class RimuHostingProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public RimuHostingProviderMetadata() {
      super(builder());
   }

   public RimuHostingProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ZONES, "DCAUCKLAND,DCLONDON,DCDALLAS,DCSYDNEY");
      properties.setProperty(PROPERTY_ZONE + ".DCAUCKLAND." + ISO3166_CODES, "NZ-AUK");
      properties.setProperty(PROPERTY_ZONE + ".DCLONDON." + ISO3166_CODES, "GB-LND");
      properties.setProperty(PROPERTY_ZONE + ".DCDALLAS." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_ZONE + ".DCSYDNEY." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],os64Bit=true,hardwareId=MIRO4B,locationId=DCDALLAS");
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("rimuhosting")
         .name("RimuHosting")
         .apiMetadata(new RimuHostingApiMetadata())
         .homepage(URI.create("http://www.rimuhosting.com"))
         .console(URI.create("https://rimuhosting.com/cp"))
         .iso3166Codes("NZ-AUK", "US-TX", "AU-NSW", "GB-LND")
         .endpoint("https://api.rimuhosting.com/r")
         .defaultProperties(RimuHostingProviderMetadata.defaultProperties());
      }

      @Override
      public RimuHostingProviderMetadata build() {
         return new RimuHostingProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
