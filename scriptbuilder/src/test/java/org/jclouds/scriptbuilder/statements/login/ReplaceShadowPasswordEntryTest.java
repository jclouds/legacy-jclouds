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

import static org.testng.Assert.*;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReplaceShadowPasswordEntryTest {
   Function<String, String> crypt = new Function<String, String>() {
      public String apply(String in) {
         assertEquals(in, "password");
         return "CRYPT";
      }
   };

   public void testWithPasswordUNIX() {
      String userAdd = new ReplaceShadowPasswordEntry(crypt, "foo", "password").render(OsFamily.UNIX);
      assert userAdd.startsWith("awk -v user=^foo: -v password='CRYPT") : userAdd;
      assert userAdd
            .endsWith("' 'BEGIN { FS=OFS=\":\" } $0 ~ user { $2 = password } 1' /etc/shadow >/etc/shadow.foo\ntest -f /etc/shadow.foo && mv /etc/shadow.foo /etc/shadow\n") : userAdd;
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testAddUserWindowsNotSupported() {
      new ReplaceShadowPasswordEntry(crypt, "user", "password").render(OsFamily.WINDOWS);
   }
}
