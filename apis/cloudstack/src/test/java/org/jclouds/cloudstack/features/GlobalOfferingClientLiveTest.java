/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType.OPTIONAL;
import static org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType.REQUIRED;
import static org.jclouds.cloudstack.options.CreateDiskOfferingOptions.Builder.diskSizeInGB;
import static org.jclouds.cloudstack.options.CreateServiceOfferingOptions.Builder.highlyAvailable;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.UpdateDiskOfferingOptions;
import org.jclouds.cloudstack.options.UpdateNetworkOfferingOptions;
import org.jclouds.cloudstack.options.UpdateServiceOfferingOptions;
import org.jclouds.logging.Logger;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code GlobalOfferingClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalOfferingClientLiveTest")
public class GlobalOfferingClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test(groups = "live", enabled = true)
   public void testCreateServiceOffering() throws Exception {
      skipIfNotGlobalAdmin();

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
      assertNotNull(offering.getId());
      assertEquals(offering.getCpuNumber(), 2);
      assertEquals(offering.getCpuSpeed(), 1024);
      assertEquals(offering.getMemory(), 2048);
      assertTrue(offering.supportsHA());
      assertEquals(offering.getStorageType(), StorageType.LOCAL);
   }

   @Test(groups = "live", enabled = true)
   public void testCreateDiskOffering() throws Exception {
      skipIfNotGlobalAdmin();

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

   @Test(groups = "live", enabled = true)
   public void testUpdateNetworkOffering() throws Exception {
      skipIfNotGlobalAdmin();

      NetworkOffering offering = getFirst(globalAdminClient.getOfferingClient().listNetworkOfferings(), null);
      assertNotNull(offering, "Unable to test, no network offering found.");

      String name = offering.getName();
      NetworkOfferingAvailabilityType availability = offering.getAvailability();

      try {
         NetworkOfferingAvailabilityType newValue = OPTIONAL;
         if (availability == OPTIONAL) {
            newValue = REQUIRED;
         }
         NetworkOffering updated = globalAdminClient.getOfferingClient().updateNetworkOffering(offering.getId(),
            UpdateNetworkOfferingOptions.Builder.name(prefix + name).availability(newValue));

         assertEquals(updated.getName(), prefix + name);
         assertEquals(updated.getAvailability(), newValue);

      } finally {
         globalAdminClient.getOfferingClient().updateNetworkOffering(offering.getId(),
            UpdateNetworkOfferingOptions.Builder.name(name).availability(availability));
      }
   }

}
