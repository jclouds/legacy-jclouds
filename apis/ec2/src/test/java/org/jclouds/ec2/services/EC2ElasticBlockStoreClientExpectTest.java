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
package org.jclouds.ec2.services;

import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.internal.BaseEC2ClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "EC2ElasticBlockStoreClientExpectTest")
public class EC2ElasticBlockStoreClientExpectTest extends BaseEC2ClientExpectTest<EC2Client> {
   Volume creating = Volume.builder()
                           .id("vol-2a21e543")
                           .status(Volume.Status.CREATING)
                           .availabilityZone("us-east-1a")
                           .region("us-east-1")
                           .id("vol-2a21e543")
                           .size(1)
                           .createTime(dateService.iso8601DateParse("2009-12-28T05:42:53.000Z"))
                           .build();
   
   public void testCreateVolumeInAvailabilityZone() {
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse>builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);
      builder.put(
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://ec2.us-east-1.amazonaws.com/")
                       .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType("Action=CreateVolume&AvailabilityZone=us-east-1a&Signature=FB5hTZHKSAuiygoafIdJh1EnfTu0ogC2VfRQOar85mg%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Size=4&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity", "application/x-www-form-urlencoded")).build(),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResource("/created_volume.xml")).build());
      
      ElasticBlockStoreClient client = requestsSendResponses(builder.build()).getElasticBlockStoreServices();

      assertEquals(client.createVolumeInAvailabilityZone("us-east-1a", 4), creating);
   }
   
   public void testCreateVolumeFromSnapshotInAvailabilityZoneEuSetsCorrectEndpoint() {
      String region = "eu-west-1";
      
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse>builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);
      builder.put(
            formSigner.filter(HttpRequest.builder()
                                         .method("POST")
                                         .endpoint("https://ec2." + region + ".amazonaws.com/")
                                         .addHeader("Host", "ec2." + region + ".amazonaws.com")
                                         .addFormParam("Action", "CreateVolume")
                                         .addFormParam("AvailabilityZone", "eu-west-1a")
                                         .addFormParam("Size", "1")
                                         .addFormParam("SnapshotId", "snap-8b7ffbdd").build()),
            HttpResponse.builder()
                        .statusCode(200)
                        .payload(payloadFromResource("/created_volume.xml")).build());
      
      ElasticBlockStoreClient client = requestsSendResponses(builder.build()).getElasticBlockStoreServices();

      assertEquals(client.createVolumeFromSnapshotInAvailabilityZone(region + "a", 1, "snap-8b7ffbdd"), creating.toBuilder().region(region).build());
   }
}
