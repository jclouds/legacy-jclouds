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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.util.ComputeServiceUtils;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseOsFromVAppTemplateName implements Function<String, OperatingSystem> {
   protected static final Pattern OS_PATTERN = Pattern.compile("(([^ ]*) ([0-9.]+) ?.*)");

   protected final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   protected ParseOsFromVAppTemplateName(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   @Override
   public OperatingSystem apply(String from) {
      OperatingSystemBuilder builder = new OperatingSystemBuilder();
      OsFamily osFamily = parseOsFamilyOrUnrecognized(checkNotNull(from, "vapp template name"));
      builder.family(osFamily);
      builder.description(from);
      builder.is64Bit(from.indexOf("64") != -1);
      Matcher matcher = OS_PATTERN.matcher(from);
      if (matcher.find()) {
         builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, matcher.group(3), osVersionMap));
      }
      return builder.build();
   }
}