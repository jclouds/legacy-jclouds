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
package org.jclouds.scriptbuilder.statements.ruby;

import static org.jclouds.scriptbuilder.statements.ruby.InstallRubyGems.DEFAULT_RUBYGEMS_VERSION;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Unit tests for the {@link InstallRubyGemsTest} statement.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "InstallRubyGemsTest")
public class InstallRubyGemsTest {

   @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = "windows not yet implemented")
   public void installRubyGemsInWindows() {
      new InstallRuby().render(OsFamily.WINDOWS);
   }

   public void installRubyGemsDefaultsUnix() throws IOException {
      assertEquals(InstallRubyGems.builder().build().render(OsFamily.UNIX), installRubyGems(DEFAULT_RUBYGEMS_VERSION));
   }

   public void installRubyGemsForcingVersion() throws IOException {
      assertEquals(InstallRubyGems.builder().version("1.8.25").build().render(OsFamily.UNIX), installRubyGems("1.8.25"));
   }

   public void installRubyGemsAndUpdateSystem() throws IOException {
      assertEquals(InstallRubyGems.builder().updateSystem(true).build().render(OsFamily.UNIX),
            installRubyGems(DEFAULT_RUBYGEMS_VERSION) + updateSystem(null));
   }

   public void installRubyGemsAndUpdateSystemForcingUpdateVersion() throws IOException {
      assertEquals(InstallRubyGems.builder().updateSystem(true, "1.8.25").build().render(OsFamily.UNIX),
            installRubyGems(DEFAULT_RUBYGEMS_VERSION) + updateSystem("1.8.25"));
   }

   public void installRubyGemsAndUpdateGems() throws IOException {
      assertEquals(InstallRubyGems.builder().updateExistingGems(true).build().render(OsFamily.UNIX),
            installRubyGems(DEFAULT_RUBYGEMS_VERSION) + updateGems());
   }

   public void installRubyGemsUpdatingSystemAndGems() throws IOException {
      assertEquals(InstallRubyGems.builder().version("1.2.3").updateSystem(true, "1.2.4").updateExistingGems(true)
            .build().render(OsFamily.UNIX), installRubyGems("1.2.3") + updateSystem("1.2.4") + updateGems());
   }

   public void installRubyGemsDefaultsWithUpgrade() throws IOException {
      assertEquals(InstallRubyGems.builder().updateSystem(true).updateExistingGems(true).build().render(OsFamily.UNIX),
            Resources.toString(Resources.getResource("test_install_rubygems." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8));
   }

   public void installRubyGemsUnixDefaultsInScriptBuilder() throws IOException {
      assertEquals(
            InitScript.builder().name("install_rubygems").run(InstallRubyGems.builder().build()).build()
                  .render(OsFamily.UNIX), Resources.toString(
                  Resources.getResource("test_install_rubygems_scriptbuilder." + ShellToken.SH.to(OsFamily.UNIX)),
                  Charsets.UTF_8));
   }

   private static String installRubyGems(String version) {
      String script = "if ! hash gem 2>/dev/null; then\n"
            + "(\n"
            + "mkdir /tmp/$$\n"
            + "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://production.cf.rubygems.org/rubygems/rubygems-"
            + version + ".tgz |(mkdir -p /tmp/$$ &&cd /tmp/$$ &&tar -xpzf -)\n" + "mkdir -p /tmp/rubygems\n"
            + "mv /tmp/$$/*/* /tmp/rubygems\n" + "rm -rf /tmp/$$\n" + "cd /tmp/rubygems\n"
            + "ruby setup.rb --no-format-executable\n" //
            + "rm -fr /tmp/rubygems\n" + //
            ")\n" + //
            "fi\n";

      return script;
   }

   private static String updateSystem(String version) {
      return version == null ? "gem update --system\n" : "gem update --system " + version + "\n";
   }

   private static String updateGems() {
      return "gem update --no-rdoc --no-ri\n";
   }

}
