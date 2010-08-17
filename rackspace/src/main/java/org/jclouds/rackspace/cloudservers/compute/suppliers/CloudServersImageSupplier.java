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

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudservers.CloudServersClient;
import org.jclouds.rackspace.cloudservers.options.ListOptions;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudServersImageSupplier implements Supplier<Set<? extends Image>> {
   public static final Pattern RACKSPACE_PATTERN = Pattern.compile("(([^ ]*) .*)");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private CloudServersClient sync;
   private Supplier<Location> location;

   @Inject
   CloudServersImageSupplier(CloudServersClient sync, Supplier<Location> location) {
      this.sync = sync;
      this.location = location;
   }

   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing images");
      for (final org.jclouds.rackspace.cloudservers.domain.Image from : sync.listImages(ListOptions.Builder
               .withDetails())) {
         OsFamily os = null;
         Architecture arch = Architecture.X86_64;
         String osDescription = "";
         String version = "";
         Matcher matcher = RACKSPACE_PATTERN.matcher(from.getName());
         osDescription = from.getName();
         if (from.getName().indexOf("Red Hat EL") != -1) {
            os = OsFamily.RHEL;
         } else if (from.getName().indexOf("Oracle EL") != -1) {
            os = OsFamily.OEL;
         } else if (matcher.find()) {
            try {
               os = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
         }
         images.add(new ImageImpl(from.getId() + "", from.getName(), from.getId() + "", location.get(), null,
                  ImmutableMap.<String, String> of(), from.getName(), version, os, osDescription, arch,
                  new Credentials("root", null)));
      }
      logger.debug("<< images(%d)", images.size());
      return images;
   }
}