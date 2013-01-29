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
package org.jclouds.azure.management;
import static org.jclouds.azure.management.config.AzureManagementProperties.SUBSCRIPTION_ID;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.azure.management.compute.config.AzureManagementComputeServiceContextModule;
import org.jclouds.azure.management.config.AzureManagementRestClientModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Microsoft Service Management Service API
 * 
 * @author Gerald Pereira
 */
public class AzureManagementApiMetadata extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<AzureManagementApi, AzureManagementAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<AzureManagementApi, AzureManagementAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AzureManagementApiMetadata() {
      this(builder());
   }

   protected AzureManagementApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {
      protected Builder() {
         super(AzureManagementApi.class, AzureManagementAsyncApi.class);
         id("azure-management")
         .name("Microsoft Azure Service Management Service API")
         .version("2012-03-01")
         .identityName("Path to Management Certificate .p12 file, or PEM string")
         .credentialName("Password to Management Certificate")
         .defaultEndpoint("https://management.core.windows.net/${" + SUBSCRIPTION_ID + "}")
         .endpointName("Service Management Endpoint ending in your Subscription Id")
         .documentation(URI.create("http://msdn.microsoft.com/en-us/library/ee460799"))
         .defaultProperties(AzureManagementApiMetadata.defaultProperties())
         .view(typeToken(ComputeServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>> of(AzureManagementComputeServiceContextModule.class, AzureManagementRestClientModule.class));
      }

      @Override
      public AzureManagementApiMetadata build() {
         return new AzureManagementApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
