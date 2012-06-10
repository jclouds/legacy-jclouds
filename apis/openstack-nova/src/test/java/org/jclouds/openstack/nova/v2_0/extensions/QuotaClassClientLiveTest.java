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
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of QuotaClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "QuotaClassClientLiveTest", singleThreaded = true)
public class QuotaClassClientLiveTest extends BaseNovaClientLiveTest {
   private Optional<QuotaClassClient> clientOption;
   private String zone;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      clientOption = novaContext.getApi().getQuotaClassExtensionForZone(zone);
   }

   public void testUpdateAndGetQuotaClass() {
      if (clientOption.isPresent()) {
         QuotaClassClient client = clientOption.get();

         QuotaClass firstVersion =
               QuotaClassClientExpectTest.getTestQuotas().toBuilder()
                     .id("jcloudstestquotas")
                     .cores(10)
                     .instances(5)
                     .ram(4096)
                     .volumes(5)
                     .build();

         assertTrue(client.updateQuotaClass(firstVersion.getId(), firstVersion));

         assertEquals(client.getQuotaClass(firstVersion.getId()), firstVersion);

         // Change it again (since we may have run this test before and we can't delete the QuotaClass)
         QuotaClass secondVersion = firstVersion.toBuilder().ram(8192).build();

         assertTrue(client.updateQuotaClass(secondVersion.getId(), secondVersion));

         assertEquals(client.getQuotaClass(secondVersion.getId()), secondVersion);
      }
   }
}
