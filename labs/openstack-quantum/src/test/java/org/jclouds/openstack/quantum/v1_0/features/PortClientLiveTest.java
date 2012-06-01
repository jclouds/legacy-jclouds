/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.quantum.v1_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.quantum.v1_0.domain.Attachment;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.openstack.quantum.v1_0.domain.PortDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Tests PortClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "PortClientLiveTest", singleThreaded = true)
public class PortClientLiveTest extends BaseQuantumClientLiveTest {

   public void testListPorts() {
      for (String regionId : quantumContext.getApi().getConfiguredRegions()) {
         NetworkClient netClient = quantumContext.getApi().getNetworkClientForRegion(regionId);
         Set<Reference> nets = netClient.listReferences();
         for(Reference net : nets) {
            PortClient portClient = quantumContext.getApi().getPortClientForRegionAndNetwork(regionId, net.getId());
            Set<Reference> portRefs = portClient.listReferences();
            Set<Port> ports = portClient.list();
            
            assertEquals(portRefs.size(), ports.size());
            for (Port port : ports) {
               assertTrue(portRefs.contains(Reference.builder().id(port.getId()).build()));
            }
         }
      }
   }

   public void testCreateUpdateAndDeletePort() {
      for (String regionId : quantumContext.getApi().getConfiguredRegions()) {
         NetworkClient netClient = quantumContext.getApi().getNetworkClientForRegion(regionId);
         Reference net = netClient.create("jclouds-port-test");
         assertNotNull(net);
         PortClient portClient = quantumContext.getApi().getPortClientForRegionAndNetwork(regionId, net.getId());

         Reference portRef = portClient.create();
         assertNotNull(portRef);
         
         Port port = portClient.get(portRef.getId());
         PortDetails portDetails = portClient.getDetails(portRef.getId());
         NetworkDetails networkDetails = netClient.getDetails(net.getId());

         assertEquals(port.getState(), portDetails.getState());

         for(Port checkme : ImmutableList.of(port, portDetails, Iterables.getOnlyElement(networkDetails.getPorts()))) {
            assertEquals(checkme.getId(), portRef.getId());
         }

         assertTrue(portClient.updateState(portRef.getId(), Port.State.DOWN));
         
         port = portClient.get(portRef.getId());
         portDetails = portClient.getDetails(portRef.getId());

         for(Port checkme : ImmutableList.of(port, portDetails)) {
            assertEquals(checkme.getId(), portRef.getId());
            assertEquals(checkme.getState(), Port.State.DOWN);
         }
         
         assertTrue(portClient.plugAttachment(port.getId(), "jclouds-live-test"));

         Attachment attachment = portClient.showAttachment(port.getId());
         portDetails = portClient.getDetails(portRef.getId());

         for(Attachment checkme : ImmutableList.of(attachment, portDetails.getAttachment())) {
            assertNotNull(checkme);
            assertEquals(checkme.getId(), "jclouds-live-test");
         }
         
         assertTrue(portClient.unplugAttachment(port.getId()));

         assertTrue(portClient.delete(portRef.getId()));
         assertTrue(netClient.delete(net.getId()));
      }
   }

   @Test(enabled=false) // assuming attachmentId matters in the wild
   public void testAttachAndDetachPort() {
      for (String regionId : quantumContext.getApi().getConfiguredRegions()) {
         NetworkClient netClient = quantumContext.getApi().getNetworkClientForRegion(regionId);
         Reference net = netClient.create("jclouds-attach-test");
         assertNotNull(net);

         PortClient portClient = quantumContext.getApi().getPortClientForRegionAndNetwork(regionId, net.getId());

         Reference port = portClient.create();
         assertNotNull(port);

         assertTrue(portClient.plugAttachment(port.getId(), "jclouds-live-test"));

         Attachment attachment = portClient.showAttachment(port.getId());
         PortDetails portDetails = portClient.getDetails(port.getId());

         for(Attachment checkme : ImmutableList.of(attachment, portDetails.getAttachment())) {
            assertNotNull(checkme);
            assertEquals(checkme.getId(), "jclouds-live-test");
         }

         assertTrue(portClient.unplugAttachment(port.getId()));

         assertTrue(portClient.delete(port.getId()));
         assertTrue(netClient.delete(net.getId()));
      }
   }
}