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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListDiskOfferingsOptions;
import org.jclouds.cloudstack.options.ListNetworkOfferingsOptions;
import org.jclouds.cloudstack.options.ListServiceOfferingsOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code OfferingClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "OfferingClientLiveTest")
public class OfferingClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListDiskOfferings() throws Exception {
      Set<DiskOffering> response = client.getOfferingClient().listDiskOfferings();
      assert null != response;
      long offeringCount = response.size();
      assertTrue(offeringCount >= 0);
      for (DiskOffering offering : response) {
         try {
           DiskOffering newDetails = Iterables.getOnlyElement(client.getOfferingClient().listDiskOfferings(
               ListDiskOfferingsOptions.Builder.id(offering.getId())));
           assertEquals(offering, newDetails);
           assertEquals(offering, client.getOfferingClient().getDiskOffering(offering.getId()));
           assert offering.getId() != null : offering;
           assert offering.getName() != null : offering;
           assert offering.getCreated() != null : offering;
           assert offering.getDisplayText() != null : offering;
           assert offering.getDiskSize() > 0 || (offering.getDiskSize() == 0 && offering.isCustomized()) : offering;
           assert offering.getTags() != null : offering;

         } catch (NoSuchElementException e) {
            // This bug is present both in 2.2.8 and 2.2.12
            assertTrue(Predicates.in(ImmutableSet.of("2.2.8", "2.2.12")).apply(apiVersion));
         }
      }
   }

   public void testListServiceOfferings() throws Exception {
      Set<ServiceOffering> response = client.getOfferingClient().listServiceOfferings();
      assert null != response;
      long offeringCount = response.size();
      assertTrue(offeringCount >= 0);
      for (ServiceOffering offering : response) {
         ServiceOffering newDetails = Iterables.getOnlyElement(client.getOfferingClient().listServiceOfferings(
               ListServiceOfferingsOptions.Builder.id(offering.getId())));
         assertEquals(offering, newDetails);

         assert offering.getId() != null : offering;
         assert offering.getName() != null : offering;
         assert offering.getDisplayText() != null : offering;
         assert offering.getCpuNumber() > 0 : offering;
         assert offering.getCpuSpeed() > 0 : offering;
         assert offering.getMemory() > 0 : offering;
         assert offering.getStorageType() != null && StorageType.UNRECOGNIZED != offering.getStorageType() : offering;
         assert offering.getTags() != null : offering;
      }
   }

   public void testListNetworkOfferings() throws Exception {
      Set<NetworkOffering> response = client.getOfferingClient().listNetworkOfferings();
      assert null != response;
      long offeringCount = response.size();
      assertTrue(offeringCount >= 0);
      for (NetworkOffering offering : response) {
         NetworkOffering newDetails = Iterables.getOnlyElement(client.getOfferingClient().listNetworkOfferings(
               ListNetworkOfferingsOptions.Builder.id(offering.getId())));
         assertEquals(offering, newDetails);
         assertEquals(offering, client.getOfferingClient().getNetworkOffering(offering.getId()));
         assert offering.getId() != null : offering;
         assert offering.getName() != null : offering;
         assert offering.getDisplayText() != null : offering;
         assert offering.getMaxConnections() == null || offering.getMaxConnections() > 0 : offering;
         assert offering.getTrafficType() != null && TrafficType.UNRECOGNIZED != offering.getTrafficType() : offering;
         assert offering.getTags() != null : offering;
      }
   }
}
