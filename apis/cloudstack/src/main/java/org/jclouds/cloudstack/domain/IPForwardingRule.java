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
package org.jclouds.cloudstack.domain;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class IPForwardingRule implements Comparable<IPForwardingRule> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String IPAddress;
      private String IPAddressId;
      private int startPort;
      private String protocol;
      public int endPort;
      private String state;
      private String virtualMachineDisplayName;
      public String virtualMachineId;
      private String virtualMachineName;
      private Set<String> CIDRs = ImmutableSet.of();
      private int privateEndPort;
      private int publicEndPort;
      public int publicPort;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return this;
      }

      public Builder IPAddressId(String IPAddressId) {
         this.IPAddressId = IPAddressId;
         return this;
      }

      public Builder startPort(int startPort) {
         this.startPort = startPort;
         return this;
      }

      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder endPort(int endPort) {
         this.endPort = endPort;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return this;
      }

      public Builder virtualMachineId(String virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return this;
      }

      public Builder publicPort(int publicPort) {
         this.publicPort = publicPort;
         return this;
      }

      public Builder CIDRs(Set<String> CIDRs) {
         this.CIDRs = CIDRs;
         return this;
      }

      public Builder privateEndPort(int privateEndPort) {
         this.privateEndPort = privateEndPort;
         return this;
      }

      public Builder publicEndPort(int publicEndPort) {
         this.publicEndPort = publicEndPort;
         return this;
      }

      public IPForwardingRule build() {
         return new IPForwardingRule(id, IPAddress, IPAddressId, startPort, protocol, endPort, state,
               virtualMachineDisplayName, virtualMachineId, virtualMachineName, publicEndPort, publicPort, CIDRs, privateEndPort);
      }
   }

   private String id;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("ipaddressid")
   private String IPAddressId;
   @SerializedName("startport")
   private int startPort;
   private String protocol;
   @SerializedName("endport")
   public int endPort;
   private String state;
   @SerializedName("virtualmachinedisplayname")
   private String virtualMachineDisplayName;
   @SerializedName("virtualmachineid")
   public String virtualMachineId;
   @SerializedName("virtualmachinename")
   private String virtualMachineName;
   @SerializedName("publicport")
   public int publicPort;
   @SerializedName("cidrlist")
   private Set<String> CIDRs;
   @SerializedName("privateendport")
   private int privateEndPort;
   @SerializedName("publicendport")
   private int publicEndPort;

   // for deserializer
   IPForwardingRule() {

   }

   public IPForwardingRule(String id, String iPAddress, String iPAddressId, int startPort, String protocol, int endPort,
                           String state, String virtualMachineDisplayName, String virtualMachineId, String virtualMachineName,
                           int publicEndPort, int publicPort, Set<String> CIDRs, int privateEndPort) {
      this.id = id;
      this.IPAddress = iPAddress;
      this.IPAddressId = iPAddressId;
      this.startPort = startPort;
      this.protocol = protocol;
      this.endPort = endPort;
      this.state = state;
      this.virtualMachineDisplayName = virtualMachineDisplayName;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.CIDRs = Sets.newHashSet(CIDRs);
      this.privateEndPort = privateEndPort;
      this.publicEndPort = publicEndPort;
      this.publicPort = publicPort;

   }

   @Override
   public int compareTo(IPForwardingRule arg0) {
      return id.compareTo(arg0.getId());
   }

   /**
    * @return the ID of the ip forwarding rule
    */
   public String getId() {
      return id;
   }

   /**
    * @return the public ip address for the ip forwarding rule
    */
   public String getIPAddress() {
      return IPAddress;
   }

   /**
    * @return the public ip address id for the ip forwarding rule
    */
   public String getIPAddressId() {
      return IPAddressId;
   }

   /**
    * @return the private port for the ip forwarding rule
    */
   public int getStartPort() {
      return startPort;
   }

   /**
    * @return the protocol of the ip forwarding rule
    */
   public String getProtocol() {
      return protocol;
   }

   /**
    * @return the public port for the ip forwarding rule
    */
   public int getEndPort() {
      return endPort;
   }

   /**
    * @return the state of the rule
    */
   public String getState() {
      return state;
   }

   /**
    * @return the VM display name for the ip forwarding rule
    */
   public String getVirtualMachineDisplayName() {
      return virtualMachineDisplayName;
   }

   /**
    * @return the VM ID for the ip forwarding rule
    */
   public String getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * @return the VM name for the ip forwarding rule
    */
   public String getVirtualMachineName() {
      return virtualMachineName;
   }

   /**
    * @return the starting port of port forwarding rule's public port range
    */
   public int getPublicPort() {
      return publicPort;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return Collections.unmodifiableSet(CIDRs);
   }

   /**
    * @return the ending port of port forwarding rule's private port range
    */
   public int getPrivateEndPort() {
      return privateEndPort;
   }

   /**
    * @return the ending port of port forwarding rule's private port range
    */
   public int getPublicEndPort() {
      return publicEndPort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IPForwardingRule that = (IPForwardingRule) o;

      if (!Objects.equal(IPAddress, that.IPAddress)) return false;
      if (!Objects.equal(IPAddressId, that.IPAddressId)) return false;
      if (!Objects.equal(endPort, that.endPort)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(protocol, that.protocol)) return false;
      if (!Objects.equal(startPort, that.startPort)) return false;
      if (!Objects.equal(publicEndPort, that.publicEndPort)) return false;
      if (!Objects.equal(privateEndPort, that.privateEndPort)) return false;
      if (!Objects.equal(publicPort, that.publicPort)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(virtualMachineDisplayName, that.virtualMachineDisplayName)) return false;
      if (!Objects.equal(virtualMachineId, that.virtualMachineId)) return false;
      if (!Objects.equal(virtualMachineName, that.virtualMachineName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(IPAddress, IPAddressId, endPort, id, protocol, startPort, publicEndPort,
                               privateEndPort, publicPort, state, virtualMachineDisplayName,
                               virtualMachineId, virtualMachineName);
   }

   @Override
   public String toString() {
      return "[IPAddress=" + IPAddress + ", IPAddressId=" + IPAddressId + ", id=" + id + ", startPort=" + startPort
            + ", protocol=" + protocol + ", endPort=" + endPort + ", state=" + state + ", virtualMachineDisplayName="
            + virtualMachineDisplayName + ", virtualMachineId=" + virtualMachineId + ", virtualMachineName="
            + virtualMachineName + "]";
   }

}
