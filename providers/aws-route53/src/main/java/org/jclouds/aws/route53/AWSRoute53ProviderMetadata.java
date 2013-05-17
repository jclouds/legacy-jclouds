/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.route53;

import java.net.URI;
import java.util.Properties;

import org.jclouds.route53.Route53ApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's Route53
 * provider.
*
* @author Adrian Cole
*/
public class AWSRoute53ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSRoute53ProviderMetadata() {
      super(builder());
   }

   public AWSRoute53ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-route53")
         .name("Amazon Route53")
         .endpoint("https://route53.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/route53/"))
         .console(URI.create("https://console.aws.amazon.com/route53/home"))
         .linkedServices("aws-ec2", "aws-elb", "aws-iam", "aws-route53", "aws-sts", "aws-cloudwatch", "aws-s3",
                     "aws-sqs", "aws-simpledb")
         .iso3166Codes("US-VA")
         .apiMetadata(new Route53ApiMetadata())
         .defaultProperties(AWSRoute53ProviderMetadata.defaultProperties());
      }

      @Override
      public AWSRoute53ProviderMetadata build() {
         return new AWSRoute53ProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
