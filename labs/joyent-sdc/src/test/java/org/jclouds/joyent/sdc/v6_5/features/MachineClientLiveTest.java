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
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Gerald Pereira
 */
@Test(groups = "live", testName = "MachineClientLiveTest")
public class MachineClientLiveTest extends BaseSDCClientLiveTest {

   @Test
   public void testListAndGetMachines() throws Exception {
      for (String datacenterId : sdcContext.getApi().getConfiguredDatacenters()) {
         MachineClient client = sdcContext.getApi().getMachineClientForDatacenter(datacenterId);
         Set<Machine> response = client.listMachines();
         assert null != response;
         for (Machine machine : response) {
            Machine newDetails = client.getMachine(machine.getId());
            assertEquals(newDetails.getId(), machine.getId());
            assertEquals(newDetails.getName(), machine.getName());
            assertEquals(newDetails.getType(), machine.getType());
            assertEquals(newDetails.getState(), machine.getState());
            assertEquals(newDetails.getDataset(), machine.getDataset());
            assertEquals(newDetails.getMemorySizeMb(), machine.getMemorySizeMb());
            assertEquals(newDetails.getDiskSizeGb(), machine.getDiskSizeGb());
            assertEquals(newDetails.getIps(), machine.getIps());
            assertEquals(newDetails.getCreated(), machine.getCreated());
            assertEquals(newDetails.getUpdated(), machine.getUpdated());
            assertEquals(newDetails.getMetadata(), machine.getMetadata());
         }
      }
   }
}
