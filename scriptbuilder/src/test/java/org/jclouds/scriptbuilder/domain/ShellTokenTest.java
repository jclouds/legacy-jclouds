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

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ShellTokenTest {

   public void testTokenValueMapUNIX() {
      Map<String, String> expected = new ImmutableMap.Builder<String, String>().put("fs", "/").put(
               "ps", ":").put("lf", "\n").put("sh", "sh").put("source", ".").put("rem", "#").put(
               "args", "$@").put("varl", "$").put("return", "return").put("exit", "exit").put(
               "varr", "").put("libraryPathVariable", "LD_LIBRARY_PATH").put("beginScript",
               "#!/bin/bash\nset +u\nshopt -s xpg_echo\nshopt -s expand_aliases\n").put(
               "endScript", "exit $?\n").put("vq", "\"").put("beginFunctions", "").put(
               "endFunctions", "").put("fncl", "function ").put("fncr", " {\n").put("fnce",
               "   return $?\n}\n").put("export", "export").put("rm", "rm").put("cd", "cd").put(
               "tmp", "/tmp").put("uid", "$USER").put("root", "/").put("closeFd", ">&-").put("md",
               "mkdir -p").put("escvar", "\\").build();
      assertEquals(ShellToken.tokenValueMap(OsFamily.UNIX), expected);
   }

   public void testTokenValueMapWindows() {
      Map<String, String> expected = new ImmutableMap.Builder<String, String>().put("fs", "\\")
               .put("ps", ";").put("lf", "\r\n").put("sh", "cmd").put("source", "@call").put("rem",
                        "@rem").put("args", "%*").put("varl", "%").put("exit", "exit /b").put(
                        "varr", "%").put("libraryPathVariable", "PATH").put("return", "exit /b")
               .put("vq", "").put("beginFunctions", "GOTO FUNCTION_END\r\n").put("endFunctions",
                        ":FUNCTION_END\r\n").put("beginScript", "@echo off\r\n").put("endScript",
                        "exit /b 0\r\n").put("fncl", ":").put("fncr", "\r\n").put("fnce",
                        "   exit /b 0\r\n").put("export", "set").put("rm", "del")
               .put("cd", "cd /d").put("tmp", "%TEMP%").put("uid", "%USERNAME%")
               .put("root", "c:\\").put("closeFd", ">NUL").put("md", "md").put("escvar", "%")
               .build();

      assertEquals(ShellToken.tokenValueMap(OsFamily.WINDOWS), expected);
   }
}
