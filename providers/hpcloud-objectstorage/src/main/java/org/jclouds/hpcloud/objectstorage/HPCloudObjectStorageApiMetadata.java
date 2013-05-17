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
package org.jclouds.hpcloud.objectstorage;

import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncHttpApi;

import java.net.URI;
import java.util.Properties;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.hpcloud.objectstorage.blobstore.HPCloudObjectStorageBlobRequestSigner;
import org.jclouds.hpcloud.objectstorage.blobstore.config.HPCloudObjectStorageBlobStoreContextModule;
import org.jclouds.hpcloud.objectstorage.config.HPCloudObjectStorageRestClientModule;
import org.jclouds.openstack.keystone.v2_0.config.MappedAuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule.RegionModule;
import org.jclouds.openstack.swift.SwiftKeystoneApiMetadata;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.KeystoneStorageEndpointModule;
import org.jclouds.openstack.swift.extensions.KeystoneTemporaryUrlKeyAsyncApi;
import org.jclouds.openstack.swift.extensions.TemporaryUrlKeyApi;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for HP Cloud Services Object Storage
 *
 * @author Jeremy Daggett
 */
public class HPCloudObjectStorageApiMetadata extends SwiftKeystoneApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(HPCloudObjectStorageApi.class)} as
    *             {@link HPCloudObjectStorageAsyncApi} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<HPCloudObjectStorageApi, HPCloudObjectStorageAsyncApi>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<HPCloudObjectStorageApi, HPCloudObjectStorageAsyncApi>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public HPCloudObjectStorageApiMetadata() {
      this(new Builder());
   }

   protected HPCloudObjectStorageApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftKeystoneApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends SwiftKeystoneApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(HPCloudObjectStorageApi.class, HPCloudObjectStorageAsyncApi.class);
         id("hpcloud-objectstorage")
         .endpointName("identity service url ending in /v2.0/")
         .defaultEndpoint("https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0/")
         .name("HP Cloud Services Object Storage API")
         .documentation(URI.create("https://build.hpcloud.com/object-storage/api"))
         .defaultProperties(HPCloudObjectStorageApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(MappedAuthenticationApiModule.class)
                                     .add(KeystoneStorageEndpointModule.class)
                                     .add(RegionModule.class)
                                     .add(HPCloudObjectStorageRestClientModule.class)
                                     .add(HPCloudObjectStorageBlobStoreContextModule.class)
                                     .add(HPCloudObjectStorageTemporaryUrlExtensionModule.class).build());
      }

      @Override
      public HPCloudObjectStorageApiMetadata build() {
         return new HPCloudObjectStorageApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   /**
    * Ensures keystone auth is used instead of swift auth
    */
   public static class HPCloudObjectStorageTemporaryUrlExtensionModule extends
         TemporaryUrlExtensionModule<HPCloudObjectStorageAsyncApi> {
      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(HPCloudObjectStorageBlobRequestSigner.class);
      }
      @Override
      protected void bindTemporaryUrlKeyApi() {
         bindSyncToAsyncHttpApi(binder(), TemporaryUrlKeyApi.class, KeystoneTemporaryUrlKeyAsyncApi.class);
      }
   }
}
