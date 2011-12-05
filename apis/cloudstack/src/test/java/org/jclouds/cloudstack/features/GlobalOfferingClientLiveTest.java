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
package org.jclouds.cloudstack.features;

import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.diskSizeInGB;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.highlyAvailable;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests behavior of {@code GlobalOfferingClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalOfferingClientLiveTest")
public class GlobalOfferingClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(groups = "live", enabled = true)
   public void testCreateServiceOffering() throws Exception {
      assertTrue(globalAdminEnabled, "Test cannot run without global admin identity and credentials");

      String name = prefix + "-test-create-service-offering";
      String displayText = name + "-display";
      ServiceOffering offering = null;
      try {
         offering = globalAdminClient.getOfferingClient().
            createServiceOffering(name, displayText, 2, 1024, 2048, highlyAvailable(true).storageType(StorageType.LOCAL));
         Logger.CONSOLE.info("Created Service Offering: " + offering);

         assertEquals(offering.getName(), name);
         assertEquals(offering.getDisplayText(), displayText);
         checkServiceOffering(offering);

         offering = globalAdminClient.getOfferingClient()
            .updateServiceOffering(offering.getId(),
               UpdateServiceOfferingOptions.Builder.name(name + "-2").displayText(displayText + "-2"));

         assertEquals(offering.getName(), name + "-2");
         assertEquals(offering.getDisplayText(), displayText + "-2");
         checkServiceOffering(offering);

      } finally {
         if (offering != null) {
            globalAdminClient.getOfferingClient().deleteServiceOffering(offering.getId());
         }
      }
   }

   private void checkServiceOffering(ServiceOffering offering) {
      assertTrue(offering.getId() > 0);
      assertEquals(offering.getCpuNumber(), 2);
      assertEquals(offering.getCpuSpeed(), 1024);
      assertEquals(offering.getMemory(), 2048);
      assertTrue(offering.supportsHA());
      assertEquals(offering.getStorageType(), StorageType.LOCAL);
   }

   @Test(groups = "live", enabled = true)
   public void testCreateDiskOffering() throws Exception {
      assertTrue(globalAdminEnabled, "Test cannot run without global admin identity and credentials");

      String name = prefix + "-test-create-disk-offering";
      String displayText = name + "-display";
      DiskOffering offering = null;
      try {
         offering = globalAdminClient.getOfferingClient().
            createDiskOffering(name, displayText,
               diskSizeInGB(100).customized(true).tags(ImmutableSet.<String>of("dummy-tag")));

         assertEquals(offering.getName(), name);
         assertEquals(offering.getDisplayText(), displayText);
         checkDiskOffering(offering);

         offering = globalAdminClient.getOfferingClient().
            updateDiskOffering(offering.getId(),
               UpdateDiskOfferingOptions.Builder.name(name + "-2").displayText(displayText + "-2"));

         assertEquals(offering.getName(), name + "-2");
         assertEquals(offering.getDisplayText(), displayText + "-2");
         checkDiskOffering(offering);

      } finally {
         if (offering != null) {
            globalAdminClient.getOfferingClient().deleteDiskOffering(offering.getId());
         }
      }
   }

   private void checkDiskOffering(DiskOffering offering) {
      assertTrue(offering.isCustomized());
      assertEquals(offering.getDiskSize(), 100);
      assertTrue(offering.getTags().contains("dummy-tag"));
   }

}
