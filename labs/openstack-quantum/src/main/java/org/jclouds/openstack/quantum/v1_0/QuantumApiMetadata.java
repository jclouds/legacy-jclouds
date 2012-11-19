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
package org.jclouds.openstack.quantum.v1_0;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.quantum.v1_0.config.QuantumRestClientModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Quantum 1.0 API
 * 
 * @author Adam Lowe
 */
public class QuantumApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<QuantumApi, QuantumAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<QuantumApi, QuantumAsyncApi>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public QuantumApiMetadata() {
      this(new Builder());
   }

   protected QuantumApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.NETWORK);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(QuantumApi.class, QuantumAsyncApi.class);
          id("openstack-quantum")
         .name("OpenStack Quantum API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .endpointName("KeyStone base url ending in /v2.0/")
         .documentation(URI.create("http://docs.openstack.org/api/openstack-network/1.0/content/"))
         .version("1.0")
         .defaultEndpoint("http://localhost:5000/v2.0/")
         .defaultProperties(QuantumApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(ZoneModule.class)
                                     .add(QuantumRestClientModule.class).build());
      }
      
      @Override
      public QuantumApiMetadata build() {
         return new QuantumApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
