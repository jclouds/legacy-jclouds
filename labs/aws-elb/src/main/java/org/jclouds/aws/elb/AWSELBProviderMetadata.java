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
package org.jclouds.aws.elb;

import java.net.URI;

import org.jclouds.elb.ELBApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's Elastic Load Balancing
 * provider.
 * 
 * @author Adrian Cole
 */
public class AWSELBProviderMetadata extends BaseProviderMetadata {

   public AWSELBProviderMetadata() {
      this(builder()
            .id("aws-elb")
            .name("Amazon Elastic Load Balancing")
            .api(new ELBApiMetadata())
            .homepage(URI.create("http://aws.amazon.com/elasticloadbalancing"))
            .console(URI.create("https://console.aws.amazon.com/ec2/home"))
            .linkedServices("aws-ec2","aws-elb", "aws-elb", "aws-s3", "aws-simpledb")
            .iso3166Codes("US-VA", "US-CA", "BR-SP", "US-OR", "IE", "SG", "JP-13"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected AWSELBProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public AWSELBProviderMetadata build() {
         return new AWSELBProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}