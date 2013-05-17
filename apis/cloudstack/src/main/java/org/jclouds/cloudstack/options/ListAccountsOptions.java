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

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what account information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/listAccounts.html"
 *      />
 * @author Adrian Cole
 */
public class ListAccountsOptions extends AccountInDomainOptions {

   public static final ListAccountsOptions NONE = new ListAccountsOptions();

   /**
    * @param id
    *           list account by account ID
    */
   public ListAccountsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param name
    *           list account by account name
    */
   public ListAccountsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param state
    *           list accounts by state. Valid states are enabled, disabled, and
    *           locked.
    */
   public ListAccountsOptions state(String state) {
      this.queryParameters.replaceValues("state", ImmutableSet.of(state));
      return this;
   }

   /**
    * @param cleanupRequired
    *           list accounts by cleanuprequred attribute
    */
   public ListAccountsOptions cleanupRequired(boolean cleanupRequired) {
      this.queryParameters.replaceValues("iscleanuprequired", ImmutableSet.of(cleanupRequired + ""));
      return this;
   }

   /**
    * @param recursive
    *           defaults to false, but if true, lists all accounts from the
    *           parent specified by the domain id till leaves.
    */
   public ListAccountsOptions recursive(boolean recursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(recursive + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListAccountsOptions#accountInDomain
       */
      public static ListAccountsOptions accountInDomain(String account, String domain) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListAccountsOptions#domainId
       */
      public static ListAccountsOptions domainId(String id) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.domainId(id);
      }

      /**
       * @see ListAccountsOptions#id
       */
      public static ListAccountsOptions id(String id) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.id(id);
      }

      /**
       * @see ListAccountsOptions#name
       */
      public static ListAccountsOptions name(String name) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.name(name);
      }

      /**
       * @see ListAccountsOptions#state
       */
      public static ListAccountsOptions state(String state) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.state(state);
      }

      /**
       * @see ListAccountsOptions#cleanupRequired
       */
      public static ListAccountsOptions cleanupRequired(boolean cleanupRequired) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.cleanupRequired(cleanupRequired);
      }

      /**
       * @see ListAccountsOptions#recursive
       */
      public static ListAccountsOptions recursive(boolean recursive) {
         ListAccountsOptions options = new ListAccountsOptions();
         return options.recursive(recursive);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListAccountsOptions accountInDomain(String account, String domain) {
      return ListAccountsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListAccountsOptions domainId(String domainId) {
      return ListAccountsOptions.class.cast(super.domainId(domainId));
   }
}
