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

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.internal.BaseAzureManagementApiExpectTest;
import org.jclouds.azure.management.parse.ListLocationsTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LocationApiExpectTest")
public class LocationApiExpectTest extends BaseAzureManagementApiExpectTest {

   HttpRequest list = HttpRequest.builder()
                                 .method("GET")
                                 .endpoint("https://management.core.windows.net/" + subscriptionId + "/locations")
                                 .addHeader("x-ms-version", "2012-03-01")
                                 .addHeader("Accept", "application/xml").build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/locations.xml", "application")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getLocationApi().list().toString(), new ListLocationsTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getLocationApi().list(), ImmutableSet.of());
   }
}
