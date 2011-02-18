/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.domain;

import java.net.URI;

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
      private long id;
      private URI broadcastURI;
      private String gateway;
      private String IPAddress;
      private boolean isDefault;
      private URI isolationURI;
      private String netmask;
      private long networkId;
      private TrafficType trafficType;
      private GuestIPType guestIPType;

      public Builder id(long id) {
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

      public Builder networkId(long networkId) {
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
         return new NIC(id, broadcastURI, gateway, IPAddress, isDefault, isolationURI, netmask, networkId, trafficType,
               guestIPType);

      }
   }

   private long id;
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
   @SerializedName("networkid")
   private long networkId;
   @SerializedName("traffictype")
   private TrafficType trafficType;
   @SerializedName("type")
   private GuestIPType guestIPType;

   /**
    * present only for serializer
    * 
    */
   NIC() {

   }

   public NIC(long id, URI broadcastURI, String gateway, String iPAddress, boolean isDefault, URI isolationURI,
         String netmask, long networkId, TrafficType trafficType, GuestIPType guestIPType) {
      this.id = id;
      this.broadcastURI = broadcastURI;
      this.gateway = gateway;
      this.IPAddress = iPAddress;
      this.isDefault = isDefault;
      this.isolationURI = isolationURI;
      this.netmask = netmask;
      this.networkId = networkId;
      this.trafficType = trafficType;
      this.guestIPType = guestIPType;
   }

   /**
    * the ID of the nic
    */
   public long getId() {
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
    * the ID of the corresponding network
    */
   public long getNetworkId() {
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((IPAddress == null) ? 0 : IPAddress.hashCode());
      result = prime * result + ((broadcastURI == null) ? 0 : broadcastURI.hashCode());
      result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
      result = prime * result + ((guestIPType == null) ? 0 : guestIPType.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + (isDefault ? 1231 : 1237);
      result = prime * result + ((isolationURI == null) ? 0 : isolationURI.hashCode());
      result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
      result = prime * result + (int) (networkId ^ (networkId >>> 32));
      result = prime * result + ((trafficType == null) ? 0 : trafficType.hashCode());
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
      NIC other = (NIC) obj;
      if (IPAddress == null) {
         if (other.IPAddress != null)
            return false;
      } else if (!IPAddress.equals(other.IPAddress))
         return false;
      if (broadcastURI == null) {
         if (other.broadcastURI != null)
            return false;
      } else if (!broadcastURI.equals(other.broadcastURI))
         return false;
      if (gateway == null) {
         if (other.gateway != null)
            return false;
      } else if (!gateway.equals(other.gateway))
         return false;
      if (guestIPType != other.guestIPType)
         return false;
      if (id != other.id)
         return false;
      if (isDefault != other.isDefault)
         return false;
      if (isolationURI == null) {
         if (other.isolationURI != null)
            return false;
      } else if (!isolationURI.equals(other.isolationURI))
         return false;
      if (netmask == null) {
         if (other.netmask != null)
            return false;
      } else if (!netmask.equals(other.netmask))
         return false;
      if (networkId != other.networkId)
         return false;
      if (trafficType != other.trafficType)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", broadcastURI=" + broadcastURI + ", gateway=" + gateway + ", IPAddress=" + IPAddress
            + ", isDefault=" + isDefault + ", isolationURI=" + isolationURI + ", netmask=" + netmask + ", networkId="
            + networkId + ", trafficType=" + trafficType + ", guestIPType=" + guestIPType + "]";
   }

}
