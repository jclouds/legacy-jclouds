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
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Provider;

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

   // nebula/ubuntu-karmic
   // nebula/karmic-large
   public static final Pattern NEBULA_PATTERN = Pattern.compile("nebula/(ubuntu-)?(.*)(-.*)?");

   // 137112412989/amzn-ami-0.9.7-beta.i386-ebs
   // 137112412989/amzn-ami-0.9.7-beta.x86_64-ebs
   // amzn-ami-us-east-1/amzn-ami-0.9.7-beta.x86_64.manifest.xml
   // amzn-ami-us-east-1/amzn-ami-0.9.7-beta.i386.manifest.xml
   public static final Pattern AMZN_PATTERN = Pattern
         .compile(".*/amzn-ami-(.*)\\.(i386|x86_64)(-ebs|\\.manifest.xml)?");

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
   private final String provider;

   @Inject
   ImageParser(PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
         Supplier<Set<? extends Location>> locations, Supplier<Location> defaultLocation, @Provider String provider) {
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.locations = checkNotNull(locations, "locations");
      this.defaultLocation = checkNotNull(defaultLocation, "defaultLocation");
      this.provider = checkNotNull(provider, "provider");
   }

   @Override
   public Image apply(final org.jclouds.aws.ec2.domain.Image from) {
      if (from.getImageType() != ImageType.MACHINE) {
         logger.trace("skipping as not a machine image(%s)", from.getId());
         return null;
      }
      ImageBuilder builder = new ImageBuilder();
      builder.providerId(from.getId());
      builder.id(from.getRegion() + "/" + from.getId());
      builder.description(from.getDescription() != null ? from.getDescription() : from.getImageLocation());
      builder.userMetadata(ImmutableMap.<String, String> of("owner", from.getImageOwnerId(), "rootDeviceType", from
            .getRootDeviceType().toString()));

      OsFamily osFamily = parseOsFamilyOrNull(provider, from.getImageLocation());
      String osName = null;
      String osArch = from.getVirtualizationType();
      String osVersion = parseVersionOrReturnEmptyString(osFamily, from.getImageLocation());
      String osDescription = from.getImageLocation();
      boolean is64Bit = from.getArchitecture() == Architecture.X86_64;
      try {
         Matcher matcher = getMatcherAndFind(from.getImageLocation());
         if (matcher.pattern() == AMZN_PATTERN) {
            osFamily = OsFamily.AMZN_LINUX;
            osVersion = matcher.group(1);
            builder.version(osVersion);
         } else if (matcher.pattern() == NEBULA_PATTERN) {
            osVersion = parseVersionOrReturnEmptyString(osFamily, matcher.group(2));
         } else {
            osFamily = OsFamily.fromValue(matcher.group(1));
            osVersion = parseVersionOrReturnEmptyString(osFamily, matcher.group(2));
            builder.version(matcher.group(3).replace(".manifest.xml", ""));
         }
      } catch (IllegalArgumentException e) {
         logger.debug("<< didn't match os(%s)", from.getImageLocation());
      } catch (NoSuchElementException e) {
         logger.debug("<< didn't match at all(%s)", from.getImageLocation());
      }

      builder.defaultCredentials(credentialProvider.execute(from));

      try {
         builder.location(Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(from.getRegion());
            }

         }));
      } catch (NoSuchElementException e) {
         System.err.printf("unknown region %s for image %s; not in %s", from.getRegion(), from.getId(), locations);
         builder.location(new LocationImpl(LocationScope.REGION, from.getRegion(), from.getRegion(), defaultLocation
               .get().getParent()));
      }
      builder.operatingSystem(new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit));
      return builder.build();
   }

   /**
    * 
    * @throws NoSuchElementException
    *            if no configured matcher matches the manifest.
    */
   private Matcher getMatcherAndFind(String manifest) {
      for (Pattern pattern : new Pattern[] { AMZN_PATTERN, NEBULA_PATTERN, CANONICAL_PATTERN, RIGHTIMAGE_PATTERN,
            RIGHTSCALE_PATTERN }) {
         Matcher matcher = pattern.matcher(manifest);
         if (matcher.find())
            return matcher;
      }
      throw new NoSuchElementException(manifest);
   }
}