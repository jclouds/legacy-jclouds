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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Optional arguments for updating an User
 *
 * @author Andrei Savu
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/updateUser.html"
 *      />
 */
public class UpdateUserOptions extends BaseHttpRequestOptions {

   public static final UpdateUserOptions NONE = new UpdateUserOptions();

   /**
    * @param email user email address
    */
   public UpdateUserOptions email(String email) {
      this.queryParameters.replaceValues("email", ImmutableSet.of(email));
      return this;
   }

   /**
    * @param firstName user account first name
    */
   public UpdateUserOptions firstName(String firstName) {
      this.queryParameters.replaceValues("firstname", ImmutableSet.of(firstName));
      return this;
   }

   /**
    * @param lastName user account last name
    */
   public UpdateUserOptions lastName(String lastName) {
      this.queryParameters.replaceValues("lastname", ImmutableSet.of(lastName));
      return this;
   }

   /**
    * @param hashedPassword hashed password (default is MD5). If you wish to use any other
    *                       hashing algorithm, you would need to write a custom authentication adapter
    */
   public UpdateUserOptions hashedPassword(String hashedPassword) {
      this.queryParameters.replaceValues("password", ImmutableSet.of(hashedPassword));
      return this;
   }

   /**
    * @param timezone specifies a timezone for this command. For more information on
    *                 the timezone parameter, see Time Zone Format.
    */
   public UpdateUserOptions timezone(String timezone) {
      this.queryParameters.replaceValues("timezone", ImmutableSet.of(timezone));
      return this;
   }

   /**
    * @param userApiKey
    */
   public UpdateUserOptions userApiKey(String userApiKey) {
      this.queryParameters.replaceValues("userapikey", ImmutableSet.of(userApiKey));
      return this;
   }

   /**
    * @param userSecretKey
    */
   public UpdateUserOptions userSecretKey(String userSecretKey) {
      this.queryParameters.replaceValues("usersecretkey", ImmutableSet.of(userSecretKey));
      return this;
   }

   /**
    * @param userName unique user name
    */
   public UpdateUserOptions userName(String userName) {
      this.queryParameters.replaceValues("username", ImmutableSet.of(userName));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateUserOptions#email
       */
      public static UpdateUserOptions email(String email) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.email(email);
      }

      /**
       * @see UpdateUserOptions#firstName
       */
      public static UpdateUserOptions firstName(String firstName) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.firstName(firstName);
      }

      /**
       * @see UpdateUserOptions#lastName
       */
      public static UpdateUserOptions lastName(String lastName) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.lastName(lastName);
      }

      /**
       * @see UpdateUserOptions#hashedPassword
       */
      public static UpdateUserOptions hashedPassword(String hashedPassword) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.hashedPassword(hashedPassword);
      }

      /**
       * @see UpdateUserOptions#timezone
       */
      public static UpdateUserOptions timezone(String timezone) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.timezone(timezone);
      }

      /**
       * @see UpdateUserOptions#userApiKey
       */
      public static UpdateUserOptions userApiKey(String userApiKey) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.userApiKey(userApiKey);
      }

      /**
       * @see UpdateUserOptions#userSecretKey
       */
      public static UpdateUserOptions userSecretKey(String userSecretKey) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.userSecretKey(userSecretKey);
      }

      /**
       * @see UpdateUserOptions#userName
       */
      public static UpdateUserOptions userName(String userName) {
         UpdateUserOptions options = new UpdateUserOptions();
         return options.userName(userName);
      }
   }

}
