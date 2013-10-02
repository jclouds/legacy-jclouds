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
package org.jclouds.cloudservers.compute.functions;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudServersImageToOperatingSystem implements
      Function<org.jclouds.cloudservers.domain.Image, OperatingSystem> {
   public static final Pattern DEFAULT_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");
   // Windows Server 2008 R2 x64
   public static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows (.*) (x[86][64])");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public CloudServersImageToOperatingSystem(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   public OperatingSystem apply(final org.jclouds.cloudservers.domain.Image from) {
      OsFamily osFamily = null;
      String osName = null;
      String osArch = null;
      String osVersion = null;
      String osDescription = from.getName();
      boolean is64Bit = true;
      if (from.getName().indexOf("Red Hat EL") != -1) {
         osFamily = OsFamily.RHEL;
      } else if (from.getName().indexOf("Oracle EL") != -1) {
         osFamily = OsFamily.OEL;
      } else if (from.getName().indexOf("Windows") != -1) {
         osFamily = OsFamily.WINDOWS;
         Matcher matcher = WINDOWS_PATTERN.matcher(from.getName());
         if (matcher.find()) {
            osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(1), osVersionMap);
            is64Bit = matcher.group(2).equals("x64");
         }
      } else {
         Matcher matcher = DEFAULT_PATTERN.matcher(from.getName());
         if (matcher.find()) {
            try {
               osFamily = OsFamily.fromValue(matcher.group(2).toLowerCase());
            } catch (IllegalArgumentException e) {
               logger.debug("<< didn't match os(%s)", matcher.group(2));
            }
            osVersion = ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(3), osVersionMap);
         }
      }
      return new OperatingSystem(osFamily, osName, osVersion, osArch, osDescription, is64Bit);
   }
}
