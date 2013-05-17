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

import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.ec2.internal.BaseNovaEC2RestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "NovaEC2ElasticBlockStoreClientTest")
public class NovaEC2ElasticBlockStoreExpectClientTest extends BaseNovaEC2RestClientExpectTest {

   public void testDescribeVolumesWithNovaEC2Status() {
      ElasticBlockStoreClient client = requestsSendResponses(
            describeAvailabilityZonesRequest,
            describeAvailabilityZonesResponse,
            HttpRequest.builder().method("POST")
                  .endpoint("http://localhost:8773/services/Cloud/")
                  .addHeader("Host", "localhost:8773")
                  .payload(payloadFromStringWithContentType("Action=DescribeVolumes&Signature=AvRznSzGExM%2Buaj2JJj66wq4v4f%2BakicyLooRDtC0t0%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2009-04-04&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/nova_ec2_describe_volumes.xml")).build()
      ).getElasticBlockStoreServices();

      Set<Volume> expected = ImmutableSet.of(Volume
            .builder()
            .status(Volume.Status.AVAILABLE)
            .availabilityZone("nova")
            .region("nova")
            .id("vol-00000007")
            .size(1)
            .attachments(Attachment.builder().region("nova").build())
            .createTime(dateService.iso8601SecondsDateParse("2012-04-10T10:39:52Z"))
            .build());

      assertEquals(client.describeVolumesInRegion("nova"), expected);
   }

}
