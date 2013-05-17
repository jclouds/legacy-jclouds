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

import org.jclouds.cloudstack.domain.User;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what user information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/domain_admin/listUsers.html"
 *      />
 * @author Andrei Savu
 */
public class ListUsersOptions extends AccountInDomainOptions {

   public static final ListUsersOptions NONE = new ListUsersOptions();

   /**
    * @param id
    *           list account by account ID
    */
   public ListUsersOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param userName
    *           retrieve user by name
    */
   public ListUsersOptions userName(String userName) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(userName));
      return this;
   }

   /**
    * @param state
    *           list accounts by state. Valid states are enabled, disabled, and
    *           locked.
    */
   public ListUsersOptions state(User.State state) {
      this.queryParameters.replaceValues("state", ImmutableSet.of(state.toString()));
      return this;
   }

   /**
    * @param accountType
    *             List users by account type. Valid types include admin,
    *             domain-admin, read-only-admin, or user.
    */
   public ListUsersOptions accountType(String accountType) {
      this.queryParameters.replaceValues("accounttype", ImmutableSet.of(accountType));
      return this;
   }

   /**
    * @param keyword
    */
   public ListUsersOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param page
    *          the page number
    */
   public ListUsersOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   /**
    * @param pageSize
    *          the number of items per page
    */
   public ListUsersOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }


   public static class Builder {
      /**
       * @see ListUsersOptions#id
       */
      public static ListUsersOptions id(String id) {
         ListUsersOptions options = new ListUsersOptions();
         return options.id(id);
      }

      /**
       * @see ListUsersOptions#userName(String)
       */
      public static ListUsersOptions userName(String name) {
         ListUsersOptions options = new ListUsersOptions();
         return options.userName(name);
      }

      /**
       * @see ListUsersOptions#state
       */
      public static ListUsersOptions state(User.State state) {
         ListUsersOptions options = new ListUsersOptions();
         return options.state(state);
      }

      /**
       * @see ListUsersOptions#accountType
       */
      public static ListUsersOptions accountType(String accountType) {
         ListUsersOptions options = new ListUsersOptions();
         return options.accountType(accountType);
      }

      /**
       * @see ListUsersOptions#keyword
       */
      public static ListUsersOptions keyword(String keyword) {
         ListUsersOptions options = new ListUsersOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListUsersOptions#page
       */
      public static ListUsersOptions page(long page) {
         ListUsersOptions options = new ListUsersOptions();
         return options.page(page);
      }

      /**
       * @see ListUsersOptions#pageSize
       */
      public static ListUsersOptions pageSize(long pageSize) {
         ListUsersOptions options = new ListUsersOptions();
         return options.pageSize(pageSize);
      }

      /**
       * @see ListUsersOptions#accountInDomain
       */
      public static ListUsersOptions accountInDomain(String account, String domain) {
         ListUsersOptions options = new ListUsersOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListUsersOptions#domainId
       */
      public static ListUsersOptions domainId(String id) {
         ListUsersOptions options = new ListUsersOptions();
         return options.domainId(id);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListUsersOptions accountInDomain(String account, String domain) {
      return ListUsersOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListUsersOptions domainId(String domainId) {
      return ListUsersOptions.class.cast(super.domainId(domainId));
   }
}
