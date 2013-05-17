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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.Account.Type;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class UserPredicates {

   public static class ApiKeyEquals implements Predicate<User> {
      private final String apiKey;

      public ApiKeyEquals(String apiKey) {
         this.apiKey = checkNotNull(apiKey, "apiKey");
      }

      @Override
      public boolean apply(User input) {
         return apiKey.equals(checkNotNull(input, "user").getApiKey());
      }

      @Override
      public String toString() {
         return "apiKeyEquals(" + apiKey + ")";
      }
   }

   /**
    * 
    * @return true, if the user's apiKey is the following
    */
   public static Predicate<User> apiKeyEquals(String apiKey) {
      return new ApiKeyEquals(apiKey);
   }

   /**
    * 
    * @return true, if the user's account type is the following
    */
   public static Predicate<User> accountTypeEquals(Account.Type type) {
      return new AccountTypeEquals(type);
   }

   public static class AccountTypeEquals implements Predicate<User> {
      public AccountTypeEquals(Type type) {
         this.type = checkNotNull(type, "type");
      }

      private final Account.Type type;

      @Override
      public boolean apply(User input) {
         return checkNotNull(input, "user").getAccountType() == type;
      }

      @Override
      public String toString() {
         return "accountTypeEquals(" + type + ")";
      }
   }

   /**
    *
    * @return true, if the account has user privileges
    */
   public static Predicate<User> isUserAccount() {
      return accountTypeEquals(Account.Type.USER);
   }

   /**
    * @return true, is the user is a domain admin
    */
   public static Predicate<User> isDomainAdminAccount() {
      return accountTypeEquals(Type.DOMAIN_ADMIN);
   }

   /**
    * 
    * @return true, if the user is a global admin
    */
   public static Predicate<User> isAdminAccount() {
      return accountTypeEquals(Account.Type.ADMIN);
   }
}
