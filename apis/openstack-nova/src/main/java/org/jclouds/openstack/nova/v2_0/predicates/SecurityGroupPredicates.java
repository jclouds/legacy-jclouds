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
package org.jclouds.openstack.nova.v2_0.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.net.domain.IpProtocol;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroup;
import org.jclouds.openstack.nova.v2_0.domain.SecurityGroupRule;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Predicates handy when working with SecurityGroups
 * 
 * @author Adrian Cole
 */

public class SecurityGroupPredicates {

   /**
    * matches name of the given security group
    *
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.equals(ext.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }

   /**
    * matches name of the given security group against a list
    *
    * @param names
    * @return predicate that matches one of the names
    */
   public static Predicate<SecurityGroup> nameIn(final Set<String> names) {
      checkNotNull(names, "names must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return Predicates.in(names).apply(ext.getName());
         }

         @Override
         public String toString() {
            return "nameIn(" + names + ")";
         }
      };
   }

   /**
    * matches name of the given security group
    * 
    * @param name
    * @return predicate that matches name
    */
   public static Predicate<SecurityGroup> nameMatches(final Predicate<String> name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<SecurityGroup>() {
         @Override
         public boolean apply(SecurityGroup ext) {
            return name.apply(ext.getName());
         }

         @Override
         public String toString() {
            return "nameMatches(" + name + ")";
         }
      };
   }

   /**
    * matches a security group rule by its cidr
    *
    * @param cidr
    * @return predicate that matches cidr
    */
   public static Predicate<SecurityGroupRule> ruleCidr(final String cidr) {
      checkNotNull(cidr, "cidr must be defined");

      return new Predicate<SecurityGroupRule>() {
         @Override
         public boolean apply(SecurityGroupRule ext) {
            return cidr.equals(ext.getIpRange());
         }

         @Override
         public String toString() {
            return "cidr(" + cidr + ")";
         }
      };
   }

   /**
    * matches a security group rule by the security group it allows
    *
    * @param groupName
    * @return predicate that matches group
    */
   public static Predicate<SecurityGroupRule> ruleGroup(final String groupName) {
      checkNotNull(groupName, "groupName must be defined");

      return new Predicate<SecurityGroupRule>() {
         @Override
         public boolean apply(SecurityGroupRule ext) {
            return ext.getGroup() != null && groupName.equals(ext.getGroup().getName());
         }

         @Override
         public String toString() {
            return "ruleGroup(" + groupName + ")";
         }
      };
   }

   /**
    * matches a security group rule by the protocol
    *
    * @param protocol
    * @return predicate that matches protocol
    */
   public static Predicate<SecurityGroupRule> ruleProtocol(final IpProtocol protocol) {
      checkNotNull(protocol, "protocol must be defined");

      return new Predicate<SecurityGroupRule>() {
         @Override
         public boolean apply(SecurityGroupRule ext) {
            return protocol.equals(ext.getIpProtocol());
         }

         @Override
         public String toString() {
            return "ruleProtocol(" + protocol + ")";
         }
      };
   }

   /**
    * matches a security group rule by the start port
    *
    * @param startPort
    * @return predicate that matches startPort
    */
   public static Predicate<SecurityGroupRule> ruleStartPort(final int startPort) {
      checkNotNull(startPort, "startPort must be defined");

      return new Predicate<SecurityGroupRule>() {
         @Override
         public boolean apply(SecurityGroupRule ext) {
            return startPort == ext.getFromPort();
         }

         @Override
         public String toString() {
            return "ruleStartPort(" + startPort + ")";
         }
      };
   }

   /**
    * matches a security group rule by the end port
    *
    * @param endPort
    * @return predicate that matches endPort
    */
   public static Predicate<SecurityGroupRule> ruleEndPort(final int endPort) {
      checkNotNull(endPort, "endPort must be defined");

      return new Predicate<SecurityGroupRule>() {
         @Override
         public boolean apply(SecurityGroupRule ext) {
            return endPort == ext.getToPort();
         }

         @Override
         public String toString() {
            return "ruleEndPort(" + endPort + ")";
         }
      };
   }
}
