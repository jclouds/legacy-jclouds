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

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class IPForwardingRule implements Comparable<IPForwardingRule> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String IPAddress;
      private long IPAddressId;
      private int startPort;
      private String protocol;
      public int endPort;
      private String state;
      private String virtualMachineDisplayName;
      public long virtualMachineId;
      private String virtualMachineName;

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

      public Builder virtualMachineId(long virtualMachineId) {
         this.virtualMachineId = virtualMachineId;
         return this;
      }

      public Builder virtualMachineName(String virtualMachineName) {
         this.virtualMachineName = virtualMachineName;
         return this;
      }

      public IPForwardingRule build() {
         return new IPForwardingRule(id, IPAddress, IPAddressId, startPort, protocol, endPort, state,
               virtualMachineDisplayName, virtualMachineId, virtualMachineName);
      }
   }

   private long id;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("ipaddressid")
   private long IPAddressId;
   @SerializedName("startport")
   private int startPort;
   private String protocol;
   @SerializedName("endport")
   public int endPort;
   private String state;
   @SerializedName("virtualmachinedisplayname")
   private String virtualMachineDisplayName;
   @SerializedName("virtualmachineid")
   public long virtualMachineId;
   @SerializedName("virtualmachinename")
   private String virtualMachineName;

   // for deserializer
   IPForwardingRule() {

   }

   public IPForwardingRule(long id, String iPAddress, long iPAddressId, int startPort, String protocol, int endPort,
         String state, String virtualMachineDisplayName, long virtualMachineId, String virtualMachineName) {
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
   }

   @Override
   public int compareTo(IPForwardingRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * 
    * @return the ID of the ip forwarding rule
    */
   public long getId() {
      return id;
   }

   /**
    * 
    * @return the public ip address for the ip forwarding rule
    */
   public String getIPAddress() {
      return IPAddress;
   }

   /**
    * 
    * @return the public ip address id for the ip forwarding rule
    */
   public long getIPAddressId() {
      return IPAddressId;
   }

   /**
    * 
    * @return the private port for the ip forwarding rule
    */
   public int getStartPort() {
      return startPort;
   }

   /**
    * 
    * @return the protocol of the ip forwarding rule
    */
   public String getProtocol() {
      return protocol;
   }

   /**
    * 
    * @return the public port for the ip forwarding rule
    */
   public int getEndPort() {
      return endPort;
   }

   /**
    * 
    * @return the state of the rule
    */
   public String getState() {
      return state;
   }

   /**
    * 
    * @return the VM display name for the ip forwarding rule
    */
   public String getVirtualMachineDisplayName() {
      return virtualMachineDisplayName;
   }

   /**
    * 
    * @return the VM ID for the ip forwarding rule
    */
   public long getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * 
    * @return the VM name for the ip forwarding rule
    */
   public String getVirtualMachineName() {
      return virtualMachineName;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((IPAddress == null) ? 0 : IPAddress.hashCode());
      result = prime * result + (int) (IPAddressId ^ (IPAddressId >>> 32));
      result = prime * result + endPort;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
      result = prime * result + startPort;
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
      IPForwardingRule other = (IPForwardingRule) obj;
      if (IPAddress == null) {
         if (other.IPAddress != null)
            return false;
      } else if (!IPAddress.equals(other.IPAddress))
         return false;
      if (IPAddressId != other.IPAddressId)
         return false;
      if (endPort != other.endPort)
         return false;
      if (id != other.id)
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      if (startPort != other.startPort)
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
      return "[IPAddress=" + IPAddress + ", IPAddressId=" + IPAddressId + ", id=" + id + ", startPort=" + startPort
            + ", protocol=" + protocol + ", endPort=" + endPort + ", state=" + state + ", virtualMachineDisplayName="
            + virtualMachineDisplayName + ", virtualMachineId=" + virtualMachineId + ", virtualMachineName="
            + virtualMachineName + "]";
   }

}
