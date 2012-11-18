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
package org.jclouds.rackspace.cloudblockstorage.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
import org.jclouds.openstack.cinder.v1.config.CinderParserModule;
import org.jclouds.openstack.cinder.v1.config.CinderRestClientModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Next Generation Cloud Block Storage.
 * 
 * @author Everett Toews
 */
public class CloudBlockStorageUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public CloudBlockStorageUSProviderMetadata() {
      super(builder());
   }

   public CloudBlockStorageUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(PROPERTY_ZONES, "ORD,DFW");
      properties.setProperty(PROPERTY_ZONE + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_ZONE + ".DFW." + ISO3166_CODES, "US-TX");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudblockstorage-us")
         .name("Rackspace Next Generation Cloud Block Storage US")
         .apiMetadata(new CinderApiMetadata().toBuilder()
                  .identityName("${userName}")
                  .credentialName("${apiKey}")
                  .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
                  .endpointName("identity service url ending in /v2.0/")
                  .documentation(URI.create("http://docs.rackspace.com/cbs/api/v1.0/cbs-devguide/content/overview.html"))
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(CloudIdentityAuthenticationModule.class)
                                              .add(ZoneModule.class)
                                              .add(CinderParserModule.class)
                                              .add(CinderRestClientModule.class).build())
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/public/blockstorage/"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudservers-us", "cloudfiles-us")
         .iso3166Codes("US-IL", "US-TX")
         .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudBlockStorageUSProviderMetadata.defaultProperties());
      }

      @Override
      public CloudBlockStorageUSProviderMetadata build() {
         return new CloudBlockStorageUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }

}
