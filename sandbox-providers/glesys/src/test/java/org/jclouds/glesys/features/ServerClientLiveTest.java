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

import com.google.common.base.Predicate;
import org.jclouds.glesys.domain.*;
import org.jclouds.glesys.options.ServerCloneOptions;
import org.jclouds.glesys.options.ServerDestroyOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code ServerClient}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ServerClientLiveTest")
public class ServerClientLiveTest extends BaseGleSYSClientLiveTest {
   public static final String testHostName1 = "jclouds-test";
   public static final String testHostName2 = "jclouds-test2";
   
   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getServerClient();
      serverStatusChecker = createServer(testHostName1);
      testServerId = serverStatusChecker.getServerId();
   }

   @AfterGroups(groups = {"live"})
   public void tearDown() {
      client.destroyServer(testServerId, ServerDestroyOptions.Builder.discardIp());
      if (testServerId2 != null) {
         client.destroyServer(testServerId2, ServerDestroyOptions.Builder.discardIp());
      }
      super.tearDown();
   }

   private ServerClient client;
   private ServerStatusChecker serverStatusChecker;
   private String testServerId;
   private String testServerId2;

   @BeforeMethod
   public void makeSureServerIsRunning() throws Exception {
      serverStatusChecker.apply(ServerState.RUNNING);
   }
   
   @Test
   public void testAllowedArguments() throws Exception {
      Map<String,ServerAllowedArguments> templates = client.getServerAllowedArguments();
      
      assertTrue(templates.containsKey("OpenVZ"));
      assertTrue(templates.containsKey("Xen"));
      
      checkAllowedArguments(templates.get("OpenVZ"));
      checkAllowedArguments(templates.get("Xen"));
   }

   private void checkAllowedArguments(ServerAllowedArguments t) {
      assertNotNull(t);

      assert t.getDataCenters().size() > 0 : t;
      assert t.getCpuCores().size() > 0 : t;
      assert t.getDiskSizes().size() > 0 : t;
      assert t.getMemorySizes().size() > 0 : t;
      assert t.getTemplates().size() > 0 : t;
      assert t.getTransfers().size() > 0 : t;
      assert t.getTransfers().size() > 0 : t;
   }
   
   @Test
   public void testListTemplates() throws Exception {
      Set<ServerTemplate> templates = client.getTemplates();

      for(ServerTemplate template : templates) {
         checkTemplate(template);
      }
   }
   
   private void checkTemplate(ServerTemplate t) {
      assertNotNull(t);
      assertNotNull(t.getName());
      assertNotNull(t.getOs());

      assertNotNull(t.getPlatform());
      assert t.getMinDiskSize() > 0 : t;
      assert t.getMinMemSize() > 0 : t;
    }
   
   @Test
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

   @Test
   public void testServerDetails() throws Exception {
      ServerDetails details = client.getServerDetails(testServerId);
      checkServer(details);
      assertEquals("Ubuntu 10.04 LTS 32-bit", details.getTemplate());
      assertEquals("Falkenberg", details.getDatacenter());
      assertEquals("OpenVZ", details.getPlatform());
      assertEquals(5, details.getDisk());
      assertEquals(512, details.getMemory());
      assertEquals(1, details.getCpuCores());
      assertEquals(50, details.getTransfer());
   }

   @Test
   public void testServerStatus() throws Exception {
      ServerStatus newStatus = client.getServerStatus(testServerId);
      checkStatus(newStatus);
   }

   @Test(enabled=false) // TODO work a better plan
   public void testRebootServer() throws Exception {
      long uptime = 0;
      
      while(uptime < 20) {
         uptime = client.getServerStatus(testServerId).getUptime();
      }
      
      assertTrue(uptime > 19);
      
      client.rebootServer(testServerId);
      
      Thread.sleep(1000);

      uptime = client.getServerStatus(testServerId).getUptime();
      
      assertTrue(uptime < 20);

      assertTrue(serverStatusChecker.apply(ServerState.RUNNING));
   }

   @Test(enabled=false) // TODO
   public void testStopAndStartServer() throws Exception {
      client.stopServer(testServerId);

      assertTrue(serverStatusChecker.apply(ServerState.STOPPED));

      client.startServer(testServerId);

      assertTrue(serverStatusChecker.apply(ServerState.RUNNING));
   }


   @Test
   public void testServerLimits() throws Exception {
      Map<String, ServerLimit> limits = client.getServerLimits(testServerId);
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

   @Test
   public void testServerConsole() throws Exception {
      ServerConsole console = client.getServerConsole(testServerId);
      assertNotNull(console);
      assertNotNull(console.getHost());
      assertTrue(console.getPort() > 0 && console.getPort() < 65537);
      assertNotNull(console.getPassword());
   }

   // takes a few minutes and requires an extra server (using 2 already)
   @Test(enabled=false)
   public void testCloneServer() throws Exception {
      ServerCreated testServer2 = client.cloneServer(testServerId, testHostName2, ServerCloneOptions.Builder.cpucores(1));

      assertNotNull(testServer2.getId());
      assertEquals(testServer2.getHostname(), "jclouds-test2");
      assertTrue(testServer2.getIps().isEmpty());
      
      testServerId2 = testServer2.getId();

      RetryablePredicate<ServerState> cloneChecker = new ServerStatusChecker(client, testServerId2, 300, 10, TimeUnit.SECONDS);
      assertTrue(cloneChecker.apply(ServerState.STOPPED));

      client.startServer(testServer2.getId());

      // TODO ServerStatus==STOPPED suggests the previous call to start should have worked
      cloneChecker = new RetryablePredicate<ServerState>(
            new Predicate<ServerState>() {

               public boolean apply(ServerState value) {
                  ServerStatus status = client.getServerStatus(testServerId2, ServerStatusOptions.Builder.state());
                  if (status.getState() == value) {
                     return true;
                  }

                  client.startServer(testServerId2);
                  return false;
               }

            }, 300, 10, TimeUnit.SECONDS);

      assertTrue(cloneChecker.apply(ServerState.RUNNING)

      );
   }

   private void checkServer(ServerDetails server) {
      // description can be null
      assert server.getCpuCores() > 0 : server;
      assert server.getDisk() > 0 : server;
      assert server.getMemory() > 0 : server;
      assert server.getCost() != null;
      assert server.getTransfer() > 0 : server;

      assertNotNull(server.getTemplate());
      assertNotNull(server.getIps());
   }

   private void checkStatus(ServerStatus status) {
      assertNotNull(status.getState());
      assertNotNull(status.getUptime());

      assertNotNull(status.getBandwidth());
      assert status.getBandwidth().getToday() >= 0 : status;
      assert status.getBandwidth().getLast30Days() >= 0 : status;
      assert status.getBandwidth().getMax() >= 0 : status;

      assertNotNull(status.getCpu());
      assert status.getCpu().getSystem() >= 0.0 : status;
      assert status.getCpu().getUser() >= 0.0 : status;
      if (status.getCpu().getNice() != null) {
         assert status.getCpu().getNice() >= 0.0 : status;
      }
      assert status.getCpu().getIdle() >= 0.0 : status;
      assertNotNull(status.getCpu().getUnit());

      assertNotNull(status.getDisk());
      assert status.getDisk().getSize() >= 0 : status;
      assert status.getDisk().getUsed() >= 0 : status;
      assertNotNull(status.getDisk().getUnit());

      assertNotNull(status.getMemory());
      assert status.getMemory().getSize() > 0 : status;
      assert status.getMemory().getUsage() >= 0 : status;
      assertNotNull(status.getMemory().getUnit());
   }
}
