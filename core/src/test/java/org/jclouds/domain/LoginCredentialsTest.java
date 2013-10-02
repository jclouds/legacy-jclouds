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
package org.jclouds.domain;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LoginCredentialsTest")
public class LoginCredentialsTest {

   public void testPasswordSetsPrivateKeyToAbsentWhenUnset() {
      LoginCredentials toTest = LoginCredentials.builder().user("user").password("password").build();
      assertEquals(toTest.getOptionalPassword(), Optional.of("password"));
      assertEquals(toTest.getOptionalPrivateKey(), Optional.absent());
   }

   public void testPasswordLeavesPrivateKeyAloneWhenSet() {
      LoginCredentials toTest = LoginCredentials.builder().user("user").privateKey("key").password("password").build();
      assertEquals(toTest.getOptionalPassword(), Optional.of("password"));
      assertEquals(toTest.getOptionalPrivateKey(), Optional.of("key"));
   }

   public void testPrivateKeySetsPasswordToAbsentWhenUnset() {
      LoginCredentials toTest = LoginCredentials.builder().user("user").privateKey("key").build();
      assertEquals(toTest.getOptionalPassword(), Optional.absent());
      assertEquals(toTest.getOptionalPrivateKey(), Optional.of("key"));
   }

   public void testPrivateKeyLeavesPasswordAloneWhenSet() {
      LoginCredentials toTest = LoginCredentials.builder().user("user").password("password").privateKey("key").build();
      assertEquals(toTest.getOptionalPassword(), Optional.of("password"));
      assertEquals(toTest.getOptionalPrivateKey(), Optional.of("key"));
   }
}
