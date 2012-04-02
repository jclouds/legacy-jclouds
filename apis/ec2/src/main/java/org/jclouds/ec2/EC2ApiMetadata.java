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
package org.jclouds.ec2;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's EC2 api.
 * 
 * @author Adrian Cole
 */
public class EC2ApiMetadata extends BaseApiMetadata {

   public EC2ApiMetadata() {
      this(builder()
            .id("ec2")
            .type(ApiType.COMPUTE)
            .name("Amazon Elastic Compute Cloud (EC2) API")
            .identityName("Access Key ID")
            .credentialName("Secret Access Key")
            .documentation(URI.create("http://docs.amazonwebservices.com/AWSEC2/latest/APIReference")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected EC2ApiMetadata(EC2ApiMetadataBuilder<?> builder) {
      super(builder);
   }

   public static class EC2ApiMetadataBuilder<B extends EC2ApiMetadataBuilder<B>> extends Builder<B> {

      @Override
      public EC2ApiMetadata build() {
         return new EC2ApiMetadata(this);
      }
   }

   private static class EC2ConcreteBuilder extends EC2ApiMetadataBuilder<EC2ConcreteBuilder> {

      @Override
      public EC2ApiMetadata build() {
         return new EC2ApiMetadata(this);
      }
   }

   private static EC2ConcreteBuilder builder() {
      return new EC2ConcreteBuilder();
   }

   @Override
   public EC2ApiMetadataBuilder<?> toBuilder() {
      return builder().fromApiMetadata(this);
   }
}