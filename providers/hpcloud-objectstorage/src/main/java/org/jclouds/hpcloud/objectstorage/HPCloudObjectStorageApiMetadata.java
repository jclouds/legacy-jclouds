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
package org.jclouds.hpcloud.objectstorage;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.net.URI;
import java.util.Properties;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;

import com.google.common.reflect.TypeToken;
/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for HP Cloud Services Object Storage
 * 
 * @author Jeremy Daggett
 */
public class HPCloudObjectStorageApiMetadata
      extends
      BaseBlobStoreApiMetadata<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient, BlobStoreContext<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient>, HPCloudObjectStorageApiMetadata> {

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public HPCloudObjectStorageApiMetadata() {
      this(builder());
   }

   protected HPCloudObjectStorageApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseBlobStoreApiMetadata.Builder.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(KeystoneProperties.VERSION, "2.0");
      properties.setProperty(CREDENTIAL_TYPE, "apiAccessKeyCredentials");
      return properties;
   }

   public static class Builder
         extends
         BaseBlobStoreApiMetadata.Builder<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient, BlobStoreContext<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient>, HPCloudObjectStorageApiMetadata> {
      
      protected Builder() {
         id("hpcloud-objectstorage")
         .name("HP Cloud Services Object Storage API")
         .identityName("tenantId:accessKey")
         .credentialName("secretKey")
         .version("1.0")
         .documentation(URI.create("https://build.hpcloud.com/object-storage/api"))
         .javaApi(HPCloudObjectStorageClient.class, HPCloudObjectStorageAsyncClient.class)
         .contextBuilder(TypeToken.of(HPCloudObjectStorageContextBuilder.class))
         .defaultProperties(HPCloudObjectStorageApiMetadata.defaultProperties());
      }

      @Override
      public HPCloudObjectStorageApiMetadata build() {
         return new HPCloudObjectStorageApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(HPCloudObjectStorageApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}
