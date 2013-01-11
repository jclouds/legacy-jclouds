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
package org.jclouds.aws.ec2.compute;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jclouds.aws.ec2.compute.internal.BaseAWSEC2ComputeServiceExpectTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
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

   public void testLaunchVPCSpotInstanceMissesVPCId() throws Exception {
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
      
      HttpResponse requestSpotInstancesResponse = HttpResponse.builder().statusCode(200)
                          .payload(payloadFromResourceWithContentType(
                                "/request_spot_instances-ebs.xml", MediaType.APPLICATION_XML)).build();
      
      HttpRequest describeSpotInstanceRequest = formSigner.filter(HttpRequest.builder().method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("Action", "DescribeSpotInstanceRequests")
                          .addFormParam("SpotInstanceRequestId.1", "sir-228e6406").build());

      HttpResponse describeSpotInstanceResponse = HttpResponse.builder().statusCode(200)
                          .payload(payloadFromResourceWithContentType(
                                "/request_spot_instances-ebs.xml", MediaType.APPLICATION_XML)).build();

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

      NodeMetadata node = Iterables.getOnlyElement(createsVPCSpotInstance.createNodesInGroup("demoGroup", 1, template));
      assertEquals(node.getId(), "us-east-1/sir-228e6406");
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
