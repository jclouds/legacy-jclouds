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

import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
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
      DELETEING,  // Revoke means this rule has been revoked. If this rule has been sent to the
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

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String IPAddress;
      private long IPAddressId;
      private int privatePort;
      private Protocol protocol;
      public int publicPort;
      private State state;
      private String virtualMachineDisplayName;
      public long virtualMachineId;
      private String virtualMachineName;
      private Set<String> CIDRs = ImmutableSet.of();
      private int privateEndPort;
      private int publicEndPort;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return this;
      }

      public Builder IPAddressId(long IPAddressId) {
         this.IPAddressId = IPAddressId;
         return this;
      }

      public Builder privatePort(int privatePort) {
         this.privatePort = privatePort;
         return this;
      }

      public Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder publicPort(int publicPort) {
         this.publicPort = publicPort;
         return this;
      }

      public Builder state(State state) {
         this.state = state;
         return this;
      }

      public Builder virtualMachineDisplayName(String virtualMachineDisplayName) {
         this.virtualMachineDisplayName = virtualMachineDisplayName;
         return this;
      }

      public Builder virtualMachineId(long virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
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

      public PortForwardingRule build() {
         return new PortForwardingRule(id, IPAddress, IPAddressId, privatePort, protocol, publicPort, state,
            virtualMachineDisplayName, virtualMachineId, virtualMachineName, CIDRs, privateEndPort, publicEndPort);
      }
   }

   private long id;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("ipaddressid")
   private long IPAddressId;
   @SerializedName("privateport")
   private int privatePort;
   private Protocol protocol;
   @SerializedName("publicport")
   public int publicPort;
   private State state;
   @SerializedName("virtualmachinedisplayname")
   private String virtualMachineDisplayName;
   @SerializedName("virtualmachineid")
   public long virtualMachineId;
   @SerializedName("virtualmachinename")
   private String virtualMachineName;
   @SerializedName("cidrlist")
   private Set<String> CIDRs;
   @SerializedName("privateendport")
   private int privateEndPort;
   @SerializedName("publicendport")
   private int publicEndPort;

   public PortForwardingRule(long id, String iPAddress, long iPAddressId, int privatePort, Protocol protocol,
                             int publicPort, State state, String virtualMachineDisplayName, long virtualMachineId,
                             String virtualMachineName, Set<String> CIDRs, int privateEndPort, int publicEndPort) {
      this.id = id;
      this.IPAddress = iPAddress;
      this.IPAddressId = iPAddressId;
      this.privatePort = privatePort;
      this.protocol = protocol;
      this.publicPort = publicPort;
      this.state = state;
      this.virtualMachineDisplayName = virtualMachineDisplayName;
      this.virtualMachineId = virtualMachineId;
      this.virtualMachineName = virtualMachineName;
      this.CIDRs = CIDRs;
      this.privateEndPort = privateEndPort;
      this.publicEndPort = publicEndPort;
   }

   @Override
   public int compareTo(PortForwardingRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return the ID of the port forwarding rule
    */
   public long getId() {
      return id;
   }

   /**
    * @return the public ip address for the port forwarding rule
    */
   public String getIPAddress() {
      return IPAddress;
   }

   /**
    * @return the public ip address id for the port forwarding rule
    */
   public long getIPAddressId() {
      return IPAddressId;
   }

   /**
    * @return the private port for the port forwarding rule
    */
   public int getPrivatePort() {
      return privatePort;
   }

   /**
    * @return the protocol of the port forwarding rule
    */
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * @return the public port for the port forwarding rule
    */
   public int getPublicPort() {
      return publicPort;
   }

   /**
    * @return the state of the rule
    */
   public State getState() {
      return state;
   }

   /**
    * @return the VM display name for the port forwarding rule
    */
   public String getVirtualMachineDisplayName() {
      return virtualMachineDisplayName;
   }

   /**
    * @return the VM ID for the port forwarding rule
    */
   public long getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * @return the VM name for the port forwarding rule
    */
   public String getVirtualMachineName() {
      return virtualMachineName;
   }

   /**
    * @return the cidr list to forward traffic from
    */
   public Set<String> getCIDRs() {
      return CIDRs;
   }

   /**
    * @return the starting port of port forwarding rule's private port range
    */
   public int getPrivateEndPort() {
      return privateEndPort;
   }

   /**
    * @return the starting port of port forwarding rule's public port range
    */
   public int getPublicEndPort() {
      return publicEndPort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PortForwardingRule that = (PortForwardingRule) o;

      if (!Objects.equal(IPAddress, that.IPAddress)) return false;
      if (!Objects.equal(IPAddressId, that.IPAddressId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(privatePort, that.privatePort)) return false;
      if (!Objects.equal(protocol, that.protocol)) return false;
      if (!Objects.equal(publicPort, that.publicPort)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(virtualMachineDisplayName, that.virtualMachineDisplayName)) return false;
      if (!Objects.equal(virtualMachineId, that.virtualMachineId)) return false;
      if (!Objects.equal(virtualMachineName, that.virtualMachineName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(IPAddress, IPAddressId, id, privatePort, protocol, publicPort, state, virtualMachineDisplayName, virtualMachineId, virtualMachineName);
   }

   @Override
   public String toString() {
      return "PortForwardingRule{" +
         "id=" + id +
         ", IPAddress='" + IPAddress + '\'' +
         ", IPAddressId=" + IPAddressId +
         ", privatePort=" + privatePort +
         ", protocol='" + protocol + '\'' +
         ", publicPort=" + publicPort +
         ", state='" + state + '\'' +
         ", virtualMachineDisplayName='" + virtualMachineDisplayName + '\'' +
         ", virtualMachineId=" + virtualMachineId +
         ", virtualMachineName='" + virtualMachineName + '\'' +
         ", CIDRs=" + getCIDRs() +
         ", privateEndPort=" + privateEndPort +
         ", publicEndPort=" + publicEndPort +
         '}';
   }

}
