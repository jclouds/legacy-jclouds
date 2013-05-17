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
package org.jclouds.compute.extensions.internal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
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

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected String imageId;

   /**
    * Returns the template for the base node, override to test different templates.
    * 
    * @return
    */
   public Template getNodeTemplate() {
      return view.getComputeService().templateBuilder().build();
   }

   /**
    * Returns the maximum amount of time (in seconds) to wait for a node spawned from the new image
    * to become available, override to increase this time.
    * 
    * @return
    */
   public long getSpawnNodeMaxWait() {
      return 600L;
   }

   /**
    * Lists the images found in the {@link ComputeService}, subclasses may override to constrain
    * search.
    * 
    * @return
    */
   protected Iterable<? extends Image> listImages() {
      return view.getComputeService().listImages();
   }

   @Test(groups = { "integration", "live" }, singleThreaded = true)
   public void testCreateImage() throws RunNodesException, InterruptedException, ExecutionException {

      ComputeService computeService = view.getComputeService();

      Optional<ImageExtension> imageExtension = computeService.getImageExtension();

      assertTrue(imageExtension.isPresent(), "image extension was not present");

      Template template = getNodeTemplate();

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test-create-image", 1, template));

      checkReachable(node);

      logger.info("Creating image from node %s, started with template: %s", node, template);

      ImageTemplate newImageTemplate = imageExtension.get().buildImageTemplateFromNode("test-create-image",
               node.getId());

      Image image = imageExtension.get().createImage(newImageTemplate).get();

      logger.info("Image created: %s", image);

      assertEquals("test-create-image", image.getName());

      imageId = image.getId();

      computeService.destroyNode(node.getId());

      Optional<? extends Image> optImage = getImage();

      assertTrue(optImage.isPresent());

   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = "testCreateImage")
   public void testSpawnNodeFromImage() throws RunNodesException {

      ComputeService computeService = view.getComputeService();

      Optional<? extends Image> optImage = getImage();

      assertTrue(optImage.isPresent());

      NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup("test-create-image", 1, view
               .getComputeService()
               // fromImage does not use the arg image's id (but we do need to set location)
               .templateBuilder().imageId(optImage.get().getId()).fromImage(optImage.get()).build()));

      checkReachable(node);

      view.getComputeService().destroyNode(node.getId());

   }

   @Test(groups = { "integration", "live" }, singleThreaded = true, dependsOnMethods = { "testCreateImage",
            "testSpawnNodeFromImage" })
   public void testDeleteImage() {

      ComputeService computeService = view.getComputeService();

      Optional<ImageExtension> imageExtension = computeService.getImageExtension();
      assertTrue(imageExtension.isPresent(), "image extension was not present");

      Optional<? extends Image> optImage = getImage();

      assertTrue(optImage.isPresent());

      Image image = optImage.get();

      assertTrue(imageExtension.get().deleteImage(image.getId()));
   }

   private Optional<? extends Image> getImage() {
      return Iterables.tryFind(listImages(), new Predicate<Image>() {
         @Override
         public boolean apply(Image input) {
            return input.getId().equals(imageId);
         }
      });
   }

   private void checkReachable(NodeMetadata node) {
      SshClient client = view.utils().sshForNode().apply(node);
      assertTrue(retry(new Predicate<SshClient>() {
         @Override
         public boolean apply(SshClient input) {
            input.connect();
            if (input.exec("id").getExitStatus() == 0) {
               return true;
            }
            return false;
         }
      }, getSpawnNodeMaxWait(), 1l, SECONDS).apply(client));
   }
}
