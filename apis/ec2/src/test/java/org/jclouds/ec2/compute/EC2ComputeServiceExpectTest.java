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
package org.jclouds.ec2.compute;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.blockUntilRunning;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.compute.internal.BaseEC2ComputeServiceExpectTest;
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
@Test(groups = "unit", testName = "EC2ComputeServiceExpectTest")
public class EC2ComputeServiceExpectTest extends BaseEC2ComputeServiceExpectTest {

   static String region = "us-east-1";
   
   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      properties.setProperty(PROPERTY_REGIONS, region);
      properties.setProperty(provider + ".template", "osDescriptionMatches=.*fedora.*");
      return properties;
   }

   private HttpRequest describeAvailabilityZonesRequest;
   private HttpResponse describeAvailabilityZonesResponse;
   private HttpRequest describeImagesRequest;
   private HttpResponse describeImagesResponse;
   private HttpRequest createKeyPairRequest;
   private HttpResponse createKeyPairResponse;
   private HttpRequest createSecurityGroupRequest;
   private HttpResponse createSecurityGroupResponse;
   private HttpRequest describeSecurityGroupRequest;
   private HttpResponse describeSecurityGroupResponse;
   private HttpRequest authorizeSecurityGroupIngressRequest22;
   private HttpResponse authorizeSecurityGroupIngressResponse;
   private HttpRequest authorizeSecurityGroupIngressRequestGroup;
   private HttpRequest runInstancesRequest;
   private HttpResponse runInstancesResponse;
   private HttpRequest describeInstanceRequest;
   private HttpResponse describeInstanceResponse;
   private HttpRequest describeImageRequest;

   public EC2ComputeServiceExpectTest() {
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

      //TODO: duplicate.. shouldn't need this
      describeImageRequest = 
               formSigner.filter(HttpRequest.builder()
                          .method("POST")
                          .endpoint("https://ec2." + region + ".amazonaws.com/")
                          .addHeader("Host", "ec2." + region + ".amazonaws.com")
                          .addFormParam("ImageId.1", "ami-aecd60c7")
                          .addFormParam("Action", "DescribeImages").build());
   }

   public void testCreateNodeWithGeneratedKeyPairAndOverriddenLoginUser() throws Exception {
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
            blockUntilRunning(false).overrideLoginUser("ec2-user")));
      assertEquals(node.getCredentials().getUser(), "ec2-user");
      System.out.println(node.getImageId());
      assertNotNull(node.getCredentials().getPrivateKey());
   }

   //FIXME - issue-1051
   @Test(enabled = false)
   public void testCreateNodeWithGeneratedKeyPairAndOverriddenLoginUserWithTemplateBuilder() throws Exception {
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

      NodeMetadata node = Iterables.getOnlyElement(
            apiThatCreatesNode.createNodesInGroup("test", 1,
            apiThatCreatesNode.templateBuilder().from("osDescriptionMatches=.*fedora.*,loginUser=ec2-user").build()));
      assertEquals(node.getCredentials().getUser(), "ec2-user");
      assertNotNull(node.getCredentials().getPrivateKey());
   }


}
