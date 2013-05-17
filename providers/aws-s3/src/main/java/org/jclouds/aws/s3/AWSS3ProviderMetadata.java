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
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.domain.Region.AP_NORTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_1;
import static org.jclouds.aws.domain.Region.AP_SOUTHEAST_2;
import static org.jclouds.aws.domain.Region.EU_WEST_1;
import static org.jclouds.aws.domain.Region.SA_EAST_1;
import static org.jclouds.aws.domain.Region.US_STANDARD;
import static org.jclouds.aws.domain.Region.US_WEST_1;
import static org.jclouds.aws.domain.Region.US_WEST_2;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for Amazon's Simple Storage Service
 * (S3) provider.
 * 
 * @author Adrian Cole
 */
public class AWSS3ProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSS3ProviderMetadata() {
      super(builder());
   }

   public AWSS3ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      properties.putAll(Region.regionPropertiesS3());
      properties.setProperty(PROPERTY_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_STANDARD + "." + ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_1 + "." + ENDPOINT, "https://s3-us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + US_WEST_2 + "." + ENDPOINT, "https://s3-us-west-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + SA_EAST_1 + "." + ENDPOINT, "https://s3-sa-east-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + EU_WEST_1 + "." + ENDPOINT, "https://s3-eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_1 + "." + ENDPOINT,
            "https://s3-ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_SOUTHEAST_2 + "." + ENDPOINT,
            "https://s3-ap-southeast-2.amazonaws.com");
      properties.setProperty(PROPERTY_REGION + "." + AP_NORTHEAST_1 + "." + ENDPOINT,
            "https://s3-ap-northeast-1.amazonaws.com");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-s3")
         .name("Amazon Simple Storage Service (S3)")
         .apiMetadata(new AWSS3ApiMetadata())
         .homepage(URI.create("http://aws.amazon.com/s3"))
         .console(URI.create("https://console.aws.amazon.com/s3/home"))
         .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
         .iso3166Codes("US", "US-CA", "US-OR", "BR-SP", "IE", "SG", "AU-NSW", "JP-13")
         .defaultProperties(AWSS3ProviderMetadata.defaultProperties());
      }

      @Override
      public AWSS3ProviderMetadata build() {
         return new AWSS3ProviderMetadata(this);
      }
      
      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
