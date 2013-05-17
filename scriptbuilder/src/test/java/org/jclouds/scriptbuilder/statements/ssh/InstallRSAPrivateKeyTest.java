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

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InstallRSAPrivateKeyTest {

   public void testInstallRSAPrivateKeyUNIXCurrentUser() {
      assertEquals(
               new InstallRSAPrivateKey("-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----\n")
                        .render(OsFamily.UNIX),
                        "mkdir -p ~/.ssh\n"+
                        "rm ~/.ssh/id_rsa\n"+
                        "cat >> ~/.ssh/id_rsa <<-'END_OF_JCLOUDS_FILE'\n"+
                        "\t-----BEGIN RSA PRIVATE KEY-----\n"+
                        "\t-----END RSA PRIVATE KEY-----\n"+
                        "\t\n"+
                        "END_OF_JCLOUDS_FILE\n"+
                        "chmod 600 ~/.ssh/id_rsa\n");                        
   }

   public void testInstallRSAPrivateKeyUNIXSpecifiedHome() {
      assertEquals(
               new InstallRSAPrivateKey("/home/me/.ssh", "-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----\n")
                        .render(OsFamily.UNIX),
                        "mkdir -p /home/me/.ssh\n"+
                        "rm /home/me/.ssh/id_rsa\n"+
                        "cat >> /home/me/.ssh/id_rsa <<-'END_OF_JCLOUDS_FILE'\n"+
                        "\t-----BEGIN RSA PRIVATE KEY-----\n"+
                        "\t-----END RSA PRIVATE KEY-----\n"+
                        "\t\n"+
                        "END_OF_JCLOUDS_FILE\n"+
                        "chmod 600 /home/me/.ssh/id_rsa\n");    }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testInstallRSAPrivateKeyWINDOWS() {
      new InstallRSAPrivateKey("-----BEGIN RSA PRIVATE KEY-----\n-----END RSA PRIVATE KEY-----\n")
               .render(OsFamily.WINDOWS);
   }
}
