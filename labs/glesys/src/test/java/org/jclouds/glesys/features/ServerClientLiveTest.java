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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.glesys.domain.*;
import org.jclouds.glesys.internal.BaseGleSYSClientWithAServerLiveTest;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.EditServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code ServerClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ServerClientLiveTest", singleThreaded = true)
public class ServerClientLiveTest extends BaseGleSYSClientWithAServerLiveTest {
   public static final String testHostName2 = "jclouds-test2";
   
   @BeforeMethod
   public void setupClient() {
      client = gleContext.getApi().getServerClient();
   }

   @AfterGroups(groups = {"live"})
   public void deleteExtraServer() {
      if (testServerId2 != null) {
         client.destroyServer(testServerId2, DestroyServerOptions.Builder.discardIp());
      }
   }

   private ServerClient client;
   private String testServerId2;

   @BeforeMethod
   public void makeSureServerIsRunning() throws Exception {
      serverStatusChecker.apply(Server.State.RUNNING);
   }
   
   @Test
   public void testAllowedArguments() throws Exception {
      Map<String,AllowedArgumentsForCreateServer> templates = client.getAllowedArgumentsForCreateServerByPlatform();
      
      assertTrue(templates.containsKey("OpenVZ"));
      assertTrue(templates.containsKey("Xen"));
      
      checkAllowedArguments(templates.get("OpenVZ"));
      checkAllowedArguments(templates.get("Xen"));
   }

   private void checkAllowedArguments(AllowedArgumentsForCreateServer t) {
      assertNotNull(t);

      assert t.getDataCenters().size() > 0 : t;
      assert t.getCpuCoreOptions().size() > 0 : t;
      assert t.getDiskSizesInGB().size() > 0 : t;
      assert t.getMemorySizesInMB().size() > 0 : t;
      assert t.getTemplateNames().size() > 0 : t;
      assert t.getTransfersInGB().size() > 0 : t;
      assert t.getTransfersInGB().size() > 0 : t;
   }
   
   public void testListTemplates() throws Exception {
      Set<OSTemplate> oSTemplates = client.listTemplates();

      for(OSTemplate oSTemplate : oSTemplates) {
         checkTemplate(oSTemplate);
      }
   }
   
   private void checkTemplate(OSTemplate t) {
      assertNotNull(t);
      assertNotNull(t.getName());
      assertNotNull(t.getOs());

      assertNotNull(t.getPlatform());
      assert t.getMinDiskSize() > 0 : t;
      assert t.getMinMemSize() > 0 : t;
    }
   
   public void testListServers() throws Exception {
      Set<Server> response = client.listServers();
      assertNotNull(response);
      assertTrue(response.size() > 0);

      for (Server server : response) {
         ServerDetails newDetails = client.getServerDetails(server.getId());
         assertEquals(newDetails.getId(), server.getId());
         assertEquals(newDetails.getHostname(), server.getHostname());
         assertEquals(newDetails.getPlatform(), server.getPlatform());
         assertEquals(newDetails.getDatacenter(), server.getDatacenter());
         checkServer(newDetails);
      }
   }

   public void testServerDetails() throws Exception {
      ServerDetails details = client.getServerDetails(serverId);
      checkServer(details);
      assertEquals("Ubuntu 10.04 LTS 32-bit", details.getTemplateName());
      assertEquals("Falkenberg", details.getDatacenter());
      assertEquals("OpenVZ", details.getPlatform());
      assertEquals(5, details.getDiskSizeGB());
      assertEquals(512, details.getMemorySizeMB());
      assertEquals(1, details.getCpuCores());
      assertEquals(50, details.getTransferGB());
   }

   public void testServerStatus() throws Exception {
      ServerStatus newStatus = client.getServerStatus(serverId);
      checkStatus(newStatus);
   }

   public void testEditServer() throws Exception {
      ServerDetails edited = client.editServer(serverId, EditServerOptions.Builder.description("this is a different description!"));
      assertEquals(edited.getDescription(), "this is a different description!");

      edited = client.editServer(serverId, EditServerOptions.Builder.description("another description!"), EditServerOptions.Builder.hostname("host-name1"));
      assertEquals(edited.getDescription(), "another description!");
      assertEquals(edited.getHostname(), "host-name1");

      edited = client.resetPassword(serverId, "anotherpass");
      assertEquals(edited.getHostname(), "host-name1");

      edited = client.editServer(serverId, EditServerOptions.Builder.hostname(hostName));
      assertEquals(edited.getHostname(), hostName);
   }

   @Test
   public void testRebootServer() throws Exception {
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));

      client.rebootServer(serverId);
      
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));
   }

   @Test
   public void testStopAndStartServer() throws Exception {
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));

      client.stopServer(serverId);

      assertTrue(serverStatusChecker.apply(Server.State.STOPPED));

      client.startServer(serverId);

      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));
   }

   public void testServerLimits() throws Exception {
      Map<String, ServerLimit> limits = client.getServerLimits(serverId);
      assertNotNull(limits);
      for (Map.Entry<String, ServerLimit> entry : limits.entrySet()) {
         assertNotNull(entry.getKey());
         assertNotNull(entry.getValue());
         ServerLimit limit = entry.getValue();
         assertTrue(limit.getBarrier() >= 0);
         assertTrue(limit.getFailCount() == 0);
         assertTrue(limit.getHeld() >= 0);
         assertTrue(limit.getLimit() > 0);
         assertTrue(limit.getMaxHeld() >= 0);
      }
   }

   public void testResourceUsage() throws Exception {
      // test server has only been in existence for less than a minute - check all servers
      for (Server server : client.listServers()) {
         ResourceUsage usage = client.getResourceUsage(server.getId(), "diskioread", "minute");
         assertEquals(usage.getInfo().getResource(), "diskioread");
         assertEquals(usage.getInfo().getResolution(), "minute");

         usage = client.getResourceUsage(server.getId(), "cpuusage", "minute");
         assertEquals(usage.getInfo().getResource(), "cpuusage");
         assertEquals(usage.getInfo().getResolution(), "minute");
      }
   }

   public void testConsole() throws Exception {
      Console console = client.getConsole(serverId);
      assertNotNull(console);
      assertNotNull(console.getHost());
      assertTrue(console.getPort() > 0 && console.getPort() < 65537);
      assertNotNull(console.getPassword());
   }

   // takes a few minutes and requires an extra server (used 1 already)
   @Test(enabled=false)
   public void testCloneServer() throws Exception {
      ServerDetails testServer2 = client.cloneServer(serverId, testHostName2, CloneServerOptions.Builder.cpucores(1));

      assertNotNull(testServer2.getId());
      assertEquals(testServer2.getHostname(), "jclouds-test2");
      assertTrue(testServer2.getIps().isEmpty());
      
      testServerId2 = testServer2.getId();

      RetryablePredicate<Server.State> cloneChecker = new ServerStatusChecker(client, testServerId2, 300, 10, TimeUnit.SECONDS);
      assertTrue(cloneChecker.apply(Server.State.STOPPED));

      client.startServer(testServer2.getId());

      // TODO ServerStatus==STOPPED suggests the previous call to start should have worked
      cloneChecker = new RetryablePredicate<Server.State>(
            new Predicate<Server.State>() {

               public boolean apply(Server.State value) {
                  ServerStatus status = client.getServerStatus(testServerId2, ServerStatusOptions.Builder.state());
                  if (status.getState() == value) {
                     return true;
                  }

                  client.startServer(testServerId2);
                  return false;
               }

            }, 600, 30, TimeUnit.SECONDS);

      assertTrue(cloneChecker.apply(Server.State.RUNNING));
   }

   private void checkServer(ServerDetails server) {
      // description can be null
      assert server.getCpuCores() > 0 : server;
      assert server.getDiskSizeGB() > 0 : server;
      assert server.getMemorySizeMB() > 0 : server;
      assert server.getCost() != null;
      assert server.getTransferGB() > 0 : server;

      assertNotNull(server.getTemplateName());
      assertNotNull(server.getIps());
   }

   private void checkStatus(ServerStatus status) {
      assertNotNull(status.getState());
      assertNotNull(status.getUptime());

      
      for (ResourceStatus usage : new ResourceStatus[] { status.getCpu(), status.getDisk(), status.getMemory() }) {
         assertNotNull(usage);
         assert usage.getMax() >= 0.0 : status;
         assert usage.getUsage() >= 0.0 : status;
         
         assertNotNull(usage.getUnit());
      }
      
      assertNotNull(status.getUptime());
      assert status.getUptime().getCurrent() > 0 : status;
      assertNotNull(status.getUptime().getUnit());
   }
}
