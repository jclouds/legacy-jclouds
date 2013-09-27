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
package org.jclouds.aws.ec2.compute.internal;

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.aws.ec2.config.AWSEC2HttpApiModule;
import org.jclouds.date.DateService;
import org.jclouds.ec2.compute.internal.BaseEC2ComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresHttpApi;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests the compute service abstraction of the EC2 api.
 * 
 * @author Adrian Cole
 */
public abstract class BaseAWSEC2ComputeServiceExpectTest extends BaseEC2ComputeServiceExpectTest {

   protected HttpRequest describeSecurityGroupByIdRequest;

   public BaseAWSEC2ComputeServiceExpectTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      // zero out cluster image query for now
      properties.setProperty(PROPERTY_EC2_CC_AMI_QUERY, "");
      properties.setProperty(PROPERTY_EC2_GENERATE_INSTANCE_NAMES, "false");
      return properties;
   }

   @BeforeClass
   @Override
   protected void setupDefaultRequests() {
      super.setupDefaultRequests();

      describeSecurityGroupByIdRequest =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2." + region + ".amazonaws.com/")
                      .addHeader("Host", "ec2." + region + ".amazonaws.com")
                      .addFormParam("Action", "DescribeSecurityGroups")
                      .addFormParam("GroupId.1", "sg-3c6ef654").build());

      authorizeSecurityGroupIngressRequestGroup =
              formSigner.filter(HttpRequest.builder()
                      .method("POST")
                      .endpoint("https://ec2." + region + ".amazonaws.com/")
                      .addHeader("Host", "ec2." + region + ".amazonaws.com")
                      .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                      .addFormParam("SourceSecurityGroupId", "sg-3c6ef654")
                      .addFormParam("SourceSecurityGroupOwnerId", "993194456877")
                      .addFormParam("GroupName", "jclouds#test").build());

      authorizeSecurityGroupIngressRequest22 = 
         formSigner.filter(HttpRequest.builder()
                           .method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                           .addFormParam("GroupId", "sg-3c6ef654")
                           .addFormParam("IpPermissions.0.FromPort", "22")
                           .addFormParam("IpPermissions.0.ToPort", "22")
                           .addFormParam("IpPermissions.0.IpRanges.0.CidrIp", "0.0.0.0/0")
                           .addFormParam("IpPermissions.0.IpProtocol", "tcp")
                           .addFormParam("IpPermissions.1.FromPort", "0")
                           .addFormParam("IpPermissions.1.ToPort", "65535")
                           .addFormParam("IpPermissions.1.Groups.0.GroupId", "sg-3c6ef654")
                           .addFormParam("IpPermissions.1.Groups.0.UserId", "993194456877")
                           .addFormParam("IpPermissions.1.IpProtocol", "tcp")
                           .addFormParam("IpPermissions.2.FromPort", "0")
                           .addFormParam("IpPermissions.2.ToPort", "65535")
                           .addFormParam("IpPermissions.2.Groups.0.GroupId", "sg-3c6ef654")
                           .addFormParam("IpPermissions.2.Groups.0.UserId", "993194456877")
                           .addFormParam("IpPermissions.2.IpProtocol", "udp")
                           .build());

      describeImagesRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeImages")
                          .addFormParam("Filter.1.Name", "owner-id")
                          .addFormParam("Filter.1.Value.1", "137112412989")
                          .addFormParam("Filter.1.Value.2", "801119661308")
                          .addFormParam("Filter.1.Value.3", "063491364108")
                          .addFormParam("Filter.1.Value.4", "099720109477")
                          .addFormParam("Filter.1.Value.5", "411009282317")
                          .addFormParam("Filter.2.Name", "state")
                          .addFormParam("Filter.2.Value.1", "available")
                          .addFormParam("Filter.3.Name", "image-type")
                          .addFormParam("Filter.3.Value.1", "machine").build());
   }

   @ConfiguresHttpApi
   protected static class TestAWSEC2HttpApiModule extends AWSEC2HttpApiModule {

      @Override
      protected void configure() {
         super.configure();
         // predicatable node names
         final AtomicInteger suffix = new AtomicInteger();
         bind(new TypeLiteral<Supplier<String>>() {
         }).toInstance(new Supplier<String>() {

            @Override
            public String get() {
               return suffix.getAndIncrement() + "";
            }

         });
      }

      @Override
      @Provides
      protected String provideTimeStamp(DateService dateService) {
         return CONSTANT_DATE;
      }
   }

   @Override
   protected Module createModule() {
      return new TestAWSEC2HttpApiModule();
   }
}
