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
package org.jclouds.aws.cloudwatch;

import java.net.URI;

import org.jclouds.cloudwatch.CloudWatchApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's CloudWatch
 * provider.
 * 
 * @author Adrian Cole
 */
public class AWSCloudWatchProviderMetadata extends BaseProviderMetadata {

   public AWSCloudWatchProviderMetadata() {
      this(builder()
            .id("aws-cloudwatch")
            .name("Amazon CloudWatch")
            .api(new CloudWatchApiMetadata())
            .homepage(URI.create("http://aws.amazon.com/cloudwatch"))
            .console(URI.create("https://console.aws.amazon.com/cloudwatch/home"))
            .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
            .iso3166Codes("US-VA", "US-CA", "BR-SP", "US-OR", "IE", "SG", "JP-13"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected AWSCloudWatchProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public AWSCloudWatchProviderMetadata build() {
         return new AWSCloudWatchProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
