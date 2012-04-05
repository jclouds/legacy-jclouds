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
package org.jclouds.trystack.nova;

import static org.jclouds.Constants.PROPERTY_TRUST_ALL_CERTS;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
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
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for TryStack Nova
 * 
 * @author Adrian Cole
 */
public class TryStackNovaProviderMetadata
      extends
      BaseProviderMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public TryStackNovaProviderMetadata() {
      super(builder());
   }

   public TryStackNovaProviderMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_TRUST_ALL_CERTS, "true");
      properties.setProperty(CREDENTIAL_TYPE, "passwordCredentials");
      properties.setProperty(AUTO_GENERATE_KEYPAIRS, "true");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> {

      protected Builder(){
         id("trystack-nova")
         .name("TryStack.org (Nova)")
               .apiMetadata(
                     new NovaApiMetadata().toBuilder().contextBuilder(TypeToken.of(TryStackNovaContextBuilder.class))
                           .build())         
         .homepage(URI.create("https://trystack.org"))
         .console(URI.create("https://trystack.org/dash"))
         .iso3166Codes("US-CA")
         .endpoint("https://nova-api.trystack.org:5443")
         .defaultProperties(TryStackNovaProviderMetadata.defaultProperties());
      }

      @Override
      public TryStackNovaProviderMetadata build() {
         return new TryStackNovaProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata<NovaClient, NovaAsyncClient, ComputeServiceContext<NovaClient, NovaAsyncClient>, NovaApiMetadata> in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}