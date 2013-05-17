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
package org.jclouds.scriptbuilder.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.functionloader.CurrentFunctionLoader;
import org.jclouds.util.Maps2;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Utilities used to build init scripts.
 * 
 * @author Adrian Cole
 */
public class Utils {

   /**
    * 
    * In {@link ShellToken}, the values whose names end in {@code _VARIABLE} designate variable
    * names we know how to translate from one platform to another. For example
    * {@link ShellToken#LIBRARY_PATH_VARIABLE} means that we can translate the variable named
    * {@code LIBRARY_PATH} to the proper platform-specific name.
    */
   public static final class VariableNameForOsFamily implements Function<String, String> {
      private final OsFamily family;

      public VariableNameForOsFamily(OsFamily family) {
         this.family = family;
      }

      @Override
      public String apply(String input) {
         String variableNameKey = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input) + "Variable";
         if (ShellToken.tokenValueMap(family).containsKey(variableNameKey))
            return ShellToken.tokenValueMap(family).get(variableNameKey);
         return input;
      }
   }

   /**
    * matches any expression inside curly braces (where the expression does not including an open
    * curly brace)
    */
   private static final Pattern pattern = Pattern.compile("\\{([^\\{]+?)\\}");

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

   /**
    * converts a map into variable exports relevant to the specified platform.
    * <p/>
    * ex. if {@code keys} is the map: "MAVEN_OPTS" -> "-Xms64m -Xmx256m" <br/>
    * and family is UNIX<br/>
    * then writeVariableExporters returns literally {@code export MAVEN_OPTS="-Xms64m -Xmx256m"\n}
    * 
    * @param exports
    *           keys are the variables to export in UPPER_UNDERSCORE case format
    * @param family
    *           operating system for formatting
    * @see VariableNameForOsFamily
    */
   public static String writeVariableExporters(Map<String, String> exports, final OsFamily family) {
      exports = Maps2.transformKeys(exports, new VariableNameForOsFamily(family));
      return replaceTokens(writeVariableExporters(exports), ShellToken.tokenValueMap(family));
   }

   /**
    * converts a map into variable exporters in shell intermediate language.
    * 
    * @param exports
    *           keys are the variables to export in UPPER_UNDERSCORE case format
    */
   public static String writeVariableExporters(Map<String, String> exports) {
      StringBuilder initializers = new StringBuilder();
      for (Entry<String, String> entry : exports.entrySet()) {
         initializers.append(String.format("{export} %s={vq}%s{vq}{lf}", entry.getKey(), entry.getValue()));
      }
      return initializers.toString();
   }

   public static String writeFunction(String function, String source, OsFamily family) {
      return replaceTokens(writeFunction(function, source), ShellToken.tokenValueMap(family));
   }

   public static String writeFunctionFromResource(String function, OsFamily family) {
      String toReturn = CurrentFunctionLoader.get().loadFunction(function, family);
      String lf = ShellToken.LF.to(family);
      return toReturn.endsWith(lf) ? toReturn : new StringBuilder(toReturn).append(lf).toString();
   }

   public static String writeFunction(String function, String source) {
      return String.format("{fncl}%s{fncr}%s{fnce}", function, source.replaceAll("^", "   "));
   }

   public static final Map<OsFamily, String> OS_TO_POSITIONAL_VAR_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "set {key}=$1\nshift\n", OsFamily.WINDOWS, "set {key}=%1\r\nshift\r\n");

   public static final Map<OsFamily, String> OS_TO_LOCAL_VAR_PATTERN = ImmutableMap.of(OsFamily.UNIX,
            "set {key}=\"{value}\"\n", OsFamily.WINDOWS, "set {key}={value}\r\n");

   /**
    * Writes an initialization statement for use inside a script or a function.
    * 
    * @param positionalVariables
    *           - transfer the value of args into these statements. Note that there is no check to
    *           ensure that all source args are indeed present.
    * 
    */
   public static String writePositionalVars(Iterable<String> positionalVariables, OsFamily family) {
      StringBuilder initializers = new StringBuilder();
      for (String positionalVariable : positionalVariables) {
         initializers.append(replaceTokens(OS_TO_POSITIONAL_VAR_PATTERN.get(family), ImmutableMap.of("key",
                  positionalVariable)));
      }
      return initializers.toString();
   }

   /**
    * Ensures that variables come from a known source instead of bleeding in from a profile
    * 
    * @param variablesToUnset
    *           - System variables to unset
    * @see VariableNameForOsFamily
    */
   public static String writeUnsetVariables(Iterable<String> variablesToUnset, OsFamily family) {
      variablesToUnset = Iterables.transform(variablesToUnset, new VariableNameForOsFamily(family));
      switch (family) {
         case UNIX:
            return String.format("unset %s\n", Joiner.on(' ').join(variablesToUnset));
         case WINDOWS:
            StringBuilder initializers = new StringBuilder();
            for (String variableToUnset : variablesToUnset) {
               initializers.append(replaceTokens(OS_TO_LOCAL_VAR_PATTERN.get(family), ImmutableMap.of("key",
                        variableToUnset, "value", "")));
            }
            return initializers.toString();
         default:
            throw new UnsupportedOperationException("unsupported os: " + family);
      }

   }

   public static final Map<OsFamily, String> OS_TO_ZERO_PATH = ImmutableMap.of(OsFamily.WINDOWS,
            "set PATH=c:\\windows\\;C:\\windows\\system32;c:\\windows\\system32\\wbem\r\n", OsFamily.UNIX,
            "export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin\n");

   /**
    * @return line used to zero out the path of the script such that basic commands such as unix ps
    *         will work.
    */
   public static String writeZeroPath(OsFamily family) {
      return OS_TO_ZERO_PATH.get(family);
   }

   public static String writeComment(String comment, OsFamily family) {
      return String.format("%s%s%s", ShellToken.REM.to(family), comment, ShellToken.LF.to(family));
   }
}
