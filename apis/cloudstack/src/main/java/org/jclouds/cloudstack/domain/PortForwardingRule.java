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
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class PortForwardingRule implements Comparable<PortForwardingRule> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String IPAddress;
      private long IPAddressId;
      private int privatePort;
      private String protocol;
      public int publicPort;
      private String state;
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

      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder publicPort(int publicPort) {
         this.publicPort = publicPort;
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
   private String protocol;
   @SerializedName("publicport")
   public int publicPort;
   private String state;
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


   // for deserializer
   PortForwardingRule() {

   }

   public PortForwardingRule(long id, String iPAddress, long iPAddressId, int privatePort, String protocol,
                             int publicPort, String state, String virtualMachineDisplayName, long virtualMachineId,
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
      this.CIDRs = new HashSet<String>(CIDRs);
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
   public String getProtocol() {
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
   public String getState() {
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
      return Collections.unmodifiableSet(CIDRs);
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((IPAddress == null) ? 0 : IPAddress.hashCode());
      result = prime * result + (int) (IPAddressId ^ (IPAddressId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + privatePort;
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
      result = prime * result + publicPort;
      result = prime * result + ((state == null) ? 0 : state.hashCode());
      result = prime * result + ((virtualMachineDisplayName == null) ? 0 : virtualMachineDisplayName.hashCode());
      result = prime * result + (int) (virtualMachineId ^ (virtualMachineId >>> 32));
      result = prime * result + ((virtualMachineName == null) ? 0 : virtualMachineName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PortForwardingRule other = (PortForwardingRule) obj;
      if (IPAddress == null) {
         if (other.IPAddress != null)
            return false;
      } else if (!IPAddress.equals(other.IPAddress))
         return false;
      if (IPAddressId != other.IPAddressId)
         return false;
      if (id != other.id)
         return false;
      if (privatePort != other.privatePort)
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      if (privatePort != other.privatePort)
         return false;
      if (state == null) {
         if (other.state != null)
            return false;
      } else if (!state.equals(other.state))
         return false;
      if (virtualMachineDisplayName == null) {
         if (other.virtualMachineDisplayName != null)
            return false;
      } else if (!virtualMachineDisplayName.equals(other.virtualMachineDisplayName))
         return false;
      if (virtualMachineId != other.virtualMachineId)
         return false;
      if (virtualMachineName == null) {
         if (other.virtualMachineName != null)
            return false;
      } else if (!virtualMachineName.equals(other.virtualMachineName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[IPAddress=" + IPAddress + ", IPAddressId=" + IPAddressId + ", id=" + id + ", privatePort=" + privatePort
            + ", protocol=" + protocol + ", publicPort=" + publicPort + ", state=" + state
            + ", virtualMachineDisplayName=" + virtualMachineDisplayName + ", virtualMachineId=" + virtualMachineId
            + ", virtualMachineName=" + virtualMachineName + "]";
   }

}
