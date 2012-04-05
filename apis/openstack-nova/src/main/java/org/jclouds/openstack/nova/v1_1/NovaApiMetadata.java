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
package org.jclouds.openstack.nova.v1_1;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;
import static org.jclouds.openstack.nova.v1_1.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v1_1.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;
import static org.jclouds.openstack.nova.v1_1.config.NovaProperties.TIMEOUT_SECURITYGROUP_PRESENT;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Nova 1.0 API
 * 
 * @author Adrian Cole
 */
public class NovaApiMetadata
      extends
      BaseComputeServiceApiMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

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

   protected static Properties defaultProperties() {
      Properties properties = BaseComputeServiceApiMetadata.Builder.defaultProperties();
      // auth fail can happen while cloud-init applies keypair updates
      properties.setProperty("jclouds.ssh.max-retries", "7");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      
      properties.setProperty(SERVICE_TYPE, ServiceType.COMPUTE);
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(KeystoneProperties.VERSION, "2.0");
      
      properties.setProperty(AUTO_ALLOCATE_FLOATING_IPS, "false");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "false");
      properties.setProperty(TIMEOUT_SECURITYGROUP_PRESENT, "500");
      return properties;
   }

   public static class Builder
         extends
         BaseComputeServiceApiMetadata.Builder<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

      protected Builder() {
          id("openstack-nova")
         .name("OpenStack Nova Diablo+ API")
         .identityName("tenantId:user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.1")
         .defaultEndpoint("http://localhost:5000")
         .javaApi(NovaClient.class, NovaAsyncClient.class)
         .defaultProperties(NovaApiMetadata.defaultProperties())
         .contextBuilder(TypeToken.of(NovaContextBuilder.class));
      }

      @Override
      public NovaApiMetadata build() {
         return new NovaApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(NovaApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}