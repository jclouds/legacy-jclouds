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

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.azureblob.blobstore.config.AzureBlobStoreContextModule;
import org.jclouds.azureblob.config.AzureBlobRestClientModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Microsoft Azure Blob Service API
 * 
 * @author Adrian Cole
 */
public class AzureBlobApiMetadata extends BaseRestApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(AzureBlobClient.class)} as
    *             {@link AzureBlobAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<AzureBlobClient, AzureBlobAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<AzureBlobClient, AzureBlobAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };
   
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AzureBlobApiMetadata() {
      this(builder());
   }

   protected AzureBlobApiMetadata(Builder builder) {
      super(builder);
   }
  
   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      return properties;
   }
   
   public static class Builder extends BaseRestApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(AzureBlobClient.class, AzureBlobAsyncClient.class);
         id("azureblob")
         .name("Microsoft Azure Blob Service API")
         .identityName("Account Name")
         .credentialName("Access Key")
         .version("2009-09-19")
         .defaultEndpoint("https://${jclouds.identity}.blob.core.windows.net")
         .documentation(URI.create("http://msdn.microsoft.com/en-us/library/dd135733.aspx"))
         .defaultProperties(AzureBlobApiMetadata.defaultProperties())
         .view(typeToken(BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(AzureBlobRestClientModule.class, AzureBlobStoreContextModule.class));
      }
      
      @Override
      public AzureBlobApiMetadata build() {
         return new AzureBlobApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
