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
package org.jclouds.ec2.compute.internal;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.BeforeClass;

public abstract class BaseEC2ComputeServiceExpectTest extends BaseEC2ComputeServiceContextExpectTest<ComputeService> {
   protected String region;

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty(PROPERTY_REGIONS, region);
      properties.setProperty(provider + ".template", "osDescriptionMatches=.*fedora.*");
      return properties;
   }

   protected HttpRequest describeAvailabilityZonesRequest;
   protected HttpResponse describeAvailabilityZonesResponse;
   protected HttpRequest describeImagesRequest;
   protected HttpResponse describeImagesResponse;
   protected HttpRequest createKeyPairRequest;
   protected HttpResponse createKeyPairResponse;
   protected HttpRequest createSecurityGroupRequest;
   protected HttpResponse createSecurityGroupResponse;
   protected HttpRequest describeSecurityGroupRequest;
   protected HttpResponse describeSecurityGroupResponse;
   protected HttpRequest authorizeSecurityGroupIngressRequest22;
   protected HttpResponse authorizeSecurityGroupIngressResponse;
   protected HttpRequest authorizeSecurityGroupIngressRequestGroup;
   protected HttpRequest runInstancesRequest;
   protected HttpResponse runInstancesResponse;
   protected HttpRequest describeInstanceRequest;
   protected HttpResponse describeInstanceResponse;
   protected HttpRequest describeInstanceMultiIdsRequest;
   protected HttpResponse describeInstanceMultiIdsResponse;
   protected HttpRequest describeImageRequest;

   public BaseEC2ComputeServiceExpectTest() {
      region = "us-east-1";
   }
   
   @BeforeClass
   @Override
   protected void setupDefaultRequests() {
      super.setupDefaultRequests();
      describeAvailabilityZonesRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeAvailabilityZones").build());
      
      describeAvailabilityZonesResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/availabilityZones-" + region + ".xml", MediaType.APPLICATION_XML)).build();
      describeImagesRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeImages").build());
      
      describeImagesResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/describe_images.xml", MediaType.APPLICATION_XML)).build();
      
      createKeyPairRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "CreateKeyPair")
                          .addFormParam("KeyName", "jclouds#test#0").build());

      createKeyPairResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/create_keypair.xml", MediaType.APPLICATION_XML)).build();
      
      createSecurityGroupRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "CreateSecurityGroup")
                          .addFormParam("GroupDescription", "jclouds#test")
                          .addFormParam("GroupName", "jclouds#test").build());

      createSecurityGroupResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/created_securitygroup.xml", MediaType.APPLICATION_XML)).build();

      describeSecurityGroupRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeSecurityGroups")
                          .addFormParam("GroupName.1", "jclouds#test").build());
      
      describeSecurityGroupResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/new_securitygroup.xml", MediaType.APPLICATION_XML)).build();
      
      authorizeSecurityGroupIngressRequest22 = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("CidrIp", "0.0.0.0/0")
                          .addFormParam("FromPort", "22")
                          .addFormParam("ToPort", "22")
                          .addFormParam("GroupName", "jclouds#test")
                          .addFormParam("IpProtocol", "tcp").build());
      
      authorizeSecurityGroupIngressRequestGroup = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                          .addFormParam("SourceSecurityGroupName", "jclouds#test")
                          .addFormParam("SourceSecurityGroupOwnerId", "993194456877")
                          .addFormParam("GroupName", "jclouds#test").build());
      
      authorizeSecurityGroupIngressResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/authorize_securitygroup_ingress_response.xml", MediaType.APPLICATION_XML)).build();
      
      runInstancesRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "RunInstances")
                          .addFormParam("ImageId", "ami-be3adfd7")
                          .addFormParam("InstanceType", "m1.small")
                          .addFormParam("KeyName", "jclouds#test#0")
                          .addFormParam("MaxCount", "1")
                          .addFormParam("MinCount", "1")
                          .addFormParam("SecurityGroup.1", "jclouds#test").build());
      
      runInstancesResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/new_instance.xml", MediaType.APPLICATION_XML)).build();
      
      describeInstanceRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeInstances")
                          .addFormParam("InstanceId.1", "i-2baa5550").build());
      
      describeInstanceResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/describe_instances_running-1.xml", MediaType.APPLICATION_XML)).build();

      describeInstanceMultiIdsRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeInstances")
                          .addFormParam("InstanceId.1", "i-2baa5550")
                          .addFormParam("InstanceId.2", "i-abcd1234").build());
      
      describeInstanceMultiIdsResponse = 
               HttpResponse.builder().statusCode(200)
                           .payload(payloadFromResourceWithContentType(
                                 "/describe_instances_multiple.xml", MediaType.APPLICATION_XML)).build();

      //TODO: duplicate.. shouldn't need this
      describeImageRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("ImageId.1", "ami-aecd60c7")
                          .addFormParam("Action", "DescribeImages").build());
   }

   @Override
   public ComputeService apply(ComputeServiceContext input) {
      return input.getComputeService();
   }

}
