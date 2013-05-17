/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.compute.functions;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerImageToImage implements Function<ServerImage, Image> {
   public static final Pattern GOGRID_OS_PATTERN = Pattern.compile("([a-zA-Z]*).*");
   public static final Pattern GOGRID_VERSION_PATTERN = Pattern.compile(".* ([0-9.]+) .*");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Map<ServerImageState, Status> toPortableImageStatus;
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   ServerImageToImage(Map<ServerImageState, Image.Status> toPortableImageStatus,
            Map<OsFamily, Map<String, String>> osVersionMap) {
      this.toPortableImageStatus = toPortableImageStatus;
      this.osVersionMap = osVersionMap;
   }

   protected OperatingSystem parseOs(ServerImage from) {
      OsFamily osFamily = null;
      String osName = from.getOs().getName();
      String osArch = from.getArchitecture().getDescription();
      String osVersion = null;
      String osDescription = from.getOs().getDescription();
      boolean is64Bit = from.getOs().getName().indexOf("64") != -1 || from.getDescription().indexOf("64") != -1;

      if (osName.startsWith("Windows")) {
         osFamily = OsFamily.WINDOWS;
      } else {
         Matcher matcher = GOGRID_OS_PATTERN.matcher(from.getName());
         if (matcher.find()) {
            try {
               osFamily = OsFamily.fromValue(matcher.group(1).toLowerCase());
            } catch (IllegalArgumentException e) {
               logger.debug("<< didn't match os(%s)", from.getName());
            }
         }
      }
      Matcher matcher = GOGRID_VERSION_PATTERN.matcher(osName);
      if (matcher.find()) {
         osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(1), osVersionMap);
      }
      // TODO determine DC images are in
      return new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit);
   }

   @Override
   public Image apply(ServerImage from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getFriendlyName());
      builder.description(from.getDescription());
      builder.operatingSystem(parseOs(from));
      builder.status(toPortableImageStatus.get(from.getState()));
      return builder.build();
   }

}
