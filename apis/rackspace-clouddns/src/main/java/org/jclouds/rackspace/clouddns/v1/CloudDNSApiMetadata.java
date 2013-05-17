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
package org.jclouds.rackspace.clouddns.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.rackspace.cloudidentity.v2_0.ServiceType.DNS;
import static org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes.API_KEY_CREDENTIALS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ProviderModule;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNSHttpApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationApiModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Rackspace Cloud DNS 1.0 API
 * 
 * @author Everett Toews
 */
public class CloudDNSApiMetadata extends BaseHttpApiMetadata<CloudDNSApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudDNSApiMetadata() {
      this(new Builder());
   }

   protected CloudDNSApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, DNS);
      properties.setProperty(CREDENTIAL_TYPE, API_KEY_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<CloudDNSApi, Builder> {

      protected Builder() {
         id("rackspace-clouddns")
         .name("Rackspace Cloud DNS API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/index.html"))
         .version("1.0")
         .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
         .defaultProperties(CloudDNSApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>> builder()
                                     .add(CloudIdentityAuthenticationApiModule.class)
                                     .add(CloudIdentityAuthenticationModule.class)
                                     .add(ProviderModule.class)
                                     .add(CloudDNSHttpApiModule.class)
                                     .build());
      }

      @Override
      public CloudDNSApiMetadata build() {
         return new CloudDNSApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
