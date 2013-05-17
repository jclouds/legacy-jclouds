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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class FirewallRule
 *
 * @author Andrei Savu
 */
public class FirewallRule implements Comparable<FirewallRule> {

   /**
    */
   public static enum Protocol {
      TCP,
      UDP,
      ICMP,
      UNKNOWN;

      public static Protocol fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return name().toUpperCase();
      }
   }

   public static enum State {
      STAGED,     // Rule been created but has never got through network rule conflict detection.
      // Rules in this state can not be sent to network elements.
      ADD,        // Add means the rule has been created and has gone through network rule conflict detection.
      ACTIVE,     // Rule has been sent to the network elements and reported to be active.
      DELETING,   // Revoke means this rule has been revoked. If this rule has been sent to the
      // network elements, the rule will be deleted from database.
      UNKNOWN;

      public static State fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromFirewallRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected Set<String> CIDRs = ImmutableSet.of();
      protected int startPort;
      protected int endPort;
      protected String icmpCode;
      protected String icmpType;
      protected String ipAddress;
      protected String ipAddressId;
      protected FirewallRule.Protocol protocol;
      protected FirewallRule.State state;

      /**
       * @see FirewallRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see FirewallRule#getCIDRs()
       */
      public T CIDRs(Set<String> CIDRs) {
         this.CIDRs = ImmutableSet.copyOf(checkNotNull(CIDRs, "CIDRs"));
         return self();
      }

      public T CIDRs(String... in) {
         return CIDRs(ImmutableSet.copyOf(in));
      }

      /**
       * @see FirewallRule#getStartPort()
       */
      public T startPort(int startPort) {
         this.startPort = startPort;
         return self();
      }

      /**
       * @see FirewallRule#getEndPort()
       */
      public T endPort(int endPort) {
         this.endPort = endPort;
         return self();
      }

      /**
       * @see FirewallRule#getIcmpCode()
       */
      public T icmpCode(String icmpCode) {
         this.icmpCode = icmpCode;
         return self();
      }

      /**
       * @see FirewallRule#getIcmpType()
       */
      public T icmpType(String icmpType) {
         this.icmpType = icmpType;
         return self();
      }

      /**
       * @see FirewallRule#getIpAddress()
       */
      public T ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return self();
      }

      /**
       * @see FirewallRule#getIpAddressId()
       */
      public T ipAddressId(String ipAddressId) {
         this.ipAddressId = ipAddressId;
         return self();
      }

      /**
       * @see FirewallRule#getProtocol()
       */
      public T protocol(FirewallRule.Protocol protocol) {
         this.protocol = protocol;
         return self();
      }

      /**
       * @see FirewallRule#getState()
       */
      public T state(FirewallRule.State state) {
         this.state = state;
         return self();
      }

      public FirewallRule build() {
         return new FirewallRule(id, CIDRs, startPort, endPort, icmpCode, icmpType, ipAddress, ipAddressId, protocol, state);
      }

      public T fromFirewallRule(FirewallRule in) {
         return this
               .id(in.getId())
               .CIDRs(in.getCIDRs())
               .startPort(in.getStartPort())
               .endPort(in.getEndPort())
               .icmpCode(in.getIcmpCode())
               .icmpType(in.getIcmpType())
               .ipAddress(in.getIpAddress())
               .ipAddressId(in.getIpAddressId())
               .protocol(in.getProtocol())
               .state(in.getState());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Set<String> CIDRs;
   private final int startPort;
   private final int endPort;
   private final String icmpCode;
   private final String icmpType;
   private final String ipAddress;
   private final String ipAddressId;
   private final FirewallRule.Protocol protocol;
   private final FirewallRule.State state;

   @ConstructorProperties({
         "id", "cidrlist", "startport", "endport", "icmpcode", "icmptype", "ipaddress", "ipaddressid", "protocol", "state"
   })
   private FirewallRule(String id, @Nullable String CIDRs, int startPort, int endPort, @Nullable String icmpCode,
                        @Nullable String icmpType, @Nullable String ipAddress, @Nullable String ipAddressId,
                        @Nullable Protocol protocol, @Nullable State state) {
      this(id, splitStringOnCommas(CIDRs), startPort, endPort, icmpCode, icmpType, ipAddress, ipAddressId, protocol, state);
   }

   private static Set<String> splitStringOnCommas(String in) {
      return in == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(in.split(","));
   }


   protected FirewallRule(String id, @Nullable Iterable<String> CIDRs, int startPort, int endPort, @Nullable String icmpCode,
                          @Nullable String icmpType, @Nullable String ipAddress, @Nullable String ipAddressId,
                          @Nullable FirewallRule.Protocol protocol, @Nullable FirewallRule.State state) {
      this.id = checkNotNull(id, "id");
      this.CIDRs = CIDRs == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(CIDRs);
      this.startPort = startPort;
      this.endPort = endPort;
      this.icmpCode = icmpCode;
      this.icmpType = icmpType;
      this.ipAddress = ipAddress;
      this.ipAddressId = ipAddressId;
      this.protocol = protocol;
      this.state = state;
   }

   public String getId() {
      return this.id;
   }

   public Set<String> getCIDRs() {
      return this.CIDRs;
   }

   public int getStartPort() {
      return this.startPort;
   }

   public int getEndPort() {
      return this.endPort;
   }

   @Nullable
   public String getIcmpCode() {
      return this.icmpCode;
   }

   @Nullable
   public String getIcmpType() {
      return this.icmpType;
   }

   @Nullable
   public String getIpAddress() {
      return this.ipAddress;
   }

   @Nullable
   public String getIpAddressId() {
      return this.ipAddressId;
   }

   @Nullable
   public FirewallRule.Protocol getProtocol() {
      return this.protocol;
   }

   @Nullable
   public FirewallRule.State getState() {
      return this.state;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, CIDRs, startPort, endPort, icmpCode, icmpType, ipAddress, ipAddressId, protocol, state);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      FirewallRule that = FirewallRule.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.CIDRs, that.CIDRs)
            && Objects.equal(this.startPort, that.startPort)
            && Objects.equal(this.endPort, that.endPort)
            && Objects.equal(this.icmpCode, that.icmpCode)
            && Objects.equal(this.icmpType, that.icmpType)
            && Objects.equal(this.ipAddress, that.ipAddress)
            && Objects.equal(this.ipAddressId, that.ipAddressId)
            && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.state, that.state);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("CIDRs", CIDRs).add("startPort", startPort).add("endPort", endPort).add("icmpCode", icmpCode)
            .add("icmpType", icmpType).add("ipAddress", ipAddress).add("ipAddressId", ipAddressId).add("protocol", protocol).add("state", state);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(FirewallRule other) {
      return id.compareTo(other.getId());
   }

}
