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
package org.jclouds.initbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

/**
 * Constants used in shell scripting.
 * 
 * @author Adrian Cole
 */
public enum ShellToken {

   FS, PS, LF, SH, SOURCE, REM, ARGS, VARSTART, VAREND, SHEBANG, LIBRARY_PATH_VARIABLE;

   private static final Map<OsFamily, Map<String, String>> familyToTokenValueMap = new MapMaker()
            .makeComputingMap(new Function<OsFamily, Map<String, String>>() {

               @Override
               public Map<String, String> apply(OsFamily from) {
                  Map<String, String> map = Maps.newHashMap();
                  for (ShellToken token : ShellToken.values()) {
                     map.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, token
                              .toString()), token.to(from));
                  }
                  return map;
               }

            });

   public static Map<String, String> tokenValueMap(OsFamily family) {
      return familyToTokenValueMap.get(family);
   }

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
         case PS:
            switch (family) {
               case WINDOWS:
                  return ";";
               case UNIX:
                  return ":";
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
                  return "bash";
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
         case VARSTART:
            switch (family) {
               case WINDOWS:
                  return "%";
               case UNIX:
                  return "$";
            }
         case VAREND:
            switch (family) {
               case WINDOWS:
                  return "%";
               case UNIX:
                  return "";
            }
         case SHEBANG:
            switch (family) {
               case WINDOWS:
                  return "@echo off\r\n";
               case UNIX:
                  return "#!/bin/bash\n";
            }

         default:
            throw new UnsupportedOperationException("token " + this + " not configured");
      }
   }

}