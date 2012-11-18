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
package org.jclouds.joyent.joyentcloud;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.net.URI;
import java.util.Properties;

import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for SDC.
 * @author Adrian Cole
 */
public class JoyentCloudProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public JoyentCloudProviderMetadata() {
      super(builder());
   }

   public JoyentCloudProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_ZONES, "us-east-1,us-west-1,us-sw-1,eu-ams-1");
      properties.setProperty(PROPERTY_ZONE + ".us-east-1." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_ZONE + ".us-west-1." + ISO3166_CODES, "US-CA");
      properties.setProperty(PROPERTY_ZONE + ".us-sw-1." + ISO3166_CODES, "US-NV");
      properties.setProperty(PROPERTY_ZONE + ".eu-ams-1." + ISO3166_CODES, "NL-NH");
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("joyentcloud")
         .name("JoyentCloud")
         .apiMetadata(new JoyentCloudApiMetadata())
         .homepage(URI.create("http://www.joyent.com/products/smartdatacenter/"))
         .console(URI.create("https://my.joyentcloud.com/login"))
         .iso3166Codes("US-VA", "US-CA", "US-NV", "NL-NH")
         .endpoint("https://api.joyentcloud.com")
         .defaultProperties(JoyentCloudProviderMetadata.defaultProperties());
      }

      @Override
      public JoyentCloudProviderMetadata build() {
         return new JoyentCloudProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
