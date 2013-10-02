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
package org.jclouds.glesys.compute.functions;

import static com.google.common.base.Predicates.containsPattern;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.util.ComputeServiceUtils;

import com.google.common.base.Function;

/**
 * Defaults to version null and 64bit, if the operating system is unrecognized
 * and the pattern "32bit" isn't in the string.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseOsFamilyVersion64BitFromImageName implements Function<String, OsFamilyVersion64Bit> {
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public ParseOsFamilyVersion64BitFromImageName(Map<OsFamily, Map<String, String>> osVersionMap) {
      this.osVersionMap = osVersionMap;
   }

   // ex Debian 6.0 64-bit
   // ex. Ubuntu 10.04 LTS 32-bit
   public static final Pattern OSFAMILY_VERSION = Pattern
         .compile("([^ ]+).*[ -]([0-9.]+)( LTS)?( [36][24]-bit)?( x[68][46])?$");
   public static final Pattern OSFAMILY = Pattern.compile("([^ ]+).*$");

   @Override
   public OsFamilyVersion64Bit apply(String input) {
      boolean is64Bit = containsPattern("64").apply(input);
      String version = "";

      if (input.indexOf("Windows") != -1) {
         Matcher matcher = Pattern.compile(".*(20[01][0-9] R[1-9]).*").matcher(input);
         if (matcher.find()) {
            version = matcher.group(1);
         } else {
            matcher = Pattern.compile(".*(20[01][0-9]).*").matcher(input);
            if (matcher.find())
               version = matcher.group(1);
         }
         return new OsFamilyVersion64Bit(OsFamily.WINDOWS, osVersionMap.get(OsFamily.WINDOWS).get(version), is64Bit);
      }
      Matcher osFamilyVersionMatcher = OSFAMILY_VERSION.matcher(input);
      if (osFamilyVersionMatcher.find()) {
         OsFamily fam = OsFamily.fromValue(osFamilyVersionMatcher.group(1).toLowerCase());
         if (fam == OsFamily.UNRECOGNIZED ) {
            return new OsFamilyVersion64Bit(OsFamily.UNRECOGNIZED, version, is64Bit);
         }
         return new OsFamilyVersion64Bit(fam, ComputeServiceUtils.parseVersionOrReturnEmptyString(fam,
               osFamilyVersionMatcher.group(2), osVersionMap), is64Bit);
      } else {
         Matcher osFamilyMatcher = OSFAMILY.matcher(input);
         OsFamily fam = OsFamily.UNRECOGNIZED;
         if (osFamilyMatcher.find()) {
            fam = OsFamily.fromValue(osFamilyMatcher.group(1).toLowerCase());
         }
         return new OsFamilyVersion64Bit(fam, version, is64Bit);
      }

   }
}
