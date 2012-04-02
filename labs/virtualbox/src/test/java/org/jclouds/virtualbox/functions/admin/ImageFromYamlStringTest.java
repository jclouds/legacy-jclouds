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

import static junit.framework.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.YamlImagesFromFileConfig;
import org.jclouds.virtualbox.predicates.DefaultImagePredicate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class ImageFromYamlStringTest {

   public static final Image TEST1 = new ImageBuilder()
            .id("ubuntu-11.04-i386")
            .name("ubuntu-11.04-server-i386")
            .description("ubuntu 11.04 server (i386)")
            .operatingSystem(
                     OperatingSystem.builder().description("ubuntu").family(OsFamily.UBUNTU).version("11.04")
                              .arch("x86").build()).build();

   Map<Image, YamlImage> images;

   @BeforeMethod
   public void setUp() {
      images = new ImagesToYamlImagesFromYamlDescriptor(new YamlImagesFromFileConfig("/default-images.yaml")).get();
   }

   @Test
   public void testNodesParse() {
      assertEquals(Iterables.getFirst(images.keySet(), null), TEST1);
   }

   @Test
   public void testDefaultImagePresent() {

      Iterable<Image> defaultImage = Iterables.filter(images.keySet(), new DefaultImagePredicate());

      assertTrue(!Iterables.isEmpty(defaultImage));
      assertEquals(1, Iterables.size(defaultImage));
   }
}