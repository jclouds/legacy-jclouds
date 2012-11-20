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
package org.jclouds.aws.iam;

import java.net.URI;
import java.util.Properties;

import org.jclouds.iam.IAMApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's IAM
 * provider.
*
* @author Adrian Cole
*/
public class AWSIAMProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSIAMProviderMetadata() {
      super(builder());
   }

   public AWSIAMProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-iam")
         .name("Amazon IAM")
         .endpoint("https://iam.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/iam"))
         .console(URI.create("https://console.aws.amazon.com/iam/home"))
         .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
         .iso3166Codes("US-VA", "US-CA", "BR-SP", "US-OR", "IE", "SG", "AU-NSW", "JP-13")
         .apiMetadata(new IAMApiMetadata())
         .defaultProperties(AWSIAMProviderMetadata.defaultProperties());
      }

      @Override
      public AWSIAMProviderMetadata build() {
         return new AWSIAMProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
