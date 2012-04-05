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
package org.jclouds.openstack.swift;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;
import org.jclouds.openstack.OpenStackAuthAsyncClient;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Rackspace Cloud Files API
 * 
 * @author Adrian Cole
 */
public class SwiftApiMetadata
      extends
      BaseBlobStoreApiMetadata<SwiftClient, SwiftAsyncClient, BlobStoreContext<SwiftClient, SwiftAsyncClient>, SwiftApiMetadata> {
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public SwiftApiMetadata() {
      this(builder());
   }

   protected SwiftApiMetadata(Builder builder) {
      super(builder);
   }

   protected static Properties defaultProperties() {
      Properties properties = BaseBlobStoreApiMetadata.Builder.defaultProperties();
      properties.setProperty(PROPERTY_REGIONS, "DEFAULT");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      return properties;
   }

   public static class Builder
         extends
         BaseBlobStoreApiMetadata.Builder<SwiftClient, SwiftAsyncClient, BlobStoreContext<SwiftClient, SwiftAsyncClient>, SwiftApiMetadata> {
      protected Builder() {
         id("swift")
         .type(ApiType.BLOBSTORE)
         .name("OpenStack Swift Pre-Diablo API")
         .identityName("tenantId:user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version(OpenStackAuthAsyncClient.VERSION)
         .contextBuilder(TypeToken.of(SwiftContextBuilder.class))
         .defaultProperties(SwiftApiMetadata.defaultProperties())
         .javaApi(SwiftClient.class, SwiftAsyncClient.class);
      }

      @Override
      public SwiftApiMetadata build() {
         return new SwiftApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(SwiftApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}