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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.features.ImageClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ImageClient}
 * 
 * @author Michael Arnold
 */
@Test(groups = "live", testName = "ImageClientLiveTest")
public class ImageClientLiveTest extends BaseNovaClientLiveTest {

   @Test
   public void testListImages() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         ImageClient client = novaContext.getApi().getImageClientForZone(zoneId);
         Set<Resource> response = client.listImages();
         assertNotNull(response);
         assertTrue(response.size() >= 0);
         for (Resource image : response) {
            Image newDetails = client.getImage(image.getId());
            assertNotNull(newDetails);
            assertEquals(newDetails.getId(), image.getId());
            assertEquals(newDetails.getName(), image.getName());
            assertEquals(newDetails.getLinks(), image.getLinks());
         }
      }
   }

   @Test
   public void testListImagesInDetail() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         ImageClient client = novaContext.getApi().getImageClientForZone(zoneId);
         Set<Image> response = client.listImagesInDetail();
         assertNotNull(response);
         assertTrue(response.size() >= 0);
         for (Image image : response) {
            Image newDetails = client.getImage(image.getId());
            assertNotNull(newDetails);
            assertEquals(newDetails.getId(), image.getId());
            assertEquals(newDetails.getName(), image.getName());
            assertEquals(newDetails.getLinks(), image.getLinks());
            assertEquals(newDetails.getCreated(), image.getCreated());
            assertEquals(newDetails.getMinDisk(), image.getMinDisk());
            assertEquals(newDetails.getMinRam(), image.getMinRam());
            assertEquals(newDetails.getProgress(), image.getProgress());
            assertEquals(newDetails.getStatus(), image.getStatus());
            assertEquals(newDetails.getServer(), image.getServer());
            assertEquals(newDetails.getTenantId(), image.getTenantId());
            assertEquals(newDetails.getUpdated(), image.getUpdated());
            assertEquals(newDetails.getUserId(), image.getUserId());
         }
      }
   }
}
