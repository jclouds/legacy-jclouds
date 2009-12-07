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
package org.jclouds.scriptbuilder;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.findPid;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.kill;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;
import static org.jclouds.scriptbuilder.domain.Statements.switchArg;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.SwitchArg;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * Tests possible uses of ScriptBuilder
 * 
 * @author Adrian Cole
 */
public class ScriptBuilderTest {

   ScriptBuilder testScriptBuilder = new ScriptBuilder()
            .unsetEnvironmentVariable("runtime")
            .addEnvironmentVariableScope("default", ImmutableMap.of("runtime", "Moo"))
            .addStatement(
                     switchArg(
                              1,
                              ImmutableMap
                                       .of(
                                                "start",
                                                newStatementList(
                                                         call("default"),
                                                         interpret("echo start {varl}RUNTIME{varr}{lf}")),
                                                "stop",
                                                newStatementList(
                                                         call("default"),
                                                         interpret("echo stop {varl}RUNTIME{varr}{lf}")),
                                                "status",
                                                newStatementList(interpret("echo {vq}the following should be []: [{varl}RUNTIME{varr}]{vq}{lf}")))));

   @Test
   public void testBuildSimpleWindows() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_script."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testBuildSimpleUNIX() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_script."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   ScriptBuilder findPidBuilder = new ScriptBuilder().addStatement(findPid("{args}")).addStatement(
            interpret("echo {varl}FOUND_PID{varr}{lf}"));

   @Test
   public void testFindPidWindows() throws MalformedURLException, IOException {
      assertEquals(findPidBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_find_pid."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testFindPidUNIX() throws MalformedURLException, IOException {
      assertEquals(findPidBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_find_pid."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   ScriptBuilder seekAndDestroyBuilder = new ScriptBuilder().addStatement(findPid("{args}"))
            .addStatement(kill());

   @Test
   public void testSeekAndDestroyWindows() throws MalformedURLException, IOException {
      assertEquals(seekAndDestroyBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_seek_and_destroy."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testSeekAndDestroyUNIX() throws MalformedURLException, IOException {
      assertEquals(seekAndDestroyBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_seek_and_destroy."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   @Test
   public void testSwitchOn() {
      ScriptBuilder builder = new ScriptBuilder();
      builder.addStatement(switchArg(1, ImmutableMap.of("start", interpret("echo started{lf}"),
               "stop", interpret("echo stopped{lf}"))));
      assertEquals(builder.statements, ImmutableList.of(new SwitchArg(1, ImmutableMap.of("start",
               interpret("echo started{lf}"), "stop", interpret("echo stopped{lf}")))));
   }

   @Test
   public void testNoSwitchOn() {
      ScriptBuilder builder = new ScriptBuilder();
      assertEquals(builder.statements.size(), 0);
   }

   @Test
   public void testExport() {
      ScriptBuilder builder = new ScriptBuilder();
      builder.addEnvironmentVariableScope("default", ImmutableMap.of("javaHome", "/apps/jdk1.6"));
      assertEquals(builder.variableScopes, ImmutableMap.of("default", ImmutableMap.of("javaHome",
               "/apps/jdk1.6")));
   }

   @Test
   public void testNoExport() {
      ScriptBuilder builder = new ScriptBuilder();
      assertEquals(builder.variableScopes.size(), 0);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExportNPE() {
      new ScriptBuilder().addEnvironmentVariableScope(null, null);
   }

}
