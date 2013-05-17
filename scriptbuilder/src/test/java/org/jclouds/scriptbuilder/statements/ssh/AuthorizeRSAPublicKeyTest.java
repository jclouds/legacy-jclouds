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
package org.jclouds.scriptbuilder.statements.ssh;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AuthorizeRSAPublicKeyTest {

   public void testAuthorizeRSAPublicKeyUNIXCurrentUser() {
      assertEquals(
               new AuthorizeRSAPublicKeys(ImmutableSet.of("ssh-dss AAAAB")).render(OsFamily.UNIX),
               "mkdir -p ~/.ssh\n"+
               "cat >> ~/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'\n"+
               "\tssh-dss AAAAB\n"+
               "END_OF_JCLOUDS_FILE\n"+
               "chmod 600 ~/.ssh/authorized_keys\n");
   }

   public void testAuthorizeRSAPublicKeyUNIXCurrentUserWith2Keys() {
      assertEquals(
               new AuthorizeRSAPublicKeys(ImmutableSet.of("ssh-dss AAAAB", "ssh-dss CCCCD")).render(OsFamily.UNIX),
               "mkdir -p ~/.ssh\n"+
               "cat >> ~/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'\n"+
               "\tssh-dss AAAAB\n"+
               "\t\n"+
               "\tssh-dss CCCCD\n"+
               "END_OF_JCLOUDS_FILE\n"+
               "chmod 600 ~/.ssh/authorized_keys\n");
   }

   public void testAuthorizeRSAPublicKeyUNIXSpecifiedDir() {
      assertEquals(
               new AuthorizeRSAPublicKeys("/home/me/.ssh", ImmutableSet.of("ssh-dss AAAAB")).render(OsFamily.UNIX),
               "mkdir -p /home/me/.ssh\n"+
               "cat >> /home/me/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'\n"+
               "\tssh-dss AAAAB\n"+
               "END_OF_JCLOUDS_FILE\n"+
               "chmod 600 /home/me/.ssh/authorized_keys\n");
   }

   public void testAuthorizeRSAPublicKeyUNIXSpecifiedDirWith2Keys() {
      assertEquals(
               new AuthorizeRSAPublicKeys("/home/me/.ssh", ImmutableSet.of("ssh-dss AAAAB", "ssh-dss CCCCD"))
                        .render(OsFamily.UNIX),
                        "mkdir -p /home/me/.ssh\n"+
                              "cat >> /home/me/.ssh/authorized_keys <<-'END_OF_JCLOUDS_FILE'\n"+
                              "\tssh-dss AAAAB\n"+
                              "\t\n"+
                              "\tssh-dss CCCCD\n"+
                              "END_OF_JCLOUDS_FILE\n"+
                              "chmod 600 /home/me/.ssh/authorized_keys\n");
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testAuthorizeRSAPublicKeyWINDOWS() {
      new AuthorizeRSAPublicKeys(ImmutableSet.of("ssh-dss AAAAB")).render(OsFamily.WINDOWS);
   }
}
