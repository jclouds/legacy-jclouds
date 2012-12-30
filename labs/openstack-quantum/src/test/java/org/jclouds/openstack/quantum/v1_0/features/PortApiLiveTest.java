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
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Tests PortApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "PortApiLiveTest", singleThreaded = true)
public class PortApiLiveTest extends BaseQuantumApiLiveTest {

   public void testListPorts() {
      for (String zoneId : quantumContext.getApi().getConfiguredZones()) {
         NetworkApi netApi = quantumContext.getApi().getNetworkApiForZone(zoneId);
         Set<? extends Reference> nets = netApi.listReferences().toSet();
         for(Reference net : nets) {
            PortApi portApi = quantumContext.getApi().getPortApiForZoneAndNetwork(zoneId, net.getId());
            Set<? extends Reference> portRefs = portApi.listReferences().toSet();
            Set<? extends Port> ports = portApi.list().toSet();
            
            assertEquals(portRefs.size(), ports.size());
            for (Port port : ports) {
               assertTrue(portRefs.contains(Reference.builder().id(port.getId()).build()));
            }
         }
      }
   }

   public void testCreateUpdateAndDeletePort() {
      for (String zoneId : quantumContext.getApi().getConfiguredZones()) {
         NetworkApi netApi = quantumContext.getApi().getNetworkApiForZone(zoneId);
         Reference net = netApi.create("jclouds-port-test");
         assertNotNull(net);
         PortApi portApi = quantumContext.getApi().getPortApiForZoneAndNetwork(zoneId, net.getId());

         Reference portRef = portApi.create();
         assertNotNull(portRef);
         
         Port port = portApi.get(portRef.getId());
         PortDetails portDetails = portApi.getDetails(portRef.getId());
         NetworkDetails networkDetails = netApi.getDetails(net.getId());

         assertEquals(port.getState(), portDetails.getState());

         for(Port checkme : ImmutableList.of(port, portDetails, Iterables.getOnlyElement(networkDetails.getPorts()))) {
            assertEquals(checkme.getId(), portRef.getId());
         }

         assertTrue(portApi.updateState(portRef.getId(), Port.State.DOWN));
         
         port = portApi.get(portRef.getId());
         portDetails = portApi.getDetails(portRef.getId());

         for(Port checkme : ImmutableList.of(port, portDetails)) {
            assertEquals(checkme.getId(), portRef.getId());
            assertEquals(checkme.getState(), Port.State.DOWN);
         }
         
         assertTrue(portApi.plugAttachment(port.getId(), "jclouds-live-test"));

         Attachment attachment = portApi.showAttachment(port.getId());
         portDetails = portApi.getDetails(portRef.getId());

         for(Attachment checkme : ImmutableList.of(attachment, portDetails.getAttachment())) {
            assertNotNull(checkme);
            assertEquals(checkme.getId(), "jclouds-live-test");
         }
         
         assertTrue(portApi.unplugAttachment(port.getId()));

         assertTrue(portApi.delete(portRef.getId()));
         assertTrue(netApi.delete(net.getId()));
      }
   }

   @Test(enabled=false) // assuming attachmentId matters in the wild
   public void testAttachAndDetachPort() {
      for (String zoneId : quantumContext.getApi().getConfiguredZones()) {
         NetworkApi netApi = quantumContext.getApi().getNetworkApiForZone(zoneId);
         Reference net = netApi.create("jclouds-attach-test");
         assertNotNull(net);

         PortApi portApi = quantumContext.getApi().getPortApiForZoneAndNetwork(zoneId, net.getId());

         Reference port = portApi.create();
         assertNotNull(port);

         assertTrue(portApi.plugAttachment(port.getId(), "jclouds-live-test"));

         Attachment attachment = portApi.showAttachment(port.getId());
         PortDetails portDetails = portApi.getDetails(port.getId());

         for(Attachment checkme : ImmutableList.of(attachment, portDetails.getAttachment())) {
            assertNotNull(checkme);
            assertEquals(checkme.getId(), "jclouds-live-test");
         }

         assertTrue(portApi.unplugAttachment(port.getId()));

         assertTrue(portApi.delete(port.getId()));
         assertTrue(netApi.delete(net.getId()));
      }
   }
}
