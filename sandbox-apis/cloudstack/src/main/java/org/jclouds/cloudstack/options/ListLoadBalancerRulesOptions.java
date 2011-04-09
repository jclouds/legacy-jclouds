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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what load balancer rules are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/user/listLoadBalancerRules.html" />
 * @author Adrian Cole
 */
public class ListLoadBalancerRulesOptions extends AccountInDomainOptions {

   public static final ListLoadBalancerRulesOptions NONE = new ListLoadBalancerRulesOptions();

   /**
    * @param id
    *           Lists rule with the specified ID.
    */
   public ListLoadBalancerRulesOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param name
    *           the name of the load balancer rule
    */
   public ListLoadBalancerRulesOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param publicIPId
    *           the public IP address id of the load balancer rule
    */
   public ListLoadBalancerRulesOptions publicIPId(long publicIPId) {
      this.queryParameters.replaceValues("publicipid", ImmutableSet.of(publicIPId + ""));
      return this;
   }

   /**
    * @param virtualMachineId
    *           the ID of the virtual machine of the load balancer rule
    */
   public ListLoadBalancerRulesOptions virtualMachineId(long virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId + ""));
      return this;

   }

   public static class Builder {

      /**
       * @see ListLoadBalancerRulesOptions#accountInDomain
       */
      public static ListLoadBalancerRulesOptions accountInDomain(String account, long domain) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListLoadBalancerRulesOptions#publicIPId
       */
      public static ListLoadBalancerRulesOptions publicIPId(long publicIPId) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.publicIPId(publicIPId);
      }

      /**
       * @see ListLoadBalancerRulesOptions#domainId
       */
      public static ListLoadBalancerRulesOptions domainId(long id) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListLoadBalancerRulesOptions#name
       */
      public static ListLoadBalancerRulesOptions name(String name) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.name(name);
      }

      /**
       * @see ListLoadBalancerRulesOptions#id
       */
      public static ListLoadBalancerRulesOptions id(long id) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.id(id);
      }

      /**
       * @see ListLoadBalancerRulesOptions#virtualMachineId
       */
      public static ListLoadBalancerRulesOptions virtualMachineId(long virtualMachineId) {
         ListLoadBalancerRulesOptions options = new ListLoadBalancerRulesOptions();
         return options.virtualMachineId(virtualMachineId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListLoadBalancerRulesOptions accountInDomain(String account, long domain) {
      return ListLoadBalancerRulesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListLoadBalancerRulesOptions domainId(long domainId) {
      return ListLoadBalancerRulesOptions.class.cast(super.domainId(domainId));
   }
}
