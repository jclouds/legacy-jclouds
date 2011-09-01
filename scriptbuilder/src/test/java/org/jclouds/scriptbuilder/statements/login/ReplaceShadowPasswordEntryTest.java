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
package org.jclouds.scriptbuilder.statements.login;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReplaceShadowPasswordEntryTest {

   public void testWithPasswordUNIX() {
      String userAdd = new ReplaceShadowPasswordEntry("foo", "bar").render(OsFamily.UNIX);
      assert userAdd.startsWith("awk -v user=^foo: -v password='$6$") : userAdd;
      assert userAdd
            .endsWith("' 'BEGIN { FS=OFS=\":\" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.foo\ntest -f /etc/shadow.foo && mv /etc/shadow.foo /etc/shadow\n") : userAdd;
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testAddUserWindowsNotSupported() {
      new ReplaceShadowPasswordEntry("user", "password").render(OsFamily.WINDOWS);
   }
}
