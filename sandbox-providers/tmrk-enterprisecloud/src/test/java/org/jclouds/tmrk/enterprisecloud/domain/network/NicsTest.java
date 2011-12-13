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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Jason King
 */
@Test(groups = "unit", testName = "NicsTest")
public class NicsTest {

   private NetworkReference networkReference;
   private VirtualNic nic;
   private Nics nics;

   @BeforeMethod()
   public void setUp() throws URISyntaxException {
      networkReference = NetworkReference.builder().href(new URI("/netref")).type("a network ref").name("my network ref").networkType(NetworkReference.NetworkType.INTERNAL).build();
      nic = VirtualNic.builder().macAddress("aa:bb").name("my nic").network(networkReference).unitNumber(1).build();
      nics = Nics.builder().addVirtualNic(nic).build();
   }

   @Test
   public void testAddDisk() throws URISyntaxException {
      VirtualNic nic2 = VirtualNic.builder().macAddress("aa:cc").name("my nic 2").network(networkReference).unitNumber(2).build();
      Nics twoNics = nics.toBuilder().addVirtualNic(nic2).build();
      Set<VirtualNic> nicSet = twoNics.getVirtualNics();

      assertEquals(2, nicSet.size());
      assertTrue(nicSet.contains(nic));
      assertTrue(nicSet.contains(nic2));
   }
}
