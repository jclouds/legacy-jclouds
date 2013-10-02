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
package org.jclouds.openstack.nova.v2_0.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
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
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * Nova implementation of {@link ImageExtension}
 * 
 * @author David Alves
 *
 */
@Singleton
public class NovaImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final NovaApi novaApi;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Set<? extends Location>> locations;
   private final Predicate<AtomicReference<Image>> imageAvailablePredicate;

   @Inject
   public NovaImageExtension(NovaApi novaApi, @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         @Memoized Supplier<Set<? extends Location>> locations,
         @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<AtomicReference<Image>> imageAvailablePredicate) {
      this.novaApi = checkNotNull(novaApi, "novaApi");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.locations = checkNotNull(locations, "locations");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      Server server = novaApi.getServerApiForZone(zoneAndId.getZone()).get(zoneAndId.getId());
      if (server == null)
         throw new NoSuchElementException("Cannot find server with id: " + zoneAndId);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " openstack-nova only supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      ZoneAndId sourceImageZoneAndId = ZoneAndId.fromSlashEncoded(cloneTemplate.getSourceNodeId());

      String newImageId = novaApi.getServerApiForZone(sourceImageZoneAndId.getZone()).createImageFromServer(
               cloneTemplate.getName(), sourceImageZoneAndId.getId());

      final ZoneAndId targetImageZoneAndId = ZoneAndId.fromZoneAndId(sourceImageZoneAndId.getZone(), newImageId);

      logger.info(">> Registered new Image %s, waiting for it to become available.", newImageId);
      
      final AtomicReference<Image> image = Atomics.newReference(new ImageBuilder()
            .location(find(locations.get(), idEquals(targetImageZoneAndId.getZone())))
            .id(targetImageZoneAndId.slashEncode())
            .providerId(targetImageZoneAndId.getId())
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
      ZoneAndId zoneAndId = ZoneAndId.fromSlashEncoded(id);
      try {
         this.novaApi.getImageApiForZone(zoneAndId.getZone()).delete(zoneAndId.getId());
      } catch (Exception e) {
         return false;
      }
      return true;
   }

}
