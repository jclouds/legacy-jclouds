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

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.hpcloud.objectstorage.blobstore.config.HPCloudObjectStorageBlobStoreContextModule;
import org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageRestClientModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for HP Cloud Services Object Storage
 * 
 * @author Jeremy Daggett
 */
public class HPCloudObjectStorageApiMetadata extends BaseRestApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 820062881469203616L;
   
   public static final TypeToken<RestContext<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<HPCloudObjectStorageClient, HPCloudObjectStorageAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

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

   public static Properties defaultProperties() {
      Properties properties = SwiftApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      // TODO: this doesn't actually do anything yet.
      properties.setProperty(KeystoneProperties.VERSION, "2.0");
      properties.setProperty(CREDENTIAL_TYPE, "apiAccessKeyCredentials");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(HPCloudObjectStorageClient.class, HPCloudObjectStorageAsyncClient.class);
         id("hpcloud-objectstorage")
         .name("HP Cloud Services Object Storage API")
         .identityName("tenantId:accessKey")
         .credentialName("secretKey")
         .version("1.0")
         .documentation(URI.create("https://build.hpcloud.com/object-storage/api"))
         .defaultProperties(HPCloudObjectStorageApiMetadata.defaultProperties())
         .view(TypeToken.of(BlobStoreContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(HPCloudObjectStorageRestClientModule.class, HPCloudObjectStorageBlobStoreContextModule.class));
      }

      @Override
      public HPCloudObjectStorageApiMetadata build() {
         return new HPCloudObjectStorageApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}
