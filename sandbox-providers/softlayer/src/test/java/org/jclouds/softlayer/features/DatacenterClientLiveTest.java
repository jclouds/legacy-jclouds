/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.softlayer.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.softlayer.domain.Datacenter;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DatacenterClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class DatacenterClientLiveTest extends BaseSoftLayerClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getDatacenterClient();
   }

   private DatacenterClient client;

   @Test
   public void testListDatacenters() throws Exception {
      Set<Datacenter> response = client.listDatacenters();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Datacenter vg : response) {
         Datacenter newDetails = client.getDatacenter(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkDatacenter(vg);
      }
   }

   private void checkDatacenter(Datacenter vg) {
      assert vg.getId() > 0 : vg;
      assert vg.getName() != null : vg;
      assert vg.getLongName() != null : vg;
   }

}
