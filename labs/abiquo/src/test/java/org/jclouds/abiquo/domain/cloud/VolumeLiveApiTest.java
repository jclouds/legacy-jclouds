/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.cloud;

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.cloud.VolumePredicates;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;

/**
 * Live integration tests for the {@link Volume} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "VolumeLiveApiTest")
public class VolumeLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testCreateVolume() {
      // We need the vdc-relative tier
      Tier tier = env.virtualDatacenter.findStorageTier(TierPredicates.name(env.tier.getName()));

      Volume volume = Volume.builder(env.context.getApiContext(), env.virtualDatacenter, tier)
            .name(PREFIX + "Hawaian volume").sizeInMb(32).build();
      volume.save();

      assertNotNull(volume.getId());
      assertNotNull(env.virtualDatacenter.getVolume(volume.getId()));
   }

   @Test(dependsOnMethods = "testCreateVolume")
   public void testFilterVolumes() {
      VolumeOptions validOptions = VolumeOptions.builder().has("hawa").build();
      VolumeOptions invalidOptions = VolumeOptions.builder().has("cacatua").build();

      List<VolumeManagementDto> volumes = env.cloudApi.listVolumes(env.virtualDatacenter.unwrap(), validOptions)
            .getCollection();

      assertEquals(volumes.size(), 1);

      volumes = env.cloudApi.listVolumes(env.virtualDatacenter.unwrap(), invalidOptions).getCollection();

      assertEquals(volumes.size(), 0);
   }

   @Test(dependsOnMethods = "testFilterVolumes")
   public void testUpdateVolume() {
      Volume volume = env.virtualDatacenter.findVolume(VolumePredicates.name(PREFIX + "Hawaian volume"));
      assertNotNull(volume);

      volume.setName("Hawaian volume updated");
      AsyncTask task = volume.update();
      assertNull(task);

      // Reload the volume to check
      Volume updated = env.virtualDatacenter.getVolume(volume.getId());
      assertEquals(updated.getName(), "Hawaian volume updated");
   }

   @Test(dependsOnMethods = "testUpdateVolume")
   public void testMoveVolume() {
      // Create the new virtual datacenter
      PrivateNetwork network = PrivateNetwork.builder(env.context.getApiContext()).name("DefaultNetwork")
            .gateway("192.168.1.1").address("192.168.1.0").mask(24).build();

      VirtualDatacenter newVdc = VirtualDatacenter.builder(env.context.getApiContext(), env.datacenter, env.enterprise)
            .name("New VDC").network(network).hypervisorType(env.machine.getType()).build();
      newVdc.save();
      assertNotNull(newVdc.getId());

      Volume volume = env.virtualDatacenter.findVolume(VolumePredicates.name("Hawaian volume updated"));
      assertNotNull(volume);

      volume.moveTo(newVdc);

      // Check that the underlying Dto has been updated to the new VDC
      assertTrue(volume.unwrap().getEditLink().getHref().startsWith(newVdc.unwrap().getEditLink().getHref()));

      // Move it back to the original VDC
      volume.moveTo(env.virtualDatacenter);

      // Check that the underlying Dto has been updated to the new VDC
      assertTrue(volume.unwrap().getEditLink().getHref()
            .startsWith(env.virtualDatacenter.unwrap().getEditLink().getHref()));

      // Tear down the virtual datacenter
      newVdc.delete();
   }

   @Test(dependsOnMethods = "testMoveVolume")
   public void testDeleteVolume() {
      Volume volume = env.virtualDatacenter.findVolume(VolumePredicates.name("Hawaian volume updated"));
      assertNotNull(volume);

      Integer id = volume.getId();
      volume.delete();

      assertNull(env.virtualDatacenter.getVolume(id));
   }

}
