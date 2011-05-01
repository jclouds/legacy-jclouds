/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.scriptbuilder.statements.login;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SudoStatementsTest {

   public void testCreateWheelUNIX() {
      assertEquals(
               SudoStatements.createWheel().render(OsFamily.UNIX),
               "rm /etc/sudoers\ncat >> /etc/sudoers <<'END_OF_FILE'\nroot ALL = (ALL) ALL\n%wheel ALL = (ALL) NOPASSWD:ALL\nEND_OF_FILE\nchmod 0440 /etc/sudoers\n");
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCreateWheelWindowsNotSupported() {
      SudoStatements.createWheel().render(OsFamily.WINDOWS);
   }
}
