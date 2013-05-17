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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.UserPredicates.apiKeyEquals;
import static org.jclouds.cloudstack.predicates.UserPredicates.isAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isDomainAdminAccount;
import static org.jclouds.cloudstack.predicates.UserPredicates.isUserAccount;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class UserPredicatesTest {

   @Test
   public void testMatchApiKey() {
      assertTrue(apiKeyEquals("random-text").apply(
         User.builder().id("random-id").apiKey("random-text").build()
      ));
      assertFalse(apiKeyEquals("something-different").apply(
         User.builder().id("random-id").apiKey("random-text").build()
      ));
   }

   @DataProvider(name = "accountType")
   public Object[][] getAccountTypes() {
      return new Object[][] {
         /* Type ID, isUser, isDomainAdmin, isAdmin */
         {Account.Type.USER, true, false, false},
         {Account.Type.DOMAIN_ADMIN, false, true, false},
         {Account.Type.ADMIN, false, false, true},
         {Account.Type.UNRECOGNIZED, false, false, false}
      };
   }

   @Test(dataProvider = "accountType")
   public void testAccountType(Account.Type type, boolean isUser, boolean isDomainAdmin, boolean isAdmin) {
      User testUser = User.builder().id("someid").accountType(type).build();
      assertEquals(isUserAccount().apply(testUser), isUser);
      assertEquals(isDomainAdminAccount().apply(testUser), isDomainAdmin);
      assertEquals(isAdminAccount().apply(testUser), isAdmin);
   }

}
