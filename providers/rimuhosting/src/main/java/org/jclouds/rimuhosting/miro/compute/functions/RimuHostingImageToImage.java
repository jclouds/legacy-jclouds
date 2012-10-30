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
package org.jclouds.rimuhosting.miro.compute.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RimuHostingImageToImage implements Function<org.jclouds.rimuhosting.miro.domain.Image, Image> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   public Image apply(org.jclouds.rimuhosting.miro.domain.Image from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getDescription());
      builder.description(from.getDescription());
      builder.operatingSystem(parseOs(from));
      builder.status(Status.AVAILABLE);
      return builder.build();
   }

   public static final Pattern RIMU_PATTERN = Pattern.compile("([a-zA-Z]+) ?([0-9.]+) .*");

   protected OperatingSystem parseOs(final org.jclouds.rimuhosting.miro.domain.Image from) {
      OsFamily osFamily = null;
      String osName = from.getId();
      String osArch = null;
      String osVersion = null;
      String osDescription = from.getDescription();
      boolean is64Bit = from.getId().indexOf("64") != -1;

      Matcher matcher = RIMU_PATTERN.matcher(osDescription);
      if (matcher.find()) {
         try {
            osFamily = OsFamily.fromValue(matcher.group(1).toLowerCase());
            osVersion = matcher.group(2).toLowerCase();
         } catch (IllegalArgumentException e) {
            logger.debug("<< didn't match os(%s)", osDescription);
         }
      }
      return new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit);
   }
}
