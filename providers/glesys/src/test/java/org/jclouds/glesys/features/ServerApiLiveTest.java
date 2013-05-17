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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.Console;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ResourceStatus;
import org.jclouds.glesys.domain.ResourceUsage;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.Server.State;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerLimit;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.internal.BaseGleSYSApiWithAServerLiveTest;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.glesys.options.UpdateServerOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Tests behavior of {@code ServerApi}
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ServerApiLiveTest", singleThreaded = true)
public class ServerApiLiveTest extends BaseGleSYSApiWithAServerLiveTest {
   public static final String testHostName2 = "jclouds-test2";
   
   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setup() {
      hostName = hostName + "-server";
      super.setup();
      serverApi = api.getServerApi();
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   public void tearDown() {
      if (testServerId2 != null) {
         serverApi.destroy(testServerId2, DestroyServerOptions.Builder.discardIp());
      }
      super.tearDown();
   }

   private ServerApi serverApi;
   private String testServerId2;

   @BeforeMethod
   public void makeSureServerIsRunning() throws Exception {
      serverStatusChecker.apply(Server.State.RUNNING);
   }
   
   @Test
   public void testAllowedArguments() throws Exception {
      Map<String,AllowedArgumentsForCreateServer> templates = serverApi.getAllowedArgumentsForCreateByPlatform();
      
      assertTrue(templates.containsKey("OpenVZ"));
      assertTrue(templates.containsKey("Xen"));
      
      checkAllowedArguments(templates.get("OpenVZ"));
      checkAllowedArguments(templates.get("Xen"));
   }

   private void checkAllowedArguments(AllowedArgumentsForCreateServer t) {
      assertNotNull(t);

      assert t.getDataCenters().size() > 0 : t;
      assert t.getCpuCoreOptions().getAllowedUnits().size() > 0 : t;
      assert t.getDiskSizesInGB().getAllowedUnits().size() > 0 : t;
      assert t.getMemorySizesInMB().getAllowedUnits().size() > 0 : t;
      assert t.getTemplateNames().size() > 0 : t;
      assert t.getTransfersInGB().getAllowedUnits().size() > 0 : t;
      assert t.getTransfersInGB().getAllowedUnits().size() > 0 : t;
   }
   
   public void testListTemplates() throws Exception {
      FluentIterable<OSTemplate> oSTemplates = serverApi.listTemplates();

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
      FluentIterable<Server> response = serverApi.list();
      assertNotNull(response);
      assertTrue(response.size() > 0);

      for (Server server : response) {
         ServerDetails newDetails = serverApi.get(server.getId());
         assertEquals(newDetails.getId(), server.getId());
         assertEquals(newDetails.getHostname(), server.getHostname());
         assertEquals(newDetails.getPlatform(), server.getPlatform());
         assertEquals(newDetails.getDatacenter(), server.getDatacenter());
         checkServer(newDetails);
      }
   }

   public void testServerDetails() throws Exception {
      ServerDetails details = serverApi.get(serverId);
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
      ServerStatus newStatus = serverApi.getStatus(serverId);
      checkStatus(newStatus);
   }

   public void testUpdateServer() throws Exception {
      ServerDetails edited = serverApi.update(serverId, UpdateServerOptions.Builder.description("this is a different description!"));
      assertEquals(edited.getDescription(), "this is a different description!");

      edited = serverApi.update(serverId, UpdateServerOptions.Builder.description("another description!").hostname("host-name1"));
      assertEquals(edited.getDescription(), "another description!");
      assertEquals(edited.getHostname(), "host-name1");

      edited = serverApi.resetPassword(serverId, "anotherpass");
      assertEquals(edited.getHostname(), "host-name1");

      edited = serverApi.update(serverId, UpdateServerOptions.Builder.hostname(hostName));
      assertEquals(edited.getHostname(), hostName);
   }

   @Test
   public void testRebootServer() throws Exception {
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));

      serverApi.reboot(serverId);
      
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));
   }

   @Test
   public void testStopAndStartServer() throws Exception {
      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));

      serverApi.stop(serverId);

      assertTrue(serverStatusChecker.apply(Server.State.STOPPED));

      serverApi.start(serverId);

      assertTrue(serverStatusChecker.apply(Server.State.RUNNING));
   }

   public void testServerLimits() throws Exception {
      Map<String, ServerLimit> limits = serverApi.getLimits(serverId);
      assertNotNull(limits);
      for (Map.Entry<String, ServerLimit> entry : limits.entrySet()) {
         assertNotNull(entry.getKey());
         assertNotNull(entry.getValue());
         ServerLimit limit = entry.getValue();
         assertTrue(limit.getBarrier() >= 0);
         assertEquals(0, limit.getFailCount());
         assertTrue(limit.getHeld() >= 0);
         assertTrue(limit.getLimit() > 0);
         assertTrue(limit.getMaxHeld() >= 0);
      }
   }

   public void testResourceUsage() throws Exception {
      // test server has only been in existence for less than a minute - check all servers
      for (Server server : serverApi.list()) {
         try {
            ResourceUsage usage = serverApi.getResourceUsage(server.getId(), "diskioread", "minute");
            assertEquals(usage.getInfo().getResource(), "diskioread");
            assertEquals(usage.getInfo().getResolution(), "minute");
         } catch (UnsupportedOperationException e) {

         }
         try {
            ResourceUsage usage = serverApi.getResourceUsage(server.getId(), "cpuusage", "minute");
            assertEquals(usage.getInfo().getResource(), "cpuusage");
            assertEquals(usage.getInfo().getResolution(), "minute");
         } catch (UnsupportedOperationException e) {

         }
      }
   }

   public void testConsole() throws Exception {
      Console console = serverApi.getConsole(serverId);
      assertNotNull(console);
      assertNotNull(console.getHost());
      assertTrue(console.getPort() > 0 && console.getPort() < 65537);
      assertNotNull(console.getPassword());
   }

   // takes a few minutes and requires an extra server (used 1 already)
   @Test(enabled=false)
   public void testCloneServer() throws Exception {
      ServerDetails testServer2 = serverApi.clone(serverId, testHostName2, CloneServerOptions.Builder.cpucores(1));

      assertNotNull(testServer2.getId());
      assertEquals(testServer2.getHostname(), "jclouds-test2");
      assertTrue(testServer2.getIps().isEmpty());
      
      testServerId2 = testServer2.getId();

      Predicate<State> cloneChecker = statusChecker(serverApi, testServerId2); 
      assertTrue(cloneChecker.apply(Server.State.STOPPED));

      serverApi.start(testServer2.getId());

      // TODO ServerStatus==STOPPED suggests the previous call to start should have worked
      cloneChecker = retry(new Predicate<Server.State>() {
         public boolean apply(Server.State value) {
            ServerStatus status = serverApi.getStatus(testServerId2, ServerStatusOptions.Builder.state());
            if (status.getState() == value) {
               return true;
            }
            serverApi.start(testServerId2);
            return false;
         }
      }, 600, 30, SECONDS);

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
