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

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Microsoft Azure Blob Service API
 * 
 * @author Adrian Cole
 */
public class AzureBlobApiMetadata
      extends
      BaseBlobStoreApiMetadata<AzureBlobClient, AzureBlobAsyncClient, BlobStoreContext<AzureBlobClient, AzureBlobAsyncClient>, AzureBlobApiMetadata> {
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
  
   protected static Properties defaultProperties() {
      Properties properties = BaseBlobStoreApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      return properties;
   }
   
   public static class Builder extends BaseBlobStoreApiMetadata.Builder<AzureBlobClient, AzureBlobAsyncClient, BlobStoreContext<AzureBlobClient, AzureBlobAsyncClient>, AzureBlobApiMetadata> {
      protected Builder(){
            id("azureblob")
            .name("Microsoft Azure Blob Service API")
            .identityName("Account Name")
            .credentialName("Access Key")
            .version("2009-09-19")
            .defaultEndpoint("https://${jclouds.identity}.blob.core.windows.net")
            .documentation(URI.create("http://msdn.microsoft.com/en-us/library/dd135733.aspx"))
            .contextBuilder(TypeToken.of(AzureBlobContextBuilder.class))
            .javaApi(AzureBlobClient.class, AzureBlobAsyncClient.class)
            .defaultProperties(AzureBlobApiMetadata.defaultProperties());
     }
      
      @Override
      public AzureBlobApiMetadata build() {
         return new AzureBlobApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(AzureBlobApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
