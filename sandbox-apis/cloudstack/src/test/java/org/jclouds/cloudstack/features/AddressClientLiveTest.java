/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code PublicIPAddressClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "PublicIPAddressClientLiveTest")
public class AddressClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;
   private RetryablePredicate<Long> jobComplete;

   @BeforeGroups(groups = "live")
   public void setupClient() {
      super.setupClient();
      jobComplete = new RetryablePredicate<Long>(new JobComplete(client), 600, 5, TimeUnit.SECONDS);
   }

   public void testAssociateDisassociatePublicIPAddress() throws Exception {
      ip = createPublicIPAddress(client, jobComplete);
      checkIP(ip);
   }

   public static PublicIPAddress createPublicIPAddress(CloudStackClient client, RetryablePredicate<Long> jobComplete) {
      Zone zone = find(client.getZoneClient().listZones(), new Predicate<Zone>() {

         @Override
         public boolean apply(Zone arg0) {
            return arg0.getNetworkType() == NetworkType.ADVANCED;
         }

         @Override
         public String toString() {
            return "networkType(ADVANCED)";
         }
      });
      AsyncCreateResponse job = client.getAddressClient().associateIPAddress(zone.getId());
      assert jobComplete.apply(job.getJobId());
      PublicIPAddress ip = client.getAddressClient().getPublicIPAddress(job.getId());
      assertEquals(ip.getZoneId(), zone.getId());
      return ip;
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   public void testListPublicIPAddresss() throws Exception {
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
      assert ip.getId() > 0 : ip;
      assert ip.getAccount() != null : ip;
      assert ip.getDomain() != null : ip;
      assert ip.getDomainId() > 0 : ip;
      assert ip.getState() != null : ip;
      assert ip.getZoneId() > 0 : ip;
      assert ip.getZoneName() != null : ip;

   }
}
