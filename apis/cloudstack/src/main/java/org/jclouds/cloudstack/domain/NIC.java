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

import java.net.URI;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Adrian Cole
 */
public class NIC {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private URI broadcastURI;
      private String gateway;
      private String IPAddress;
      private boolean isDefault;
      private URI isolationURI;
      private String netmask;
      private String macAddress;
      private String networkId;
      private TrafficType trafficType;
      private GuestIPType guestIPType;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder broadcastURI(URI broadcastURI) {
         this.broadcastURI = broadcastURI;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder IPAddress(String IPAddress) {
         this.IPAddress = IPAddress;
         return this;
      }

      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      public Builder isolationURI(URI isolationURI) {
         this.isolationURI = isolationURI;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      public Builder networkId(String networkId) {
         this.networkId = networkId;
         return this;
      }

      public Builder trafficType(TrafficType trafficType) {
         this.trafficType = trafficType;
         return this;
      }

      public Builder guestIPType(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
         return this;
      }

      public NIC build() {
         return new NIC(id, broadcastURI, gateway, IPAddress, isDefault, isolationURI, netmask, macAddress, networkId,
               trafficType, guestIPType);

      }
   }

   private String id;
   @SerializedName("broadcasturi")
   private URI broadcastURI;
   private String gateway;
   @SerializedName("ipaddress")
   private String IPAddress;
   @SerializedName("isdefault")
   private boolean isDefault;
   @SerializedName("isolationuri")
   private URI isolationURI;
   private String netmask;
   @SerializedName("macaddress")
   private String macAddress;
   @SerializedName("networkid")
   private String networkId;
   @SerializedName("traffictype")
   private TrafficType trafficType;
   @SerializedName("type")
   private GuestIPType guestIPType;

   /**
    * present only for serializer
    */
   NIC() {

   }

   public NIC(String id, URI broadcastURI, String gateway, String iPAddress, boolean isDefault, URI isolationURI,
              String netmask, String macAddress, String networkId, TrafficType trafficType, GuestIPType guestIPType) {
      this.id = id;
      this.broadcastURI = broadcastURI;
      this.gateway = gateway;
      this.IPAddress = iPAddress;
      this.isDefault = isDefault;
      this.isolationURI = isolationURI;
      this.netmask = netmask;
      this.macAddress = macAddress;
      this.networkId = networkId;
      this.trafficType = trafficType;
      this.guestIPType = guestIPType;
   }

   /**
    * the ID of the nic
    */
   public String getId() {
      return id;
   }

   /**
    * the broadcast uri of the nic
    */
   public URI getBroadcastURI() {
      return broadcastURI;
   }

   /**
    * the gateway of the nic
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * the ip address of the nic
    */
   public String getIPAddress() {
      return IPAddress;
   }

   /**
    * true if nic is default, false otherwise
    */
   public boolean isDefault() {
      return isDefault;
   }

   /**
    * the isolation uri of the nic
    */
   public URI getIsolationURI() {
      return isolationURI;
   }

   /**
    * the netmask of the nic
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * the MAC Address of the NIC
    */
   public String getMacAddress() {
      return macAddress;
   }

   /**
    * the ID of the corresponding network
    */
   public String getNetworkId() {
      return networkId;
   }

   /**
    * the traffic type of the nic
    */
   public TrafficType getTrafficType() {
      return trafficType;
   }

   /**
    * the type of the nic
    */
   public GuestIPType getGuestIPType() {
      return guestIPType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NIC that = (NIC) o;

      if (!Objects.equal(IPAddress, that.IPAddress)) return false;
      if (!Objects.equal(broadcastURI, that.broadcastURI)) return false;
      if (!Objects.equal(gateway, that.gateway)) return false;
      if (!Objects.equal(guestIPType, that.guestIPType)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(isDefault, that.isDefault)) return false;
      if (!Objects.equal(isolationURI, that.isolationURI)) return false;
      if (!Objects.equal(netmask, that.netmask)) return false;
      if (!Objects.equal(macAddress, that.macAddress)) return false;
      if (!Objects.equal(networkId, that.networkId)) return false;
      if (!Objects.equal(trafficType, that.trafficType)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(IPAddress, broadcastURI, gateway, guestIPType, id, isDefault, isolationURI, netmask, macAddress, networkId, trafficType);
   }

   @Override
   public String toString() {
      return "NIC{" +
            "id=" + id +
            ", broadcastURI=" + broadcastURI +
            ", gateway='" + gateway + '\'' +
            ", IPAddress='" + IPAddress + '\'' +
            ", isDefault=" + isDefault +
            ", isolationURI=" + isolationURI +
            ", netmask='" + netmask + '\'' +
            ", macAddress='" + macAddress + '\'' +
            ", networkId=" + networkId +
            ", trafficType=" + trafficType +
            ", guestIPType=" + guestIPType +
            '}';
   }

}
