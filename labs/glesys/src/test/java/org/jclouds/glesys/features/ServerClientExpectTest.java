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
import java.util.LinkedHashMap;
import java.util.Map;

import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.Console;
import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.Ip;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ResourceUsage;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.domain.ServerUptime;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.CreateServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.EditServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

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

      Map<String, AllowedArgumentsForCreateServer> expected = new LinkedHashMap<String, AllowedArgumentsForCreateServer>();
      AllowedArgumentsForCreateServer openvz = AllowedArgumentsForCreateServer.builder()
            .dataCenters("Amsterdam", "Falkenberg", "New York City", "Stockholm")
            .memorySizes(128, 256, 512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288)
            .diskSizes(5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120, 140, 150)
            .cpuCores(1, 2, 3, 4, 5, 6, 7, 8)
            .templates("Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit", "Debian 5.0 32-bit",
                  "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", "Fedora Core 11", "Fedora Core 11 64-bit",
                  "Gentoo", "Gentoo 64-bit", "Scientific Linux 6", "Scientific Linux 6 64-bit", "Slackware 12",
                  "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit")
            .transfers(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000)
            .build();
      AllowedArgumentsForCreateServer xen = AllowedArgumentsForCreateServer.builder()
            .memorySizes(512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288, 14336, 16384)
            .diskSizes(5, 10, 20, 30, 40, 50, 80, 100, 120, 140, 150, 160, 160, 200, 250, 300)
            .cpuCores(1, 2, 3, 4, 5, 6, 7, 8)
            .templates("CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86", "Debian-6 x64",
                  "Debian 5.0.1 x64", "FreeBSD 8.2", "Gentoo 10.1 x64", "Ubuntu 8.04 x64", "Ubuntu 10.04 LTS 64-bit",
                  "Ubuntu 10.10 x64", "Ubuntu 11.04 x64", "Windows Server 2008 R2 x64 std",
                  "Windows Server 2008 R2 x64 web", "Windows Server 2008 x64 web", "Windows Server 2008 x86 web")
            .transfers(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000)
            .dataCenters("Falkenberg")
            .build();
      expected.put("Xen", xen);
      expected.put("OpenVZ", openvz);
      assertEquals(client.getAllowedArgumentsForCreateServerByPlatform(), expected);
   }

   public void testGetTemplatesWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint(URI.create("https://api.glesys.com/server/templates/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_templates.json")).build()).getServerClient();

      ImmutableSet.Builder<OSTemplate> expectedBuilder = ImmutableSet.builder();

      for (String name : new String[] { "Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit",
            "Debian 5.0 32-bit", "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", "Fedora Core 11",
            "Fedora Core 11 64-bit", "Gentoo", "Gentoo 64-bit", "Scientific Linux 6", "Scientific Linux 6 64-bit",
            "Slackware 12", "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit" }) {
         expectedBuilder.add(new OSTemplate(name, 5, 128, "linux", "OpenVZ"));
      }

      for (String name : new String[] { "CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86",
            "Debian-6 x64", "Debian 5.0.1 x64", "FreeBSD 8.2", "Gentoo 10.1 x64", "Ubuntu 8.04 x64",
            "Ubuntu 10.04 LTS 64-bit", "Ubuntu 10.10 x64", "Ubuntu 11.04 x64" }) {
         expectedBuilder.add(new OSTemplate(name, 5, 512, name.startsWith("FreeBSD") ? "freebsd" : "linux", "Xen"));
      }
      for (String name : new String[] { "Windows Server 2008 R2 x64 std", "Windows Server 2008 R2 x64 web",
            "Windows Server 2008 x64 web", "Windows Server 2008 x86 web" }) {
         expectedBuilder.add(new OSTemplate(name, 20, 1024, "windows", "Xen"));
      }
      
      assertEquals(client.listTemplates(), expectedBuilder.build());
   }

   public void testGetServerDetailsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/details/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("includestate", "true")
                        .put("serverid", "xm3276891").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerClient();

      ServerDetails actual = client.getServerDetails("xm3276891");
      assertEquals(actual.toString(), expectedServerDetails().toString());
   }

   public static ServerDetails expectedServerDetails() {
      Ip ip = Ip.builder().version4().ip("109.74.10.45").cost(2.0).build();
      Cost cost = Cost.builder().amount(13.22).currency("EUR").timePeriod("month").build();
      return ServerDetails.builder().id("xm3276891").transferGB(50).hostname("glesys-s-6dd").cpuCores(1).memorySizeMB(512)
            .diskSizeGB(5).description("glesys-s-6dd").datacenter("Falkenberg").platform("Xen")
            .templateName("Ubuntu 11.04 x64").state(Server.State.LOCKED).cost(cost).ips(ip).build();
   }

   @Test
   public void testServerDetailsWhenResponseIs4xxReturnsNull() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/details/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("includestate", "true")
                        .put("serverid", "xm3276891").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getServerClient();

      assertNull(client.getServerDetails("xm3276891"));
   }

   @Test
   public void testCreateServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/create/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("hostname", "jclouds-test")
                        .put("rootpassword", "password")
                        .put("datacenter", "Falkenberg")
                        .put("platform", "OpenVZ")
                        .put("templatename", "Ubuntu 32-bit")
                        .put("disksize", "5")
                        .put("memorysize", "512")
                        .put("cpucores", "1")
                        .put("transfer", "50")
                        .build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_noip.json")).build()).getServerClient();

      Cost cost = Cost.builder().amount(6.38).currency("EUR").timePeriod("month").build();
      ServerDetails expected = ServerDetails.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ")
            .templateName("Ubuntu 11.04 64-bit").description("description").cpuCores(1).memorySizeMB(128).diskSizeGB(5).transferGB(50).cost(cost).build();

      assertEquals(
            client.createServerWithHostnameAndRootPassword(
                  ServerSpec.builder().datacenter("Falkenberg").platform("OpenVZ").templateName("Ubuntu 32-bit")
                        .diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50).build(), "jclouds-test", "password").toString(),
            expected.toString());
   }

   public void testCreateServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/create/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("hostname", "jclouds-test")
                        .put("rootpassword", "password")
                        .put("datacenter", "Falkenberg")
                        .put("platform", "OpenVZ")
                        .put("templatename", "Ubuntu 32-bit")
                        .put("disksize", "5")
                        .put("memorysize", "512")
                        .put("cpucores", "1")
                        .put("transfer", "50")
                        .put("ip", "10.0.0.1")
                        .put("description", "Description-of-server")
                        .build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerClient();

      CreateServerOptions options = CreateServerOptions.Builder.description("Description-of-server").ip("10.0.0.1");


      assertEquals(client.createServerWithHostnameAndRootPassword(ServerSpec.builder().datacenter("Falkenberg")
            .platform("OpenVZ").templateName("Ubuntu 32-bit").diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50)
            .build(), "jclouds-test", "password", options), expectedServerDetails());
   }

   @Test
   public void testEditServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891").build())).build(),
            HttpResponse.builder().statusCode(206).build()).getServerClient();

      client.editServer("xm3276891");
   }

   @Test
   public void testEditServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/edit/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891")
                        .put("description", "Description-of-server")
                        .put("disksize", "1")
                        .put("memorysize", "512")
                        .put("cpucores", "1")
                        .put("hostname", "jclouds-test")
                        .build())).build(),
            HttpResponse.builder().statusCode(200).build()).getServerClient();

      EditServerOptions options =
            EditServerOptions.Builder.description("Description-of-server").diskSizeGB(1).memorySizeMB(512).cpuCores(1).hostname("jclouds-test");

      client.editServer("xm3276891", options);
   }
   
   @Test
   public void testCloneServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891")
                        .put("hostname", "hostname1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerClient();
      
      assertEquals(client.cloneServer("xm3276891", "hostname1"), expectedServerDetails());
   }

   @Test
   public void testCloneServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891")
                        .put("hostname", "hostname1")
                        .put("description", "Description-of-server")
                        .put("disksize", "1")
                        .put("memorysize", "512")
                        .put("cpucores", "1").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerClient();
      CloneServerOptions options = (CloneServerOptions) CloneServerOptions.Builder.description("Description-of-server").diskSizeGB(1).memorySizeMB(512).cpuCores(1);

      assertEquals(client.cloneServer("xm3276891", "hostname1", options), expectedServerDetails());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCloneServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/clone/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891")
                        .put("hostname", "hostname1").build())).build(),
            HttpResponse.builder().statusCode(404).build()).getServerClient();

      client.cloneServer("xm3276891", "hostname1");
   }

   public void testGetServerStatusWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/status/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "xm3276891").build())).build(),
            HttpResponse.builder().statusCode(206).payload(payloadFromResource("/server_status.json")).build())
            .getServerClient();

      assertEquals(client.getServerStatus("xm3276891"), expectedServerStatus());
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

      assertEquals(client.getServerStatus("server321", ServerStatusOptions.Builder.state()), expectedServerStatus());
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

   public void testGetConsoleWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/console/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server322").build())).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_console.json")).build())
            .getServerClient();

      Console expected = Console.builder().host("79.99.2.147").port(59478).password("1476897311").protocol("vnc").build();

      assertEquals(client.getConsole("server322"), expected);
   }

   public void testGetConsoleWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/console/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("serverid", "server322").build())).build(),
            HttpResponse.builder().statusCode(404).build())
            .getServerClient();

      assertNull(client.getConsole("server322"));
   }

   public void testStartServerWhenResponseIs2xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/start/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
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
                        .put("Accept", "application/json")
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
                        .put("Accept", "application/json")
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
                        .put("Accept", "application/json")
                        .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                  .payload(newUrlEncodedFormPayload(ImmutableMultimap.<String, String>builder()
                        .put("type", "hard").put("serverid", "server777").build())).build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerClient();

      client.hardStopServer("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testStopServerWhenResponseIs4xx() throws Exception {
      ServerClient client = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint(URI.create("https://api.glesys.com/server/stop/format/json"))
                  .headers(ImmutableMultimap.<String, String>builder()
                        .put("Accept", "application/json")
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
                        .put("Accept", "application/json")
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
                        .put("Accept", "application/json")
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

      client.destroyServer("server777", DestroyServerOptions.Builder.keepIp());
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

      client.destroyServer("server777", DestroyServerOptions.Builder.discardIp());
   }


   private ServerStatus expectedServerStatus() {
      ResourceUsage cpu = ResourceUsage.builder().unit("cores").max(1.0).usage(0.0).build();
      ResourceUsage disk = ResourceUsage.builder().unit("GB").usage(0.0).max(5).build();
      ResourceUsage memory = ResourceUsage.builder().unit("MB").usage(0.0).max(512).build();
      ServerUptime uptime = ServerUptime.builder().current(0).unit("seconds").build();
      return ServerStatus.builder().state(Server.State.RUNNING).uptime(uptime).
            cpu(cpu).disk(disk).memory(memory).build();
   }
   
}
