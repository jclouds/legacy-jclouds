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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.azure.management.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.internal.BaseAzureManagementApiExpectTest;
import org.jclouds.azure.management.parse.GetOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "OperationApiExpectTest")
public class OperationApiExpectTest extends BaseAzureManagementApiExpectTest {
   private static final String REQUEST_ID ="request-id";

   HttpRequest get = HttpRequest.builder().method("GET")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/operations/" + REQUEST_ID)
            .addHeader("x-ms-version", "2012-03-01")
            .addHeader("Accept", "application/xml").build();
   
   public void testGetWhenResponseIs2xx() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/operation.xml", "application")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(get, getResponse);

      assertEquals(apiWhenExist.getOperationApi().get(REQUEST_ID).toString(), new GetOperationTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(get, getResponse);

      assertNull(apiWhenDontExist.getOperationApi().get(REQUEST_ID));
   }

}
