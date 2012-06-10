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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.VirtualInterface;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VirtualInterfaceClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VirtualInterfaceClientLiveTest", singleThreaded = true)
public class VirtualInterfaceClientLiveTest extends BaseNovaClientLiveTest {
   private Optional<VirtualInterfaceClient> clientOption;
   private String zone;


   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      clientOption = novaContext.getApi().getVirtualInterfaceExtensionForZone(zone);
   }

   public void testListVirtualInterfaces() {
      if (clientOption.isPresent()) {
         Server testServer = null;
         try {
            testServer = createServerInZone(zone);
            Set<VirtualInterface> results = clientOption.get().listVirtualInterfacesForServer(testServer.getId());
            for (VirtualInterface vif : results) {
               assertNotNull(vif.getId());
               assertNotNull(vif.getMacAddress());
            }
         } finally {
            if (testServer != null) {
               novaContext.getApi().getServerClientForZone(zone).deleteServer(testServer.getId());
            }
         }
   }
}
}
