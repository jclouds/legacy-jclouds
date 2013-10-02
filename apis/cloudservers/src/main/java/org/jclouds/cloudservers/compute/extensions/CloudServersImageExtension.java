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
package org.jclouds.cloudservers.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * CloudServers implementation of {@link ImageExtension}
 * 
 * @author David Alves
 * @see <a href="http://docs.rackspace.com/servers/api/v1.0/cs-devguide/content/Images-d1e4062.html">docs</a>
 */
@Singleton
public class CloudServersImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudServersClient client;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Location> location;
   private final Predicate<AtomicReference<Image>> imageAvailablePredicate;

   @Inject
   public CloudServersImageExtension(CloudServersClient client, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         Supplier<Location> location,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<AtomicReference<Image>> imageAvailablePredicate) {
      this.client = checkNotNull(client, "client");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.location = checkNotNull(location, "location");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      Server server = client.getServer(Integer.parseInt(id));
      if (server == null)
         throw new NoSuchElementException("Cannot find server with id: " + id);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " openstack-nova only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      org.jclouds.cloudservers.domain.Image csImage = client.createImageFromServer(cloneTemplate.getName(),
               Integer.parseInt(cloneTemplate.getSourceNodeId()));
      
      final AtomicReference<Image> image = Atomics.newReference(new ImageBuilder()
            .location(location.get())
            .ids(csImage.getId() + "")
            .description(cloneTemplate.getName())
            .operatingSystem(OperatingSystem.builder().description(cloneTemplate.getName()).build())
            .status(Image.Status.PENDING).build());

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
            if (imageAvailablePredicate.apply(image))
               return image.get();
            // TODO: get rid of the expectation that the image will be available, as it is very brittle
            throw new UncheckedTimeoutException("Image was not created within the time limit: " + image.get());
         }
      });
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         this.client.deleteImage(Integer.parseInt(id));
      } catch (Exception e) {
         return false;
      }
      return true;
   }

}
