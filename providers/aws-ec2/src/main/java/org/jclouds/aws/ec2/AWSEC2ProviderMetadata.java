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
package org.jclouds.aws.ec2;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_REGIONS;
import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import java.net.URI;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@ link org.jclouds.types.ProviderMetadata} for Amazon's
 * Elastic Compute Cloud (EC2) provider.
 *
 * @author Adrian Cole
 */
public class AWSEC2ProviderMetadata extends BaseProviderMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
   
   public AWSEC2ProviderMetadata() {
      super(builder());
   }

   public AWSEC2ProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      // sometimes, like in ec2, stop takes a very long time, perhaps
      // due to volume management. one example spent 2 minutes moving
      // from stopping->stopped state on an ec2 micro
      properties.setProperty(TIMEOUT_NODE_SUSPENDED, 120 * 1000 + "");
      properties.putAll(Region.regionProperties());
      // Amazon Linux, Amazon Windows, alestic, canonical, and rightscale
      properties.setProperty(PROPERTY_EC2_AMI_QUERY,
               "owner-id=137112412989,801119661308,063491364108,099720109477,411009282317;state=available;image-type=machine");
      // amis that work with the cluster instances
      properties.setProperty(PROPERTY_EC2_CC_REGIONS, Region.US_EAST_1);
      properties
               .setProperty(
                        PROPERTY_EC2_CC_AMI_QUERY,
                        "virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;state=available;image-type=machine;root-device-type=ebs");
      properties.setProperty(TEMPLATE, "osFamily=AMZN_LINUX,os64Bit=true");
      return properties;
   }
   
   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("aws-ec2")
         .name("Amazon Elastic Compute Cloud (EC2)")
         .apiMetadata(new AWSEC2ApiMetadata())
         .endpoint("https://ec2.us-east-1.amazonaws.com")
         .homepage(URI.create("http://aws.amazon.com/ec2"))
         .console(URI.create("https://console.aws.amazon.com/ec2/home"))
         .defaultProperties(AWSEC2ProviderMetadata.defaultProperties())
         .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
         .iso3166Codes("US-VA", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13");
      }

      @Override
      public AWSEC2ProviderMetadata build() {
         return new AWSEC2ProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(
            ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}