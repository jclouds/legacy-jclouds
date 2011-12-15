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

import org.jclouds.glesys.domain.*;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

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

   private ServerClient client;
   
   @Test
   public void testAllowedArguments() throws Exception {
      Map<String,ServerAllowedArguments> templates = client.getAllowedArguments();
      
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
