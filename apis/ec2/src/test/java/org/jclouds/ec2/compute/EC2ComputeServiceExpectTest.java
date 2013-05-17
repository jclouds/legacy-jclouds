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
package org.jclouds.ec2.compute;
import static org.jclouds.ec2.compute.options.EC2TemplateOptions.Builder.blockUntilRunning;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
      requestResponseMap.put(describeInstanceMultiIdsRequest, describeInstanceMultiIdsResponse);
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
      requestResponseMap.put(describeInstanceMultiIdsRequest, describeInstanceMultiIdsResponse);
      requestResponseMap.put(describeImageRequest, describeImagesResponse);

      ComputeService apiThatCreatesNode = requestsSendResponses(requestResponseMap.build());

      NodeMetadata node = Iterables.getOnlyElement(
            apiThatCreatesNode.createNodesInGroup("test", 1,
            apiThatCreatesNode.templateBuilder().from("osDescriptionMatches=.*fedora.*,loginUser=ec2-user").build()));
      assertEquals(node.getCredentials().getUser(), "ec2-user");
      assertNotNull(node.getCredentials().getPrivateKey());
   }

}
