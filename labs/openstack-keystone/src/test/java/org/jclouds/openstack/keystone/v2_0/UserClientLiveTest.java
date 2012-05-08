/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests UserClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "UserClientLiveTest")
public class UserClientLiveTest extends BaseKeystoneClientLiveTest {

   public void testGetApiMetaData() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         ApiMetadata result = keystoneContext.getApi().getServiceClientForRegion(regionId).getApiMetadata();
         assertNotNull(result);
         assertNotNull(result.getId());
         assertNotNull(result.getStatus());
         assertNotNull(result.getUpdated());
      }
   }

   public void testListTenants() {
      for (String regionId : keystoneContext.getApi().getConfiguredRegions()) {
         Set<Tenant> result = keystoneContext.getApi().getServiceClientForRegion(regionId).listTenants();
         assertNotNull(result);
         assertFalse(result.isEmpty());

         for (Tenant tenant : result) {
            assertNotNull(tenant.getId());
         }
      }
   }
}
