/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what port forwarding rules are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/user/listIpForwardingRules.html" />
 * @author Adrian Cole
 */
public class ListPortForwardingRulesOptions extends BaseHttpRequestOptions {

   public static final ListPortForwardingRulesOptions NONE = new ListPortForwardingRulesOptions();

   /**
    * @param domainId
    *           Lists all rules for this id. If used with the account parameter, returns all rules
    *           for an account in the specified domain ID.
    */
   public ListPortForwardingRulesOptions domainId(long domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   /**
    * @param account
    *           the account associated with the port forwarding rule. Must be used with the domainId
    *           parameter.
    * 
    * @param domain
    *           domain id
    */
   public ListPortForwardingRulesOptions accountInDomain(String account, long domain) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domain + ""));
      return this;
   }

   /**
    * @param IPAddressId
    *           list the rule belonging to this public ip address
    */
   public ListPortForwardingRulesOptions IPAddressId(long IPAddressId) {
      this.queryParameters.replaceValues("ipaddressid", ImmutableSet.of(IPAddressId + ""));
      return this;

   }

   public static class Builder {
      /**
       * @see ListPortForwardingRulesOptions#accountInDomain
       */
      public static ListPortForwardingRulesOptions accountInDomain(String account, long domain) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListPortForwardingRulesOptions#IPAddressId
       */
      public static ListPortForwardingRulesOptions IPAddressId(long IPAddressId) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.IPAddressId(IPAddressId);
      }

      /**
       * @see ListPortForwardingRulesOptions#domainId
       */
      public static ListPortForwardingRulesOptions domainId(long id) {
         ListPortForwardingRulesOptions options = new ListPortForwardingRulesOptions();
         return options.domainId(id);
      }

   }
}
