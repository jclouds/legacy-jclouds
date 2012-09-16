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
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ContainerApiLiveTest")
public class AccountApiLiveTest extends BaseSwiftApiLiveTest {

   @Test
   public void testGetAccountMetadata() throws Exception {
      for (String regionId : swiftContext.getApi().getConfiguredRegions()) {
         AccountApi api = swiftContext.getApi().getAccountApiForRegion(regionId);
         Account account = api.get();
         assertNotNull(account);
         assertTrue(account.getContainerCount() >= 0);
         assertTrue(account.getBytesUsed() >= 0);
      }
   }

}
