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
package org.jclouds.rackspace.cloudservers.us;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.v2_0.config.NovaRestClientModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule.CloudIdentityAuthenticationModuleForZones;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Next Generation Cloud Servers.
 * 
 * @author Adrian Cole
 */
public class CloudServersUSProviderMetadata extends BaseProviderMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -300987074165012648L;

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
      properties.setProperty(TEMPLATE, "imageId=DFW/3afe97b2-26dc-49c5-a2cc-a2fc8d80c001,loginUser=root");

      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("rackspace-cloudservers-us")
         .name("Rackspace Next Generation Cloud Servers")
         .apiMetadata(new NovaApiMetadata().toBuilder()
                  .identityName("username")
                  .credentialName("API Key")
                  .version("2")
                  .documentation(URI.create("http://docs.rackspace.com/servers/api/v2/cs-devguide/content/ch_preface.html#webhelp-currentid"))
                  .defaultModules(ImmutableSet.<Class<? extends Module>>of(CloudIdentityAuthenticationModuleForZones.class, NovaRestClientModule.class, NovaComputeServiceContextModule.class))
                  .build())
         .homepage(URI.create("http://www.rackspace.com/cloud/nextgen"))
         .console(URI.create("https://mycloud.rackspace.com"))
         .linkedServices("rackspace-cloudservers-us", "cloudfiles-swift-us")
         .iso3166Codes("US-TX")
         .endpoint("https://identity.api.rackspacecloud.com")
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
