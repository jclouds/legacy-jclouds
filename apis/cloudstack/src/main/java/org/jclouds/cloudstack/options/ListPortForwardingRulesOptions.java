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
 * Options used to control what port forwarding rules are returned
 *
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listPortForwardingRules.html"
 *      />
 */
public class ListPortForwardingRulesOptions extends AccountInDomainOptions {

   public static final ListPortForwardingRulesOptions NONE = new ListPortForwardingRulesOptions();

   /**
    * @param id
    *       lists rule with the specified ID
    */
   public ListPortForwardingRulesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param IPAddressId
    *       list the rule belonging to this public ip address
    */
   public ListPortForwardingRulesOptions ipAddressId(String IPAddressId) {
      this.queryParameters.replaceValues("ipaddressid", ImmutableSet.of(IPAddressId + ""));
      return this;

   }

   /**
    * @param projectId
    *       list the rules in this project
    */
   public ListPortForwardingRulesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;

   }
   public static class Builder {

      /**
       * @see ListPortForwardingRulesOptions#id
       */
      public static ListPortForwardingRulesOptions id(String id) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.id(id);
      }

      /**
       * @see ListPortForwardingRulesOptions#ipAddressId
       */
      public static ListPortForwardingRulesOptions ipAddressId(String ipAddressId) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.ipAddressId(ipAddressId);
      }

      /**
       * @see ListPortForwardingRulesOptions#projectId(String)
       */
      public static ListPortForwardingRulesOptions projectId(String projectId) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.projectId(projectId);
      }

      /**
       * @see ListPortForwardingRulesOptions#accountInDomain
       */
      public static ListPortForwardingRulesOptions accountInDomain(String account, String domain) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListPortForwardingRulesOptions#domainId
       */
      public static ListPortForwardingRulesOptions domainId(String id) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.domainId(id);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListPortForwardingRulesOptions accountInDomain(String account, String domain) {
      return ListPortForwardingRulesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListPortForwardingRulesOptions domainId(String domainId) {
      return ListPortForwardingRulesOptions.class.cast(super.domainId(domainId));
   }
}
