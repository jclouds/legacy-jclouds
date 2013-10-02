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

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SshStatementsTest {

   public void testLockSshdUNIX() {
      assertEquals(SshStatements.lockSshd().render(OsFamily.UNIX), new StringBuilder().append(
               "exec 3<> /etc/ssh/sshd_config && awk -v TEXT=\"")//
               .append("PasswordAuthentication no").append("\n")//
               .append("PermitRootLogin no").append("\n")//
               .append("\" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3").append("\n")//
               .append("hash service 2>&- && service ssh reload 2>&- || /etc/init.d/ssh* reload").append("\n").toString());
   }

   public void testSshdConfigUNIX() {
      assertEquals(SshStatements.sshdConfig(ImmutableMap.of("AddressFamily", "inet6")).render(OsFamily.UNIX),
               new StringBuilder().append("exec 3<> /etc/ssh/sshd_config && awk -v TEXT=\"")//
                        .append("AddressFamily inet6").append("\n")//
                        .append("\" 'BEGIN {print TEXT}{print}' /etc/ssh/sshd_config >&3").append("\n")//
                        .append("hash service 2>&- && service ssh reload 2>&- || /etc/init.d/ssh* reload").append("\n").toString());
   }

}
