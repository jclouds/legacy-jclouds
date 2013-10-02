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
 * Options used to control what ip forwarding rules are returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api/user/listIpForwardingRules.html"
 *      />
 * @author Adrian Cole
 */
public class ListIPForwardingRulesOptions extends AccountInDomainOptions {

   public static final ListIPForwardingRulesOptions NONE = new ListIPForwardingRulesOptions();

   /**
    * @param id
    *           Lists rule with the specified ID.
    */
   public ListIPForwardingRulesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param IPAddressId
    *           list the rule belonging to this public ip address
    */
   public ListIPForwardingRulesOptions IPAddressId(String IPAddressId) {
      this.queryParameters.replaceValues("ipaddressid", ImmutableSet.of(IPAddressId + ""));
      return this;

   }

   /**
    * @param virtualMachineId
    *           Lists all rules applied to the specified Vm.
    */
   public ListIPForwardingRulesOptions virtualMachineId(String virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId + ""));
      return this;

   }

   /**
    * @param projectId
    *           Lists all rules applied to VMs in the given project.
    */
   public ListIPForwardingRulesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;

   }

   public static class Builder {

      /**
       * @see ListIPForwardingRulesOptions#accountInDomain
       */
      public static ListIPForwardingRulesOptions accountInDomain(String account, String domain) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListIPForwardingRulesOptions#IPAddressId
       */
      public static ListIPForwardingRulesOptions IPAddressId(String IPAddressId) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.IPAddressId(IPAddressId);
      }

      /**
       * @see ListIPForwardingRulesOptions#domainId
       */
      public static ListIPForwardingRulesOptions domainId(String id) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListIPForwardingRulesOptions#id
       */
      public static ListIPForwardingRulesOptions id(String id) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.id(id);
      }

      /**
       * @see ListIPForwardingRulesOptions#virtualMachineId
       */
      public static ListIPForwardingRulesOptions virtualMachineId(String virtualMachineId) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.virtualMachineId(virtualMachineId);
      }

      /**
       * @see ListIPForwardingRulesOptions#projectId(String)
       */
      public static ListIPForwardingRulesOptions projectId(String projectId) {
         ListIPForwardingRulesOptions options = new ListIPForwardingRulesOptions();
         return options.projectId(projectId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListIPForwardingRulesOptions accountInDomain(String account, String domain) {
      return ListIPForwardingRulesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListIPForwardingRulesOptions domainId(String domainId) {
      return ListIPForwardingRulesOptions.class.cast(super.domainId(domainId));
   }
}
