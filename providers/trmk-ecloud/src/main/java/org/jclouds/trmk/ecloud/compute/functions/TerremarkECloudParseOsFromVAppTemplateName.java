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
package org.jclouds.trmk.ecloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.trmk.vcloud_0_8.compute.functions.ParseOsFromVAppTemplateName;

/**
 * @author Adrian Cole
 */
@Singleton
public class TerremarkECloudParseOsFromVAppTemplateName extends ParseOsFromVAppTemplateName {
   // CentOS 5 (x64)
   public static final Pattern OS_PATTERN = Pattern.compile("^-?([^ ]*) ([0-9.]+)( R[1-9])? ?.*");

   @Inject
   protected TerremarkECloudParseOsFromVAppTemplateName(Map<OsFamily, Map<String, String>> osVersionMap) {
      super(osVersionMap);
   }

   @Override
   public OperatingSystem apply(String from) {
      checkNotNull(from, "vapp template name");
      Builder builder = new OperatingSystem.Builder();
      builder.description(from);
      if (from.equals("-Windows 2003 Std. R2 SQL 2005 Std. (x64)"))
         System.out.print(';');
      builder.is64Bit(from.indexOf("64") != -1);
      from = from.replace("Red Hat Enterprise Linux", "RHEL").replace("Sun Solaris", "SOLARIS").replace(
               " Server", "").replace("Std. ", "");
      Matcher matcher = OS_PATTERN.matcher(from);
      if (matcher.find()) {
         OsFamily osFamily = parseOsFamilyOrUnrecognized(matcher.group(1));
         builder.family(osFamily);
         String version = (matcher.group(3) != null) ? matcher.group(2) + matcher.group(3) : matcher.group(2);
         builder.version(ComputeServiceUtils.parseVersionOrReturnEmptyString(osFamily, version, osVersionMap));
      } else {
         OsFamily osFamily = parseOsFamilyOrUnrecognized(from);
         builder.family(osFamily);
      }
      return builder.build();
   }
}
