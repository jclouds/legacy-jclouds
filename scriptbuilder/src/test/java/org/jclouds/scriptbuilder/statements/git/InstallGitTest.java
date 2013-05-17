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
package org.jclouds.scriptbuilder.statements.git;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.statements.git.InstallGit;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "InstallGitTest")
public class InstallGitTest {

   public void testInstallGitUNIX() throws IOException {
      assertEquals(new InstallGit().render(OsFamily.UNIX), "setupPublicCurl || return 1\ninstallGit || return 1\n");
   }

   public void testInstallGitUNIXInScriptBuilderSourcesSetupPublicCurl() throws IOException {
      assertEquals(InitScript.builder().name("install_git").run(new InstallGit()).build().render(OsFamily.UNIX),
               Resources.toString(Resources.getResource("test_install_git_scriptbuilder." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8));
   }

}
