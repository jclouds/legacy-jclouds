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
package org.jclouds.glesys.features;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.glesys.domain.ArchiveDetails;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests parsing of {@code ArchiveAsyncClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ArchiveClientExpectTest")
public class ArchiveClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {
   public ArchiveClientExpectTest() {
      provider = "glesys";
   }

   public void testListArchivesWhenReponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_list.json")).build())
            .getArchiveClient();

      List<Archive> expected = ImmutableList.<Archive>of(
            Archive.builder().username("xxxxx_test1").freeSize("20 GB").totalSize("20 GB").locked(false).build());

      assertEquals(client.listArchives(), expected);
   }

   public void testListArchivesWhenResponseIs4xxReturnsEmpty() {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveClient();

      assertTrue(client.listArchives().isEmpty());
   }

   public void testArchiveDetailsWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/details/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("username", "xxxxxx_test1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_details.json")).build())
            .getArchiveClient();
      ArchiveDetails expected = ArchiveDetails.builder().username("xxxxxx_test1").freeSize("30 GB").totalSize("30 GB").locked(false).build();

      assertEquals(client.getArchiveDetails("xxxxxx_test1"), expected);
   }

   public void testArchiveDetailsWhenResponseIs4xxReturnsNull() {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/details/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(
                        ImmutableMultimap.<String, String>builder().put("username", "xxxxxx_test1").build())).build(),
            HttpResponse.builder().statusCode(404).build())
            .getArchiveClient();
      assertNull(client.getArchiveDetails("xxxxxx_test1"));
   }

   public void testCreateArchiveWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/create/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "xxxxxx_test1")
                        .put("size", "5")
                        .put("password", "somepass").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getArchiveClient();
      client.createArchive("xxxxxx_test1", "somepass", 5);
   }

   public void testDeleteArchiveWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/delete/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "xxxxxx_test1").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getArchiveClient();

      client.deleteArchive("xxxxxx_test1");
   }

   @Test(expectedExceptions = {HttpResponseException.class})
   public void testDeleteArchiveWhenResponseIs4xxThrows() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/delete/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "xxxxxx_test1").build())).build(),
            HttpResponse.builder().statusCode(402).build()).getArchiveClient();
      client.deleteArchive("xxxxxx_test1");
   }

   public void testResizeArchiveWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/resize/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "username1")
                        .put("size", "5").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getArchiveClient();

      client.resizeArchive("username1", 5);
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testResizeArchiveWhenResponseIs4xxThrows() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/archive/resize/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "username1")
                        .put("size", "5").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveClient();

      client.resizeArchive("username1", 5);
   }

   public void testChangeArchivePasswordWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST")
                  .endpoint(URI.create("https://api.glesys.com/archive/changepassword/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "username")
                        .put("password", "newpass").build())).build(),
            HttpResponse.builder().statusCode(200).build()).getArchiveClient();

      client.changeArchivePassword("username", "newpass");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testChangeArchivePasswordWhenResponseIs4xxThrows() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("POST")
                  .endpoint(URI.create("https://api.glesys.com/archive/changepassword/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder().put(
                        "Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("username", "username")
                        .put("password", "newpass").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveClient();

      client.changeArchivePassword("username", "newpass");
   }

   public void testGetArchiveAllowedArgumentsWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("https://api.glesys.com/archive/allowedarguments/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                        .build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/archive_allowed_arguments.json")).build()).getArchiveClient();
      ArchiveAllowedArguments expected = ArchiveAllowedArguments.builder().archiveSizes(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 400, 425, 450, 475, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000).build();

      assertEquals(client.getArchiveAllowedArguments(), expected);
   }

   public void testGetArchiveAllowedArguments4xxWhenResponseIs2xx() throws Exception {
      ArchiveClient client = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("https://api.glesys.com/archive/allowedarguments/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build()).getArchiveClient();

      assertNull(client.getArchiveAllowedArguments());
   }
}
