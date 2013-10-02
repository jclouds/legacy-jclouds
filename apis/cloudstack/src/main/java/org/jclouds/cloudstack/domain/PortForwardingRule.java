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
 * Class PortForwardingRule
 *
 * @author Adrian Cole, Andrei Savu
 */
public class PortForwardingRule implements Comparable<PortForwardingRule> {

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
         return name().toLowerCase();
      }
   }

   public static enum State {
      STAGED,     // Rule been created but has never got through network rule conflict detection.
      // Rules in this state can not be sent to network elements.
      ADD,        // Add means the rule has been created and has gone through network rule conflict detection.
      ACTIVE,     // Rule has been sent to the network elements and reported to be active.
      DELETING,  // Revoke means this rule has been revoked. If this rule has been sent to the
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
      return new ConcreteBuilder().fromPortForwardingRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String IPAddress;
      protected String IPAddressId;
      protected int privatePort;
      protected Protocol protocol;
      protected int publicPort;
      protected State state;
      protected String virtualMachineDisplayName;
      protected String virtualMachineId;
      protected String virtualMachineName;
      protected Set<String> CIDRs = ImmutableSet.of();
      protected int privateEndPort;
      protected int publicEndPort;

      /**
       * @see PortForwardingRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see PortForwardingRule#getIPAddress()
       */
      public T IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return self();
      }

      /**
       * @see PortForwardingRule#getIPAddressId()
       */
      public T IPAddressId(String IPAddressId) {
         this.IPAddressId = IPAddressId;
         return self();
      }

      /**
       * @see PortForwardingRule#getPrivatePort()
       */
      public T privatePort(int privatePort) {
         this.privatePort = privatePort;
         return self();
      }

      /**
       * @see PortForwardingRule#getProtocol()
       */
      public T protocol(Protocol protocol) {
         this.protocol = protocol;
         return self();
      }

      /**
       * @see PortForwardingRule#getPublicPort()
       */
      public T publicPort(int publicPort) {
         this.publicPort = publicPort;
         return self();
      }

      /**
       * @see PortForwardingRule#getState()
       */
      public T state(State state) {
         this.state = state;
         return self();
      }

      /**
       * @see PortForwardingRule#getVirtualMachineDisplayName()
       */
      public T virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return self();
      }

      /**
       * @see PortForwardingRule#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see PortForwardingRule#getVirtualMachineName()
       */
      public T virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return self();
      }

      /**
       * @see PortForwardingRule#getCIDRs()
       */
      public T CIDRs(Set<String> CIDRs) {
         this.CIDRs = ImmutableSet.copyOf(checkNotNull(CIDRs, "CIDRs"));
         return self();
      }

      public T CIDRs(String... in) {
         return CIDRs(ImmutableSet.copyOf(in));
      }

      /**
       * @see PortForwardingRule#getPrivateEndPort()
       */
      public T privateEndPort(int privateEndPort) {
         this.privateEndPort = privateEndPort;
         return self();
      }

      /**
       * @see PortForwardingRule#getPublicEndPort()
       */
      public T publicEndPort(int publicEndPort) {
         this.publicEndPort = publicEndPort;
         return self();
      }

      public PortForwardingRule build() {
         return new PortForwardingRule(id, IPAddress, IPAddressId, privatePort, protocol, publicPort, state, virtualMachineDisplayName,
               virtualMachineId, virtualMachineName, CIDRs, privateEndPort, publicEndPort);
      }

      public T fromPortForwardingRule(PortForwardingRule in) {
         return this
               .id(in.getId())
               .IPAddress(in.getIPAddress())
               .IPAddressId(in.getIPAddressId())
               .privatePort(in.getPrivatePort())
               .protocol(in.getProtocol())
               .publicPort(in.getPublicPort())
               .state(in.getState())
               .virtualMachineDisplayName(in.getVirtualMachineDisplayName())
               .virtualMachineId(in.getVirtualMachineId())
               .virtualMachineName(in.getVirtualMachineName())
               .CIDRs(in.getCIDRs())
               .privateEndPort(in.getPrivateEndPort())
               .publicEndPort(in.getPublicEndPort());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String IPAddress;
   private final String IPAddressId;
   private final int privatePort;
   private final PortForwardingRule.Protocol protocol;
   private final int publicPort;
   private final PortForwardingRule.State state;
   private final String virtualMachineDisplayName;
   private final String virtualMachineId;
   private final String virtualMachineName;
   private final Set<String> CIDRs;
   private final int privateEndPort;
   private final int publicEndPort;

   @ConstructorProperties({
         "id", "ipaddress", "ipaddressid", "privateport", "protocol", "publicport", "state", "virtualmachinedisplayname",
         "virtualmachineid", "virtualmachinename", "cidrlist", "privateendport", "publicendport"
   })
   private PortForwardingRule(String id, @Nullable String IPAddress, @Nullable String IPAddressId, int privatePort,
                              @Nullable Protocol protocol, int publicPort, @Nullable State state, @Nullable String virtualMachineDisplayName,
                              @Nullable String virtualMachineId, @Nullable String virtualMachineName, @Nullable String CIDRs,
                              int privateEndPort, int publicEndPort) {
      this(id, IPAddress, IPAddressId, privatePort, protocol, publicPort, state, virtualMachineDisplayName, virtualMachineId,
            virtualMachineName, splitStringOnCommas(CIDRs), privateEndPort, publicEndPort);
   }

   private static Set<String> splitStringOnCommas(String in) {
      return in == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(in.split(","));
   }

   protected PortForwardingRule(String id, @Nullable String IPAddress, @Nullable String IPAddressId, int privatePort,
                                @Nullable Protocol protocol, int publicPort, @Nullable State state,
                                @Nullable String virtualMachineDisplayName, @Nullable String virtualMachineId,
                                @Nullable String virtualMachineName, @Nullable Set<String> CIDRs, int privateEndPort, int publicEndPort) {
      this.id = checkNotNull(id, "id");
      this.IPAddress = IPAddress;
      this.IPAddressId = IPAddressId;
      this.privatePort = privatePort;
      this.protocol = protocol;
      this.publicPort = publicPort;
      this.state = state;
      this.virtualMachineDisplayName = virtualMachineDisplayName;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.CIDRs = CIDRs == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(CIDRs);
      this.privateEndPort = privateEndPort;
      this.publicEndPort = publicEndPort;
   }

   /**
    * @return the ID of the port forwarding rule
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the public ip address for the port forwarding rule
    */
   @Nullable
   public String getIPAddress() {
      return this.IPAddress;
   }

   /**
    * @return the public ip address id for the port forwarding rule
    */
   @Nullable
   public String getIPAddressId() {
      return this.IPAddressId;
   }

   /**
    * @return the private port for the port forwarding rule
    */
   public int getPrivatePort() {
      return this.privatePort;
   }

   /**
    * @return the protocol of the port forwarding rule
    */
   @Nullable
   public Protocol getProtocol() {
      return this.protocol;
   }

   /**
    * @return the public port for the port forwarding rule
    */
   public int getPublicPort() {
      return this.publicPort;
   }

   /**
    * @return the state of the rule
    */
   @Nullable
   public State getState() {
      return this.state;
   }

   /**
    * @return the VM display name for the port forwarding rule
    */
   @Nullable
   public String getVirtualMachineDisplayName() {
      return this.virtualMachineDisplayName;
   }

   /**
    * @return the VM ID for the port forwarding rule
    */
   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   /**
    * @return the VM name for the port forwarding rule
    */
   @Nullable
   public String getVirtualMachineName() {
      return this.virtualMachineName;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return this.CIDRs;
   }

   /**
    * @return the starting port of port forwarding rule's private port range
    */
   public int getPrivateEndPort() {
      return this.privateEndPort;
   }

   /**
    * @return the starting port of port forwarding rule's public port range
    */
   public int getPublicEndPort() {
      return this.publicEndPort;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, IPAddress, IPAddressId, privatePort, protocol, publicPort, state, virtualMachineDisplayName, virtualMachineId, virtualMachineName, CIDRs, privateEndPort, publicEndPort);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      PortForwardingRule that = PortForwardingRule.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.IPAddress, that.IPAddress)
            && Objects.equal(this.IPAddressId, that.IPAddressId)
            && Objects.equal(this.privatePort, that.privatePort)
            && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.publicPort, that.publicPort)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.virtualMachineDisplayName, that.virtualMachineDisplayName)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.virtualMachineName, that.virtualMachineName)
            && Objects.equal(this.CIDRs, that.CIDRs)
            && Objects.equal(this.privateEndPort, that.privateEndPort)
            && Objects.equal(this.publicEndPort, that.publicEndPort);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("IPAddress", IPAddress).add("IPAddressId", IPAddressId).add("privatePort", privatePort)
            .add("protocol", protocol).add("publicPort", publicPort).add("state", state).add("virtualMachineDisplayName", virtualMachineDisplayName)
            .add("virtualMachineId", virtualMachineId).add("virtualMachineName", virtualMachineName).add("CIDRs", CIDRs)
            .add("privateEndPort", privateEndPort).add("publicEndPort", publicEndPort);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(PortForwardingRule o) {
      return id.compareTo(o.getId());
   }
}
