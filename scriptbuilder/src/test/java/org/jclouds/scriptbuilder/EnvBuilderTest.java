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
package org.jclouds.scriptbuilder;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

/**
 * Tests possible uses of EnvBuilder
 * 
 * @author Adrian Cole
 */
public class EnvBuilderTest {

   EnvBuilder testScriptBuilder = new EnvBuilder().export("JAVA_HOME",
            "/apps/jdk1.6");

   @Test
   public void testBuildSimpleWindows() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.WINDOWS),
              Resources.toString(Resources.getResource("test_env."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8));
   }

   @Test
   public void testBuildSimpleUNIX() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.UNIX),
               Resources.toString(Resources.getResource("test_env."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8));
   }

   @Test
   public void testExport() {
      EnvBuilder builder = new EnvBuilder();
      builder.export("JAVA_HOME", "/apps/jdk1.6");
      assertEquals(builder.variables, ImmutableMap.of("JAVA_HOME", "/apps/jdk1.6"));
   }

   @Test
   public void testNoExport() {
      EnvBuilder builder = new EnvBuilder();
      assertEquals(builder.variables.size(), 0);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExportNPE() {
      new EnvBuilder().export(null, null);
   }

}
