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
package org.jclouds.ec2.compute.extensions;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.ec2.compute.internal.BaseEC2ComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.Futures;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "EC2ImageExtensionExpectTest")
public class EC2ImageExtensionExpectTest extends BaseEC2ComputeServiceExpectTest {

   public void testCreateImage() {
      Builder<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder();
      requestResponseMap.put(describeRegionsRequest, describeRegionsResponse);
      requestResponseMap.put(describeAvailabilityZonesRequest, describeAvailabilityZonesResponse);
      requestResponseMap.put(describeImagesRequest, describeImagesResponse);
      requestResponseMap.put(createKeyPairRequest, createKeyPairResponse);
      requestResponseMap.put(createSecurityGroupRequest, createSecurityGroupResponse);
      requestResponseMap.put(describeSecurityGroupRequest, describeSecurityGroupResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequest22, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(authorizeSecurityGroupIngressRequestGroup, authorizeSecurityGroupIngressResponse);
      requestResponseMap.put(describeInstanceRequest, describeInstanceResponse);
      
      HttpRequest createImageRequest = formSigner.filter(HttpRequest.builder().method("POST")
                       .endpoint("https://ec2." + region + ".amazonaws.com/")
                       .addHeader("Host", "ec2." + region + ".amazonaws.com")
                       .addFormParam("Action", "CreateImage")
                       .addFormParam("InstanceId", "i-2baa5550")
                       .addFormParam("Name", "test").build());

      HttpResponse createImageResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromStringWithContentType(
            "<CreateImageResponse><imageId>ami-be3adfd7</imageId></CreateImageResponse>", MediaType.APPLICATION_XML)).build();

      requestResponseMap.put(createImageRequest, createImageResponse);

      HttpRequest describeImageRequest = formSigner.filter(HttpRequest.builder().method("POST")
                       .endpoint("https://ec2." + region + ".amazonaws.com/")
                       .addHeader("Host", "ec2." + region + ".amazonaws.com")
                       .addFormParam("Action", "DescribeImages")
                       .addFormParam("ImageId.1", "ami-be3adfd7").build());

      requestResponseMap.put(describeImageRequest, describeImagesResponse);

      ImageExtension apiThatCreatesImage = requestsSendResponses(requestResponseMap.build()).getImageExtension().get();
      
      ImageTemplate newImageTemplate = apiThatCreatesImage.buildImageTemplateFromNode("test", "us-east-1/i-2baa5550");

      Image image = Futures.getUnchecked(apiThatCreatesImage.createImage(newImageTemplate));
      assertEquals(image.getId(), "us-east-1/ami-be3adfd7");
   }

}
