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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ExtensionApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ExtensionApiLiveTest")
public class ExtensionApiLiveTest extends BaseNovaApiLiveTest {

   /**
    * Tests the listing of Extensions (getExtension() is tested too!)
    * 
    * @throws Exception
    */
   @Test
   public void testListExtensions() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         ExtensionApi api = novaContext.getApi().getExtensionApiForZone(zoneId);
         Set<? extends Extension> response = api.listExtensions();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Extension extension : response) {
            Extension newDetails = api.getExtensionByAlias(extension.getId());
            assertEquals(newDetails.getId(), extension.getId());
            assertEquals(newDetails.getName(), extension.getName());
            assertEquals(newDetails.getDescription(), extension.getDescription());
            assertEquals(newDetails.getNamespace(), extension.getNamespace());
            assertEquals(newDetails.getUpdated(), extension.getUpdated());
            assertEquals(newDetails.getLinks(), extension.getLinks());
         }
      }
   }

}
