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
package org.jclouds.jenkins.v1;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.jenkins.v1.internal.BaseJenkinsClientExpectTest;
import org.jclouds.jenkins.v1.parse.ParseNodeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JenkinsClientExpectTest")
public class JenkinsClientExpectTest extends BaseJenkinsClientExpectTest {

   public void testGetMasterWhenResponseIs2xx() {
      HttpRequest getMaster = HttpRequest
            .builder()
            .method("GET")
            .endpoint(URI.create("http://localhost:8080/api/json"))
            .headers(
                  ImmutableMultimap.<String, String> builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

      HttpResponse getMasterResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/master.json")).build();

      JenkinsClient clientWhenMasterExists = requestSendsResponse(getMaster, getMasterResponse);

      assertEquals(clientWhenMasterExists.getMaster().toString(),
            new ParseNodeTest().expected().toString());
   }
}
