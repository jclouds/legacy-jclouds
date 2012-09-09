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

import static org.jclouds.azure.management.options.CreateHostedServiceOptions.Builder.description;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.internal.BaseAzureManagementApiExpectTest;
import org.jclouds.azure.management.parse.GetHostedServiceDetailsTest;
import org.jclouds.azure.management.parse.GetHostedServiceTest;
import org.jclouds.azure.management.parse.ListHostedServicesTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "HostedServiceApiExpectTest")
public class HostedServiceApiExpectTest extends BaseAzureManagementApiExpectTest {

   private static final String SERVICE_NAME = "myservice";
   HttpRequest list = HttpRequest.builder().method("GET")
                                 .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices")
                                 .addHeader("x-ms-version", "2012-03-01")
                                 .addHeader("Accept", "application/xml").build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/hostedservices.xml", "application")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getHostedServiceApi().list().toString(), new ListHostedServicesTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getHostedServiceApi().list(), ImmutableSet.of());
   }
   

   public void testCreateServiceWithLabelInLocationWhenResponseIs2xx() throws Exception {
      HttpRequest create = HttpRequest.builder().method("POST")
                                      .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices")
                                      .addHeader("x-ms-version", "2012-03-01")
                                      .payload(payloadFromResourceWithContentType("/create_hostedservice_location.xml", "application/xml")).build();
            
      HttpResponse createResponse = HttpResponse.builder()
                                                .addHeader("x-ms-request-id", "171f77920784404db208200702e59227")
                                                .statusCode(201).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(create, createResponse);

      assertEquals(
               apiWhenExist.getHostedServiceApi().createServiceWithLabelInLocation(SERVICE_NAME, "service mine",
                        "West US"), "171f77920784404db208200702e59227");
   }

   public void testCreateWithOptionalParamsWhenResponseIs2xx() throws Exception {
      HttpRequest create = HttpRequest.builder().method("POST")
               .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices")
               .addHeader("x-ms-version", "2012-03-01")
               .payload(payloadFromResourceWithContentType("/create_hostedservice_location_options.xml", "application/xml")).build();

      HttpResponse createResponse = HttpResponse.builder()
                                                .addHeader("x-ms-request-id", "171f77920784404db208200702e59227")
                                                .statusCode(201).build();
      
      AzureManagementApi apiWhenExist = requestSendsResponse(create, createResponse);
      
      assertEquals(
               apiWhenExist.getHostedServiceApi().createServiceWithLabelInLocation(SERVICE_NAME, "service mine",
                        "West US",
                        description("my description").extendedProperties(ImmutableMap.of("Role", "Production"))),
               "171f77920784404db208200702e59227");
   }
   
   HttpRequest get = HttpRequest.builder().method("GET")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices/" + SERVICE_NAME)
            .addHeader("x-ms-version", "2012-03-01")
            .addHeader("Accept", "application/xml").build();
   
   public void testGetWhenResponseIs2xx() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/hostedservice.xml", "application")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(get, getResponse);

      assertEquals(apiWhenExist.getHostedServiceApi().get(SERVICE_NAME).toString(), new GetHostedServiceTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(get, getResponse);

      assertNull(apiWhenDontExist.getHostedServiceApi().get(SERVICE_NAME));
   }
   
   HttpRequest getDetails = HttpRequest.builder().method("GET")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices/" + SERVICE_NAME + "?embed-detail=true")
            .addHeader("x-ms-version", "2012-03-01")
            .addHeader("Accept", "application/xml").build();
   
   public void testGetDetailsWhenResponseIs2xx() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/hostedservice_details.xml", "application")).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(getDetails, getResponse);

      assertEquals(apiWhenExist.getHostedServiceApi().getDetails(SERVICE_NAME).toString(), new GetHostedServiceDetailsTest().expected().toString());
   }

   public void testGetDetailsWhenResponseIs404() throws Exception {
      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(getDetails, getResponse);

      assertNull(apiWhenDontExist.getHostedServiceApi().getDetails(SERVICE_NAME));
   }
   
   HttpRequest delete = HttpRequest.builder().method("DELETE")
            .endpoint("https://management.core.windows.net/" + subscriptionId + "/services/hostedservices/" + SERVICE_NAME)
            .addHeader("x-ms-version", "2012-03-01")
            .build();
   
   public void testDeleteWhenResponseIs2xx() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder()
                                                .addHeader("x-ms-request-id", "171f77920784404db208200702e59227")
                                                .statusCode(200).build();

      AzureManagementApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      assertEquals(apiWhenExist.getHostedServiceApi().delete(SERVICE_NAME), "171f77920784404db208200702e59227");
   }

   public void testDeleteWhenResponseIs404() throws Exception {
      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      AzureManagementApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      assertNull(apiWhenDontExist.getHostedServiceApi().delete(SERVICE_NAME));
   }
}
