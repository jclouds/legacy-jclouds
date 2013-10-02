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

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class UtilsTest {

   public void testReplaceTokens() {
      assertEquals(Utils.replaceTokens("hello {where}", ImmutableMap.of("where", "world")),
               "hello world");
   }

   public void testWriteFunctionFromResourceAddsNewlineUNIX() {
      assertEquals(Utils.writeFunctionFromResource("nonewline", OsFamily.UNIX), "foo\n");
   }
   

   public void testWriteFunctionFromResourceAddsNewlineWINDOWS() {
      assertEquals(Utils.writeFunctionFromResource("nonewline", OsFamily.WINDOWS), "foo\r\n");
   }
   
   public void testWriteVariableExportersUNIX() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("MAVEN_OPTS",
               "-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError"), OsFamily.UNIX),
               "export MAVEN_OPTS=\"-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError\"\n");
   }

   public void testWriteVariableExportersWindows() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("MAVEN_OPTS",
               "-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError"), OsFamily.WINDOWS),
               "set MAVEN_OPTS=-Xms128m -Xmx256m -XX:+HeapDumpOnOutOfMemoryError\r\n");
   }
   
   public void testWriteVariableExportersNameReplaceUNIX() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("LIBRARY_PATH", "{tmp}"), OsFamily.UNIX),
               "export LD_LIBRARY_PATH=\"/tmp\"\n");
   }

   public void testWriteVariableExportersNameReplaceWindows() {
      assertEquals(Utils.writeVariableExporters(ImmutableMap.of("LIBRARY_PATH", "{tmp}"), OsFamily.WINDOWS),
               "set PATH=%TEMP%\r\n");
   }

   public void testWritePositionalVarsUNIX() {
      assertEquals(Utils.writePositionalVars(ImmutableList.of("HOST", "PORT"), OsFamily.UNIX),
               "set HOST=$1\nshift\nset PORT=$1\nshift\n");
   }

   public void testWritePositionalVarsWindows() {
      assertEquals(Utils.writePositionalVars(ImmutableList.of("HOST", "PORT"), OsFamily.WINDOWS),
               "set HOST=%1\r\nshift\r\nset PORT=%1\r\nshift\r\n");
   }

   public void testWriteUnsetVariablesUNIX() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("HOST", "PORT"), OsFamily.UNIX),
               "unset HOST PORT\n");
   }

   public void testWriteUnsetVariablesWindows() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("HOST", "PORT"), OsFamily.WINDOWS),
               "set HOST=\r\nset PORT=\r\n");
   }
   

   public void testWriteUnsetVariablesNameReplaceUNIX() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("LIBRARY_PATH"), OsFamily.UNIX),
               "unset LD_LIBRARY_PATH\n");
   }

   public void testWriteUnsetVariablesNameReplaceWindows() {
      assertEquals(Utils.writeUnsetVariables(ImmutableList.of("LIBRARY_PATH"), OsFamily.WINDOWS),
               "set PATH=\r\n");
   }

   public void testSingleCurlyBraceDoesntBreakLfTokenReplacement() {
      assertEquals(Utils.replaceTokens("{{lf}", ShellToken.tokenValueMap(OsFamily.UNIX)),
            "{\n");
   }

}
