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
package org.jclouds.cloudfiles;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;
import org.jclouds.openstack.OpenStackAuthAsyncClient;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Rackspace Cloud Files API
 * 
 * @author Adrian Cole
 */
public class CloudFilesApiMetadata
      extends
      BaseBlobStoreApiMetadata<CloudFilesClient, CloudFilesAsyncClient, BlobStoreContext<CloudFilesClient, CloudFilesAsyncClient>, CloudFilesApiMetadata> {
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public CloudFilesApiMetadata() {
      this(builder());
   }

   protected CloudFilesApiMetadata(Builder builder) {
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
         BaseBlobStoreApiMetadata.Builder<CloudFilesClient, CloudFilesAsyncClient, BlobStoreContext<CloudFilesClient, CloudFilesAsyncClient>, CloudFilesApiMetadata> {
      protected Builder() {
         id("cloudfiles")
         .name("Rackspace Cloud Files API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/files/api/v1/cfdevguide_d5/content/ch01.html"))
         .version(OpenStackAuthAsyncClient.VERSION)
         .contextBuilder(TypeToken.of(CloudFilesContextBuilder.class))
         .defaultProperties(CloudFilesApiMetadata.defaultProperties())
         .javaApi(CloudFilesClient.class, CloudFilesAsyncClient.class);
      }

      @Override
      public CloudFilesApiMetadata build() {
         return new CloudFilesApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(CloudFilesApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}