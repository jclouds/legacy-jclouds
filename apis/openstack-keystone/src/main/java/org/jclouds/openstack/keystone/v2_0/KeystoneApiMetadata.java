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
package org.jclouds.openstack.keystone.v2_0;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneParserModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneRestClientModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneRestClientModule.KeystoneAdminURLModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link org.jclouds.apis.ApiMetadata} for Keystone 2.0 API
 * 
 * @author Adrian Cole
 */
public class KeystoneApiMetadata extends BaseRestApiMetadata {
   
   public static final TypeToken<RestContext<? extends KeystoneApi,? extends  KeystoneAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<? extends KeystoneApi,? extends  KeystoneAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public KeystoneApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected KeystoneApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      properties.setProperty(SERVICE_TYPE, ServiceType.IDENTITY);
      return properties;
   }

   public static abstract class Builder<T extends Builder<T>> extends BaseRestApiMetadata.Builder<T> {
      protected Builder() {
         this(KeystoneApi.class, KeystoneAsyncApi.class);
      }

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
          id("openstack-keystone")
         .name("OpenStack Keystone Essex+ API")
         .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
         .credentialName("${password}")
         .endpointName("KeyStone base url ending in /v${jclouds.api-version}/")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("2.0")
         .defaultEndpoint("http://localhost:5000/v${jclouds.api-version}/")
         .defaultProperties(KeystoneApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(KeystoneAdminURLModule.class)
                                     .add(KeystoneParserModule.class)
                                     .add(KeystoneRestClientModule.class).build());
      }
      
      @Override
      public KeystoneApiMetadata build() {
         return new KeystoneApiMetadata(this);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
