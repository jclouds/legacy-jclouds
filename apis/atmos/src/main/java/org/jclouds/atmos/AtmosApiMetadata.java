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
package org.jclouds.atmos;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for Rackspace Cloud Files API
 * 
 * @author Adrian Cole
 */
public class AtmosApiMetadata
      extends
      BaseBlobStoreApiMetadata<AtmosClient, AtmosAsyncClient, BlobStoreContext<AtmosClient, AtmosAsyncClient>, AtmosApiMetadata> {
   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public AtmosApiMetadata() {
      this(builder());
   }

   protected AtmosApiMetadata(Builder builder) {
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
         BaseBlobStoreApiMetadata.Builder<AtmosClient, AtmosAsyncClient, BlobStoreContext<AtmosClient, AtmosAsyncClient>, AtmosApiMetadata> {
      protected Builder() {
         id("atmos")
         .name("EMC's Atmos API")
         .identityName("Subtenant ID (UID)")
         .credentialName("Shared Secret")
         .documentation(URI.create("https://community.emc.com/docs/DOC-10508"))
         .version("1.4.0")
         .defaultEndpoint("https://accesspoint.atmosonline.com")
         .contextBuilder(TypeToken.of(AtmosContextBuilder.class))
         .defaultProperties(AtmosApiMetadata.defaultProperties())
         .javaApi(AtmosClient.class, AtmosAsyncClient.class);
      }

      @Override
      public AtmosApiMetadata build() {
         return new AtmosApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(AtmosApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}