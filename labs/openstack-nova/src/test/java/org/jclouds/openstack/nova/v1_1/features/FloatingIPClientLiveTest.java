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
package org.jclouds.openstack.nova.v1_1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v1_1.domain.FloatingIP;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ServerClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "FloatingIPClientLiveTest")
public class FloatingIPClientLiveTest extends BaseNovaClientLiveTest {

   @Test
   public void testListFloatingIPs() throws Exception {
      for (String regionId : context.getApi().getConfiguredRegions()) {
         
         FloatingIPClient client = context.getApi().getFloatingIPClientForRegion(regionId);
         Set<FloatingIP> response = client.listFloatingIPs();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (FloatingIP ip : response) {
            FloatingIP newDetails = client.getFloatingIP(ip.getId());
            
            
            assertEquals(newDetails.getId(), ip.getId());
            assertEquals(newDetails.getIp(), ip.getIp());
            assertEquals(newDetails.getFixedIp(), ip.getFixedIp());
            assertEquals(newDetails.getInstanceId(), ip.getInstanceId());
            
         }
      }
   }
}
