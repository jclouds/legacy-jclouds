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

package org.jclouds.epc.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.logging.Logger;

/**
 * @author Adrian Cole
 */
@Singleton
public class EucalyptusPartnerCloudReviseParsedImage implements ReviseParsedImage {

   // centos-5.3-x86_64-xen/centos.5-3.x86-64.img.manifest.xml
   public static final Pattern PATTERN = Pattern.compile("^([^-]+)-([^-]+)-.*");
   public static final Pattern WINDOWS = Pattern.compile("^windows-([^/]+)/.*");

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public EucalyptusPartnerCloudReviseParsedImage(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   @Override
   public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystemBuilder osBuilder) {
      try {
         if (from.getImageLocation().startsWith("windows")) {
            family = OsFamily.WINDOWS;
            osBuilder.family(family);
            Matcher matcher = WINDOWS.matcher(from.getImageLocation());
            if (matcher.find()) {
               osBuilder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(1).replace(
                        '-', ' ').replace('s', 'S'), osVersionMap));
            }
         } else {
            Matcher matcher = PATTERN.matcher(from.getImageLocation());
            if (matcher.find()) {
               family = OsFamily.fromValue(matcher.group(1));
               osBuilder.family(family);
               osBuilder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(2),
                        osVersionMap));
            }
         }
      } catch (IllegalArgumentException e) {
         logger.debug("<< didn't match os(%s)", from.getImageLocation());
      } catch (NoSuchElementException e) {
         logger.debug("<< didn't match at all(%s)", from.getImageLocation());
      }
   }
}