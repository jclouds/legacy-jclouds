/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ibmdev.compute.suppliers;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ibmdev.compute.domain.IBMImage;
import org.jclouds.ibmdev.compute.domain.IBMSize;
import org.jclouds.ibmdev.domain.InstanceType;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMDeveloperCloudSizeSupplier implements Supplier<Set<? extends Size>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final Supplier<Set<? extends Image>> images;

   @Inject
   IBMDeveloperCloudSizeSupplier(Supplier<Set<? extends Image>> images) {
      this.images = images;
   }

   @Override
   public Set<? extends Size> get() {
      final Set<Size> sizes = Sets.newHashSet();
      logger.debug(">> providing sizes");
      for (Image in : images.get()) {
         IBMImage image = IBMImage.class.cast(in);
         for (InstanceType instanceType : image.getRawImage().getSupportedInstanceTypes())
            sizes.add(new IBMSize(image, instanceType));
      }
      logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }
}