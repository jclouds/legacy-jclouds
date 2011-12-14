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

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="NetworkSettingsType">
 * @author Jason King
 */
public class NetworkSettings {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkAdapterSetting(this);
   }

   public static class Builder {

      private NetworkAdapterSettings networkAdapterSettings;
      private DnsSettings dnsSettings;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.NetworkSettings#getNetworkAdapterSettings
       */
      public Builder networkAdapterSettings(NetworkAdapterSettings networkAdapterSettings) {
         this.networkAdapterSettings = networkAdapterSettings;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.NetworkSettings#getDnsSettings
       */
      public Builder dnsSettings(DnsSettings dnsSettings) {
         this.dnsSettings = dnsSettings;
         return this;
      }

      public NetworkSettings build() {
         return new NetworkSettings(networkAdapterSettings, dnsSettings);
      }

      public Builder fromNetworkAdapterSetting(NetworkSettings in) {
         return networkAdapterSettings(in.getNetworkAdapterSettings()).dnsSettings(in.getDnsSettings());
      }
   }

   @XmlElement(name = "NetworkAdapterSettings", required = false)
   private NetworkAdapterSettings networkAdapterSettings;

   @XmlElement(name = "DnsSettings", required = false)
   private DnsSettings dnsSettings;

   public NetworkSettings(@Nullable NetworkAdapterSettings networkAdapterSettings, @Nullable DnsSettings dnsSettings) {
      this.networkAdapterSettings = networkAdapterSettings;
      this.dnsSettings = dnsSettings;
   }

   protected NetworkSettings() {
       //For JAXB
   }

   public NetworkAdapterSettings getNetworkAdapterSettings() {
      return networkAdapterSettings;
   }

   public DnsSettings getDnsSettings() {
      return dnsSettings;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      NetworkSettings that = (NetworkSettings) o;

      if (dnsSettings != null ? !dnsSettings.equals(that.dnsSettings) : that.dnsSettings != null)
         return false;
      if (networkAdapterSettings != null ? !networkAdapterSettings.equals(that.networkAdapterSettings) : that.networkAdapterSettings != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = networkAdapterSettings != null ? networkAdapterSettings.hashCode() : 0;
      result = 31 * result + (dnsSettings != null ? dnsSettings.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[networkAdapterSettings="+ networkAdapterSettings +",dnsSettings="+ dnsSettings +"]";
   }
}