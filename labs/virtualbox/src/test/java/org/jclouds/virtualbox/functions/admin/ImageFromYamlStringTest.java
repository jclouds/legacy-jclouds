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

package org.jclouds.virtualbox.functions.admin;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.YamlImagesFromFileConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "ImageFromYamlStringTest")
public class ImageFromYamlStringTest {

   public static final Image TEST1 = new ImageBuilder()
            .id("ubuntu-10.04.4-server-i386")
            .name("ubuntu-10.04-server-i386")
            .description("ubuntu")
            .operatingSystem(
                     OperatingSystem.builder().description("ubuntu").family(OsFamily.UBUNTU).version("10.04.4")
                              .arch("x86").build())
            .status(Image.Status.AVAILABLE).build();

   Map<Image, YamlImage> images;

   @BeforeMethod
   public void setUp() {
      images = new ImagesToYamlImagesFromYamlDescriptor(new YamlImagesFromFileConfig("/default-images.yaml")).get();
   }

   @Test
   public void testNodesParse() {
      assertEquals(Iterables.getFirst(images.keySet(), null), TEST1);
   }
}
