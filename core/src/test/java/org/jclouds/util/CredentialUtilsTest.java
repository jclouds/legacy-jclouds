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

package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CredentialUtilsTest {

   public void testOverridingCredentialsWhenOverridingIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = null;
      assertEquals(CredentialUtils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials),
            defaultCredentials);
   }

   public void testOverridingCredentialsWhenOverridingLoginIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials(null, "baz");
      assertEquals(CredentialUtils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials),
            new Credentials("foo", "baz"));
   }

   public void testOverridingCredentialsWhenOverridingCredentialIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials("fooble", null);
      assertEquals(CredentialUtils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials),
            new Credentials("fooble", "bar"));
   }

   public void testOverridingCredentialsWhenOverridingCredentialsAreNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials(null, null);
      assertEquals(CredentialUtils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials),
            new Credentials("foo", "bar"));
   }

}
