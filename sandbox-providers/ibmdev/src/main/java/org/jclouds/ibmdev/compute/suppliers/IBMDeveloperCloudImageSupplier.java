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

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMDeveloperCloudImageSupplier implements Supplier<Set<? extends Image>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final IBMDeveloperCloudClient sync;
   private final Supplier<Map<String, ? extends Location>> locations;

   @Inject
   IBMDeveloperCloudImageSupplier(final IBMDeveloperCloudClient sync,
         Supplier<Map<String, ? extends Location>> locations) {
      this.sync = sync;
      this.locations = locations;
   }

   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing images");

      for (org.jclouds.ibmdev.domain.Image image : sync.listImages()) {
         // TODO parse correct OS
         // TODO manifest fails to parse due to encoding issues in the path
         // TODO get correct default credentials
         // http://www-180.ibm.com/cloud/enterprise/beta/ram/community/_rlvid.jsp.faces?_rap=pc_DiscussionForum.doDiscussionTopic&_rvip=/community/discussionForum.jsp&guid={DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&submission=false&fid=1068&tid=1527
         images.add(new ImageBuilder()
               .ids(image.getId())
               .name(image.getName())
               .location(locations.get().get(image.getLocation()))
               .operatingSystem(
                     new OperatingSystemBuilder()
                           .family((image.getPlatform().indexOf("Red Hat") != -1) ? OsFamily.RHEL : OsFamily.SUSE)
                           .arch(image.getPlatform()).is64Bit(image.getPlatform().indexOf("32") == -1).build())
               .description(image.getName()).version(image.getCreatedTime().getTime() + "")
               .defaultCredentials(new Credentials("idcuser", null)).build());
      }

      logger.debug("<< images(%d)", images.size());
      return images;
   }
}