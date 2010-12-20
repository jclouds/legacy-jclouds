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

package org.jclouds.cloudsigma.compute.functions;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;

import com.google.common.base.Function;

/**
 * Defaults to version null and 64bit, if the operating system is unrecognized and the pattern
 * "32bit" isn't in the string.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseOsFamilyVersion64BitFromImageName implements Function<String, OsFamilyVersion64Bit> {

   // ex CentOS 5.5 Linux 64bit Preinstalled System with AppFirst Monitoring
   public static final Pattern PATTERN = Pattern.compile("([^ ]+)[^0-9]([0-9.]+) .*");

   @Override
   public OsFamilyVersion64Bit apply(String input) {
      boolean is64Bit = and(not(containsPattern("32bit")),
            or(containsPattern("64bit"), not(containsPattern("Windows")))).apply(input);
      if (input.contains("Windows")) {
         String version = null;
         Matcher matcher = Pattern.compile(".*(20[01][0-9] R[1-9]).*").matcher(input);
         if (matcher.find()) {
            version = matcher.group(1);
         } else {
            matcher = Pattern.compile(".*(20[01][0-9]).*").matcher(input);
            if (matcher.find())
               version = matcher.group(1);
         }
         return new OsFamilyVersion64Bit(OsFamily.WINDOWS, version, is64Bit);
      } else {
         Matcher matcher = PATTERN.matcher(input);
         if (matcher.find()) {
            OsFamily fam = OsFamily.fromValue(matcher.group(1).toLowerCase());
            String version = matcher.group(2);
            switch (fam) {
            case UNRECOGNIZED:
               return new OsFamilyVersion64Bit(OsFamily.UNRECOGNIZED, null, is64Bit);
            case WINDOWS:
            }
            return new OsFamilyVersion64Bit(fam, version, is64Bit);
         } else {
            return new OsFamilyVersion64Bit(OsFamily.UNRECOGNIZED, null, is64Bit);
         }
      }
   }
}