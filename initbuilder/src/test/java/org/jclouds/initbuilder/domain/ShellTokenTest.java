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

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "initbuilder.ShellTokenTest")
public class ShellTokenTest {

   public void testTokenValueMapUNIX() {
      Map<String, String> expected = new ImmutableMap.Builder<String, String>().put("fs", "/").put(
               "ps", ":").put("lf", "\n").put("sh", "bash").put("source", ".").put("rem", "#").put(
               "args", "$@").put("varstart", "$").put("varend", "").put("libraryPathVariable",
               "LD_LIBRARY_PATH").put("shebang", "#!/bin/bash\n").build();

      assertEquals(ShellToken.tokenValueMap(OsFamily.UNIX), expected);
   }

   public void testTokenValueMapWindows() {
      Map<String, String> expected = new ImmutableMap.Builder<String, String>().put("fs", "\\")
               .put("ps", ";").put("lf", "\r\n").put("sh", "cmd").put("source", "@call").put("rem",
                        "@rem").put("args", "%*").put("varstart", "%").put("varend", "%").put(
                        "libraryPathVariable", "PATH").put("shebang", "@echo off\r\n").build();

      assertEquals(ShellToken.tokenValueMap(OsFamily.WINDOWS), expected);
   }
}
