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
package org.jclouds.aws.cloudwatch;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.cloudwatch.CloudWatchApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for Amazon's CloudWatch
 * provider.
*
* @author Adrian Cole
*/
public class AWSCloudWatchProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSCloudWatchProviderMetadata() {
      super(builder());
   }

   public AWSCloudWatchProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.putAll(Region.regionProperties());
      properties.setProperty(PROPERTY_REGION + "." + Region.US_EAST_1 + ".endpoint",
            "https://monitoring.us-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_1 + ".endpoint",
            "https://monitoring.us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.US_WEST_2 + ".endpoint",
            "https://monitoring.us-west-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.SA_EAST_1 + ".endpoint",
            "https://monitoring.sa-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.EU_WEST_1 + ".endpoint",
            "https://monitoring.eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_1 + ".endpoint",
            "https://monitoring.ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_SOUTHEAST_2 + ".endpoint",
            "https://monitoring.ap-southeast-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + Region.AP_NORTHEAST_1 + ".endpoint",
            "https://monitoring.ap-northeast-1.amazonaws.com");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-cloudwatch")
         .name("Amazon CloudWatch")
         .endpoint("https://monitoring.us-east-1.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/cloudwatch"))
         .console(URI.create("https://console.aws.amazon.com/cloudwatch/home"))
         .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
         .iso3166Codes("US-VA", "US-CA", "BR-SP", "US-OR", "IE", "SG", "AU-NSW", "JP-13")
               .apiMetadata(
                     new CloudWatchApiMetadata().toBuilder()
                           .version("2010-08-01").build())
         .defaultProperties(AWSCloudWatchProviderMetadata.defaultProperties());
      }

      @Override
      public AWSCloudWatchProviderMetadata build() {
         return new AWSCloudWatchProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
