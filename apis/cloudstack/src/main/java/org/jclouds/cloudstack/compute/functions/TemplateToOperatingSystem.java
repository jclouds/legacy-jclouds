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
package org.jclouds.cloudstack.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class TemplateToOperatingSystem implements Function<Template, OperatingSystem> {
   // CentOS 5.2 (32-bit)
   public static final Pattern DEFAULT_PATTERN = Pattern.compile(".* ([0-9.]+) ?\\(.*");

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<Map<String, OSType>> osTypes;
   private final Supplier<Map<String, String>> osCategories;
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public TemplateToOperatingSystem(@Memoized Supplier<Map<String, OSType>> osTypes,
         @Memoized Supplier<Map<String, String>> osCategories, Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osTypes = checkNotNull(osTypes, "osTypes");
      this.osCategories = checkNotNull(osCategories, "osCategories");
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   public OperatingSystem apply(Template from) {
      Builder builder = OperatingSystem.builder().description(from.getOSType());

      OSType type = osTypes.get().get(from.getOSTypeId());
      if (type == null) {
         logger.warn("Template refers to OS type ID %s but this does not exist. Template=%s Known OS types=%s", from.getOSTypeId(), from, osTypes.get());
         return builder.build();
      }
      builder.description(type.getDescription());
      builder.is64Bit(type.getDescription().indexOf("64-bit") != -1);
      String osCategory = osCategories.get().get(type.getOSCategoryId());
      if (osCategory == null) {
         logger.warn("OS type refers to OS category ID %s but this does not exist. OS type=%s Known OS categories=%s", type.getOSCategoryId(), type, osCategories.get());
         return builder.build();
      }
      builder.name(osCategory);
      OsFamily family = OsFamily.fromValue(osCategory.toLowerCase());
      builder.family(family);
      Matcher matcher = DEFAULT_PATTERN.matcher(type.getDescription());
      if (matcher.find()) {
         builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(family, matcher.group(1), osVersionMap));
      }
      return builder.build();
   }
}
