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
package org.jclouds.azure.servicemanagement;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.azure.servicemanagement.config.AzureServiceManagementRestClientModule;
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
public class AzureServiceManagementApiMetadata extends BaseRestApiMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 8067252472547486854L;

   public static final TypeToken<RestContext<AzureServiceManagementClient, AzureServiceManagementAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<AzureServiceManagementClient, AzureServiceManagementAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AzureServiceManagementApiMetadata() {
      this(builder());
   }

   protected AzureServiceManagementApiMetadata(Builder builder) {
      super(builder);
   }
  
   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      // TODO is there the same for Compute ?
//      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      return properties;
   }
   
   public static class Builder extends BaseRestApiMetadata.Builder {
      protected Builder(){
         super(AzureServiceManagementClient.class, AzureServiceManagementAsyncClient.class);
         id("azurevirtualmachines")
         .name("Microsoft Azure Service Management Service API")
         .identityName("Account Name")
         .credentialName("Access Key")
         .version("2012-03-01")
         .defaultEndpoint("https://management.core.windows.net")
         .documentation(URI.create("http://msdn.microsoft.com/en-us/library/ee460799"))
         .defaultProperties(AzureServiceManagementApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AzureServiceManagementRestClientModule.class));
      }
      
      @Override
      public AzureServiceManagementApiMetadata build() {
         return new AzureServiceManagementApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
