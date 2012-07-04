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

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneParserModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
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
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   
   public static final TypeToken<RestContext<? extends KeystoneClient,? extends  KeystoneAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<? extends KeystoneClient,? extends  KeystoneAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return (Builder) new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public KeystoneApiMetadata() {
      this(new Builder(KeystoneClient.class, KeystoneAsyncClient.class));
   }

   protected KeystoneApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(KeystoneProperties.VERSION, "2.0");
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      properties.put(SERVICE_TYPE, ServiceType.IDENTITY);
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder(Class<?> api, Class<?> asyncApi) {
         super(api, asyncApi);
          id("openstack-keystone")
         .name("OpenStack Keystone Essex+ API")
         .identityName("tenantId:user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("2.0")
         .defaultEndpoint("http://localhost:5000")
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

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}