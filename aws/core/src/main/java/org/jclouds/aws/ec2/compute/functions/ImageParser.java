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

package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.Image.Architecture;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImageParser implements Function<org.jclouds.aws.ec2.domain.Image, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public static final Pattern CANONICAL_PATTERN = Pattern.compile(".*/([^-]*)-([^-]*)-.*-(.*)(\\.manifest.xml)?");

   // ex rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml
   public static final Pattern RIGHTSCALE_PATTERN = Pattern
            .compile("[^/]*/([^_]*)_([^_]*)_[^vV]*[vV](.*)(\\.manifest.xml)?");

   // ex 411009282317/RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha
   public static final Pattern RIGHTIMAGE_PATTERN = Pattern
            .compile("[^/]*/RightImage_([^_]*)_([^_]*)_[^vV]*[vV](.*)(\\.manifest.xml)?");

   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Supplier<Set<? extends Location>> locations;

   private final Supplier<Location> defaultLocation;

   @Inject
   ImageParser(PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
            Supplier<Set<? extends Location>> locations, Supplier<Location> defaultLocation) {
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.locations = checkNotNull(locations, "locations");
      this.defaultLocation = checkNotNull(defaultLocation, "defaultLocation");

   }

   @Override
   public Image apply(final org.jclouds.aws.ec2.domain.Image from) {
      if (from.getImageLocation().indexOf("test") != -1) {
         logger.trace("skipping test image(%s)", from.getId());
         return null;
      }
      if (from.getImageType() != ImageType.MACHINE) {
         logger.trace("skipping as not a machine image(%s)", from.getId());
         return null;
      }
      String name = null;
      String description = from.getDescription() != null ? from.getDescription() : from.getImageLocation();
      String version = null;

      OsFamily osFamily = parseOsFamilyOrNull(from.getImageLocation());
      String osName = null;
      String osArch = from.getVirtualizationType();
      String osVersion = parseVersionOrReturnEmptyString(osFamily, from.getImageLocation());
      String osDescription = from.getImageLocation();
      boolean is64Bit = from.getArchitecture() == Architecture.X86_64;
      try {
         Matcher matcher = getMatcherAndFind(from.getImageLocation());
         osFamily = OsFamily.fromValue(matcher.group(1));
         osVersion = parseVersionOrReturnEmptyString(osFamily, matcher.group(2));
         version = matcher.group(3).replace(".manifest.xml", "");
      } catch (IllegalArgumentException e) {
         logger.debug("<< didn't match os(%s)", from.getImageLocation());
      } catch (NoSuchElementException e) {
         logger.debug("<< didn't match at all(%s)", from.getImageLocation());
      }

      Credentials defaultCredentials = credentialProvider.execute(from);

      Location location = null;
      try {
         location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(from.getRegion());
            }

         });
      } catch (NoSuchElementException e) {
         System.err.printf("unknown region %s for image %s; not in %s", from.getRegion(), from.getId(), locations);
         location = new LocationImpl(LocationScope.REGION, from.getRegion(), from.getRegion(), defaultLocation.get()
                  .getParent());
      }
      OperatingSystem os = new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit);
      return new ImageImpl(from.getId(), name, from.getRegion() + "/" + from.getId(), location, null, ImmutableMap
               .<String, String> of("owner", from.getImageOwnerId()), os, description, version, defaultCredentials);

   }

   /**
    * 
    * @throws NoSuchElementException
    *            if no configured matcher matches the manifest.
    */
   private Matcher getMatcherAndFind(String manifest) {
      for (Pattern pattern : new Pattern[] { CANONICAL_PATTERN, RIGHTIMAGE_PATTERN, RIGHTSCALE_PATTERN }) {
         Matcher matcher = pattern.matcher(manifest);
         if (matcher.find())
            return matcher;
      }
      throw new NoSuchElementException(manifest);
   }
}