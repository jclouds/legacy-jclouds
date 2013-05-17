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
package org.jclouds.gogrid;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for GoGrid.
 * @author Adrian Cole
 */
public class GoGridProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public GoGridProviderMetadata() {
      super(builder());
   }

   public GoGridProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ZONES, "1,2,3");
      properties.setProperty(PROPERTY_ZONE + ".1." + ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_ZONE + ".2." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_ZONE + ".3." + ISO3166_CODES, "NL-NH");
      properties.setProperty(TEMPLATE, "osFamily=UBUNTU,osVersionMatches=1[012].[01][04],imageNameMatches=.*w/ None.*,locationId=1");
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("gogrid")
         .name("GoGrid")
         .apiMetadata(new GoGridApiMetadata())
         .homepage(URI.create("http://www.gogrid.com"))
         .console(URI.create("https://my.gogrid.com/gogrid"))
         .iso3166Codes("US-CA", "US-VA", "NL-NH")
         .endpoint("https://api.gogrid.com/api")
         .defaultProperties(GoGridProviderMetadata.defaultProperties());
      }

      @Override
      public GoGridProviderMetadata build() {
         return new GoGridProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
