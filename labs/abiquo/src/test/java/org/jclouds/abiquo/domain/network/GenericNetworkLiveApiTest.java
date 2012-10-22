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

package org.jclouds.abiquo.domain.network;

import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.NetworkType;

/**
 * Live integration tests for the {@link Network} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "GenericNetworkLiveApiTest")
public class GenericNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testListDatacenterNetworks() {
      // Make sure all network types are listed
      List<Network<?>> networks = env.datacenter.listNetworks();
      assertNotNull(networks);
      assertEquals(networks.size(), 3);
   }

   public void testListPublicNetworks() {
      List<Network<?>> networks = env.datacenter.listNetworks(NetworkType.PUBLIC);
      assertNotNull(networks);
      assertEquals(networks.size(), 1);

      // Make sure it can be converted
      networks.get(0).toPublicNetwork();
   }

   public void testListExternaletworks() {
      List<Network<?>> networks = env.datacenter.listNetworks(NetworkType.EXTERNAL);
      assertNotNull(networks);
      assertEquals(networks.size(), 1);

      // Make sure it can be converted
      networks.get(0).toExternalNetwork();
   }

   public void testListUnmanagedNetworks() {
      List<Network<?>> networks = env.datacenter.listNetworks(NetworkType.UNMANAGED);
      assertNotNull(networks);
      assertEquals(networks.size(), 1);

      // Make sure it can be converted
      networks.get(0).toUnmanagedNetwork();
   }

   public void testListPrivateNetworks() {
      try {
         env.datacenter.listNetworks(NetworkType.INTERNAL);
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.BAD_REQUEST, "QUERY-1");
      }
   }
}
