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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.email;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.firstName;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.lastName;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.timezone;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.userApiKey;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.userName;
import static org.jclouds.cloudstack.options.UpdateUserOptions.Builder.userSecretKey;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code UpdateUserOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class UpdateUserOptionsTest {

   public void testEmail() {
      UpdateUserOptions options = new UpdateUserOptions().email("andrei@example.com");
      assertEquals(ImmutableSet.of("andrei@example.com"), options.buildQueryParameters().get("email"));
   }

   public void testEmailStatic() {
      UpdateUserOptions options = email("andrei@example.com");
      assertEquals(ImmutableSet.of("andrei@example.com"), options.buildQueryParameters().get("email"));
   }

   public void testFirstName() {
      UpdateUserOptions options = new UpdateUserOptions().firstName("First");
      assertEquals(ImmutableSet.of("First"), options.buildQueryParameters().get("firstname"));
   }

   public void testFirstStatic() {
      UpdateUserOptions options = firstName("First");
      assertEquals(ImmutableSet.of("First"), options.buildQueryParameters().get("firstname"));
   }

   public void testLastName() {
      UpdateUserOptions options = new UpdateUserOptions().lastName("Last");
      assertEquals(ImmutableSet.of("Last"), options.buildQueryParameters().get("lastname"));
   }

   public void testLastNameStatic() {
      UpdateUserOptions options = lastName("Last");
      assertEquals(ImmutableSet.of("Last"), options.buildQueryParameters().get("lastname"));
   }

   public void testTimezone() {
      UpdateUserOptions options = new UpdateUserOptions().timezone("timez");
      assertEquals(ImmutableSet.of("timez"), options.buildQueryParameters().get("timezone"));
   }

   public void testTimezoneStatic() {
      UpdateUserOptions options = timezone("timez");
      assertEquals(ImmutableSet.of("timez"), options.buildQueryParameters().get("timezone"));
   }

   public void testUserApiKey() {
      UpdateUserOptions options = new UpdateUserOptions().userApiKey("sdfoasdjfo");
      assertEquals(ImmutableSet.of("sdfoasdjfo"), options.buildQueryParameters().get("userapikey"));
   }

   public void testUserApiKeyStatic() {
      UpdateUserOptions options = userApiKey("sdfoasdjfo");
      assertEquals(ImmutableSet.of("sdfoasdjfo"), options.buildQueryParameters().get("userapikey"));
   }

   public void testUserSecretKey() {
      UpdateUserOptions options = new UpdateUserOptions().userSecretKey("sdfoasdjfo");
      assertEquals(ImmutableSet.of("sdfoasdjfo"), options.buildQueryParameters().get("usersecretkey"));
   }

   public void testUserSecretKeyStatic() {
      UpdateUserOptions options = userSecretKey("sdfoasdjfo");
      assertEquals(ImmutableSet.of("sdfoasdjfo"), options.buildQueryParameters().get("usersecretkey"));
   }
   
   public void testUserName() {
      UpdateUserOptions options = new UpdateUserOptions().userName("andrei");
      assertEquals(ImmutableSet.of("andrei"), options.buildQueryParameters().get("username"));
   }

   public void testUserNameStatic() {
      UpdateUserOptions options = userName("andrei");
      assertEquals(ImmutableSet.of("andrei"), options.buildQueryParameters().get("username"));
   }
}
