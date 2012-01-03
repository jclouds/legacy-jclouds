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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.BaseRestClientExpectTest.RegisterContext;
import org.testng.annotations.Test;

/**
 * 
 * Allows us to test a client via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "IntegrationTestClientExpectTest")
// only needed as IntegrationTestClient is not registered in rest.properties
@RegisterContext(sync = IntegrationTestClient.class, async = IntegrationTestAsyncClient.class)
public class IntegrationTestClientExpectTest extends BaseRestClientExpectTest<IntegrationTestClient> {

   public void testWhenResponseIs2xxExistsReturnsTrue() {

      IntegrationTestClient client = requestSendsResponse(HttpRequest.builder().method("HEAD").endpoint(
               URI.create("http://mock/objects/rabbit")).build(), HttpResponse.builder().statusCode(200).build());

      assertEquals(client.exists("rabbit"), true);

   }

   public void testWhenResponseIs404ExistsReturnsFalse() {

      IntegrationTestClient client = requestSendsResponse(HttpRequest.builder().method("HEAD").endpoint(
               URI.create("http://mock/objects/rabbit")).build(), HttpResponse.builder().statusCode(404).build());

      assertEquals(client.exists("rabbit"), false);

   }
}
