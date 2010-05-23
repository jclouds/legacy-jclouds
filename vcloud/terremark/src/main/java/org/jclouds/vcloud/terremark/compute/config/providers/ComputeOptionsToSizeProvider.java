/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute.config.providers;

import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.domain.ComputeOptions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ComputeOptionsToSizeProvider implements Provider<Set<? extends Size>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final TerremarkVCloudClient client;
   private final ComputeOptionsToSize sizeConverter;
   private final Provider<Set<? extends Image>> imageProvider;

   @Inject
   ComputeOptionsToSizeProvider(TerremarkVCloudClient client, ComputeOptionsToSize sizeConverter,
            Provider<Set<? extends Image>> imageProvider) {
      this.client = client;
      this.sizeConverter = sizeConverter;
      this.imageProvider = imageProvider;
   }

   @Singleton
   private static class ComputeOptionsToSize implements Function<ComputeOptions, Size> {
      @Override
      public Size apply(ComputeOptions from) {
         return new SizeImpl(from.toString(), from.toString(), from.toString(), null, null,
                  ImmutableMap.<String, String> of(), from.getProcessorCount(), from.getMemory(),
                  10, ImagePredicates.any());
      }
   }

   /**
    * TODO this implementation isn't purely correct, as the sizes for image 0 are not
    * necessarily the same for all other images
    */
   @Override
   public Set<? extends Size> get() {
      Image anyImage = Iterables.get(imageProvider.get(), 0);
      logger.debug(">> providing sizes");
      SortedSet<Size> sizes = Sets.newTreeSet(Iterables.transform(client
               .getComputeOptionsOfCatalogItem(anyImage.getProviderId()), sizeConverter));
      logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

}