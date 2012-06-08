package org.jclouds.rackspace.cloudidentity.v2_0;
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


import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.KeystoneApiMetadata;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncClient;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.rackspace.cloudidentity.v2_0.config.CloudIdentityRestClientModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Rackspace Cloud Identity Service
 * 
 * @author Adrian Cole
 */
public class CloudIdentityApiMetadata extends KeystoneApiMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = -1572520638079261710L;
   
   public static final TypeToken<RestContext<KeystoneClient, KeystoneAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<KeystoneClient, KeystoneAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public CloudIdentityApiMetadata() {
      this(builder());
   }

   protected CloudIdentityApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = KeystoneApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CloudIdentityCredentialTypes.API_KEY_CREDENTIALS);

      return properties;
   }

   public static class Builder extends KeystoneApiMetadata.Builder {
      protected Builder(){
         super(KeystoneClient.class, KeystoneAsyncClient.class);
         id("rackspace-cloudidentity")
         .name("Rackspace Cloud Identity Service")
         .defaultEndpoint("https://identity.api.rackspacecloud.com")
         .identityName("username")
         .credentialName("API Key")
         .defaultProperties(CloudIdentityApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .documentation(URI.create("http://docs.rackspace.com/auth/api/v2.0/auth-client-devguide/"))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(CloudIdentityRestClientModule.class));
      }
      
      @Override
      public CloudIdentityApiMetadata build() {
         return new CloudIdentityApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}