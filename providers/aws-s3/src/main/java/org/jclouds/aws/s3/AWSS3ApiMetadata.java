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
package org.jclouds.aws.s3;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.s3.S3ApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for the Amazon-specific S3 API
 * 
 * @author Adrian Cole
 */
public class AWSS3ApiMetadata extends S3ApiMetadata {

   public AWSS3ApiMetadata() {
      this(builder().fromApiMetadata(new S3ApiMetadata())
            .id("aws-s3")
            .name("Amazon-specific S3 API"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected AWSS3ApiMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends S3ApiMetadataBuilder<ConcreteBuilder> {

      @Override
      public AWSS3ApiMetadata build() {
         return new AWSS3ApiMetadata(this);
      }
   }

   private static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   @Override
   public ConcreteBuilder toBuilder() {
      return builder().fromApiMetadata(this);
   }
}