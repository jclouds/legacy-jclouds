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
package org.jclouds.virtualbox.predicates;

import static junit.framework.Assert.assertTrue;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_ARCH;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_OS;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_VERSION;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * A simple test for {@link DefaultImagePredicate} that makes sure the predicate returns true when
 * an image built with the defaults is passed and false when it's not.
 * 
 * @author dralves
 * 
 */
public class DefaultImagePredicateTest {

   @Test
   public void testFindDefaultImage() {
      Image image = new ImageBuilder()
               .id("test-id")
               .description("test-image")
               .operatingSystem(
                        OperatingSystem.builder().arch(VIRTUALBOX_DEFAULT_IMAGE_ARCH)
                                 .version(VIRTUALBOX_DEFAULT_IMAGE_VERSION).description("test-os")
                                 .family(VIRTUALBOX_DEFAULT_IMAGE_OS).build()).build();
      assertTrue(new DefaultImagePredicate().apply(image));
   }

   @Test
   public void testNotFindDefaultImage() {
      Image image = new ImageBuilder()
               .id("test-id")
               .description("test-image")
               .operatingSystem(
                        OperatingSystem.builder().arch(VIRTUALBOX_DEFAULT_IMAGE_ARCH)
                                 .version(VIRTUALBOX_DEFAULT_IMAGE_VERSION).description("test-os")
                                 .family(OsFamily.UNRECOGNIZED).build()).build();
      assertTrue(!new DefaultImagePredicate().apply(image));
   }
}
