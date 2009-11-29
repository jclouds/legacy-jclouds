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

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.jclouds.initbuilder.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "initbuilder.UtilsTest")
public class UtilsTest {

   public void testReplaceTokens() throws UnsupportedEncodingException {
      assertEquals(Utils.replaceTokens("hello {where}", ImmutableMap.of("where", "world")),
               "hello world");
   }

   public void testWriteVariableExportersUNIX() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("mavenOpts",
               "-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError"), OsFamily.UNIX),
               "export MAVEN_OPTS=\"-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError\"\n");
   }

   public void testWriteVariableExportersWindows() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("mavenOpts",
               "-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError"), OsFamily.WINDOWS),
               "set MAVEN_OPTS=-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError\r\n");
   }

   public void testWritePositionalVarsUNIX() {
      assertEquals(Utils.writePositionalVars(ImmutableList.of("host", "port"), OsFamily.UNIX),
               "set HOST=$1\nshift\nset PORT=$1\nshift\n");
   }

   public void testWritePositionalVarsWindows() {
      assertEquals(Utils.writePositionalVars(ImmutableList.of("host", "port"), OsFamily.WINDOWS),
               "set HOST=%1\r\nshift\r\nset PORT=%1\r\nshift\r\n");
   }

   public void testWriteUnsetVariablesUNIX() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("host", "port"), OsFamily.UNIX),
               "unset HOST PORT\n");
   }

   public void testWriteUnsetVariablesWindows() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("host", "port"), OsFamily.WINDOWS),
               "set HOST=\r\nset PORT=\r\n");
   }

   public void testWriteSwitchUNIX() {
      assertEquals(Utils.writeSwitch("i", ImmutableMap.of("0", "echo hello zero", "1",
               "echo hello one"), OsFamily.UNIX),
               "case $I in\n0)\n   echo hello zero\n   ;;\n1)\n   echo hello one\n   ;;\nesac\n");
   }

   public void testWriteSwitchWindows() {
      assertEquals(
               Utils.writeSwitch("i", ImmutableMap
                        .of("0", "echo hello zero", "1", "echo hello one"), OsFamily.WINDOWS),
               "goto CASE%I\r\n:CASE_0\r\n   echo hello zero\r\n   GOTO END_SWITCH\r\n:CASE_1\r\n   echo hello one\r\n   GOTO END_SWITCH\r\n:END_SWITCH\r\n");
   }
}
