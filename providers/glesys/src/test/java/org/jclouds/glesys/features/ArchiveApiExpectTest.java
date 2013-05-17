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
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.glesys.internal.BaseGleSYSApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing of {@code ArchiveAsyncApi}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ArchiveApiExpectTest")
public class ArchiveApiExpectTest extends BaseGleSYSApiExpectTest {

   public void testListArchivesWhenReponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_list.json")).build())
            .getArchiveApi();

      Set<Archive> expected = ImmutableSet.of(
            Archive.builder().username("xxxxx_test1").freeSize("20 GB").totalSize("30 GB").locked(false).build());

      assertEquals(api.list().toSet(), expected);
   }

   public void testListArchivesWhenResponseIs4xxReturnsEmpty() {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveApi();

      assertTrue(api.list().isEmpty());
   }

   public void testArchiveDetailsWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "xxxxxx_test1").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_details.json")).build())
            .getArchiveApi();

      assertEquals(api.get("xxxxxx_test1"), detailsInArchiveDetails());
   }

   private Archive detailsInArchiveDetails() {
      return Archive.builder().username("xxxxxx_test1").freeSize("30 GB").totalSize("30 GB").locked(false).build();
   }

   public void testArchiveDetailsWhenResponseIs4xxReturnsNull() {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "xxxxxx_test1").build(),
            HttpResponse.builder().statusCode(404).build())
            .getArchiveApi();
      assertNull(api.get("xxxxxx_test1"));
   }

   public void testCreateArchiveWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/create/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParams(ImmutableMultimap.<String, String>builder()
                             .put("username", "xxxxxx_test1")
                             .put("password", "somepass")
                             .put("size", "5").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_details.json")).build()).getArchiveApi();
      assertEquals(api.createWithCredentialsAndSize("xxxxxx_test1", "somepass", 5), detailsInArchiveDetails());
   }

   public void testDeleteArchiveWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "xxxxxx_test1").build(),
            HttpResponse.builder().statusCode(200).build()).getArchiveApi();

      api.delete("xxxxxx_test1");
   }

   @Test(expectedExceptions = {HttpResponseException.class})
   public void testDeleteArchiveWhenResponseIs4xxThrows() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/delete/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "xxxxxx_test1").build(),
            HttpResponse.builder().statusCode(402).build()).getArchiveApi();
      api.delete("xxxxxx_test1");
   }

   public void testResizeArchiveWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/resize/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "username1")
                       .addFormParam("size", "5").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_details.json")).build()).getArchiveApi();

      assertEquals(api.resize("username1", 5), detailsInArchiveDetails());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testResizeArchiveWhenResponseIs4xxThrows() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/archive/resize/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "username1")
                       .addFormParam("size", "5").build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveApi();

      api.resize("username1", 5);
   }

   public void testChangeArchivePasswordWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST")
                       .endpoint("https://api.glesys.com/archive/changepassword/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("username", "username")
                       .addFormParam("password", "newpass").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_details.json")).build()).getArchiveApi();
      
      assertEquals(api.changePassword("username", "newpass"), detailsInArchiveDetails());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testChangeArchivePasswordWhenResponseIs4xxThrows() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("POST")
                  .endpoint("https://api.glesys.com/archive/changepassword/format/json")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                  .addFormParam("username", "username")
                  .addFormParam("password", "newpass").build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveApi();

      api.changePassword("username", "newpass");
   }

   public void testGetArchiveAllowedArgumentsWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint("https://api.glesys.com/archive/allowedarguments/format/json")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_allowed_arguments.json")).build()).getArchiveApi();
      ArchiveAllowedArguments expected = ArchiveAllowedArguments.builder().archiveSizes(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000).build();

      assertEquals(api.getAllowedArguments(), expected);
   }

   public void testGetArchiveAllowedArguments4xxWhenResponseIs2xx() throws Exception {
      ArchiveApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint("https://api.glesys.com/archive/allowedarguments/format/json")
                  .addHeader("Accept", "application/json")
                  .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveApi();

      assertNull(api.getAllowedArguments());
   }
}
