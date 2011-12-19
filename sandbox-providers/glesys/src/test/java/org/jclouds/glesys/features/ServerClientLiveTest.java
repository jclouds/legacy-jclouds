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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import org.jclouds.glesys.domain.*;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.annotation.Nullable;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code ServerClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ServerClientLiveTest")
public class ServerClientLiveTest extends BaseGleSYSClientLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getServerClient();
   }

   @AfterGroups(groups={"live"})
   public void teardownClient() {
      if (serverId != null) {
         client.stopServer(serverId);
         assert notRunning.apply(client);
         client.destroyServer(serverId, 0);
      }
   }
   
   public static class ServerStatePredicate implements Predicate<ServerClient> {
      private ServerState state;
      private String serverId;
      public ServerStatePredicate(ServerState state, String serverId) {
         this.state = state;
         this.serverId = serverId;
      }
      @Override
      public boolean apply(ServerClient client) {
         return client.getServerStatus(serverId) != null && client.getServerStatus(serverId).getState() == state;
      }
   }
   
   private ServerClient client;
   
   private String serverId;
   private RetryablePredicate<ServerClient> running;
   private RetryablePredicate<ServerClient> notRunning;
   
   @Test
   public void testCreateServer() throws Exception {
      ServerCreated server = client.createServer("Falkenberg", "OpenVZ", "jclouds-test2", "Ubuntu 10.04 LTS 32-bit", 5, 512, 1, "password", 50, "jclouds live test server", null);

      serverId = server.getId();

      running = new RetryablePredicate<ServerClient>(new ServerStatePredicate(ServerState.RUNNING, server.getId()), 180, 20, TimeUnit.SECONDS) ;
      notRunning = new RetryablePredicate<ServerClient>(new ServerStatePredicate(ServerState.UNRECOGNIZED, server.getId()), 180, 20, TimeUnit.SECONDS);

      assertNotNull(serverId);
      assertEquals(server.getHostname(), "jclouds-test2");
      assertTrue(!server.getIps().isEmpty(), "Server has no ip address!");
      
      for(int i=0; i<600; i++) {
         System.out.println(client.getServerStatus(serverId));
         Thread.sleep(1000);
      }

      fail();
      assert running.apply(client);
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testStartServer() throws Exception {
      client.startServer(serverId);
      assert running.apply(client);
   }

   @Test(dependsOnMethods = "testStartServer")
   public void testStopServer() throws Exception {
      client.stopServer(serverId);
      ServerStatus details = client.getServerStatus(serverId);
      
      // TODO correct status of stopped server
      assert notRunning.apply(client);
   }

   @Test(dependsOnMethods = "testCreateServer")
   public void testRebootServer() throws Exception {
      client.rebootServer(serverId);
      assert notRunning.apply(client);
      assert running.apply(client);
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
      Map<String,Set<ServerTemplate>> templates = client.getTemplates();

      assertTrue(templates.containsKey("OpenVZ"));
      assertTrue(templates.containsKey("Xen"));

      for(ServerTemplate template : templates.get("OpenVZ")) {
         checkTemplate(template, "OpenVZ");
      }

      for(ServerTemplate template : templates.get("Xen")) {
         checkTemplate(template, "Xen");
      }
   }
   
   private void checkTemplate(ServerTemplate t, String platform) {
      assertNotNull(t);
      assertNotNull(t.getName());
      assertNotNull(t.getOs());

      assertEquals(t.getPlatform(), platform);
      assert t.getMinDiskSize() > 0 : t;
      assert t.getMinMemSize() > 0 : t;
    }
   
   @Test
   public void testListServers() throws Exception {
      Set<Server> response = client.listServers();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Server server : response) {
         ServerDetails newDetails = client.getServerDetails(server.getId());
         assertEquals(newDetails.getId(), server.getId());
         assertEquals(newDetails.getHostname(), server.getHostname());
         assertEquals(newDetails.getPlatform(), server.getPlatform());
         assertEquals(newDetails.getDatacenter(), server.getDatacenter());
         checkServer(newDetails);
         ServerStatus newStatus = client.getServerStatus(server.getId());
         checkStatus(newStatus);
      }
   }

   private void checkServer(ServerDetails server) {
      // description can be null
      assert server.getCpuCores() > 0 : server;
      assert server.getDisk() > 0 : server;
      assert server.getMemory() > 0 : server;
      assert server.getCost() != null;
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
      assert status.getCpu().getNice() >= 0.0 : status;
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
