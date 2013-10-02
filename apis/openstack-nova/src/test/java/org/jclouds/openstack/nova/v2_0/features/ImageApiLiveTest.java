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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link ImageApi}
 * 
 * @author Michael Arnold
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseNovaApiLiveTest {

   @Test(description = "GET /v${apiVersion}/{tenantId}/images")
   public void testListImages() throws Exception {
      for (String zoneId : zones) {
         ImageApi imageApi = api.getImageApiForZone(zoneId);
         Set<? extends Resource> response = imageApi.list().concat().toSet();
         assertNotNull(response);
         assertFalse(response.isEmpty());
         for (Resource image : response) {
            assertNotNull(image.getId());
            assertNotNull(image.getName());
            assertNotNull(image.getLinks());
         }
      }
   }

   @Test(description = "GET /v${apiVersion}/{tenantId}/images/detail")
   public void testListImagesInDetail() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         ImageApi imageApi = api.getImageApiForZone(zoneId);
         Set<? extends Image> response = imageApi.listInDetail().concat().toSet();
         assertNotNull(response);
         assertFalse(response.isEmpty());
         for (Image image : response) {
            assertNotNull(image.getId());
            assertNotNull(image.getName());
            assertNotNull(image.getLinks());
            assertNotNull(image.getCreated());
            // image.getMinDisk() can be zero
            // image.getMinRam() can be zero
            assertTrue(image.getProgress() >= 0 && image.getProgress() <= 100);
            assertNotNull(image.getStatus());
            // image.getServer() can be null
            // image.getTenantId() can be null
            // image.getUpdated() can be null
            // image.getUserId() can be null
         }
      }
   }

   @Test(description = "GET /v${apiVersion}/{tenantId}/images/{id}", dependsOnMethods = { "testListImagesInDetail" })
   public void testGetImageById() throws Exception {
      for (String zoneId : api.getConfiguredZones()) {
         ImageApi imageApi = api.getImageApiForZone(zoneId);
         Set<? extends Image> response = imageApi.listInDetail().concat().toSet();
         for (Image image : response) {
            Image details = imageApi.get(image.getId());
            assertNotNull(details);
            assertEquals(details.getId(), image.getId());
            assertEquals(details.getName(), image.getName());
            assertEquals(details.getLinks(), image.getLinks());
            assertEquals(details.getCreated(), image.getCreated());
            assertEquals(details.getMinDisk(), image.getMinDisk());
            assertEquals(details.getMinRam(), image.getMinRam());
            assertEquals(details.getProgress(), image.getProgress());
            assertEquals(details.getStatus(), image.getStatus());
            assertEquals(details.getServer(), image.getServer());
            assertEquals(details.getTenantId(), image.getTenantId());
            assertEquals(details.getUpdated(), image.getUpdated());
            assertEquals(details.getUserId(), image.getUserId());
         }
      }
   }
}
