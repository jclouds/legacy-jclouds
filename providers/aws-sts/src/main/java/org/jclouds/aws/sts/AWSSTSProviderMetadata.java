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
package org.jclouds.aws.sts;

import java.net.URI;
import java.util.Properties;

import org.jclouds.sts.STSApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's STS
 * provider.
*
* @author Adrian Cole
*/
public class AWSSTSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSSTSProviderMetadata() {
      super(builder());
   }

   public AWSSTSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-sts")
         .name("Amazon STS")
         .endpoint("https://sts.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/iam/"))
         .console(URI.create("https://console.aws.amazon.com/iam/home"))
         .linkedServices("aws-ec2", "aws-elb", "aws-iam", "aws-sts", "aws-cloudwatch", "aws-s3", "aws-sqs", "aws-simpledb")
         .iso3166Codes("US-VA")
         .apiMetadata(new STSApiMetadata())
         .defaultProperties(AWSSTSProviderMetadata.defaultProperties());
      }

      @Override
      public AWSSTSProviderMetadata build() {
         return new AWSSTSProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
