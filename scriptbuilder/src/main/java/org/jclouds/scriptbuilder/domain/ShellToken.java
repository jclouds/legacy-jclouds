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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

/**
 * Constants used in shell scripting.
 * 
 * @author Adrian Cole
 */
public enum ShellToken {

   FS, RM, CD, TMP, UID, ROOT, CLOSE_FD, PS, MD, ESCVAR,

   /**
    * If variable values need to be quoted when they include spaces, this will contain quotation
    * mark
    */
   VQ,
   /**
    * Left hand side of the function declaration directly before the name of the function.
    */
   FNCL,
   /**
    * Right hand side of the function declaration directly after the name of the function. opens the
    * code block
    */
   FNCR,
   /**
    * End the function. exits successfully and closes the code block.
    */
   FNCE, BEGIN_SCRIPT, END_SCRIPT, BEGIN_FUNCTIONS, EXIT, END_FUNCTIONS, EXPORT, LF, SH, SOURCE, REM, RETURN, ARGS, VARL, VARR, LIBRARY_PATH_VARIABLE;

   private static final LoadingCache<OsFamily, Map<String, String>> familyToTokenValueMap = CacheBuilder.newBuilder().build(
         new CacheLoader<OsFamily, Map<String, String>>() {

            @Override
            public Map<String, String> load(OsFamily from) {
               Map<String, String> map = Maps.newHashMap();
               for (ShellToken token : ShellToken.values()) {
                  map.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, token.toString()), token.to(from));
               }
               return map;
            }

         });

   public static Map<String, String> tokenValueMap(OsFamily family) {
      return familyToTokenValueMap.getUnchecked(family);
   }

   @SuppressWarnings("fallthrough")
   public String to(OsFamily family) {
      checkNotNull(family, "family");
      switch (this) {
         case FS:
            switch (family) {
               case WINDOWS:
                  return "\\";
               case UNIX:
                  return "/";
            }
         case FNCL:
            switch (family) {
               case WINDOWS:
                  return ":";
               case UNIX:
                  return "function ";
            }
         case FNCR:
            switch (family) {
               case WINDOWS:
                  return "\r\n";
               case UNIX:
                  return " {\n";
            }
         case FNCE:
            switch (family) {
               case WINDOWS:
                  return "   exit /b 0\r\n";
               case UNIX:
                  return "   return $?\n}\n";
            }
         case ESCVAR:
            switch (family) {
               case WINDOWS:
                  return "%";
               case UNIX:
                  return "\\";
            }
         case PS:
            switch (family) {
               case WINDOWS:
                  return ";";
               case UNIX:
                  return ":";
            }
         case CLOSE_FD:
            switch (family) {
               case WINDOWS:
                  return ">NUL";
               case UNIX:
                  return ">&-";
            }
         case RM:
            switch (family) {
               case WINDOWS:
                  return "del";
               case UNIX:
                  return "rm";
            }
         case MD:
            switch (family) {
               case WINDOWS:
                  return "md";
               case UNIX:
                  return "mkdir -p";
            }
         case VQ:
            switch (family) {
               case WINDOWS:
                  return "";
               case UNIX:
                  return "\"";
            }
         case BEGIN_FUNCTIONS:
            switch (family) {
               case WINDOWS:
                  return "GOTO FUNCTION_END\r\n";
               case UNIX:
                  return "";
            }
         case END_FUNCTIONS:
            switch (family) {
               case WINDOWS:
                  return ":FUNCTION_END\r\n";
               case UNIX:
                  return "";
            }
         case BEGIN_SCRIPT:
            switch (family) {
               case WINDOWS:
                  return "@echo off\r\n";
               case UNIX:
                  return "#!/bin/bash\nset +u\nshopt -s xpg_echo\nshopt -s expand_aliases\n";
            }
         case END_SCRIPT:
            switch (family) {
               case WINDOWS:
                  return "exit /b 0\r\n";
               case UNIX:
                  return "exit $?\n";
            }
         case EXPORT:
            switch (family) {
               case WINDOWS:
                  return "set";
               case UNIX:
                  return "export";
            }
         case RETURN:
            switch (family) {
               case WINDOWS:
                  return "exit /b";
               case UNIX:
                  return "return";
            }
         case EXIT:
            switch (family) {
               case WINDOWS:
                  return "exit /b";
               case UNIX:
                  return "exit";
            }
         case ROOT:
            switch (family) {
               case WINDOWS:
                  return "c:\\";
               case UNIX:
                  return "/";
            }
         case TMP:
            switch (family) {
               case WINDOWS:
                  return "%TEMP%";
               case UNIX:
                  return "/tmp";
            }
         case UID:
            switch (family) {
               case WINDOWS:
                  return "%USERNAME%";
               case UNIX:
                  return "$USER";
            }
         case LF:
            switch (family) {
               case WINDOWS:
                  return "\r\n";
               case UNIX:
                  return "\n";
            }
         case SH:
            switch (family) {
               case WINDOWS:
                  return "cmd";
               case UNIX:
                  return "sh";
            }
         case LIBRARY_PATH_VARIABLE:
            switch (family) {
               case WINDOWS:
                  return "PATH";
               case UNIX:
                  return "LD_LIBRARY_PATH";
            }
         case SOURCE:
            switch (family) {
               case WINDOWS:
                  return "@call";
               case UNIX:
                  return ".";
            }
         case CD:
            switch (family) {
               case WINDOWS:
                  return "cd /d";
               case UNIX:
                  return "cd";
            }
         case REM:
            switch (family) {
               case WINDOWS:
                  return "@rem";
               case UNIX:
                  return "#";
            }
         case ARGS:
            switch (family) {
               case WINDOWS:
                  return "%*";
               case UNIX:
                  return "$@";
            }
         case VARL:
            switch (family) {
               case WINDOWS:
                  return "%";
               case UNIX:
                  return "$";
            }
         case VARR:
            switch (family) {
               case WINDOWS:
                  return "%";
               case UNIX:
                  return "";
            }
         default:
            throw new UnsupportedOperationException("token " + this + " not configured");
      }
   }

}
