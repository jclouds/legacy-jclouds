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
package org.jclouds.scriptbuilder.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * Utilities used to build init scripts.
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static final LowerCamelToUpperUnderscore FUNCTION_LOWER_CAMEL_TO_UPPER_UNDERSCORE = new LowerCamelToUpperUnderscore();

   public static final class LowerCamelToUpperUnderscore implements Function<String, String> {
      @Override
      public String apply(String from) {
         return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, from);
      }
   }

   public static final UpperUnderscoreToLowerCamel FUNCTION_UPPER_UNDERSCORE_TO_LOWER_CAMEL = new UpperUnderscoreToLowerCamel();

   public static final class UpperUnderscoreToLowerCamel implements Function<String, String> {
      @Override
      public String apply(String from) {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, from);
      }
   }

   /** matches any expression inside curly braces (where the expression does not including an open curly brace) */
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
      return replaceTokens(writeVariableExporters(variablesInLowerCamelCase), ShellToken
               .tokenValueMap(family));
   }

   /**
    * converts a map into variable exporters in shell intermediate language.
    * 
    * @param variablesInLowerCamelCase
    *           lower camel keys to values
    */
   public static String writeVariableExporters(Map<String, String> variablesInLowerCamelCase) {
      StringBuilder initializers = new StringBuilder();
      for (Entry<String, String> entry : variablesInLowerCamelCase.entrySet()) {
         String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, entry.getKey());
         initializers.append(String.format("{export} %s={vq}%s{vq}{lf}", key, entry.getValue()));
      }
      return initializers.toString();
   }

   public static String writeFunction(String function, String source, OsFamily family) {
      return replaceTokens(writeFunction(function, source), ShellToken.tokenValueMap(family));
   }

   public static String writeFunctionFromResource(String function, OsFamily family) {
      try {
         String toReturn = CharStreams.toString(Resources.newReaderSupplier(Resources.getResource(Utils.class, String
                  .format("/functions/%s.%s", function, ShellToken.SH.to(family))), Charsets.UTF_8));
         String lf = ShellToken.LF.to(family);
         return toReturn.endsWith(lf) ? toReturn : new StringBuilder(toReturn).append(lf).toString();
      } catch (IOException e) {
         throw new FunctionNotFoundException(function, family, e);
      }
   }

   public static class FunctionNotFoundException extends RuntimeException {
      /** The serialVersionUID */
      private static final long serialVersionUID = 1L;

      public FunctionNotFoundException(String functionName, OsFamily family) {
         super("function: " + functionName + " not found for famiy: " + family);
      }

      public FunctionNotFoundException(String functionName, OsFamily family, Throwable cause) {
         super("function: " + functionName + " not found for famiy: " + family, cause);
      }
   }

   public static String writeFunction(String function, String source) {
      return String.format("{fncl}%s{fncr}%s{fnce}", function, source.replaceAll("^", "   "));
   }

   public static final Map<OsFamily, String> OS_TO_POSITIONAL_VAR_PATTERN = ImmutableMap.of(
            OsFamily.UNIX, "set {key}=$1\nshift\n", OsFamily.WINDOWS, "set {key}=%1\r\nshift\r\n");

   public static final Map<OsFamily, String> OS_TO_LOCAL_VAR_PATTERN = ImmutableMap.of(
            OsFamily.UNIX, "set {key}=\"{value}\"\n", OsFamily.WINDOWS, "set {key}={value}\r\n");

   /**
    * Writes an initialization statement for use inside a script or a function.
    * 
    * @param positionalVariablesInLowerCamelCase
    *           - transfer the value of args into these statements. Note that there is no check to
    *           ensure that all source args are indeed present.
    */
   public static String writePositionalVars(List<String> positionalVariablesInLowerCamelCase,
            OsFamily family) {
      StringBuilder initializers = new StringBuilder();
      for (String variableInLowerCamelCase : positionalVariablesInLowerCamelCase) {
         String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,
                  variableInLowerCamelCase);
         initializers.append(replaceTokens(OS_TO_POSITIONAL_VAR_PATTERN.get(family), ImmutableMap
                  .of("key", key)));
      }
      return initializers.toString();
   }

   /**
    * Ensures that variables come from a known source instead of bleeding in from a profile
    * 
    * @param variablesInLowerCamelCase
    *           - System variables to unset
    */
   public static String writeUnsetVariables(List<String> variablesInLowerCamelCase, OsFamily family) {
      switch (family) {
         case UNIX:
            return String.format("unset %s\n", Joiner.on(' ').join(
                     Iterables.transform(variablesInLowerCamelCase,
                              FUNCTION_LOWER_CAMEL_TO_UPPER_UNDERSCORE)));
         case WINDOWS:
            StringBuilder initializers = new StringBuilder();
            for (String variableInLowerCamelCase : variablesInLowerCamelCase) {
               String key = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,
                        variableInLowerCamelCase);
               initializers.append(replaceTokens(OS_TO_LOCAL_VAR_PATTERN.get(family), ImmutableMap
                        .of("key", key, "value", "")));
            }
            return initializers.toString();
         default:
            throw new UnsupportedOperationException("unsupported os: " + family);
      }

   }

   public static final Map<OsFamily, String> OS_TO_ZERO_PATH = ImmutableMap.of(OsFamily.WINDOWS,
            "set PATH=c:\\windows\\;C:\\windows\\system32;c:\\windows\\system32\\wbem\r\n",
            OsFamily.UNIX, "export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin\n");

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
