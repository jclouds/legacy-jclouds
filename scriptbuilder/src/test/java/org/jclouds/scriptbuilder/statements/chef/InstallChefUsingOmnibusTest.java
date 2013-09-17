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
package org.jclouds.scriptbuilder.statements.chef;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Unit tests for the {@link InstallChefUsingOmnibus} statement.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "InstallChefClientUsingOmnibusTest")
public class InstallChefUsingOmnibusTest {

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "windows not supported")
   public void installChefUsingOmnibusInWindows() {
      new InstallChefUsingOmnibus().render(OsFamily.WINDOWS);
   }

   public void installChefUsingOmnibusInUnix() throws IOException {
      assertEquals(new InstallChefUsingOmnibus().render(OsFamily.UNIX), "setupPublicCurl || return 1\n"
            + "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 "
            + "-X GET  https://www.opscode.com/chef/install.sh |(bash)\n");
   }

   public void installChefUsingOmnibusInUnixInScriptBuilder() throws IOException {
      assertEquals(InitScript.builder().name("install_chef_omnibus").run(new InstallChefUsingOmnibus()).build()
            .render(OsFamily.UNIX), Resources.toString(
            Resources.getResource("test_install_chef_omnibus_scriptbuilder." + ShellToken.SH.to(OsFamily.UNIX)),
            Charsets.UTF_8));
   }
}
