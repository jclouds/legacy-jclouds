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
package org.jclouds.openstack.nova.ec2.services;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.ec2.internal.BaseNovaEC2RestClientExpectTest;
import org.testng.annotations.Test;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "NovaEC2ElasticBlockStoreClientTest")
public class NovaEC2AMIClientExpectTest extends BaseNovaEC2RestClientExpectTest {

   public void testDescribeImagesWithNonMachineTypes() {
      AMIClient client = requestsSendResponses(
            describeAvailabilityZonesRequest,
            describeAvailabilityZonesResponse,
            HttpRequest.builder().method("POST")
                  .endpoint("http://localhost:8773/services/Cloud/")
                  .addHeader("Host", "localhost:8773")
                  .payload(payloadFromStringWithContentType("Action=DescribeImages&Signature=Z3q3jSutwlfgvbcINT0Ed3AjrjxM4WMvQloXu/1kd40%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2009-04-04&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/nova_ec2_images_with_ramdisk.xml")).build()
      ).getAMIServices();

      Set<? extends Image> images = client.describeImagesInRegion("nova");
      
      assertEquals(images.size(), 1);
      
   }

}
