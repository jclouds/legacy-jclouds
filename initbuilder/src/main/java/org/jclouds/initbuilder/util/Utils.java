/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.initbuilder.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.initbuilder.domain.OsFamily;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;

/**
 * Utilities used to build init scripts.
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static final Pattern pattern = Pattern.compile("\\{(.+?)\\}");

   /**
    * replaces tokens that are expressed as <code>{token}</code>
    * 
    * <p/>
    * ex. if input is "hello {where}"<br/>
    * and replacements is "where" -> "world" <br/>
    * then replaceTokens returns "hello world"
    * 
    * @param input
    *           source to replace
    * @param replacements
    *           token/value pairs
    */
   public static String replaceTokens(String input, Map<String, String> replacements) {
      Matcher matcher = pattern.matcher(input);
      StringBuilder builder = new StringBuilder();
      int i = 0;
      while (matcher.find()) {
         String replacement = replacements.get(matcher.group(1));
         builder.append(input.substring(i, matcher.start()));
         if (replacement == null)
            builder.append(matcher.group(0));
         else
            builder.append(replacement);
         i = matcher.end();
      }
      builder.append(input.substring(i, input.length()));
      return builder.toString();
   }

   public static final Map<OsFamily, String> OS_TO_EXPORTER_PATTERN = ImmutableMap.of(
            OsFamily.UNIX, "export {key}=\"{value}\"\n", OsFamily.WINDOWS, "set {key}={value}\r\n");

   /**
    * converts a map into variable exports relevant to the specified platform.
    * <p/>
    * ex. if variablesInLowerCamelCase is "mavenOpts" -> "-Xms64m -Xmx256m" <br/>
    * and family is UNIX<br/>
    * then writeVariableExporters returns literally {@code export MAVEN_OPTS="-Xms64m -Xmx256m"\n}
    * 
    * @param variablesInLowerCamelCase
    *           lower camel keys to values
    * @param family
    *           operating system for formatting
    */
   public static String writeVariableExporters(Map<String, String> variablesInLowerCamelCase,
            OsFamily family) {
      StringBuilder initializers = new StringBuilder();
      for (Entry<String, String> entry : variablesInLowerCamelCase.entrySet()) {
         String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, entry.getKey());
         initializers.append(replaceTokens(OS_TO_EXPORTER_PATTERN.get(family), ImmutableMap.of(
                  "key", key, "value", entry.getValue())));
      }
      return initializers.toString();
   }
}
