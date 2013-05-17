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
package org.jclouds.rackspace.cloudidentity.v2_0;



import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApiMetadata;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncApi;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneParserModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneRestClientModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneRestClientModule.KeystoneAdminURLModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityAuthenticationModule;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rackspace.cloudidentity.v2_0.config.SyncToAsyncCloudIdentityAuthenticationApiModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Rackspace Cloud Identity Service
 * 
 * @author Adrian Cole
 */
public class CloudIdentityApiMetadata extends KeystoneApiMetadata {
   
   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(KeystoneApi.class)} as
    *             {@link KeystoneAsyncApi} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<KeystoneApi, KeystoneAsyncApi>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<KeystoneApi, KeystoneAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudIdentityApiMetadata() {
      this(new Builder());
   }

   protected CloudIdentityApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = KeystoneApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);
      return properties;
   }

   public static class Builder extends KeystoneApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(KeystoneApi.class, KeystoneAsyncApi.class);
         id("rackspace-cloudidentity")
         .name("Rackspace Cloud Identity Service")
         .identityName("${userName}")
         .credentialName("${apiKey}")
         .defaultEndpoint("https://identity.api.rackspacecloud.com/v${jclouds.api-version}/")
         .endpointName("identity service url ending in /v${jclouds.api-version}/")
         .defaultProperties(CloudIdentityApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .documentation(URI.create("http://docs.rackspace.com/auth/api/v2.0/auth-api-devguide/"))
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(SyncToAsyncCloudIdentityAuthenticationApiModule.class)
                                     .add(CloudIdentityAuthenticationModule.class)
                                     .add(KeystoneAdminURLModule.class)
                                     .add(KeystoneParserModule.class)
                                     .add(KeystoneRestClientModule.class).build());
      }
      
      @Override
      public CloudIdentityApiMetadata build() {
         return new CloudIdentityApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
