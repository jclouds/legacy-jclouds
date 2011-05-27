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

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class AdminAccessTest {

   public void testStandardUNIX() throws IOException {
      TestConfiguration.INSTANCE.reset();
      try {
         assertEquals(AdminAccess.standard().apply(TestConfiguration.INSTANCE).render(OsFamily.UNIX),
               CharStreams.toString(Resources.newReaderSupplier(Resources.getResource("test_adminaccess_standard.sh"),
                     Charsets.UTF_8)));
      } finally {
         TestConfiguration.INSTANCE.reset();
      }
   }

   public void testWithParamsUNIX() throws IOException {
      TestConfiguration.INSTANCE.reset();
      try {
         assertEquals(
               AdminAccess.builder().adminPassword("bar").adminPrivateKey("fooPrivateKey")
                     .adminPublicKey("fooPublicKey").adminUsername("foo").build().apply(TestConfiguration.INSTANCE)
                     .render(OsFamily.UNIX), CharStreams.toString(Resources.newReaderSupplier(
                     Resources.getResource("test_adminaccess_params.sh"), Charsets.UTF_8)));
      } finally {
         TestConfiguration.INSTANCE.reset();
      }
   }

   public void testOnlyInstallUserUNIX() throws IOException {
      TestConfiguration.INSTANCE.reset();
      try {
         assertEquals(
               AdminAccess.builder().grantSudoToAdminUser(false).authorizeAdminPublicKey(true)
                     .installAdminPrivateKey(true).lockSsh(false).resetLoginPassword(false).build()
                     .apply(TestConfiguration.INSTANCE).render(OsFamily.UNIX), CharStreams.toString(Resources
                     .newReaderSupplier(Resources.getResource("test_adminaccess_plainuser.sh"), Charsets.UTF_8)));
      } finally {
         TestConfiguration.INSTANCE.reset();
      }
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testCreateWheelWindowsNotSupported() {
      AdminAccess.standard().apply(TestConfiguration.INSTANCE).render(OsFamily.WINDOWS);
   }
}
