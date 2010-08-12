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

package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * @author Adrian Cole
 */
public class FirewallRule {
   public static enum Policy {
      DENY, ALLOW;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Policy fromValue(String policy) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(policy,
                  "policy")));
      }

   }

   public static enum Protocol {
      TCP, UDP, ICMP;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Protocol fromValue(String protocol) {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(
                  protocol, "protocol")));
      }

   }

   private final Policy policy;
   private final Protocol protocol;
   private final String sourceIP;
   private final String sourcePort;

   public FirewallRule(Policy policy, Protocol protocol, String sourceIP, String sourcePort) {
      this.policy = policy;
      this.protocol = protocol;
      this.sourceIP = sourceIP;
      this.sourcePort = sourcePort;
   }

   /**
    * One of deny, allow
    */
   public Policy getPolicy() {
      return policy;
   }

   /**
    * An attribute that specifies the protocol to which the rule applies. One of tcp, udp, icmp
    */
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * An IP address to which this rule applies
    */
   public String getSourceIP() {
      return sourceIP;
   }

   /**
    * An IP port or port range to which this rule applies. A value of * specifies all ports.
    */
   public String getSourcePort() {
      return sourcePort;
   }
}