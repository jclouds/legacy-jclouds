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
package org.jclouds.openstack.swift.v1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.v1.config.SwiftRestClientModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Swift 1.0 API
 * 
 * @author Adrian Cole
 */
public class SwiftApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<SwiftApi, SwiftAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<SwiftApi, SwiftAsyncApi>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public SwiftApiMetadata() {
      this(new Builder());
   }

   protected SwiftApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(SwiftApi.class, SwiftAsyncApi.class);
          id("openstack-swift")
         .name("OpenStack Swift Diablo+ API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .documentation(URI.create("http://docs.openstack.org/api/openstack-object-storage/1.0/content/ch_object-storage-dev-overview.html"))
         .version("1.0")
         .endpointName("KeyStone base url ending in /v2.0/")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(SwiftApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(RegionModule.class)
                                     .add(SwiftRestClientModule.class).build());
      }
      
      @Override
      public SwiftApiMetadata build() {
         return new SwiftApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
