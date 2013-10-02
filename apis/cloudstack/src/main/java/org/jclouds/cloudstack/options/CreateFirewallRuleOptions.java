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

import java.util.Set;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a firewall rule is created
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createFirewallRule.html"
 *      />
 * @author Andrei Savu
 */
public class CreateFirewallRuleOptions extends BaseHttpRequestOptions {

   public static final CreateFirewallRuleOptions NONE = new CreateFirewallRuleOptions();

   /**
    * @param CIDRs
    *       the list of CIDRs to forward traffic from
    */
   public CreateFirewallRuleOptions CIDRs(Set<String> CIDRs) {
      this.queryParameters.replaceValues("cidrlist", ImmutableSet.of(Joiner.on(",").join(CIDRs)));
      return this;
   }

   /**
    * @param startPort
    *       the starting port of firewall rule
    */
   public CreateFirewallRuleOptions startPort(int startPort) {
      this.queryParameters.replaceValues("startport", ImmutableSet.of(startPort + ""));
      return this;
   }

   /**
    * @param endPort
    *       the ending port of firewall rule
    */
   public CreateFirewallRuleOptions endPort(int endPort) {
      this.queryParameters.replaceValues("endport", ImmutableSet.of(endPort + ""));
      return this;
   }

   /**
    * @param icmpCode
    *       error code for this icmp message
    */
   public CreateFirewallRuleOptions icmpCode(String icmpCode) {
      this.queryParameters.replaceValues("icmpcode", ImmutableSet.of(icmpCode));
      return this;
   }

   /**
    * @param icmpType
    *       type of the icmp message being sent
    */
   public CreateFirewallRuleOptions icmpType(String icmpType) {
      this.queryParameters.replaceValues("icmptype", ImmutableSet.of(icmpType));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateFirewallRuleOptions#CIDRs
       */
      public static CreateFirewallRuleOptions CIDRs(Set<String> CIDRs) {
         CreateFirewallRuleOptions options = new CreateFirewallRuleOptions();
         return options.CIDRs(CIDRs);
      }

      /**
       * @see CreateFirewallRuleOptions#startPort
       */
      public static CreateFirewallRuleOptions startPort(int startPort) {
         CreateFirewallRuleOptions options = new CreateFirewallRuleOptions();
         return options.startPort(startPort);
      }

      /**
       * @see CreateFirewallRuleOptions#endPort
       */
      public static CreateFirewallRuleOptions endPort(int endPort) {
         CreateFirewallRuleOptions options = new CreateFirewallRuleOptions();
         return options.endPort(endPort);
      }

      /**
       * @see CreateFirewallRuleOptions#icmpCode
       */
      public static CreateFirewallRuleOptions icmpCode(String icmpCode) {
         CreateFirewallRuleOptions options = new CreateFirewallRuleOptions();
         return options.icmpCode(icmpCode);
      }

      /**
       * @see CreateFirewallRuleOptions#icmpType
       */
      public static CreateFirewallRuleOptions icmpType(String icmpType) {
         CreateFirewallRuleOptions options = new CreateFirewallRuleOptions();
         return options.icmpType(icmpType);
      }
   }
}
