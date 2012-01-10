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

import java.util.Map;

import com.google.common.collect.ImmutableSet;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.*;
import org.jclouds.glesys.options.ServerCloneOptions;
import org.jclouds.glesys.options.ServerCreateOptions;
import org.jclouds.glesys.options.ServerDestroyOptions;
import org.jclouds.glesys.options.ServerEditOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.glesys.options.ServerStopOptions;
import org.jclouds.glesys.parse.ParseServerAllowedArgumentsTest;
import org.jclouds.glesys.parse.ParseServerDetailsTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ServerAsyncClientTest")
public class ServerClientExpectTest extends BaseGleSYSClientExpectTest<ServerClient> {

   public ServerClientExpectTest() {
      remoteServicePrefix = "server";
   }

   private final String serverId = "abcd";
   private final Map.Entry<String, String> serverIdOnly = entry("serverid", serverId);
   
   public void testListServers() throws Exception {
      ServerClient client = createMock("list", "POST", 200, "/server_list.json");
      Server expected = Server.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ").build();
      assertEquals(client.listServers(), ImmutableSet.<Server>of(expected));
      
      // check we get empty set, if not-found
      assertTrue(createMock("list", "POST", 404, "Not found").listServers().isEmpty());
   }
   
   public void testGetAllowedArguments() throws Exception {
      ServerClient client = createMock("allowedarguments", "GET", 200, "/server_allowed_arguments.json");
      assertEquals(client.getServerAllowedArguments(), ParseServerAllowedArgumentsTest.getData());
   }

   public void testGetTemplates() throws Exception {
      createMock("templates", "GET", 200, "/server_templates.json");
   }

   public void testGetServer() throws Exception {
      ServerClient client = createMock("details", "POST", 200, "/server_details.json", serverIdOnly);

      ServerDetails actual = client.getServerDetails(serverId);
      assertEquals(actual, ParseServerDetailsTest.getData());
      assertEquals(actual.toString(), ParseServerDetailsTest.getData().toString());
   }
   
   @Test
   public void testCreateServer() throws Exception {
      ServerCreated expected = ServerCreated.builder().hostname("jclouds-test").id("xm3630641").ips(ServerCreatedIp.builder().ip("109.74.10.27").build()).build();
      ServerClient client = createMock("create", "POST", 200, "/server_created.json",
            entry("cpucores", 1), entry("memorysize", 512),
            entry("datacenter", "Falkenberg"), entry("transfer", 50),
            entry("rootpw", "password"), entry("hostname", "jclouds-test"), entry("platform", "OpenVZ"),
            entry("template", "Ubuntu 32-bit"),
            entry("disksize", 5));
      assertEquals(client.createServer("Falkenberg", "OpenVZ", "jclouds-test", "Ubuntu 32-bit", 5, 512, 1, "password", 50), expected);
      
      ServerCreateOptions options = ServerCreateOptions.Builder.description("Description-of-server").ip("10.0.0.1");
      client = createMock("create", "POST", 200, "/server_created.json",
            entry("cpucores", 1), entry("memorysize", 512),
            entry("datacenter", "Falkenberg"), entry("transfer", 50),
            entry("rootpw", "password"), entry("hostname", "jclouds-test"), entry("platform", "OpenVZ"),
            entry("template", "Ubuntu 32-bit"),
            entry("disksize", 5), options);
      assertEquals(client.createServer("Falkenberg", "OpenVZ", "jclouds-test", "Ubuntu 32-bit", 5, 512, 1, "password", 50, options), expected);
   }

   @Test
   public void testEditServer() throws Exception {
      createMock("edit", "POST", 200, null, serverIdOnly).editServer(serverId);
      ServerEditOptions options =
          ServerEditOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1).hostname("jclouds-test");
      createMock("edit", "POST", 200, null, serverIdOnly, options).editServer(serverId, options);
   }

   @Test
   public void testCloneServer() throws Exception {
      createMock("clone", "POST", 200, "/server_created.json", serverIdOnly, entry("hostname", "somename")).cloneServer(serverId, "somename");
      ServerCloneOptions options = (ServerCloneOptions) ServerCloneOptions.Builder.description("Description-of-server").disksize(1).memorysize(512).cpucores(1);
      createMock("clone", "POST", 200, "/server_created.json", serverIdOnly, entry("hostname", "somename"), options).cloneServer(serverId, "somename", options);
   }

   public void testGetServerStatus() throws Exception {
      createMock("status", "POST", 200, "/server_status.json", serverIdOnly).getServerStatus(serverId);
      createMock("status", "POST", 200, "/server_status.json", serverIdOnly, ServerStatusOptions.Builder.state()).
            getServerStatus(serverId, ServerStatusOptions.Builder.state());
      createMock("status", "POST", 404, "Not found", serverIdOnly).getServerStatus(serverId);
   }

   public void testGetServerLimits() throws Exception {
      createMock("limits", "POST", 200, "/server_limits.json", serverIdOnly).getServerLimits(serverId);
      assertNull(createMock("limits", "POST", 404, "Not found", serverIdOnly).getServerLimits(serverId));     
   }

   public void testGetServerConsole() throws Exception {
      createMock("console", "POST", 200, "/server_console.json", serverIdOnly).getServerConsole(serverId);
      assertNull(createMock("console", "POST", 404, "Not found", serverIdOnly).getServerConsole(serverId));
   }

   public void testStartServer() throws Exception {
      createMock("start", "POST", 200, null, serverIdOnly).startServer(serverId);
   }
   
   public void testStopServer() throws Exception {
      createMock("stop", "POST", 200, null, serverIdOnly).stopServer(serverId);
      createMock("stop", "POST", 200, null, serverIdOnly, ServerStopOptions.Builder.hard()).stopServer(serverId, ServerStopOptions.Builder.hard());
   }

   public void testRebootServer() throws Exception {
      createMock("reboot", "POST", 200, null, serverIdOnly).rebootServer(serverId);
   }

   public void testDestroyServer() throws Exception {
      createMock("destroy", "POST", 200, null, serverIdOnly, ServerDestroyOptions.Builder.keepIp()).destroyServer(serverId, ServerDestroyOptions.Builder.keepIp());
      createMock("destroy", "POST", 200, null, serverIdOnly, ServerDestroyOptions.Builder.discardIp()).destroyServer(serverId, ServerDestroyOptions.Builder.discardIp());
   }

   @Override
   protected ServerClient getClient(GleSYSClient gleSYSClient) {
      return gleSYSClient.getServerClient();
   }

}
