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

package org.jclouds.virtualbox.compute;

import static junit.framework.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ImageExtension;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "VirtualBoxImageExtensionLiveTest")
public class VirtualBoxImageExtensionLiveTest extends BaseVirtualBoxClientLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public void testCreateImage() throws RunNodesException, InterruptedException {

      ComputeService vboxComputeService = context.getComputeService();

      Optional<ImageExtension> vboxImageExtension = vboxComputeService.getImageExtension();
      assertTrue("image extension was not present", vboxImageExtension.isPresent());

      Set<? extends Image> imagesBefore = vboxComputeService.listImages();

      NodeMetadata node = Iterables.getOnlyElement(vboxComputeService.createNodesInGroup("test-create-image", 1));

      ImageTemplate newImageTemplate = vboxImageExtension.get().buildImageTemplateFromNode("test-create-image",
               node.getId());

      Image image = vboxImageExtension.get().createImage(newImageTemplate);

      vboxComputeService.destroyNode(node.getId());

      Set<? extends Image> imagesAfter = vboxComputeService.listImages();

      assertTrue(imagesBefore.size() == imagesAfter.size() - 1);
      
      assertTrue(vboxImageExtension.get().deleteImage(image.getId()));

   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
