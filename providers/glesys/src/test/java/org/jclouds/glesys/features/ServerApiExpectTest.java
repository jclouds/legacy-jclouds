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

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.AllowedArguments;
import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.Console;
import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.Ip;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ResourceStatus;
import org.jclouds.glesys.domain.ResourceUsage;
import org.jclouds.glesys.domain.ResourceUsageInfo;
import org.jclouds.glesys.domain.ResourceUsageValue;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.domain.ServerUptime;
import org.jclouds.glesys.internal.BaseGleSYSApiExpectTest;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.CreateServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.UpdateServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Tests annotation parsing of {@code ServerAsyncApi}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerAsyncApiTest")
public class ServerApiExpectTest extends BaseGleSYSApiExpectTest {

   public void testListServersWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(204).payload(payloadFromResource("/server_list.json")).build()).getServerApi();
      Server expected = Server.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ").build();

      assertEquals(api.list().toSet(), ImmutableSet.<Server>of(expected));
   }

   public void testListServersWhenReponseIs404IsEmpty() {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/list/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(404).build()).getServerApi();

      assertTrue(api.list().isEmpty());
   }

   public void testGetAllowedArgumentsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/server/allowedarguments/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(204).payload(payloadFromResource("/server_allowed_arguments.json")).build()).getServerApi();

      Map<String, AllowedArgumentsForCreateServer> expected = Maps.newLinkedHashMap();
      AllowedArguments openvzAllowedMemorySizes = AllowedArguments.builder().allowedUnits(128, 256, 512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288, 14336, 16384).costPerUnit(Cost.builder().amount(0.09).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments openvzAllowedDiskSizes = AllowedArguments.builder().allowedUnits(5, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 120, 140, 150).costPerUnit(Cost.builder().amount(2.2).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments openvzAllowedCpuCores = AllowedArguments.builder().allowedUnits(1, 2, 3, 4, 5, 6, 7, 8).costPerUnit(Cost.builder().amount(30).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments openvzAllowedTransfers = AllowedArguments.builder().allowedUnits(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000).costPerUnit(Cost.builder().amount(0.2).currency("SEK").timePeriod("month").build()).build();
      AllowedArgumentsForCreateServer openvz = AllowedArgumentsForCreateServer.builder()
            .dataCenters("Amsterdam", "Falkenberg", "New York City", "Stockholm")
            .memorySizes(openvzAllowedMemorySizes)
            .diskSizes(openvzAllowedDiskSizes)
            .cpuCores(openvzAllowedCpuCores)
            .templates("Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit", "Debian 5.0 32-bit",
                    "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", "Fedora Core 11", "Fedora Core 11 64-bit",
                    "Gentoo", "Gentoo 64-bit", "Scientific Linux 6", "Scientific Linux 6 64-bit", "Slackware 12",
                    "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit", "Ubuntu 12.04 LTS 32-bit",
                    "Ubuntu 12.04 LTS 64-bit")
            .transfers(openvzAllowedTransfers)
            .build();
      
      AllowedArguments xenAllowedMemorySizes = AllowedArguments.builder().allowedUnits(512, 768, 1024, 1536, 2048, 2560, 3072, 3584, 4096, 5120, 6144, 7168, 8192, 9216, 10240, 11264, 12288, 14336, 16384).costPerUnit(Cost.builder().amount(0.09).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments xenAllowedDiskSizes = AllowedArguments.builder().allowedUnits(5, 10, 20, 30, 40, 50, 80, 100, 120, 140, 150, 160, 160, 200, 250, 300).costPerUnit(Cost.builder().amount(2.2).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments xenAllowedCpuCores = AllowedArguments.builder().allowedUnits(1, 2, 3, 4, 5, 6, 7, 8).costPerUnit(Cost.builder().amount(30).currency("SEK").timePeriod("month").build()).build();
      AllowedArguments xenAllowedTransfers = AllowedArguments.builder().allowedUnits(50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000).costPerUnit(Cost.builder().amount(0.2).currency("SEK").timePeriod("month").build()).build();      
      AllowedArgumentsForCreateServer xen = AllowedArgumentsForCreateServer.builder()
            .memorySizes(xenAllowedMemorySizes)
            .diskSizes(xenAllowedDiskSizes)
            .cpuCores(xenAllowedCpuCores)
            
            
            .templates("CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86", 
            		"Debian-6 x64", "Debian 5.0.1 x64", "FreeBSD 8.2", "FreeBSD 9.0", 
            		"Gentoo 10.1 x64", "OpenSUSE 11.4 64-bit", "Ubuntu 8.04 x64", 
            		"Ubuntu 10.04 LTS 64-bit", "Ubuntu 10.10 x64", "Ubuntu 11.04 x64", 
            		"Ubuntu 12.04 x64", "Ubuntu 12.04 x86", "Windows Server 2008 R2 x64 std", 
            		"Windows Server 2008 R2 x64 web", "Windows Server 2008 x64 web")
            .transfers(xenAllowedTransfers)
            .dataCenters("Falkenberg")
            .build();
      expected.put("Xen", xen);
      expected.put("OpenVZ", openvz);
      assertEquals(api.getAllowedArgumentsForCreateByPlatform(), expected);
   }

   public void testGetTemplatesWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("GET").endpoint("https://api.glesys.com/server/templates/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_templates.json")).build()).getServerApi();

      ImmutableSet.Builder<OSTemplate> expectedBuilder = ImmutableSet.builder();

      for (String name : new String[]{"Centos 5", "Centos 5 64-bit", "Centos 6 32-bit", "Centos 6 64-bit",
            "Debian 5.0 32-bit", "Debian 5.0 64-bit", "Debian 6.0 32-bit", "Debian 6.0 64-bit", "Fedora Core 11",
            "Fedora Core 11 64-bit", "Gentoo", "Gentoo 64-bit", "Scientific Linux 6", "Scientific Linux 6 64-bit",
            "Slackware 12", "Ubuntu 10.04 LTS 32-bit", "Ubuntu 10.04 LTS 64-bit", "Ubuntu 11.04 64-bit"}) {
         expectedBuilder.add(OSTemplate.builder().name(name).minDiskSize(5).minMemSize(128).os("linux").platform("OpenVZ").build());
      }

      for (String name : new String[]{"CentOS 5.5 x64", "CentOS 5.5 x86", "Centos 6 x64", "Centos 6 x86",
            "Debian-6 x64", "Debian 5.0.1 x64", "FreeBSD 8.2", "Gentoo 10.1 x64", "Ubuntu 8.04 x64",
            "Ubuntu 10.04 LTS 64-bit", "Ubuntu 10.10 x64", "Ubuntu 11.04 x64"}) {
         expectedBuilder.add(OSTemplate.builder().name(name).minDiskSize(5).minMemSize(512)
               .os(name.startsWith("FreeBSD") ? "freebsd" : "linux").platform("Xen").build());
      }
      for (String name : new String[]{"Windows Server 2008 R2 x64 std", "Windows Server 2008 R2 x64 web",
            "Windows Server 2008 x64 web", "Windows Server 2008 x86 web"}) {
         expectedBuilder.add(OSTemplate.builder().name(name).minDiskSize(20).minMemSize(1024).os("windows").platform("Xen").build());
      }

      assertEquals(api.listTemplates().toSet(), expectedBuilder.build());
   }

   public void testGetServerDetailsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("includestate", "true")
                       .addFormParam("serverid", "xm3276891").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerApi();

      ServerDetails actual = api.get("xm3276891");
      assertEquals(actual.toString(), expectedServerDetails().toString());
   }

   public static ServerDetails expectedServerDetails() {
      Ip ip = Ip.builder().version4().ip("31.192.231.254").version4().cost(2.0).currency("EUR").build();
      Cost cost = Cost.builder().amount(10.22).currency("EUR").timePeriod("month").build();
      return ServerDetails.builder().id("vz1840356").transferGB(50).hostname("glesys-s").cpuCores(1).memorySizeMB(512)
            .diskSizeGB(5).datacenter("Falkenberg").description("glesys-s-6dd").platform("OpenVZ")
            .templateName("Ubuntu 10.04 LTS 32-bit").state(Server.State.RUNNING).cost(cost).ips(ip).build();
   }

   @Test
   public void testServerDetailsWhenResponseIs4xxReturnsNull() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/details/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("includestate", "true")
                       .addFormParam("serverid", "xm3276891").build(),
            HttpResponse.builder().statusCode(404).build()).getServerApi();

      assertNull(api.get("xm3276891"));
   }

   @Test
   public void testCreateServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/create/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("hostname", "jclouds-test")
                       .addFormParam("rootpassword", "password")
                       .addFormParam("datacenter", "Falkenberg")
                       .addFormParam("platform", "OpenVZ")
                       .addFormParam("templatename", "Ubuntu 32-bit")
                       .addFormParam("disksize", "5")
                       .addFormParam("memorysize", "512")
                       .addFormParam("cpucores", "1")
                       .addFormParam("transfer", "50").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_noip.json")).build()).getServerApi();

      Cost cost = Cost.builder().amount(6.38).currency("EUR").timePeriod("month").build();
      ServerDetails expected = ServerDetails.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ")
            .templateName("Ubuntu 11.04 64-bit").description("description").cpuCores(1).memorySizeMB(128).diskSizeGB(5).transferGB(50).cost(cost).build();

      assertEquals(
            api.createWithHostnameAndRootPassword(
                  ServerSpec.builder().datacenter("Falkenberg").platform("OpenVZ").templateName("Ubuntu 32-bit")
                        .diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50).build(), "jclouds-test", "password").toString(),
            expected.toString());
   }

   public void testCreateServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/create/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("hostname", "jclouds-test")
                       .addFormParam("rootpassword", "password")
                       .addFormParam("datacenter", "Falkenberg")
                       .addFormParam("platform", "OpenVZ")
                       .addFormParam("templatename", "Ubuntu 32-bit")
                       .addFormParam("disksize", "5")
                       .addFormParam("memorysize", "512")
                       .addFormParam("cpucores", "1")
                       .addFormParam("transfer", "50")
                       .addFormParam("ip", "10.0.0.1")
                       .addFormParam("description", "Description-of-server").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerApi();

      CreateServerOptions options = CreateServerOptions.Builder.description("Description-of-server").ip("10.0.0.1");


      assertEquals(api.createWithHostnameAndRootPassword(ServerSpec.builder().datacenter("Falkenberg")
            .platform("OpenVZ").templateName("Ubuntu 32-bit").diskSizeGB(5).memorySizeMB(512).cpuCores(1).transferGB(50)
            .build(), "jclouds-test", "password", options), expectedServerDetails());
   }

   @Test
   public void testUpdateServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/edit/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891")
                       .addFormParam("description", "this is a different description!")
                       .addFormParam("hostname", "new-hostname").build(),
            HttpResponse.builder().statusCode(206).build()).getServerApi();

      api.update("xm3276891", UpdateServerOptions.Builder.description("this is a different description!").hostname("new-hostname"));
   }

   @Test
   public void testUpdateServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/edit/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891")
                       .addFormParam("description", "Description-of-server")
                       .addFormParam("disksize", "1")
                       .addFormParam("memorysize", "512")
                       .addFormParam("cpucores", "1")
                       .addFormParam("hostname", "jclouds-test").build(),
            HttpResponse.builder().statusCode(200).build()).getServerApi();

      UpdateServerOptions options =
            UpdateServerOptions.Builder.description("Description-of-server").diskSizeGB(1).memorySizeMB(512).cpuCores(1).hostname("jclouds-test");

      api.update("xm3276891", options);
   }

   @Test
   public void testCloneServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/clone/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891")
                       .addFormParam("hostname", "hostname1").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerApi();

      assertEquals(api.clone("xm3276891", "hostname1"), expectedServerDetails());
   }

   @Test
   public void testCloneServerWithOptsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/clone/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891")
                       .addFormParam("hostname", "hostname1")
                       .addFormParam("description", "Description-of-server")
                       .addFormParam("disksize", "1")
                       .addFormParam("memorysize", "512")
                       .addFormParam("cpucores", "1").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_details.json")).build()).getServerApi();
      CloneServerOptions options = (CloneServerOptions) CloneServerOptions.Builder.description("Description-of-server").diskSizeGB(1).memorySizeMB(512).cpuCores(1);

      assertEquals(api.clone("xm3276891", "hostname1", options), expectedServerDetails());
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testCloneServerWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/clone/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891")
                       .addFormParam("hostname", "hostname1").build(),
            HttpResponse.builder().statusCode(404).build()).getServerApi();

      api.clone("xm3276891", "hostname1");
   }

   public void testGetServerStatusWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/status/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "xm3276891").build(),
            HttpResponse.builder().statusCode(206).payload(payloadFromResource("/server_status.json")).build())
            .getServerApi();

      assertEquals(api.getStatus("xm3276891"), expectedServerStatus());
   }

   public void testGetServerStatusWithOptsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/status/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server321")
                       .addFormParam("statustype", "state").build(),
            HttpResponse.builder().statusCode(206).payload(payloadFromResource("/server_status.json")).build())
            .getServerApi();

      assertEquals(api.getStatus("server321", ServerStatusOptions.Builder.state()), expectedServerStatus());
   }

   public void testGetServerStatusWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/status/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server321")
                       .addFormParam("statustype", "state").build(),
            HttpResponse.builder().statusCode(404).build())
            .getServerApi();

      assertNull(api.getStatus("server321", ServerStatusOptions.Builder.state()));
   }

   public void testGetServerLimitsWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/limits/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server321").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_limits.json")).build())
            .getServerApi();

      api.getLimits("server321");
   }

   public void testGetConsoleWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/console/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server322").build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_console.json")).build())
            .getServerApi();

      Console expected = Console.builder().host("79.99.2.147").port(59478).password("1476897311").protocol("vnc").build();

      assertEquals(api.getConsole("server322"), expected);
   }

   public void testGetConsoleWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/console/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server322").build(),
            HttpResponse.builder().statusCode(404).build())
            .getServerApi();

      assertNull(api.getConsole("server322"));
   }

   public void testStartServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/start/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerApi();

      api.start("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testStartServerWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/start/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerApi();

      api.start("server777");
   }

   public void testStopServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/stop/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerApi();

      api.stop("server777");
   }

   public void testHardStopServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/stop/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("type", "hard")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerApi();

      api.hardStop("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testStopServerWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/stop/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerApi();

      api.stop("server777");
   }

   public void testRebootServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/reboot/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerApi();

      api.reboot("server777");
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testRebootServerWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/reboot/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777").build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerApi();

      api.reboot("server777");
   }

   public void testDestroyServerWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/destroy/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777")
                       .addFormParam("keepip", "true").build(),
            HttpResponse.builder().statusCode(200).build())
            .getServerApi();

      api.destroy("server777", DestroyServerOptions.Builder.keepIp());
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testDestroyServerWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/destroy/format/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777")
                       .addFormParam("keepip", "false").build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerApi();

      api.destroy("server777", DestroyServerOptions.Builder.discardIp());
   }

   public void testResourceUsageWhenResponseIs2xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/resourceusage/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777")
                       .addFormParam("resource", "diskioread")
                       .addFormParam("resolution", "minute").build(),
            HttpResponse.builder().statusCode(200)
                  .payload(payloadFromResourceWithContentType("/server_resource_usage.json", MediaType.APPLICATION_JSON))
                  .build())
            .getServerApi();

      ResourceUsage expected = ResourceUsage.builder().info(
            ResourceUsageInfo.builder().resolution("minute").resource("diskioread").unit("KB").build())
            .values(
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:21:07+02:00")).build(),
                  ResourceUsageValue.builder().value(5.1).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:22:05+02:00")).build(),
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:23:05+02:00")).build(),
                  ResourceUsageValue.builder().value(10.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:24:08+02:00")).build(),
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:25:12+02:00")).build(),
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:26:07+02:00")).build(),
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:27:12+02:00")).build(),
                  ResourceUsageValue.builder().value(0.0).timestamp(dateService.iso8601SecondsDateParse("2012-06-24T14:28:05+02:00")).build()
            ).build();
      assertEquals(api.getResourceUsage("server777", "diskioread", "minute").toString(), expected.toString());
   }

   @Test(expectedExceptions = {AuthorizationException.class})
   public void testResouceUsageWhenResponseIs4xx() throws Exception {
      ServerApi api = requestSendsResponse(
            HttpRequest.builder().method("POST").endpoint("https://api.glesys.com/server/resourceusage/format/json")
                       .addHeader("Accept", "application/json")
                       .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                       .addFormParam("serverid", "server777")
                       .addFormParam("resource", "diskioread")
                       .addFormParam("resolution", "minute").build(),
            HttpResponse.builder().statusCode(401).build())
            .getServerApi();

      api.getResourceUsage("server777", "diskioread", "minute");
   }

   private ServerStatus expectedServerStatus() {
      ResourceStatus cpu = ResourceStatus.builder().unit("cores").max(1.0).usage(0.0).build();
      ResourceStatus disk = ResourceStatus.builder().unit("MB").usage(0.0).max(5120).build();
      ResourceStatus memory = ResourceStatus.builder().unit("MB").usage(2.0).max(512).build();
      ServerUptime uptime = ServerUptime.builder().current(21).unit("seconds").build();
      return ServerStatus.builder().state(Server.State.RUNNING).uptime(uptime).
            cpu(cpu).disk(disk).memory(memory).build();
   }

}
