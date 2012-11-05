/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.fujitsu.fgcp.domain;

import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Grouping of firewall rules pertaining to a particular direction in network
 * traffic, e.g. from the Internet to a server in the DMZ zone, or from a server
 * in the SECURE2 zone to the SECURE1 zone, etc.
 * 
 * @author Dies Koper
 */
public class Direction {
   private String from;
   private String to;
   private Set<Policy> policies = Sets.newLinkedHashSet();
   private Acceptable acceptable;
   private Prefix prefix;
   private int maxPolicyNum;

   enum Acceptable {OK, NG}
   enum Prefix {free, src, dst, proto, srcport, dstport, action, rule, tab}

   /**
    * @return the from
    */
   public String getFrom() {
      return from;
   }

   /**
    * @return the to
    */
   public String getTo() {
      return to;
   }

   /**
    * @return the policies
    */
   public Set<Policy> getPolicies() {
      return policies == null ? ImmutableSet.<Policy> of() : ImmutableSet
            .copyOf(policies);
   }

   /**
    * @return the acceptable
    */
   public Acceptable getAcceptable() {
      return acceptable;
   }

   /**
    * @return the prefix
    */
   public Prefix getPrefix() {
      return prefix;
   }

   /**
    * @return the maxPolicyNum
    */
   public int getMaxPolicyNum() {
      return maxPolicyNum;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(from, to);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Direction that = Direction.class.cast(obj);
      return Objects.equal(this.from, that.from)
            && Objects.equal(this.to, that.to);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("from", from)
            .add("to", to).add("prefix", prefix).add("policies", policies)
            .add("maxPolicyNum", maxPolicyNum)
            .add("acceptable", acceptable).toString();
   }
}
