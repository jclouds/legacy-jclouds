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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.domain.Image;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;

/**
 * A function for transforming a nova specific Image into a generic OperatingSystem object.
 * 
 * @author Matt Stephenson
 */
public class ImageToOperatingSystem implements Function<Image, OperatingSystem> {
   public static final Pattern DEFAULT_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");
   // Windows Server 2008 R2 x64
   public static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows (.*) (x[86][64])");

   @javax.annotation.Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public ImageToOperatingSystem(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   public OperatingSystem apply(final Image from) {
      OsFamily osFamily = null;
      String osVersion = null;

      String imageName = Objects.firstNonNull(from.getName(), "unspecified");

      boolean is64Bit = true;

      if (imageName.indexOf("Windows") != -1) {
         osFamily = OsFamily.WINDOWS;
         Matcher matcher = WINDOWS_PATTERN.matcher(from.getName());
         if (matcher.find()) {
            osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(1), osVersionMap);
            is64Bit = matcher.group(2).equals("x64");
         }
      } else {
         if (imageName.contains("Red Hat EL")) {
            osFamily = OsFamily.RHEL;
         } else if (imageName.contains("Oracle EL")) {
            osFamily = OsFamily.OEL;
         } else {
            final Iterable<String> imageNameParts = Splitter.on(CharMatcher.WHITESPACE).trimResults().split(
                     imageName.toLowerCase());

            try {
               osFamily = find(Arrays.asList(OsFamily.values()), new Predicate<OsFamily>() {
                  @Override
                  public boolean apply(@Nullable OsFamily osFamily) {
                     return any(imageNameParts, equalTo(osFamily.name().toLowerCase()));
                  }
               });
            } catch (NoSuchElementException e) {
               String ubuntuVersion = startsWithUbuntuVersion(imageNameParts);
               if (ubuntuVersion != null) {
                  osFamily = OsFamily.UBUNTU;
                  osVersion = ubuntuVersion;
               } else {
                  logger.trace("could not parse operating system family for image(%s): %s", from.getId(), imageNameParts);
                  osFamily = OsFamily.UNRECOGNIZED;
               }
            }
         }
         Matcher matcher = DEFAULT_PATTERN.matcher(imageName);
         if (matcher.find() && matcher.groupCount() >= 3) {
            osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(3), osVersionMap);
         }
      }
      return new OperatingSystem(osFamily, imageName, osVersion, null, imageName, is64Bit);
   }

   String startsWithUbuntuVersion(final Iterable<String> imageNameParts) {
      Map<String, String> ubuntuVersions = osVersionMap.get(OsFamily.UBUNTU);
      for (String ubuntuKey : filter(ubuntuVersions.keySet(), not(equalTo("")))) {
         if (any(imageNameParts, containsPattern("^" + ubuntuKey + ".*"))) {
            return ubuntuVersions.get(ubuntuKey);
         }
      }
      return null;
   }
}
