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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerCreated;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerIp;
import org.jclouds.glesys.options.*;
import org.jclouds.glesys.parse.*;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.net.URI;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.*;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerClientExpectTest extends BaseRestClientExpectTest<GleSYSClient> {

   public ServerClientExpectTest() {
      provider = "glesys";
   }

   public void testListServersWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(204).payload(payloadFromResource("/server_list.json")).build()).getServerClient();
      Server expected = Server.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ").build();

      assertEquals(client.listServers(), ImmutableSet.<Server>of(expected));
   }

   public void testListServersWhenReponseIs404IsEmpty() {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/list/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(404).build()).getServerClient();

      assertTrue(client.listServers().isEmpty());
   }

   public void testGetAllowedArgumentsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("https://api.glesys.com/server/allowedarguments/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(204).payload(payloadFromResource("/server_allowed_arguments.json")).build()).getServerClient();

      assertEquals(client.getServerAllowedArguments(), new ParseServerAllowedArgumentsTest().expected());
   }

   public void testGetTemplatesWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("https://api.glesys.com/server/templates/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_templates.json")).build()).getServerClient();

      assertEquals(client.getTemplates(), new ParseServerTemplatesTest().expected());
   }

   public void testGetServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/details/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server1ssg-1.1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerClient();

      ServerDetails actual = client.getServerDetails("server1ssg-1.1");
      assertEquals(actual.toString(), new ParseServerDetailsTest().expected().toString());
   }

   @Test
   public void testCreateServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/create/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("cpucores", "1").put("memorysize", "512")
                        .put("datacenter", "Falkenberg")
                        .put("transfer", "50")
                        .put("rootpw", "password")
                        .put("hostname", "jclouds-test")
                        .put("platform", "OpenVZ")
                        .put("template", "Ubuntu 32-bit")
                        .put("disksize", "5").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_created.json")).build()).getServerClient();
      ServerCreated expected = ServerCreated.builder().hostname("jclouds-test").id("xm3630641").ips(ServerIp.builder().ip("109.74.10.27").build()).build();

      assertEquals(client.createServer("Falkenberg", "OpenVZ", "jclouds-test", "Ubuntu 32-bit", 5, 512, 1, "password", 50), expected);
   }

   public void testCreateServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/create/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("cpucores", "1").put("memorysize", "512")
                        .put("datacenter", "Falkenberg")
                        .put("transfer", "50")
                        .put("rootpw", "password")
                        .put("hostname", "jclouds-test")
                        .put("platform", "OpenVZ")
                        .put("template", "Ubuntu 32-bit")
                        .put("disksize", "5")
                        .put("description", "Description-of-server")
                        .put("ip", "10.0.0.1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_created.json")).build()).getServerClient();
      ServerCreateOptions options = ServerCreateOptions.Builder.description("Description-of-server").ip("10.0.0.1");
      ServerCreated expected = ServerCreated.builder().hostname("jclouds-test").id("xm3630641").ips(ServerIp.builder().ip("109.74.10.27").build()).build();

      assertEquals(client.createServer("Falkenberg", "OpenVZ", "jclouds-test", "Ubuntu 32-bit", 5, 512, 1, "password", 50, options), expected);
   }

   @Test
   public void testEditServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111").build())).build(),
            HttpResponse.builder().statusCode(206).build()).getServerClient();

      client.editServer("server111");
   }

   @Test
   public void testEditServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111")
                        .put("description", "Description-of-server")
                        .put("disksize", "1")
                        .put("memorysize", "512")
                        .put("cpucores", "1")
                        .put("hostname", "jclouds-test")
                        .build())).build(),
            HttpResponse.builder().statusCode(200).build()).getServerClient();

      ServerEditOptions options =
            ServerEditOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1).hostname("jclouds-test");

      client.editServer("server111", options);
   }

   @Test
   public void testCloneServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111")
                        .put("hostname", "hostname1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_created.json")).build()).getServerClient();
      ServerCreated expected = ServerCreated.builder().hostname("jclouds-test").id("xm3630641").ips(ServerIp.builder().ip("109.74.10.27").build()).build();

      assertEquals(client.cloneServer("server111", "hostname1"), expected);
   }

   @Test
   public void testCloneServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111")
                        .put("hostname", "hostname1")
                        .put("description", "Description-of-server")
                        .put("disksize", "1")
                        .put("memorysize", "512")
                        .put("cpucores", "1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_created.json")).build()).getServerClient();
      ServerCloneOptions options = (ServerCloneOptions) ServerCloneOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1);
      ServerCreated expected = ServerCreated.builder().hostname("jclouds-test").id("xm3630641").ips(ServerIp.builder().ip("109.74.10.27").build()).build();

      assertEquals(client.cloneServer("server111", "hostname1", options), expected);
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCloneServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111")
                        .put("hostname", "hostname1").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getServerClient();

      client.cloneServer("server111", "hostname1");
   }

   public void testGetServerStatusWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/status/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server111").build())).build(),
            HttpResponse.builder().statusCode(206).payload(payloadFromResource("/server_status.json")).build())
            .getServerClient();

      assertEquals(client.getServerStatus("server111"), new ParseServerStatusTest().expected());
   }

   public void testGetServerStatusWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/status/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server321").put("statustype", "state").build())).build(),
            HttpResponse.builder().statusCode(206).payload(payloadFromResource("/server_status.json")).build())
            .getServerClient();

      assertEquals(client.getServerStatus("server321", ServerStatusOptions.Builder.state()), new ParseServerStatusTest().expected());
   }

   public void testGetServerStatusWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/status/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server321").put("statustype", "state").build())).build(),
            HttpResponse.builder().statusCode(404).build())
            .getServerClient();

      assertNull(client.getServerStatus("server321", ServerStatusOptions.Builder.state()));
   }

   public void testGetServerLimitsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/limits/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server321").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_limits.json")).build())
            .getServerClient();
      
      client.getServerLimits("server321");
   }

   public void testGetServerConsoleWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/console/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server322").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_console.json")).build())
            .getServerClient();

      assertEquals(client.getServerConsole("server322"), new ParseServerConsoleTest().expected());
   }

   public void testGetServerConsoleWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/console/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server322").build())).build(),
            HttpResponse.builder().statusCode(404).build())
            .getServerClient();

      assertNull(client.getServerConsole("server322"));
   }

   public void testStartServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/start/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.startServer("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testStartServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/start/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerClient();

      client.startServer("server777");
   }

   public void testStopServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/stop/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.stopServer("server777");
   }

   public void testHardStopServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/stop/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").put("type", "hard").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.stopServer("server777", ServerStopOptions.Builder.hard());
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testStopServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/stop/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerClient();

      client.stopServer("server777");
   }

   public void testRebootServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/reboot/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.rebootServer("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testRebootServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/reboot/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerClient();

      client.rebootServer("server777");
   }

   public void testDestroyServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/destroy/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").put("keepip", "1").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.destroyServer("server777", ServerDestroyOptions.Builder.keepIp());
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testDestroyServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/destroy/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server777").put("keepip", "0").build())).build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerClient();

      client.destroyServer("server777", ServerDestroyOptions.Builder.discardIp());
   }

}
