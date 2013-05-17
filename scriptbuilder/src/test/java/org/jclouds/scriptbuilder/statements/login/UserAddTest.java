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
package org.jclouds.scriptbuilder.statements.login;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class UserAddTest {

   public void testUNIX() {
      assertEquals(UserAdd.builder().login("me").build().render(OsFamily.UNIX),
               "mkdir -p /home/users\nuseradd -c me -s /bin/bash -m  -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   public void testWithFullNameUNIX() {
      assertEquals(UserAdd.builder().login("me").fullName("JClouds Guy").build().render(OsFamily.UNIX),
            "mkdir -p /home/users\nuseradd -c 'JClouds Guy' -s /bin/bash -m  -d /home/users/me me\nchown -R me /home/users/me\n");

   }

   public void testWithBaseUNIX() {
      assertEquals(UserAdd.builder().login("me").defaultHome("/export/home").build().render(OsFamily.UNIX),
               "mkdir -p /export/home\nuseradd -c me -s /bin/bash -m  -d /export/home/me me\nchown -R me /export/home/me\n");
   }

   public void testWithGroupUNIX() {
      assertEquals(UserAdd.builder().login("me").group("wheel").build().render(OsFamily.UNIX),
               "mkdir -p /home/users\ngroupadd -f wheel\nuseradd -c me -s /bin/bash -g wheel -m  -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   public void testWithGroupsUNIX() {
      assertEquals(UserAdd.builder().login("me").groups(ImmutableList.of("wheel", "candy")).build().render(
               OsFamily.UNIX),
               "mkdir -p /home/users\ngroupadd -f wheel\ngroupadd -f candy\nuseradd -c me -s /bin/bash -g wheel -G candy -m  -d /home/users/me me\nchown -R me /home/users/me\n");
   }

   Function<String, String> crypt = new Function<String, String>() {
      public String apply(String in) {
         assertEquals(in, "password");
         return "CRYPT";
      }
   };

   public void testWithPasswordUNIX() {
      String userAdd = UserAdd.builder().cryptFunction(crypt).login("me").password("password").group("wheel").build().render(OsFamily.UNIX);
      assert userAdd.startsWith("mkdir -p /home/users\ngroupadd -f wheel\nuseradd -c me -s /bin/bash -g wheel -m  -d /home/users/me -p 'CRYPT'") : userAdd;
      assert userAdd.endsWith("' me\nchown -R me /home/users/me\n") : userAdd;
   }

   public void testWithSshAuthorizedKeyUNIX() {
      assertEquals(
               UserAdd.builder().login("me").authorizeRSAPublicKey("rsapublickey").build().render(OsFamily.UNIX),
               "mkdir -p /home/users\nuseradd -c me -s /bin/bash -m  -d /home/users/me me\nmkdir -p /home/users/me/.ssh\ncat >> /home/users/me/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'\n\trsapublickey\nEND_OF_JCLOUDS_FILE\nchmod 600 /home/users/me/.ssh/authorized_keys\nchown -R me /home/users/me\n");
   }

   public void testWithSshInstalledKeyUNIX() {
      assertEquals(
               UserAdd.builder().login("me").installRSAPrivateKey("rsaprivate").build().render(OsFamily.UNIX),
               "mkdir -p /home/users\nuseradd -c me -s /bin/bash -m  -d /home/users/me me\nmkdir -p /home/users/me/.ssh\nrm /home/users/me/.ssh/id_rsa\ncat >> /home/users/me/.ssh/id_rsa <<-'END_OF_JCLOUDS_FILE'\n\trsaprivate\nEND_OF_JCLOUDS_FILE\nchmod 600 /home/users/me/.ssh/id_rsa\nchown -R me /home/users/me\n");
   }

   public void testWithHomeUNIX() {
      assertEquals(UserAdd.builder().login("me").home("/myhome/myme").build().render(
               OsFamily.UNIX),
               "mkdir -p /myhome\nuseradd -c me -s /bin/bash -m  -d /myhome/myme me\nchown -R me /myhome/myme\n");
      
      assertEquals(UserAdd.builder().login("me").home("/myhome/myme").defaultHome("/ignoreddefault").build().render(
                              OsFamily.UNIX),
                              "mkdir -p /myhome\nuseradd -c me -s /bin/bash -m  -d /myhome/myme me\nchown -R me /myhome/myme\n");
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testAddUserWindowsNotSupported() {
      UserAdd.builder().login("me").build().render(OsFamily.WINDOWS);
   }
}
