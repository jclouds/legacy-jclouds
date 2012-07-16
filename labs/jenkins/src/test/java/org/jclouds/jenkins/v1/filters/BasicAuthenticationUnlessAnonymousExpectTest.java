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
package org.jclouds.jenkins.v1.filters;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.jenkins.v1.JenkinsApiMetadata;
import org.jclouds.jenkins.v1.JenkinsApi;
import org.jclouds.jenkins.v1.internal.BaseJenkinsApiExpectTest;
import org.jclouds.jenkins.v1.parse.ParseComputerViewTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BasicAuthenticationUnlessAnonymousExpectTest")
public class BasicAuthenticationUnlessAnonymousExpectTest extends BaseJenkinsApiExpectTest {
   
   public BasicAuthenticationUnlessAnonymousExpectTest(){
      identity = JenkinsApiMetadata.ANONYMOUS_IDENTITY;
   }
   
   public void testWhenIdentityIsAnonymousNoAuthorizationHeader() {
      HttpRequest getComputerView = HttpRequest
            .builder()
            .method("GET")
            .endpoint("http://localhost:8080/computer/api/json")
            .addHeader("Accept", "application/json").build();

      HttpResponse getComputerViewResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/computerview.json")).build();

      JenkinsApi apiWhenServersExist = requestSendsResponse(getComputerView, getComputerViewResponse);

      assertEquals(apiWhenServersExist.getComputerApi().getView().toString(),
            new ParseComputerViewTest().expected().toString());
   }
}
