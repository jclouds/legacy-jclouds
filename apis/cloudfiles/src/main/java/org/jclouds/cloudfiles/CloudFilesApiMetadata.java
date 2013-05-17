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
package org.jclouds.cloudfiles;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.cloudfiles.config.CloudFilesRestClientModule.StorageAndCDNManagementEndpointModule;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.blobstore.SwiftBlobSigner;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Implementation of {@link ApiMetadata} for Rackspace Cloud Files API
 *
 * @author Adrian Cole
 */
public class CloudFilesApiMetadata extends SwiftApiMetadata {

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudFilesClient.class)} as
    *             {@link CloudFilesAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<CloudFilesClient, CloudFilesAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<CloudFilesClient, CloudFilesAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudFilesApiMetadata() {
      this(new Builder());
   }

   protected CloudFilesApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends SwiftApiMetadata.Builder<Builder> {
      @SuppressWarnings("deprecation")
      protected Builder(){
         super(CloudFilesClient.class, CloudFilesAsyncClient.class);
         id("cloudfiles")
         .name("Rackspace Cloud Files API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/files/api/v1/cfdevguide_d5/content/ch01.html"))
         .defaultProperties(CloudFilesApiMetadata.defaultProperties())
         .context(CONTEXT_TOKEN)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(StorageAndCDNManagementEndpointModule.class)
                                     .add(CloudFilesRestClientModule.class)
                                     .add(CloudFilesBlobStoreContextModule.class)
                                     .add(CloudFilesTemporaryUrlExtensionModule.class).build());
      }

      @Override
      public CloudFilesApiMetadata build() {
         return new CloudFilesApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   public static class CloudFilesTemporaryUrlExtensionModule extends TemporaryUrlExtensionModule<CloudFilesAsyncClient> {
      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<CloudFilesAsyncClient>>() {
         });
      }
   }
}
