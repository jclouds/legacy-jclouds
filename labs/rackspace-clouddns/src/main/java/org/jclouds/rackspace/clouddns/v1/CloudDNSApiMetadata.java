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
package org.jclouds.rackspace.clouddns.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ProviderModule;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNSRestClientModule;
import org.jclouds.rackspace.cloudidentity.v2_0.ServiceType;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for DynECT 1.0 API
 * 
 * @author Everett Toews
 */
public class CloudDNSApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<CloudDNSApi, CloudDNSAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<CloudDNSApi, CloudDNSAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

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
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.DNS);
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {

      protected Builder() {
         super(CloudDNSApi.class, CloudDNSAsyncApi.class);
         id("rackspace-clouddns")
               .name("Rackspace Cloud DNS API")
               .identityName("Username")
               .credentialName("API Key")
               .documentation(URI.create("http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/index.html"))
               .version("1.0")
               .defaultEndpoint("https://identity.api.rackspacecloud.com/v2.0/")
               .defaultProperties(CloudDNSApiMetadata.defaultProperties())
               .defaultModules(
                     ImmutableSet.<Class<? extends Module>> builder()
                        .add(CloudIdentityAuthenticationModule.class)
                        .add(ProviderModule.class)
                        .add(CloudDNSRestClientModule.class)
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
