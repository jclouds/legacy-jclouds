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
package org.jclouds.rackspace.cloudservers.us;

import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.config.NovaParserModule;
import org.jclouds.openstack.nova.v2_0.config.NovaRestClientModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rackspace.cloudidentity.v2_0.config.SyncToAsyncCloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudservers.us.config.CloudServersUSComputeServiceContextModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Next Generation Cloud Servers.
 * 
 * @author Adrian Cole
 */
public class CloudServersUSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public CloudServersUSProviderMetadata() {
      super(builder());
   }

   public CloudServersUSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      properties.setProperty(PROPERTY_ZONES, "ORD,DFW,IAD,SYD");
      properties.setProperty(PROPERTY_ZONE + ".ORD." + ISO3166_CODES, "US-IL");
      properties.setProperty(PROPERTY_ZONE + ".DFW." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_ZONE + ".IAD." + ISO3166_CODES, "US-VA");
      properties.setProperty(PROPERTY_ZONE + ".SYD." + ISO3166_CODES, "AU-NSW");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudservers-us")
         .name("Rackspace Next Generation Cloud Servers US")
         .apiMetadata(new NovaApiMetadata().toBuilder()
                  .identityName("${userName}")
                  .credentialName("${apiKey}")
                  .version("2")
                  .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
                  .endpointName("identity service url ending in /v2.0/")
                  .documentation(URI.create("http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/index.html"))
                  .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                              .add(SyncToAsyncCloudIdentityAuthenticationApiModule.class)
                                              .add(CloudIdentityAuthenticationModule.class)
                                              .add(ZoneModule.class)
                                              .add(NovaParserModule.class)
                                              .add(NovaRestClientModule.class)
                                              .add(CloudServersUSComputeServiceContextModule.class).build())
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/nextgen"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudservers-us", "cloudfiles-swift-us")
         .iso3166Codes("US-IL", "US-TX", "AU-NSW")
         .endpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudServersUSProviderMetadata.defaultProperties());
      }

      @Override
      public CloudServersUSProviderMetadata build() {
         return new CloudServersUSProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
