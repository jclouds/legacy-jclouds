/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.scriptbuilder.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "scriptbuilder.InstallRSAPrivateKeyTest")
public class InstallRSAPrivateKeyTest {

   InstallRSAPrivateKey key = new InstallRSAPrivateKey("-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----\n");

   public void testInstallRSAPrivateKeyUNIX() {
      assertEquals(
               key.render(OsFamily.UNIX),
               "mkdir -p .ssh\nrm .ssh/id_rsa\ncat >> .ssh/id_rsa <<'END_OF_FILE'\n-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----\n\nEND_OF_FILE\nchmod 600 .ssh/id_rsa\n");
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testInstallRSAPrivateKeyWINDOWS() {
      key.render(OsFamily.WINDOWS);
   }
}
