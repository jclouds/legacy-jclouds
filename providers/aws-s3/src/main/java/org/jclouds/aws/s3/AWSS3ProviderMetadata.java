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

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for Amazon's Simple Storage Service
 * (S3) provider.
 * 
 * @author Adrian Cole
 */
public class AWSS3ProviderMetadata extends BaseProviderMetadata {

   public AWSS3ProviderMetadata() {
      this(builder()
            .id("aws-s3")
            .name("Amazon Simple Storage Service (S3)")
            .api(new AWSS3ApiMetadata())
            .homepage(URI.create("http://aws.amazon.com/s3"))
            .console(URI.create("https://console.aws.amazon.com/s3/home"))
            .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
            .iso3166Codes("US", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected AWSS3ProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public AWSS3ProviderMetadata build() {
         return new AWSS3ProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}