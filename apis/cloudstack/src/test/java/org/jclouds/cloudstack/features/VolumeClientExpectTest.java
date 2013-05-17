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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.testng.Assert.assertNotNull;

/**
* Test the CloudStack VolumeClient
*
* @author Adam Lowe
*/
@Test(groups = "unit", testName = "VolumeClientExpectTest")
public class VolumeClientExpectTest extends BaseCloudStackExpectTest<VolumeClient> {

   public void testCreateVolumeFromCustomDiskOffering() throws NoSuchAlgorithmException, CertificateException {
      VolumeClient client = requestSendsResponse(
              HttpRequest.builder()
                      .method("GET")
                      .endpoint(
                              URI.create("http://localhost:8080/client/api?response=json&" +
                                      "command=createVolume&name=VolumeClientExpectTest-jclouds-volume&diskofferingid=0473f5dd-bca5-4af4-a9b6-db9e8a88a2f6&zoneid=6f9a2921-b22a-4149-8b71-6ffc275a2177&size=1&apiKey=identity&signature=Y4%2BmdvhS/jlKRNSJ3nQqrjwg1CY%3D"))
                      .addHeader("Accept", "application/json")
                      .build(),
              HttpResponse.builder()
                      .statusCode(200)
                      .payload(payloadFromResource("/queryasyncjobresultresponse-createvolume.json"))
                      .build());
      
      AsyncCreateResponse response = client.createVolumeFromCustomDiskOfferingInZone("VolumeClientExpectTest-jclouds-volume", "0473f5dd-bca5-4af4-a9b6-db9e8a88a2f6", "6f9a2921-b22a-4149-8b71-6ffc275a2177", 1);
      assertNotNull(response);
   }

   @Override
   protected VolumeClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getVolumeClient();
   }
}
