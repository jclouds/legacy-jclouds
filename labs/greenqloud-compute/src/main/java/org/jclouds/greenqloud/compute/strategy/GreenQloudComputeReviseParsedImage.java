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
package org.jclouds.greenqloud.compute.strategy;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.logging.Logger;

/**
 * @author Adrian Cole
 */
@Singleton
public class GreenQloudComputeReviseParsedImage implements ReviseParsedImage {
   public static final Pattern DEFAULT_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");

   @javax.annotation.Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public GreenQloudComputeReviseParsedImage(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystem.Builder osBuilder) {
      if (from.getDescription() != null)
         osBuilder.is64Bit(from.getDescription().contains("64-bit"));

      Matcher matcher = DEFAULT_PATTERN.matcher(from.getDescription());
      if (matcher.find() && matcher.groupCount() >= 3) {
         osBuilder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(3), osVersionMap));
      }
   }
}
