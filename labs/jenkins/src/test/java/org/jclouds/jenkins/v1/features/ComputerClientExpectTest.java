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
package org.jclouds.jenkins.v1.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.jenkins.v1.JenkinsClient;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientExpectTest;
import org.jclouds.jenkins.v1.parse.ParseComputerTest;
import org.jclouds.jenkins.v1.parse.ParseComputerViewTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ComputerClientExpectTest")
public class ComputerClientExpectTest extends BaseJenkinsClientExpectTest {

   public void testGetComputerViewWhenResponseIs2xx() {
      HttpRequest getComputerView = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("http://localhost:8080/computer/api/json"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

      HttpResponse getComputerViewResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/computerview.json")).build();

      JenkinsClient clientWhenServersExist = requestSendsResponse(getComputerView, getComputerViewResponse);

      assertEquals(clientWhenServersExist.getComputerClient().getView().toString(),
            new ParseComputerViewTest().expected().toString());
   }
   
   public void testGetComputerWhenResponseIs2xx() {
      HttpRequest getComputer = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("http://localhost:8080/computer/Ruboto/api/json"))
            .headers(
                  ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

      HttpResponse getComputerResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/computer.json")).build();

      JenkinsClient clientWhenServersExist = requestSendsResponse(getComputer, getComputerResponse);

      assertEquals(clientWhenServersExist.getComputerClient().get("Ruboto").toString(),
            new ParseComputerTest().expected().toString());
   }
}
