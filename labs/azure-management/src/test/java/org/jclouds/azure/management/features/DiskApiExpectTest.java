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
import org.jclouds.azure.management.parse.ListDisksTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Gérald Pereira
 */
@Test(groups = "unit", testName = "DiskApiExpectTest")
public class DiskApiExpectTest extends BaseAzureManagementApiExpectTest {

	private static final String DISK_NAME = "mydisk";
	
   HttpRequest list = HttpRequest.builder().method("GET")
                                 .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/disks")
                                 .addHeader("x-ms-version", "2012-03-01")
                                 .addHeader("Accept", "application/xml").build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/disks.xml", "application/xml")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getDiskApi().list().toString(), new ListDisksTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getDiskApi().list(), ImmutableSet.of());
   }

   HttpRequest delete = HttpRequest.builder().method("DELETE")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/disks/" + DISK_NAME)
            .addHeader("x-ms-version", "2012-03-01")
            .build();
   
   public void testDeleteWhenResponseIs2xx() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200).addHeader("x-ms-request-id", "fakerequestid").build();

      AzureManagementApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getDiskApi().delete(DISK_NAME);
   }

   public void testDeleteWhenResponseIs404() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getDiskApi().delete(DISK_NAME);
   }
}
