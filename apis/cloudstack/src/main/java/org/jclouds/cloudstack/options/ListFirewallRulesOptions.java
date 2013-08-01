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
 * Options used to control what firewall rules are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listFirewallRules.html"
 *      />
 * @author Andrei Savu
 */
public class ListFirewallRulesOptions extends AccountInDomainOptions {

   public static final ListFirewallRulesOptions NONE = new ListFirewallRulesOptions();

   /**
    * @param id
    *    firewall rule ID
    */
   public ListFirewallRulesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param ipAddressId
    *    the id of IP address of the firewall services
    */
   public ListFirewallRulesOptions ipAddressId(String ipAddressId) {
      this.queryParameters.replaceValues("ipaddressid", ImmutableSet.of(ipAddressId + ""));
      return this;
   }

   /**
    * @param projectId
    *    List firewall rules in this project.
    */
   public ListFirewallRulesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   /**
    * @param keyword
    *    list by keyword
    */
   public ListFirewallRulesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListFirewallRulesOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   public ListFirewallRulesOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListFirewallRulesOptions#id
       */
      public static ListFirewallRulesOptions id(String id) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.id(id);
      }

      /**
       * @see ListFirewallRulesOptions#ipAddressId
       */
      public static ListFirewallRulesOptions ipAddressId(String ipAddressId) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.ipAddressId(ipAddressId);
      }

      /**
       * @see ListFirewallRulesOptions#projectId(String)
       */
      public static ListFirewallRulesOptions projectId(String projectId) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.projectId(projectId);
      }

      /**
       * @see ListFirewallRulesOptions#keyword
       */
      public static ListFirewallRulesOptions keyword(String keyword) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListFirewallRulesOptions#page
       */
      public static ListFirewallRulesOptions page(long page) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.page(page);
      }

      /**
       * @see ListFirewallRulesOptions#pageSize
       */
      public static ListFirewallRulesOptions pageSize(long pageSize) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.pageSize(pageSize);
      }

      /**
       * @see ListFirewallRulesOptions#accountInDomain
       */
      public static ListFirewallRulesOptions accountInDomain(String account, String domain) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListFirewallRulesOptions#domainId
       */
      public static ListFirewallRulesOptions domainId(String id) {
         ListFirewallRulesOptions options = new ListFirewallRulesOptions();
         return options.domainId(id);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListFirewallRulesOptions accountInDomain(String account, String domain) {
      return ListFirewallRulesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListFirewallRulesOptions domainId(String domainId) {
      return ListFirewallRulesOptions.class.cast(super.domainId(domainId));
   }
}
