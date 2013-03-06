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
package org.jclouds.openstack.glance.v1_0.features;


import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.containerFormat;
import static org.jclouds.openstack.glance.v1_0.options.CreateImageOptions.Builder.diskFormat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.io.payloads.StringPayload;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceApiLiveTest;
import org.jclouds.openstack.glance.v1_0.options.ListImageOptions;
import org.jclouds.openstack.glance.v1_0.options.UpdateImageOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "ImageApiLiveTest")
public class ImageApiLiveTest extends BaseGlanceApiLiveTest {

   @Test
   public void testList() throws Exception {
      for (String zoneId : glanceContext.getApi().getConfiguredZones()) {
         ImageApi api = glanceContext.getApi().getImageApiForZone(zoneId);
         Set<? extends Image> response = api.list().concat().toSet();
         assert null != response;
         for (Image image : response) {
            checkImage(image);
         }
      }
   }

   private void checkImage(Image image) {
      assert image.getId() != null : image;
      assert image.getName() != null : image;
      assert image.getLinks() != null : image;
   }

   @Test
   public void testListInDetail() throws Exception {
      for (String zoneId : glanceContext.getApi().getConfiguredZones()) {
         ImageApi api = glanceContext.getApi().getImageApiForZone(zoneId);
         Set<? extends ImageDetails> response = api.listInDetail().concat().toSet();
         assert null != response;
         for (ImageDetails image : response) {
            checkImage(image);
            ImageDetails newDetails = api.get(image.getId());
            checkImageDetails(newDetails);
            checkImageDetailsEqual(image, newDetails);
         }
      }
   }

   private void checkImageDetails(ImageDetails image) {
      checkImage(image);
      assertTrue(image.getMinDisk() >= 0);
      assertTrue(image.getMinRam() >= 0);
   }

   private void checkImageDetailsEqual(ImageDetails image, ImageDetails newDetails) {
      assertEquals(newDetails.getId(), image.getId());
      assertEquals(newDetails.getName(), image.getName());
      assertEquals(newDetails.getLinks(), image.getLinks());
   }

   @Test
   public void testCreateUpdateAndDeleteImage() {
      StringPayload imageData = new StringPayload("This isn't really an image!");
      for (String zoneId : glanceContext.getApi().getConfiguredZones()) {
         ImageApi api = glanceContext.getApi().getImageApiForZone(zoneId);
         ImageDetails details = api.create("jclouds-live-test", imageData, diskFormat(DiskFormat.RAW), containerFormat(ContainerFormat.BARE));
         assertEquals(details.getName(), "jclouds-live-test");
         assertEquals(details.getSize().get().longValue(), imageData.getRawContent().length());
         
         details = api.update(details.getId(), UpdateImageOptions.Builder.name("jclouds-live-test2"), UpdateImageOptions.Builder.minDisk(10));
         assertEquals(details.getName(), "jclouds-live-test2");
         assertEquals(details.getMinDisk(), 10);
         
         Image fromListing = api.list(
                  ListImageOptions.Builder.containerFormat(ContainerFormat.BARE).name("jclouds-live-test2").limit(2))
                  .get(0);
         assertEquals(fromListing.getId(), details.getId());
         assertEquals(fromListing.getSize(), details.getSize());

         assertEquals(Iterables.getOnlyElement(api.listInDetail(ListImageOptions.Builder.name("jclouds-live-test2"))), details);

         assertTrue(api.delete(details.getId()));
         
         assertTrue(api.list(ListImageOptions.Builder.name("jclouds-live-test2")).isEmpty());
      }
   }

   @Test
   public void testReserveUploadAndDeleteImage() {
      StringPayload imageData = new StringPayload("This isn't an image!");
      for (String zoneId : glanceContext.getApi().getConfiguredZones()) {
         ImageApi api = glanceContext.getApi().getImageApiForZone(zoneId);
         ImageDetails details = api.reserve("jclouds-live-res-test", diskFormat(DiskFormat.RAW), containerFormat(ContainerFormat.BARE));
         assertEquals(details.getName(), "jclouds-live-res-test");
 
         details = api.upload(details.getId(), imageData, UpdateImageOptions.Builder.name("jclouds-live-res-test2"), UpdateImageOptions.Builder.minDisk(10));
         assertEquals(details.getName(), "jclouds-live-res-test2");
         assertEquals(details.getSize().get().longValue(), imageData.getRawContent().length());
         assertEquals(details.getMinDisk(), 10);

         Image fromListing = Iterables.getOnlyElement(api.list(ListImageOptions.Builder.name("jclouds-live-res-test2").limit(2).containerFormat(ContainerFormat.BARE)));
         assertEquals(fromListing.getId(), details.getId());
         assertEquals(fromListing.getSize(), details.getSize());

         assertEquals(Iterables.getOnlyElement(api.listInDetail(ListImageOptions.Builder.name("jclouds-live-res-test2"))), details);

         assertTrue(api.delete(details.getId()));

         assertTrue(api.list(ListImageOptions.Builder.name("jclouds-live-res-test2")).isEmpty());
      }
   }

}
