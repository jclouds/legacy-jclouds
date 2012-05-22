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

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.openstack.glance.v1_0.internal.BaseGlanceClientLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ImageClientLiveTest")
public class ImageClientLiveTest extends BaseGlanceClientLiveTest {

   @Test
   public void testList() throws Exception {
      for (String zoneId : glanceContext.getApi().getConfiguredRegions()) {
         ImageClient client = glanceContext.getApi().getImageClientForRegion(zoneId);
         Set<Image> response = client.list();
         assert null != response;
         for (Image image : response) {
            checkImage(image);
         }
      }
   }

   private void checkImage(Image image) {
      assert image.getId() != null : image;
      assert image.getSize().isPresent() : image;
      assert image.getChecksum().isPresent() : image;
      assert image.getContainerFormat().isPresent() : image;
      assert image.getContainerFormat().isPresent() : image;
   }

   @Test
   public void testListInDetail() throws Exception {
      for (String zoneId : glanceContext.getApi().getConfiguredRegions()) {
         ImageClient client = glanceContext.getApi().getImageClientForRegion(zoneId);
         Set<ImageDetails> response = client.listInDetail();
         assert null != response;
         for (ImageDetails image : response) {
            checkImage(image);
            ImageDetails newDetails = client.show(image.getId());
            checkImageDetails(newDetails);
            checkImageDetailsEqual(image, newDetails);
         }
      }
   }

   private void checkImageDetails(ImageDetails image) {
      //TODO
   }

   private void checkImageDetailsEqual(ImageDetails image, ImageDetails newDetails) {
      assertEquals(newDetails.getId(), image.getId());
      assertEquals(newDetails.getName(), image.getName());
      assertEquals(newDetails.getLinks(), image.getLinks());
   }

}
