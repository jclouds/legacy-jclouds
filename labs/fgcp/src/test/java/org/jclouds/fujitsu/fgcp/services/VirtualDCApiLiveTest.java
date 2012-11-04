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
package org.jclouds.fujitsu.fgcp.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "VirtualDCApiLiveTest")
public class VirtualDCApiLiveTest extends BaseFGCPApiLiveTest {

   private VirtualDCApi api;

   @BeforeGroups(groups = { "live" })
   public void setupContext() {
      super.setupContext();
      api = fgcpContext.getApi().getVirtualDCApi();
   }

   public void testListVirtualSystems() {
/*      Properties overrides = setupProperties();
      RestContext<FGCPClient, FGCPAsyncClientTest> context = new RestContextFactory().createContext(provider, ImmutableSet.<Module> of(new Log4JLoggingModule()),
            overrides);*/

      Set<VSystem> vsysSet = api.listVirtualSystems();
      assertNotNull(vsysSet, "vsysSet");
      assertTrue(vsysSet.size() > 0, "vsysSet.size() should be greater than 0");
      for (VSystem vsys : vsysSet) {
         System.out.println(vsys);
      }
   }

/*   public void testCreateVirtualSystem() {
      String vsysId = api.createVirtualSystem("abc", "def");

      assertNotNull(vsysId, "vsysId");
      assertNotEquals("", vsysId, "vsysId is empty (\"\")");
      System.out.println("vsysId: " + vsysId);
   }*/

   public void testListServerTypes() {
      Set<ServerType> serverTypes = api.listServerTypes();

      assertNotNull(serverTypes, "serverTypes");
      assertEquals(4, serverTypes.size(), "serverTypes.size should return 4, not " + serverTypes.size());

//      System.out.println("listServerTypes: " + serverTypes);
   }



}
