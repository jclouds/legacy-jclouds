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
package org.jclouds.virtualbox.domain;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

@Test(groups = "unit")
public class GuestAdditionsInstallationTest {
  @Test
  public void testUnix() throws IOException {
	  InstallGuestAdditions statement = new InstallGuestAdditions("4.1.6");
	  assertEquals(statement.render(OsFamily.UNIX), CharStreams.toString(Resources.newReaderSupplier(Resources
           .getResource("test_guest_additions_installer." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
  }
}