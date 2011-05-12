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

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class UserAddTest {

   public void testUNIX() {
      assertEquals(UserAdd.builder().login("me").build().render(OsFamily.UNIX),
               "mkdir -p /home/users/me\nuseradd -s /bin/bash -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   public void testWithBaseUNIX() {
      assertEquals(UserAdd.builder().login("me").defaultHome("/export/home").build().render(OsFamily.UNIX),
               "mkdir -p /export/home/me\nuseradd -s /bin/bash -d /export/home/me me\nchown -R me /export/home/me\n");
   }

   public void testWithGroupUNIX() {
      assertEquals(UserAdd.builder().login("me").group("wheel").build().render(OsFamily.UNIX),
               "mkdir -p /home/users/me\ngroupadd -f wheel\nuseradd -s /bin/bash -g wheel -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   public void testWithGroupsUNIX() {
      assertEquals(UserAdd.builder().login("me").groups(ImmutableList.of("wheel", "candy")).build().render(
               OsFamily.UNIX),
               "mkdir -p /home/users/me\ngroupadd -f wheel\ngroupadd -f candy\nuseradd -s /bin/bash -g wheel -G candy -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   public void testWithPasswordUNIX() {
      String userAdd = UserAdd.builder().login("me").password("foo").group("wheel").build().render(OsFamily.UNIX);
      assert userAdd.startsWith("mkdir -p /home/users/me\ngroupadd -f wheel\nuseradd -s /bin/bash -g wheel -d /home/users/me -p '$6$") : userAdd;
      assert userAdd.endsWith("' me\nchown -R me /home/users/me\n") : userAdd;
   }

   public void testWithSshAuthorizedKeyUNIX() {
      assertEquals(
               UserAdd.builder().login("me").authorizeRSAPublicKey("rsapublickey").build().render(OsFamily.UNIX),
               "mkdir -p /home/users/me\nuseradd -s /bin/bash -d /home/users/me me\nmkdir -p /home/users/me/.ssh\ncat >> /home/users/me/.ssh/authorized_keys <<'END_OF_FILE'\nrsapublickey\nEND_OF_FILE\nchmod 600 /home/users/me/.ssh/authorized_keys\nchown -R me /home/users/me\n");
   }

   public void testWithSshInstalledKeyUNIX() {
      assertEquals(
               UserAdd.builder().login("me").installRSAPrivateKey("rsaprivate").build().render(OsFamily.UNIX),
               "mkdir -p /home/users/me\nuseradd -s /bin/bash -d /home/users/me me\nmkdir -p /home/users/me/.ssh\nrm /home/users/me/.ssh/id_rsa\ncat >> /home/users/me/.ssh/id_rsa <<'END_OF_FILE'\nrsaprivate\nEND_OF_FILE\nchmod 600 /home/users/me/.ssh/id_rsa\nchown -R me /home/users/me\n");
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testAddUserWindowsNotSupported() {
      UserAdd.builder().login("me").build().render(OsFamily.WINDOWS);
   }
}
