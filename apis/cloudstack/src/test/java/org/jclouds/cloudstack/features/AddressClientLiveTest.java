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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code AddressClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "AddressClientLiveTest")
public class AddressClientLiveTest extends BaseCloudStackClientLiveTest {
   private boolean networksEnabled;

   @BeforeGroups(groups = "live")
   void networksEnabled() {
      networksEnabled = client.getNetworkClient().listNetworks().size() > 0;
   }

   private PublicIPAddress ip = null;

   public void testAssociateDisassociatePublicIPAddress() throws Exception {
      if (!networksEnabled)
         return;
      AsyncCreateResponse job = client.getAddressClient().associateIPAddressInZone(
            Iterables.get(client.getNetworkClient().listNetworks(), 0).getZoneId());
      checkState(jobComplete.apply(job.getJobId()), "job %s failed to complete", job.getJobId());
      ip = client.getAsyncJobClient().<PublicIPAddress> getAsyncJob(job.getJobId()).getResult();
      checkIP(ip);
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDownContext();
   }

   public void testListPublicIPAddresss() throws Exception {
      if (!networksEnabled)
         return;
      Set<PublicIPAddress> response = client.getAddressClient().listPublicIPAddresses();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (PublicIPAddress ip : response) {
         PublicIPAddress newDetails = getOnlyElement(client.getAddressClient().listPublicIPAddresses(
               ListPublicIPAddressesOptions.Builder.id(ip.getId())));
         assertEquals(ip.getId(), newDetails.getId());
         checkIP(ip);
      }
   }

   protected void checkIP(PublicIPAddress ip) {
      assertEquals(ip.getId(), client.getAddressClient().getPublicIPAddress(ip.getId()).getId());
      assert ip.getId() != null : ip;
      assert ip.getAccount() != null : ip;
      assert ip.getDomain() != null : ip;
      assert ip.getDomainId() != null : ip;
      assert ip.getState() != null : ip;
      assert ip.getZoneId() != null : ip;
      assert ip.getZoneName() != null : ip;

   }
}
