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
package org.jclouds.snia.cdmi.v1.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.snia.cdmi.v1.CDMIClient;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIClientExpectTest;
import org.jclouds.snia.cdmi.v1.parse.ParseContainerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ContainerAsyncClientTest")
public class ContainerClientExpectTest extends BaseCDMIClientExpectTest {
   
   public void testGetContainerWhenResponseIs2xx() throws Exception {

      HttpRequest getContainer = HttpRequest.builder()
            .method("GET")
            .endpoint(URI.create("http://localhost:8080/MyContainer/"))
            .headers(ImmutableMultimap.<String, String> builder()
                        .put("X-CDMI-Specification-Version", "1.0.1")
                        .put("Accept", "application/cdmi-container")
                        .put("TID", "tenantId")
                        .put("Authorization", "Basic " + CryptoStreams.base64("username:password".getBytes()))
                        .build())
            .build();
      
      HttpResponse getContainerResponse = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/container.json"))
            .build();

      CDMIClient clientWhenContainersExist = requestSendsResponse(getContainer, getContainerResponse);

      assertEquals(
            clientWhenContainersExist.getContainerClient().getContainer("MyContainer"),
            new ParseContainerTest().expected());
   }
   
}
