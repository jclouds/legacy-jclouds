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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.VirtualInterface;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of VirtualInterfaceApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "VirtualInterfaceApiLiveTest", singleThreaded = true)
public class VirtualInterfaceApiLiveTest extends BaseNovaApiLiveTest {
   private Optional<? extends VirtualInterfaceApi> apiOption;
   private String zone;


   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      apiOption = api.getVirtualInterfaceExtensionForZone(zone);
   }

   public void testListVirtualInterfaces() {
      if (apiOption.isPresent()) {
         Server testServer = null;
         try {
            testServer = createServerInZone(zone);
            Set<? extends VirtualInterface> results = apiOption.get().listOnServer(testServer.getId()).toSet();
            for (VirtualInterface vif : results) {
               assertNotNull(vif.getId());
               assertNotNull(vif.getMacAddress());
            }
         } finally {
            if (testServer != null) {
               api.getServerApiForZone(zone).delete(testServer.getId());
            }
         }
   }
}
}
