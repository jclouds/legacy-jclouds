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
package org.jclouds.aws.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImageParser implements Function<org.jclouds.aws.ec2.domain.Image, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public static final Pattern CANONICAL_PATTERN = Pattern
            .compile(".*/([^-]*)-([^-]*)-.*-(.*)(\\.manifest.xml)?");

   public static final Map<String, String> NAME_VERSION_MAP = ImmutableMap
            .<String, String> builder().put("hardy", "8.04").put("intrepid", "8.10").put("jaunty",
                     "9.04").put("karmic", "9.10").put("lucid", "10.04").put("maverick", "10.10")
            .build();

   private final PopulateDefaultLoginCredentialsForImageStrategy credentialProvider;
   private final Map<String, ? extends Location> locations;

   @Inject
   ImageParser(PopulateDefaultLoginCredentialsForImageStrategy credentialProvider,
            Map<String, ? extends Location> locations) {
      this.credentialProvider = checkNotNull(credentialProvider, "credentialProvider");
      this.locations = checkNotNull(locations, "locations");

   }

   @Override
   public Image apply(org.jclouds.aws.ec2.domain.Image from) {
      if (from.getImageLocation().indexOf("test") != -1) {
         logger.trace("skipping test image(%s)", from.getId());
         return null;
      }
      if (from.getImageType() != ImageType.MACHINE) {
         logger.trace("skipping as not a machine image(%s)", from.getId());
         return null;
      }
      OsFamily os = null;
      String name = "";
      String description = from.getDescription() != null ? from.getDescription() : from
               .getImageLocation();
      String osDescription = from.getImageLocation();
      String version = "";
      Matcher matcher = CANONICAL_PATTERN.matcher(from.getImageLocation());
      if (matcher.find()) {
         try {
            os = OsFamily.fromValue(matcher.group(1));
            name = matcher.group(2);// TODO no field for os version
            // normalize versions across ubuntu from alestic and canonical
            if (NAME_VERSION_MAP.containsKey(name))
               name = NAME_VERSION_MAP.get(name);
            version = matcher.group(3).replace(".manifest.xml", "");
         } catch (IllegalArgumentException e) {
            logger.debug("<< didn't match os(%s)", matcher.group(1));
         }
      }
      Credentials defaultCredentials = credentialProvider.execute(from);

      return new ImageImpl(
               from.getId(),
               name,
               locations.get(from.getRegion()),
               null,
               ImmutableMap.<String, String> of("owner", from.getImageOwnerId()),
               description,
               version,
               os,
               osDescription,
               from.getArchitecture() == org.jclouds.aws.ec2.domain.Image.Architecture.I386 ? Architecture.X86_32
                        : Architecture.X86_64, defaultCredentials);
   }
}