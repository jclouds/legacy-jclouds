/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.abiquo.domain.infrastructure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.domain.network.NetworkServiceType;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link NetworkServiceType} domain class.
 * 
 * @author Jaume Devesa
 */
@Test(groups = "api", testName = "NetworkServiceTypeLiveApiTest")
public class NetworkServiceTypeLiveApiTest extends BaseAbiquoApiLiveApiTest {

   private NetworkServiceType nst = null;

   @Test
   public void testCreate() {
      nst = NetworkServiceType.builder(env.context.getApiContext(), env.datacenter).name("Storage Network").build();
      nst.save();

      assertNotNull(nst.getId());
      NetworkServiceType copy = env.datacenter.getNetworkServiceType(nst.getId());
      assertEquals(copy.getName(), nst.getName());

   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdate() {
      nst.setName("Storage Network Updated");
      nst.update();

      NetworkServiceType copy = env.datacenter.getNetworkServiceType(nst.getId());
      assertEquals(copy.getName(), nst.getName());
   }

   @Test(dependsOnMethods = "testUpdate")
   public void testDelete() {
      Integer deleteId = nst.getId();
      nst.delete();

      // Assert it is deleted
      assertNull(env.datacenter.getNetworkServiceType(deleteId));

   }
}
