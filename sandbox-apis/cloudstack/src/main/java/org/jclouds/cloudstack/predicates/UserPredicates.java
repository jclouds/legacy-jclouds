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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;

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

   public static enum IsUserAccount implements Predicate<User> {
      INSTANCE;

      @Override
      public boolean apply(User input) {
         return checkNotNull(input, "user").getAccountType() == Account.Type.USER;
      }

      @Override
      public String toString() {
         return "isUserAccount()";
      }
   }

   /**
    * 
    * @return true, if the user's apiKey is the following
    */
   public static Predicate<User> isUserAccount() {
      return IsUserAccount.INSTANCE;
   }

}
