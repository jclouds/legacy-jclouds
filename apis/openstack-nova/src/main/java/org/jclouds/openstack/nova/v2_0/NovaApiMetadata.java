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
package org.jclouds.openstack.nova.v2_0;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.VERSION;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.TIMEOUT_SECURITYGROUP_PRESENT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.ZoneModule;
import org.jclouds.openstack.nova.v2_0.compute.config.NovaComputeServiceContextModule;
import org.jclouds.openstack.nova.v2_0.config.NovaRestClientModule;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Nova 1.0 API
 * 
 * @author Adrian Cole
 */
public class NovaApiMetadata extends BaseRestApiMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<NovaClient, NovaAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<NovaClient, NovaAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public NovaApiMetadata() {
      this(new Builder());
   }

   protected NovaApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // auth fail can happen while cloud-init applies keypair updates
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      
      properties.setProperty(SERVICE_TYPE, ServiceType.COMPUTE);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(VERSION, "2.0");
      
      properties.setProperty(AUTO_ALLOCATE_FLOATING_IPS, "false");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "false");
      properties.setProperty(TIMEOUT_SECURITYGROUP_PRESENT, "500");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(NovaClient.class, NovaAsyncClient.class);
          id("openstack-nova")
         .name("OpenStack Nova Diablo+ API")
         .identityName("tenantName:user or user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.1")
         .defaultEndpoint("http://localhost:5000")
         .defaultProperties(NovaApiMetadata.defaultProperties())
         .view(TypeToken.of(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(KeystoneAuthenticationModule.class)
                                     .add(ZoneModule.class)
                                     .add(NovaRestClientModule.class)
                                     .add(NovaComputeServiceContextModule.class).build());
      }
      
      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}