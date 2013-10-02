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
package org.jclouds.aws.ec2.compute;

import static org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions.Builder.blockUntilRunning;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.ec2.compute.internal.BaseAWSEC2ComputeServiceExpectTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;

/**
 * Tests the compute service abstraction of the EC2 api.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSEC2ComputeServiceExpectTest")
public class AWSEC2ComputeServiceExpectTest extends BaseAWSEC2ComputeServiceExpectTest {

   private HttpResponse requestSpotInstancesResponse;
   private HttpRequest describeSpotInstanceRequest;
   private HttpResponse describeSpotInstanceResponse;

   @BeforeClass
   @Override
   protected void setupDefaultRequests() {
      super.setupDefaultRequests();
      requestSpotInstancesResponse = HttpResponse.builder().statusCode(200)
                       .payload(payloadFromResourceWithContentType(
                             "/request_spot_instances-ebs.xml", MediaType.APPLICATION_XML)).build();
   
      describeSpotInstanceRequest = formSigner.filter(HttpRequest.builder().method("POST")
                       .endpoint("https://ec2." + region + ".amazonaws.com/")
                       .addHeader("Host", "ec2." + region + ".amazonaws.com")
                       .addFormParam("Action", "DescribeSpotInstanceRequests")
                       .addFormParam("SpotInstanceRequestId.1", "sir-228e6406").build());

      describeSpotInstanceResponse = HttpResponse.builder().statusCode(200)
                       .payload(payloadFromResourceWithContentType(
                             "/request_spot_instances-ebs.xml", MediaType.APPLICATION_XML)).build();
   }

   public void testLaunchVPCSpotInstanceSubnetId() throws Exception {
      HttpRequest requestSpotInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "RequestSpotInstances")
                          .addFormParam("InstanceCount", "1")
                          .addFormParam("LaunchSpecification.ImageId", "ami-be3adfd7")
                          .addFormParam("LaunchSpecification.InstanceType", "m1.small")
                          .addFormParam("LaunchSpecification.KeyName", "Demo")
                          .addFormParam("LaunchSpecification.Placement.AvailabilityZone", "us-east-1a")
                          .addFormParam("LaunchSpecification.SubnetId", "subnet-xyz")
                          .addFormParam("LaunchSpecification.UserData", "I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK")
                          .addFormParam("SpotPrice", "1.0").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(requestSpotInstancesRequest, requestSpotInstancesResponse);
      requestResponseMap.put(describeSpotInstanceRequest, describeSpotInstanceResponse);

      ComputeService createsVPCSpotInstance = requestsSendResponses(requestResponseMap.build());

      Template template = createsVPCSpotInstance.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(1f).subnetId("subnet-xyz").keyPair("Demo").blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(createsVPCSpotInstance.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");
   }

   String iamInstanceProfileArn = "arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver";

   public void testLaunchSpotInstanceIAMInstanceProfileArn() throws Exception {
      HttpRequest requestSpotInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "RequestSpotInstances")
                          .addFormParam("InstanceCount", "1")
                          .addFormParam("LaunchSpecification.IamInstanceProfile.Arn", iamInstanceProfileArn)
                          .addFormParam("LaunchSpecification.ImageId", "ami-be3adfd7")
                          .addFormParam("LaunchSpecification.InstanceType", "m1.small")
                          .addFormParam("LaunchSpecification.Placement.AvailabilityZone", "us-east-1a")
                          .addFormParam("LaunchSpecification.SecurityGroup.1", "jclouds#test")
                          .addFormParam("LaunchSpecification.UserData", "I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK")
                          .addFormParam("SpotPrice", "1.0").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(requestSpotInstancesRequest, requestSpotInstancesResponse);
      requestResponseMap.put(describeSpotInstanceRequest, describeSpotInstanceResponse);

      ComputeService createsSpotInstance = requestsSendResponses(requestResponseMap.build());

      Template template = createsSpotInstance.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(1f).iamInstanceProfileArn(iamInstanceProfileArn)
            .noKeyPair().blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(createsSpotInstance.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");
   }

   public void testLaunchSpotInstanceIAMInstanceProfileName() throws Exception {
      HttpRequest requestSpotInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "RequestSpotInstances")
                          .addFormParam("InstanceCount", "1")
                          .addFormParam("LaunchSpecification.IamInstanceProfile.Name", "Webserver")
                          .addFormParam("LaunchSpecification.ImageId", "ami-be3adfd7")
                          .addFormParam("LaunchSpecification.InstanceType", "m1.small")
                          .addFormParam("LaunchSpecification.Placement.AvailabilityZone", "us-east-1a")
                          .addFormParam("LaunchSpecification.SecurityGroup.1", "jclouds#test")
                          .addFormParam("LaunchSpecification.UserData", "I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK")
                          .addFormParam("SpotPrice", "1.0").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(requestSpotInstancesRequest, requestSpotInstancesResponse);
      requestResponseMap.put(describeSpotInstanceRequest, describeSpotInstanceResponse);

      ComputeService createsSpotInstance = requestsSendResponses(requestResponseMap.build());

      Template template = createsSpotInstance.templateBuilder().locationId("us-east-1a").build();

      template.getOptions().as(AWSEC2TemplateOptions.class).spotPrice(1f).iamInstanceProfileName("Webserver")
            .noKeyPair().blockUntilRunning(false);

      NodeMetadata node = Iterables.getOnlyElement(createsSpotInstance.createNodesInGroup("test", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");
   }

   public void testCreateNodeWithIAMInstanceProfileArn() throws Exception {
      HttpRequest runInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
            .endpoint("https://ec2." + region + ".amazonaws.com/")
            .addHeader("Host", "ec2." + region + ".amazonaws.com")
            .addFormParam("Action", "RunInstances")
            .addFormParam("IamInstanceProfile.Arn", iamInstanceProfileArn)
            .addFormParam("ImageId", "ami-be3adfd7")
            .addFormParam("InstanceType", "m1.small")
            .addFormParam("MaxCount", "1")
            .addFormParam("MinCount", "1")
            .addFormParam("SecurityGroup.1", "jclouds#test")
            .addFormParam("UserData", "I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runInstancesRequest, runInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
            blockUntilRunning(false).iamInstanceProfileArn(iamInstanceProfileArn).noKeyPair()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");
   }

   public void testCreateNodeWithIAMInstanceProfileName() throws Exception {
      HttpRequest runInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
            .endpoint("https://ec2." + region + ".amazonaws.com/")
            .addHeader("Host", "ec2." + region + ".amazonaws.com")
            .addFormParam("Action", "RunInstances")
            .addFormParam("IamInstanceProfile.Name", "Webserver")
            .addFormParam("ImageId", "ami-be3adfd7")
            .addFormParam("InstanceType", "m1.small")
            .addFormParam("MaxCount", "1")
            .addFormParam("MinCount", "1")
            .addFormParam("SecurityGroup.1", "jclouds#test")
            .addFormParam("UserData", "I2Nsb3VkLWNvbmZpZwpyZXBvX3VwZ3JhZGU6IG5vbmUK").build());

      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(runInstancesRequest, runInstancesResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(apiThatCreatesNode.createNodesInGroup("test", 1,
            blockUntilRunning(false).iamInstanceProfileName("Webserver").noKeyPair()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");
   }

   public void testListNodesWhereImageDoesntExist() throws Exception {
      HttpRequest describeInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
                           .endpoint("https://ec2." + region + ".amazonaws.com/")
                           .addHeader("Host", "ec2." + region + ".amazonaws.com")
                           .addFormParam("Action", "DescribeInstances").build());

      HttpRequest describeSpotInstancesRequest = formSigner.filter(HttpRequest.builder().method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeSpotInstanceRequests").build());

      HttpResponse noSpotInstancesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromStringWithContentType(
                  "<DescribeSpotInstanceRequestsResponse><spotInstanceRequestSet></spotInstanceRequestSet></DescribeSpotInstanceRequestsResponse>",
                  MediaType.APPLICATION_XML)).build();
      
      HttpResponse noImagesResponse = HttpResponse.builder().statusCode(200)
                          .payload(payloadFromStringWithContentType(
                                 "<DescribeImagesResponse><imagesSet></imagesSet></DescribeImagesResponse>",
                                 MediaType.APPLICATION_XML)).build();
      
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, noImagesResponse);
      requestResponseMap.put(describeInstancesRequest, describeInstanceResponse);
      requestResponseMap.put(describeSpotInstancesRequest, noSpotInstancesResponse);

      ComputeService listsWithoutImages = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(listsWithoutImages.listNodesDetailsMatching(NodePredicates.all()));
      assertEquals(node.getId(), "us-east-1/i-2baa5550");
   }
}
