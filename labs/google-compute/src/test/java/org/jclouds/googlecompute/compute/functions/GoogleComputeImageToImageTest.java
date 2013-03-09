/*
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

package org.jclouds.googlecompute.compute.functions;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.googlecompute.domain.Image;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class GoogleComputeImageToImageTest {

   Image.Builder imageBuilder = Image.builder()
           .id("1234")
           .selfLink(URI.create("http://test.com"))
           .sourceType("RAW")
           .description("")
           .rawDisk(Image.RawDisk.builder().source("").containerType("TAR").build());

   public void testArbitratyImageName() {
      GoogleComputeImageToImage imageToImage = new GoogleComputeImageToImage();
      Image image = imageBuilder.name("arbitratyname").build();
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.getName());
      assertEquals(transformed.getId(), image.getName());
      assertEquals(transformed.getProviderId(), image.getId());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.LINUX);
   }

   public void testWellFormedImageName() {
      GoogleComputeImageToImage imageToImage = new GoogleComputeImageToImage();
      Image image = imageBuilder.name("ubuntu-12-04-v123123").build();
      org.jclouds.compute.domain.Image transformed = imageToImage.apply(image);
      assertEquals(transformed.getName(), image.getName());
      assertEquals(transformed.getId(), image.getName());
      assertEquals(transformed.getProviderId(), image.getId());
      assertSame(transformed.getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(transformed.getOperatingSystem().getVersion(), "12.04");
   }


}
