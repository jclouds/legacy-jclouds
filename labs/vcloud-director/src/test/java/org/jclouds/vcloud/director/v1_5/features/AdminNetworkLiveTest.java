/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.ExternalNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Network;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link AdminNetworkClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "network" }, singleThreaded = true, testName = "AdminNetworkLiveTest")
public class AdminNetworkLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String NETWORK = "AdminNetwork";

   /*
    * Convenience references to API clients.
    */
   private AdminNetworkClient networkClient;

   /*
    * Shared state between dependant tests.
    */
   Reference networkRef;
   Network network;
   
   @BeforeClass(inheritGroups = true)
   protected void setupRequiredClients() {
      networkClient = context.getApi().getAdminNetworkClient();
      networkRef = Reference.builder().href(networkURI).build().toAdminReference(endpoint);
   }
   
   @Test(testName = "GET /admin/network/{id}")
   public void testGetNetwork() {
      assertNotNull(networkRef, String.format(REF_REQ_LIVE, NETWORK));
      network = networkClient.getNetwork(networkRef.getHref());
      
      if(network instanceof ExternalNetwork) {
         Checks.checkExternalNetwork(Network.<ExternalNetwork>toSubType(network));
      } else if (network instanceof OrgNetwork) {
         Checks.checkOrgNetwork(Network.<OrgNetwork>toSubType(network));
      } else {
         fail(String.format(REQUIRED_VALUE_OBJECT_FMT, ".class", NETWORK, 
               network.getClass(),"ExternalNetwork,. OrgNetwork"));
      }
   }
   
   // PUT /admin/network/{id}
   
   // POST /admin/network/{id}/action/reset
}
