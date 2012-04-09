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
package org.jclouds.azureblob;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Microsoft Azure Blob Service.
 * 
 * @author Adrian Cole
 */
public class AzureBlobProviderMetadata extends BaseProviderMetadata {
   public AzureBlobProviderMetadata() {
      this(builder()
            .id("azureblob")
            .name("Microsoft Azure Blob Service")
            .api(new AzureBlobApiMetadata())
            .homepage(URI.create("http://www.microsoft.com/windowsazure/storage/"))
            .console(URI.create("https://windows.azure.com/default.aspx"))
            .linkedServices("azureblob", "azurequeue", "azuretable")
            .iso3166Codes("US-TX","US-IL","IE-D","SG","NL-NH","HK"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected AzureBlobProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public AzureBlobProviderMetadata build() {
         return new AzureBlobProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}