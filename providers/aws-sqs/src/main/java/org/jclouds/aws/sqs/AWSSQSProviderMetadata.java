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
package org.jclouds.aws.sqs;

import static org.jclouds.aws.domain.Region.AP_NORTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_2;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.SA_EAST_1;
import static org.jclouds.aws.domain.Region.US_EAST_1;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.aws.domain.Region.US_WEST_2;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;
import org.jclouds.sqs.SQSApiMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's Simple Queue Service
 * provider.
 * 
 * @author Adrian Cole
 */
public class AWSSQSProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromProviderMetadata(this));
   }
   
   public AWSSQSProviderMetadata() {
      super(builder());
   }

   public AWSSQSProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.putAll(Region.regionProperties());
      properties.setProperty(PROPERTY_REGION + "." + US_EAST_1 + ".endpoint",
            "https://sqs.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_1 + ".endpoint",
            "https://sqs.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_2 + ".endpoint",
            "https://sqs.us-west-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + SA_EAST_1 + ".endpoint",
            "https://sqs.sa-east-1.amazonaws.com");      
      properties.setProperty(PROPERTY_REGION + "." + EU_WEST_1 + ".endpoint",
            "https://sqs.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_1 + ".endpoint",
            "https://sqs.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_2 + ".endpoint",
            "https://sqs.ap-southeast-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_NORTHEAST_1 + ".endpoint",
            "https://sqs.ap-northeast-1.amazonaws.com");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-sqs")
         .name("Amazon Simple Queue Service")
         .endpoint("https://sqs.us-east-1.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/sqs"))
         .console(URI.create("https://console.aws.amazon.com/ec2/home"))
         .linkedServices("aws-ec2", "aws-rds", "aws-sqs", "aws-elb", "aws-iam","aws-cloudwatch", "aws-s3", "aws-simpledb")
         .iso3166Codes("US-VA", "US-CA", "BR-SP", "US-OR", "IE", "SG", "AU-NSW", "JP-13")
         .apiMetadata(new SQSApiMetadata())
         .defaultProperties(AWSSQSProviderMetadata.defaultProperties());
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
