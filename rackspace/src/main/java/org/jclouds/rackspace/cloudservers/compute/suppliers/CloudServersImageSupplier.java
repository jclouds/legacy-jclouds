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

package org.jclouds.rackspace.cloudservers.compute.suppliers;

import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.withDetails;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudServersImageSupplier implements Supplier<Set<? extends Image>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final CloudServersClient sync;
   protected final Function<org.jclouds.rackspace.cloudservers.domain.Image, Image> cloudServersImageToImage;

   @Inject
   CloudServersImageSupplier(CloudServersClient sync,
            Function<org.jclouds.rackspace.cloudservers.domain.Image, Image> cloudServersImageToImage) {
      this.sync = sync;
      this.cloudServersImageToImage = cloudServersImageToImage;
   }

   @Override
   public Set<? extends Image> get() {
      Set<Image> images;
      logger.debug(">> providing images");
      images = Sets.<Image> newLinkedHashSet(Iterables.transform(sync.listImages(withDetails()),
               cloudServersImageToImage));
      logger.debug("<< images(%d)", images.size());
      return images;
   }
}