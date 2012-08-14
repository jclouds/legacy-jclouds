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

package org.jclouds.imagemaker.internal;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.imagemaker.ImageMaker;
import org.jclouds.imagemaker.PackageProcessor;
import org.jclouds.imagemaker.PackageProcessor.Type;
import org.jclouds.imagemaker.config.ImageDescriptor;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

/**
 * Implementation of an ImageMaker. Takes a yaml descriptor of packages to install (maybe later also
 * scripts to run and URLS to download), runs them with the adequate {@link PackageProcessor} and
 * creates an image out of it. The resulting image should have a much smaller startup time all
 * relevant packages should be downloaded/installed.
 * 
 * @author David Alves
 * 
 */
public class ImageMakerImpl implements ImageMaker {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<PackageProcessor.Type, Set<PackageProcessor>> packageProcessors;
   private final Supplier<Map<String, ImageDescriptor>> images;
   private final ComputeServiceContext ctx;

   @Inject
   public ImageMakerImpl(Supplier<Map<String, ImageDescriptor>> images, Set<PackageProcessor> processors,
            ComputeServiceContext ctx) {
      this.images = images;
      this.packageProcessors = new EnumMap<PackageProcessor.Type, Set<PackageProcessor>>(Type.class);
      for (Type type : PackageProcessor.Type.values()) {
         Set<PackageProcessor> thisTypesProcessors = Sets.newLinkedHashSet();
         for (PackageProcessor processor : processors) {
            if (processor.type() == type) {
               thisTypesProcessors.add(processor);
            }
         }
         packageProcessors.put(type, thisTypesProcessors);
      }
      this.ctx = ctx;
   }

   @Override
   public Image makeImage(NodeMetadata node, String imageDescriptorId, String newImageName) {
      checkState(ctx.getComputeService().getImageExtension().isPresent(), "image extension is not present");
      checkState(this.images.get().containsKey(imageDescriptorId), "no image descriptor with id: " + imageDescriptorId);
      // get the descriptor that contains which packages are to be processed for this image
      ImageDescriptor descriptor = images.get().get(imageDescriptorId);

      // run the processors (in sequence as we have no guarantee they don't interact)
      boolean ranProcessors = false;
      for (Map.Entry<Type, Set<PackageProcessor>> entry : processorsCompatibleWithNode(node).entrySet()) {
         for (PackageProcessor processor : entry.getValue()) {
            processor.process(node, descriptor.getPackagesFor(processor.name(), processor.type()));
            ranProcessors = true;
         }
      }

      if (!ranProcessors) {
         logger.warn("Image not created because no PackageProcessors were compatible with node ");
         return null;
      }

      // build the image
      ImageExtension extension = ctx.getComputeService().getImageExtension().get();
      ImageTemplate template = extension.buildImageTemplateFromNode(newImageName, node.getId());

      try {
         return extension.createImage(template).get();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public Map<Type, Set<PackageProcessor>> registeredProcessors() {
      return Collections.unmodifiableMap(packageProcessors);
   }

   private Map<Type, Set<PackageProcessor>> processorsCompatibleWithNode(final NodeMetadata node) {
      Map<Type, Set<PackageProcessor>> compatibleProcessorsMap = new EnumMap<PackageProcessor.Type, Set<PackageProcessor>>(
               Type.class);
      for (Map.Entry<Type, Set<PackageProcessor>> entry : packageProcessors.entrySet()) {
         compatibleProcessorsMap.put(entry.getKey(), Sets.filter(entry.getValue(), new Predicate<PackageProcessor>() {
            @Override
            public boolean apply(@Nullable PackageProcessor input) {
               return input.isCompatible(node);
            }
         }));
      }
      return compatibleProcessorsMap;
   }
}
