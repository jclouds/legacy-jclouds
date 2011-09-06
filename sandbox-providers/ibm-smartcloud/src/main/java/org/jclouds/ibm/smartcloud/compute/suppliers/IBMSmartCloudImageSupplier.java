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
package org.jclouds.ibm.smartcloud.compute.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.ibm.smartcloud.IBMSmartCloudClient;
import org.jclouds.ibm.smartcloud.domain.Image.Architecture;
import org.jclouds.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class IBMSmartCloudImageSupplier implements Supplier<Set<? extends Image>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final IBMSmartCloudClient sync;
   private final Supplier<Map<String, ? extends Location>> locations;

   @Inject
   IBMSmartCloudImageSupplier(final IBMSmartCloudClient sync, Supplier<Map<String, ? extends Location>> locations) {
      this.sync = sync;
      this.locations = locations;
   }

   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing images");

      for (org.jclouds.ibm.smartcloud.domain.Image image : sync.listImages()) {

         OperatingSystem.Builder osBuilder = OperatingSystem.builder();
         Iterable<String> osVersion = Splitter.on('/').split(checkNotNull(image.getPlatform(), "platform"));
         osBuilder.version(Iterables.get(osVersion, 1));
         osBuilder.name(image.getPlatform());
         osBuilder.description(image.getPlatform());
         if ("Red Hat Enterprise Linux".equals(Iterables.get(osVersion, 0)))
            osBuilder.family(OsFamily.RHEL);
         else if ("SUSE Linux Enterprise Server".equals(Iterables.get(osVersion, 0)))
            osBuilder.family(OsFamily.SUSE);
         else if ("Windows".equals(Iterables.get(osVersion, 0)))
            osBuilder.family(OsFamily.WINDOWS);
         else
            osBuilder.family(OsFamily.UNRECOGNIZED);
         osBuilder.arch(image.getArchitecture().toString());
         osBuilder.is64Bit(image.getArchitecture() == Architecture.X86_64);

         // TODO manifest fails to parse due to encoding issues in the path
         // TODO get correct default credentials
         // http://www-180.ibm.com/cloud/enterprise/beta/ram/community/_rlvid.jsp.faces?_rap=pc_DiscussionForum.doDiscussionTopic&_rvip=/community/discussionForum.jsp&guid={DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&submission=false&fid=1068&tid=1527
         images.add(new ImageBuilder().ids(image.getId()).name(image.getName()).location(
                  locations.get().get(image.getLocation())).operatingSystem(osBuilder.build()).description(
                  image.getName()).version(image.getCreatedTime().getTime() + "").defaultCredentials(
                  new Credentials("idcuser", null)).build());
      }

      logger.debug("<< images(%d)", images.size());
      return images;
   }
}
