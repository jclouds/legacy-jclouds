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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="NetworkAdapterSettingType">
 * @author Jason King
 */
public class NetworkAdapterSetting {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkAdapterSetting(this);
   }

   public static class Builder {

      private NamedResource network;
      private String ipAddress;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.NetworkAdapterSetting#getNetwork
       */
      public Builder network(NamedResource network) {
         this.network = network;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.NetworkAdapterSetting#getIpAddress
       */
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public NetworkAdapterSetting build() {
         return new NetworkAdapterSetting(ipAddress, network);
      }

      public Builder fromNetworkAdapterSetting(NetworkAdapterSetting in) {
         return ipAddress(in.getIpAddress()).network(in.getNetwork());
      }
   }

   @XmlElement(name = "Network", required = false)
   private NamedResource network;

   @XmlElement(name = "IpAddress", required = false)
   private String ipAddress;

   public NetworkAdapterSetting(@Nullable String ipAddress, @Nullable NamedResource network) {
      this.ipAddress = ipAddress;
      this.network = network;
   }

   protected NetworkAdapterSetting() {
       //For JAXB
   }

   @Nullable
   public String getIpAddress() {
       return ipAddress;
   }

   @Nullable
   public NamedResource getNetwork() {
       return network;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NetworkAdapterSetting that = (NetworkAdapterSetting) o;

      if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null)
         return false;
      if (network != null ? !network.equals(that.network) : that.network != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = network != null ? network.hashCode() : 0;
      result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[ipAddress="+ ipAddress +",network="+ network +"]";
   }
}