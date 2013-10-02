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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what load balancer rules are returned
 *
 * @author Adrian Cole, Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/user/createLoadBalancerRule.html"
 *      />
 */
public class CreateLoadBalancerRuleOptions extends AccountInDomainOptions {

   public static final CreateLoadBalancerRuleOptions NONE = new CreateLoadBalancerRuleOptions();

   /**
    * @param allowedSourceCIRDs the cidr list to forward traffic from
    */
   public CreateLoadBalancerRuleOptions allowedSourceCIDRs(Set<String> allowedSourceCIRDs) {
      this.queryParameters.replaceValues("cidrlist",
         ImmutableSet.of(Joiner.on(",").join(allowedSourceCIRDs)));
      return this;
   }

   /**
    * @param description the description of the load balancer rule
    */
   public CreateLoadBalancerRuleOptions description(String description) {
      this.queryParameters.replaceValues("description", ImmutableSet.of(description));
      return this;
   }

   /**
    * @param openFirewall if true, firewall rule for source/end pubic port is automatically
    *    created; if false - firewall rule has to be created explicitly. Has value true by default
    */
   public CreateLoadBalancerRuleOptions openFirewall(boolean openFirewall) {
      this.queryParameters.replaceValues("openfirewall", ImmutableSet.of(openFirewall + ""));
      return this;
   }

   /**
    * @param zoneId the availability zone ID
    */
   public CreateLoadBalancerRuleOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateLoadBalancerRuleOptions#allowedSourceCIDRs
       */
      public static CreateLoadBalancerRuleOptions allowedSourceCIDRs(Set<String> allowedSourceCIDRs) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.allowedSourceCIDRs(allowedSourceCIDRs);
      }

      /**
       * @see CreateLoadBalancerRuleOptions#description
       */
      public static CreateLoadBalancerRuleOptions description(String description) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.description(description);
      }

      /**
       * @see CreateLoadBalancerRuleOptions#openFirewall
       */
      public static CreateLoadBalancerRuleOptions openFirewall(boolean openFirewall) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.openFirewall(openFirewall);
      }

      /**
       * @see CreateLoadBalancerRuleOptions#zoneId
       */
      public static CreateLoadBalancerRuleOptions zoneId(String zoneId) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.zoneId(zoneId);
      }

      /**
       * @see CreateLoadBalancerRuleOptions#accountInDomain
       */
      public static CreateLoadBalancerRuleOptions accountInDomain(String account, String domain) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see CreateLoadBalancerRuleOptions#domainId
       */
      public static CreateLoadBalancerRuleOptions domainId(String id) {
         CreateLoadBalancerRuleOptions options = new CreateLoadBalancerRuleOptions();
         return options.domainId(id);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateLoadBalancerRuleOptions accountInDomain(String account, String domain) {
      return CreateLoadBalancerRuleOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateLoadBalancerRuleOptions domainId(String domainId) {
      return CreateLoadBalancerRuleOptions.class.cast(super.domainId(domainId));
   }
}
