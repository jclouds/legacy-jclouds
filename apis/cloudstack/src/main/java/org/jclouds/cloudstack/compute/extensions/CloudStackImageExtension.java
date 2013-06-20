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
package org.jclouds.cloudstack.compute.extensions;

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
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.options.CreateTemplateOptions;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
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

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * CloudStack implementation of {@link ImageExtension}
 * 
 * @author Andrew Bayer
 *
 */
@Singleton
public class CloudStackImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudStackClient client;
   private final ListeningExecutorService userExecutor;
   private final Supplier<Set<? extends Location>> locations;
   private final Predicate<AtomicReference<Image>> imageAvailablePredicate;
   private final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   private final Predicate<String> jobComplete;

   @Inject
   public CloudStackImageExtension(CloudStackClient client,
                                   @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                   @Memoized Supplier<Set<? extends Location>> locations,
                                   @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<AtomicReference<Image>> imageAvailablePredicate,
                                   BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult,
                                   Predicate<String> jobComplete) {
      this.client = checkNotNull(client, "client");
      this.userExecutor = checkNotNull(userExecutor, "userExecutor");
      this.locations = checkNotNull(locations, "locations");
      this.imageAvailablePredicate = checkNotNull(imageAvailablePredicate, "imageAvailablePredicate");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
                                                                "blockUntilJobCompletesAndReturnResult");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      VirtualMachine vm = client.getVirtualMachineClient().getVirtualMachine(id);
      if (vm == null)
         throw new NoSuchElementException("Cannot find vm with id: " + id);
      CloneImageTemplate template = new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
      return template;
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate,
               " cloudstack only currently supports creating images through cloning.");
      CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;

      VirtualMachine vm = client.getVirtualMachineClient().getVirtualMachine(cloneTemplate.getSourceNodeId());
      String stopJob = client.getVirtualMachineClient().stopVirtualMachine(vm.getId());
      jobComplete.apply(stopJob);

      Set<Volume> volumes = client.getVolumeClient().listVolumes(ListVolumesOptions.Builder.virtualMachineId(vm.getId()));
      Volume volume = Iterables.getOnlyElement(volumes);
      
      CreateTemplateOptions options = CreateTemplateOptions.Builder.volumeId(volume.getId());
      AsyncCreateResponse templateJob = client.getTemplateClient().createTemplate(TemplateMetadata.builder()
                                                                                  .name(cloneTemplate.getName())
                                                                                  .osTypeId(vm.getGuestOSId())
                                                                                  .displayText(cloneTemplate.getName())
                                                                                  .build(), options);
      Template newTemplate = blockUntilJobCompletesAndReturnResult.<Template>apply(templateJob);

      logger.info(">> Registered new template %s, waiting for it to become available.", newTemplate.getId());

      final AtomicReference<Image> image = Atomics.newReference(new ImageBuilder()
                                                                .location(find(locations.get(), idEquals(vm.getZoneId())))
                                                                .id(newTemplate.getId())
                                                                .providerId(newTemplate.getId())
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
         AsyncCreateResponse deleteJob = client.getTemplateClient().deleteTemplate(id);
         jobComplete.apply(deleteJob.getJobId());
         return true;
      } catch (Exception e) {
         return false;
      }
   }

}
