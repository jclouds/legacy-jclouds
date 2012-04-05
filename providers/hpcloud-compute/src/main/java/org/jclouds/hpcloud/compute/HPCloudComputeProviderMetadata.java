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
package org.jclouds.hpcloud.compute;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.nova.v1_1.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v1_1.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.openstack.nova.v1_1.NovaApiMetadata;
import org.jclouds.openstack.nova.v1_1.NovaAsyncClient;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for HP Cloud Compute Services.
 * 
 * @author Adrian Cole
 */
public class HPCloudComputeProviderMetadata
      extends
      BaseProviderMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public HPCloudComputeProviderMetadata() {
      super(builder());
   }

   public HPCloudComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = new Properties();
      // deallocating ip addresses can take a while
      properties.setProperty(TIMEOUT_NODE_TERMINATED, 60 * 1000 + "");

      properties.setProperty(CREDENTIAL_TYPE, "apiAccessKeyCredentials");
      properties.setProperty(AUTO_ALLOCATE_FLOATING_IPS, "true");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "true");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

      protected Builder(){
         id("hpcloud-compute")
         .name("HP Cloud Compute Services")
         .apiMetadata(new NovaApiMetadata().toBuilder().identityName("tenantId:accessKey")
               .credentialName("secretKey")
                           .contextBuilder(TypeToken.of(HPCloudComputeContextBuilder.class)).build())
         .homepage(URI.create("http://hpcloud.com"))
         .console(URI.create("https://manage.hpcloud.com/compute"))
         .linkedServices("hpcloud-compute", "hpcloud-objectstorage")
         .iso3166Codes("US-NV")
         .endpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357")
         .defaultProperties(HPCloudComputeProviderMetadata.defaultProperties());
      }

      @Override
      public HPCloudComputeProviderMetadata build() {
         return new HPCloudComputeProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}