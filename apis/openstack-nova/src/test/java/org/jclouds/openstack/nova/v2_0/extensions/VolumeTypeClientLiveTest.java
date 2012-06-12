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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.VolumeType;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateVolumeTypeOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VolumeTypeClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VolumeTypeClientLiveTest", singleThreaded = true)
public class VolumeTypeClientLiveTest extends BaseNovaClientLiveTest {

   private Optional<VolumeTypeClient> volumeTypeOption;
   private String zone;

   private VolumeType testVolumeType;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      volumeTypeOption = novaContext.getApi().getVolumeTypeExtensionForZone(zone);
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDown() {
      if (volumeTypeOption.isPresent()) {
         if (testVolumeType != null) {
            final String id = testVolumeType.getId();
            assertTrue(volumeTypeOption.get().deleteVolumeType(id));
            assertTrue(new RetryablePredicate<VolumeTypeClient>(new Predicate<VolumeTypeClient>() {
               @Override
               public boolean apply(VolumeTypeClient volumeClient) {
                  return volumeClient.getVolumeType(id) == null;
               }
            }, 5 * 1000L).apply(volumeTypeOption.get()));
         }
      }
      super.tearDown();
   }

   public void testCreateVolumeType() {
      if (volumeTypeOption.isPresent()) {
         testVolumeType = volumeTypeOption.get().createVolumeType(
               "jclouds-test-1", CreateVolumeTypeOptions.Builder.specs(ImmutableMap.of("test", "value1")));
         assertTrue(new RetryablePredicate<VolumeTypeClient>(new Predicate<VolumeTypeClient>() {
            @Override
            public boolean apply(VolumeTypeClient volumeTypeClient) {
               return volumeTypeClient.getVolumeType(testVolumeType.getId()) != null;
            }
         }, 180 * 1000L).apply(volumeTypeOption.get()));

         assertEquals(volumeTypeOption.get().getVolumeType(testVolumeType.getId()).getName(), "jclouds-test-1");
         assertEquals(volumeTypeOption.get().getVolumeType(testVolumeType.getId()).getExtraSpecs(), ImmutableMap.of("test", "value1"));
      }
   }

   @Test(dependsOnMethods = "testCreateVolumeType")
   public void testListVolumeTypes() {
      if (volumeTypeOption.isPresent()) {
         Set<VolumeType> volumeTypes = volumeTypeOption.get().listVolumeTypes();
         assertNotNull(volumeTypes);
         boolean foundIt = false;
         for (VolumeType vt : volumeTypes) {
            VolumeType details = volumeTypeOption.get().getVolumeType(vt.getId());
            assertNotNull(details);
            if (Objects.equal(details.getId(), testVolumeType.getId())) {
               foundIt = true;
            }
         }
         assertTrue(foundIt, "Failed to find the volume type we created in listVolumeTypes() response");
      }
   }

   @Test(dependsOnMethods = "testCreateVolumeType")
   public void testExtraSpecs() {
      if (volumeTypeOption.isPresent()) {
         assertEquals(volumeTypeOption.get().getAllExtraSpecs(testVolumeType.getId()), ImmutableMap.of("test", "value1"));
         assertEquals(volumeTypeOption.get().getExtraSpec(testVolumeType.getId(), "test"),  "value1");
         assertTrue(volumeTypeOption.get().setAllExtraSpecs(testVolumeType.getId(), ImmutableMap.of("test1", "wibble")));
      }
   }

   @Test(dependsOnMethods = "testCreateVolumeType")
   public void testUpdateIndividualSpec() {
      if (volumeTypeOption.isPresent()) {
         assertTrue(volumeTypeOption.get().setExtraSpec(testVolumeType.getId(), "test1", "freddy"));
         assertEquals(volumeTypeOption.get().getExtraSpec(testVolumeType.getId(), "test1"), "freddy");
      }
   }
}
