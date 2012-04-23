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

package org.jclouds.compute.internal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ImageExtension;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Base test for {@link ImageExtension} implementations.
 * 
 * @author David Alves
 * 
 */
public abstract class BaseImageExtensionLiveTest extends BaseComputeServiceContextLiveTest {

   /**
    * Returns the template for the base node, override to test different templates.
    * 
    * @return
    */
   public Template getNodeTemplate() {
      return wrapper.getComputeService().templateBuilder().any().build();
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testCreateImage() throws RunNodesException, InterruptedException {

      ComputeService computeService = wrapper.getComputeService();

      Optional<ImageExtension> imageExtension = computeService.getImageExtension();
      assertTrue("image extension was not present", imageExtension.isPresent());

      Set<? extends Image> imagesBefore = computeService.listImages();

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test-create-image", 1,
               getNodeTemplate()));

      ImageTemplate newImageTemplate = imageExtension.get().buildImageTemplateFromNode("test-create-image",
               node.getId());

      Image image = imageExtension.get().createImage(newImageTemplate);

      assertEquals("test-create-image", image.getName());

      computeService.destroyNode(node.getId());

      Set<? extends Image> imagesAfter = computeService.listImages();

      assertTrue(imagesBefore.size() == imagesAfter.size() - 1);

   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testCreateImage")
   public void testSpawnNodeFromImage() throws RunNodesException {

      ComputeService computeService = wrapper.getComputeService();

      Template template = computeService.templateBuilder().fromImage(getImage().get()).build();

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test-create-image", 1, template));

      SshClient client = wrapper.utils().sshForNode().apply(node);
      client.connect();

      ExecResponse hello = client.exec("echo hello");

      assertEquals(hello.getOutput().trim(), "hello");

      wrapper.getComputeService().destroyNode(node.getId());

   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = { "testCreateImage",
            "testSpawnNodeFromImage" })
   public void testDeleteImage() {

      ComputeService computeService = wrapper.getComputeService();

      Optional<ImageExtension> imageExtension = computeService.getImageExtension();
      assertTrue("image extension was not present", imageExtension.isPresent());

      Optional<? extends Image> optImage = getImage();

      assertTrue(optImage.isPresent());

      Image image = optImage.get();

      assertTrue(imageExtension.get().deleteImage(image.getId()));
   }

   private Optional<? extends Image> getImage() {
      return Iterables.tryFind(wrapper.getComputeService().listImages(), new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return input.getId().contains("test-create-image");
         }
      });
   }

}
