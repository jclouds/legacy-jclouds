/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azureblob;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Microsoft Azure Blob Service.
 * 
 * @author Adrian Cole
 */
public class AzureBlobProviderMetadata extends BaseProviderMetadata {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AzureBlobProviderMetadata() {
      super(builder());
   }

   public AzureBlobProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
            id("azureblob")
            .name("Microsoft Azure Blob Service")
            .apiMetadata(new AzureBlobApiMetadata())
            .endpoint("https://${jclouds.identity}.blob.core.windows.net")
            .homepage(URI.create("http://www.microsoft.com/windowsazure/storage/"))
            .console(URI.create("https://windows.azure.com/default.aspx"))
            .linkedServices("azureblob", "azurequeue", "azuretable")
            .iso3166Codes("US-TX","US-IL","IE-D","SG","NL-NH","HK")
            .defaultProperties(AzureBlobProviderMetadata.defaultProperties());
      }

      @Override
      public AzureBlobProviderMetadata build() {
         return new AzureBlobProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
