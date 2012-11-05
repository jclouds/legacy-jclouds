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
package org.jclouds.scriptbuilder.statements.chef;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

@Test(groups = "unit", testName = "InstallChefGemsTest")
public class InstallChefGemsTest {

   @Test(expectedExceptions = UnsupportedOperationException.class,
         expectedExceptionsMessageRegExp = "windows not yet implemented")
   public void installChefGemsInWindows() {
      new InstallChefGems().render(OsFamily.WINDOWS);
   }

   public void installChefGemsUnix() throws IOException {
      assertEquals(
            new InstallChefGems().render(OsFamily.UNIX),
            Resources.toString(Resources.getResource("test_install_ruby." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8) + "installChefGems || return 1\n");
   }

   public void installChefGemsUnixInScriptBuilderSourcesSetupPublicCurl() throws IOException {
      assertEquals(
            InitScript.builder().name("install_chef_gems").run(new InstallChefGems()).build().render(OsFamily.UNIX),
            Resources.toString(
                  Resources.getResource("test_install_chef_gems_scriptbuilder." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8));
   }
}
