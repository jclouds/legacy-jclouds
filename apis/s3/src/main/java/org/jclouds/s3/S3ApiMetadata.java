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
package org.jclouds.s3;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's S3 api.
 * 
 * @author Adrian Cole
 */
public class S3ApiMetadata extends BaseApiMetadata {

   public S3ApiMetadata() {
      this(builder()
            .id("s3")
            .type(ApiType.BLOBSTORE)
            .name("Amazon Simple Storage Service (S3) API")
            .identityName("Access Key ID")
            .credentialName("Secret Access Key")
            .documentation(URI.create("http://docs.amazonwebservices.com/AmazonS3/latest/API")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected S3ApiMetadata(S3ApiMetadataBuilder<?> builder) {
      super(builder);
   }
   
   public static class S3ApiMetadataBuilder<B extends S3ApiMetadataBuilder<B>> extends Builder<B> {

      @Override
      public S3ApiMetadata build() {
         return new S3ApiMetadata(this);
      }
   }

   private static class S3ConcreteBuilder extends S3ApiMetadataBuilder<S3ConcreteBuilder> {

      @Override
      public S3ApiMetadata build() {
         return new S3ApiMetadata(this);
      }
   }

   private static S3ConcreteBuilder builder() {
      return new S3ConcreteBuilder();
   }

   @Override
   public S3ApiMetadataBuilder<?> toBuilder() {
      return builder().fromApiMetadata(this);
   }
}