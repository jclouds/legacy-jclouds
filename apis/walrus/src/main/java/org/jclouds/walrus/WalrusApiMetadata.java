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
package org.jclouds.walrus;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.s3.S3ApiMetadata;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.config.S3BlobStoreContextModule;
import org.jclouds.walrus.config.WalrusRestClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for the Walrus S3 API
 * 
 * @author Adrian Cole
 */
public class WalrusApiMetadata extends S3ApiMetadata {

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public WalrusApiMetadata() {
      this(builder());
   }

   protected WalrusApiMetadata(Builder builder) {
      super(builder);
   }
   
   public static Properties defaultProperties() {
      Properties properties = S3ApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_S3_SERVICE_PATH, "/services/Walrus");
      properties.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return properties;
   }

   public static class Builder extends S3ApiMetadata.Builder {
      protected Builder(){
         super(S3Client.class, S3AsyncClient.class);
         id("walrus")
         .name("Walrus (S3 clone) API")
         .version("Walrus-1.6")
         .defaultProperties(WalrusApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(WalrusRestClientModule.class, S3BlobStoreContextModule.class));
      }
      
      @Override
      public WalrusApiMetadata build() {
         return new WalrusApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }

}
