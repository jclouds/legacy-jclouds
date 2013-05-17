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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class IPForwardingRule
 *
 * @author Adrian Cole
 */
public class IPForwardingRule implements Comparable<IPForwardingRule> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromIPForwardingRule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String IPAddress;
      protected String IPAddressId;
      protected int startPort;
      protected String protocol;
      protected int endPort;
      protected String state;
      protected String virtualMachineDisplayName;
      protected String virtualMachineId;
      protected String virtualMachineName;
      protected int publicPort;
      protected Set<String> CIDRs = ImmutableSet.of();
      protected int privateEndPort;
      protected int publicEndPort;

      /**
       * @see IPForwardingRule#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see IPForwardingRule#getIPAddress()
       */
      public T IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return self();
      }

      /**
       * @see IPForwardingRule#getIPAddressId()
       */
      public T IPAddressId(String IPAddressId) {
         this.IPAddressId = IPAddressId;
         return self();
      }

      /**
       * @see IPForwardingRule#getStartPort()
       */
      public T startPort(int startPort) {
         this.startPort = startPort;
         return self();
      }

      /**
       * @see IPForwardingRule#getProtocol()
       */
      public T protocol(String protocol) {
         this.protocol = protocol;
         return self();
      }

      /**
       * @see IPForwardingRule#getEndPort()
       */
      public T endPort(int endPort) {
         this.endPort = endPort;
         return self();
      }

      /**
       * @see IPForwardingRule#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see IPForwardingRule#getVirtualMachineDisplayName()
       */
      public T virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return self();
      }

      /**
       * @see IPForwardingRule#getVirtualMachineId()
       */
      public T virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return self();
      }

      /**
       * @see IPForwardingRule#getVirtualMachineName()
       */
      public T virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return self();
      }

      /**
       * @see IPForwardingRule#getPublicPort()
       */
      public T publicPort(int publicPort) {
         this.publicPort = publicPort;
         return self();
      }

      /**
       * @see IPForwardingRule#getCIDRs()
       */
      public T CIDRs(Set<String> CIDRs) {
         this.CIDRs = ImmutableSet.copyOf(checkNotNull(CIDRs, "CIDRs"));
         return self();
      }

      public T CIDRs(String... in) {
         return CIDRs(ImmutableSet.copyOf(in));
      }

      /**
       * @see IPForwardingRule#getPrivateEndPort()
       */
      public T privateEndPort(int privateEndPort) {
         this.privateEndPort = privateEndPort;
         return self();
      }

      /**
       * @see IPForwardingRule#getPublicEndPort()
       */
      public T publicEndPort(int publicEndPort) {
         this.publicEndPort = publicEndPort;
         return self();
      }

      public IPForwardingRule build() {
         return new IPForwardingRule(id, IPAddress, IPAddressId, startPort, protocol, endPort, state, virtualMachineDisplayName,
               virtualMachineId, virtualMachineName, publicPort, CIDRs, privateEndPort, publicEndPort);
      }

      public T fromIPForwardingRule(IPForwardingRule in) {
         return this
               .id(in.getId())
               .IPAddress(in.getIPAddress())
               .IPAddressId(in.getIPAddressId())
               .startPort(in.getStartPort())
               .protocol(in.getProtocol())
               .endPort(in.getEndPort())
               .state(in.getState())
               .virtualMachineDisplayName(in.getVirtualMachineDisplayName())
               .virtualMachineId(in.getVirtualMachineId())
               .virtualMachineName(in.getVirtualMachineName())
               .publicPort(in.getPublicPort())
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
   private final int startPort;
   private final String protocol;
   private final int endPort;
   private final String state;
   private final String virtualMachineDisplayName;
   private final String virtualMachineId;
   private final String virtualMachineName;
   private final int publicPort;
   private final Set<String> CIDRs;
   private final int privateEndPort;
   private final int publicEndPort;

   @ConstructorProperties({
         "id", "ipaddress", "ipaddressid", "startport", "protocol", "endport", "state", "virtualmachinedisplayname",
         "virtualmachineid", "virtualmachinename", "publicport", "cidrlist", "privateendport", "publicendport"
   })
   protected IPForwardingRule(String id, String IPAddress, String IPAddressId, int startPort, @Nullable String protocol,
                              int endPort, @Nullable String state, @Nullable String virtualMachineDisplayName,
                              @Nullable String virtualMachineId, @Nullable String virtualMachineName, int publicPort,
                              @Nullable Set<String> CIDRs, int privateEndPort, int publicEndPort) {
      this.id = checkNotNull(id, "id");
      this.IPAddress = IPAddress;
      this.IPAddressId = IPAddressId;
      this.startPort = startPort;
      this.protocol = protocol;
      this.endPort = endPort;
      this.state = state;
      this.virtualMachineDisplayName = virtualMachineDisplayName;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.publicPort = publicPort;
      this.CIDRs = CIDRs == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(CIDRs);
      this.privateEndPort = privateEndPort;
      this.publicEndPort = publicEndPort;
   }

   /**
    * @return the ID of the ip forwarding rule
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the public ip address for the ip forwarding rule
    */
   @Nullable
   public String getIPAddress() {
      return this.IPAddress;
   }

   /**
    * @return the public ip address id for the ip forwarding rule
    */
   @Nullable
   public String getIPAddressId() {
      return this.IPAddressId;
   }

   /**
    * @return the private port for the ip forwarding rule
    */
   public int getStartPort() {
      return this.startPort;
   }

   /**
    * @return the protocol of the ip forwarding rule
    */
   @Nullable
   public String getProtocol() {
      return this.protocol;
   }

   /**
    * @return the public port for the ip forwarding rule
    */
   public int getEndPort() {
      return this.endPort;
   }

   /**
    * @return the state of the rule
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return the VM display name for the ip forwarding rule
    */
   @Nullable
   public String getVirtualMachineDisplayName() {
      return this.virtualMachineDisplayName;
   }

   /**
    * @return the VM ID for the ip forwarding rule
    */
   @Nullable
   public String getVirtualMachineId() {
      return this.virtualMachineId;
   }

   /**
    * @return the VM name for the ip forwarding rule
    */
   @Nullable
   public String getVirtualMachineName() {
      return this.virtualMachineName;
   }

   /**
    * @return the starting port of port forwarding rule's public port range
    */
   public int getPublicPort() {
      return this.publicPort;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return this.CIDRs;
   }

   /**
    * @return the ending port of port forwarding rule's private port range
    */
   public int getPrivateEndPort() {
      return this.privateEndPort;
   }

   /**
    * @return the ending port of port forwarding rule's private port range
    */
   public int getPublicEndPort() {
      return this.publicEndPort;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, IPAddress, IPAddressId, startPort, protocol, endPort, state, virtualMachineDisplayName, virtualMachineId, virtualMachineName, publicPort, CIDRs, privateEndPort, publicEndPort);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      IPForwardingRule that = IPForwardingRule.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.IPAddress, that.IPAddress)
            && Objects.equal(this.IPAddressId, that.IPAddressId)
            && Objects.equal(this.startPort, that.startPort)
            && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.endPort, that.endPort)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.virtualMachineDisplayName, that.virtualMachineDisplayName)
            && Objects.equal(this.virtualMachineId, that.virtualMachineId)
            && Objects.equal(this.virtualMachineName, that.virtualMachineName)
            && Objects.equal(this.publicPort, that.publicPort)
            && Objects.equal(this.CIDRs, that.CIDRs)
            && Objects.equal(this.privateEndPort, that.privateEndPort)
            && Objects.equal(this.publicEndPort, that.publicEndPort);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("IPAddress", IPAddress).add("IPAddressId", IPAddressId).add("startPort", startPort)
            .add("protocol", protocol).add("endPort", endPort).add("state", state).add("virtualMachineDisplayName", virtualMachineDisplayName)
            .add("virtualMachineId", virtualMachineId).add("virtualMachineName", virtualMachineName).add("publicPort", publicPort)
            .add("CIDRs", CIDRs).add("privateEndPort", privateEndPort).add("publicEndPort", publicEndPort);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(IPForwardingRule o) {
      return id.compareTo(o.getId());
   }
}
