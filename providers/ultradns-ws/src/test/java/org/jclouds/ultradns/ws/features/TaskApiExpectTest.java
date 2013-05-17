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
package org.jclouds.ultradns.ws.features;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.jclouds.ultradns.ws.parse.GetAllTasksResponseTest;
import org.jclouds.ultradns.ws.parse.GetStatusForTaskResponseResponseTest;
import org.testng.annotations.Test;
/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TaskApiExpectTest")
public class TaskApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest runTest = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/run_test.xml", "application/xml")).build();

   HttpResponse runTestResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/taskid.xml", "application/xml")).build();

   public void testRunTestWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(runTest, runTestResponse);

      assertEquals(success.getTaskApi().runTest("foo").toString(), "8d7a1725-4f4a-4b70-affa-f01dcce1526e");
   }

   HttpRequest get = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/get_task.xml", "application/xml")).build();

   HttpResponse getResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/task.xml", "application/xml")).build();

   public void testGetWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(get, getResponse);

      assertEquals(
            success.getTaskApi().get("0b40c7dd-748d-4c49-8506-26f0c7d2ea9c").toString(),
            new GetStatusForTaskResponseResponseTest().expected().toString());
   }
   
   HttpResponse taskDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/task_doesnt_exist.xml")).build();
   
   public void testGetWhenResponseError2401() {
      UltraDNSWSApi notFound = requestSendsResponse(get, taskDoesntExist);
      assertNull(notFound.getTaskApi().get("0b40c7dd-748d-4c49-8506-26f0c7d2ea9c"));
   }

   HttpRequest clear = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/clear_task.xml", "application/xml")).build();

   HttpResponse clearResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/task.xml", "application/xml")).build();

   public void testClearWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(clear, clearResponse);
      success.getTaskApi().clear("0b40c7dd-748d-4c49-8506-26f0c7d2ea9c");
   }

   public void testClearWhenResponseError2401() {
      UltraDNSWSApi notFound = requestSendsResponse(clear, taskDoesntExist);
      notFound.getTaskApi().clear("0b40c7dd-748d-4c49-8506-26f0c7d2ea9c");
   }
   
   HttpRequest list = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/list_tasks.xml", "application/xml")).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tasks.xml", "application/xml")).build();

   public void testListWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(list, listResponse);

      assertEquals(
            success.getTaskApi().list().toString(),
            new GetAllTasksResponseTest().expected().toString());
   }
}
