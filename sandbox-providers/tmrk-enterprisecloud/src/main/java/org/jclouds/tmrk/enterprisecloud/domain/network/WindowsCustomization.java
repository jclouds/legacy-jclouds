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
 * <xs:complexType name="WindowsCustomizationType">
 * @author Jason King
 */
public class WindowsCustomization {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLinuxCustomization(this);
   }

   public static class Builder {

      private NetworkSettings networkSettings;
      private String password;
      private String licenseKey;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.WindowsCustomization#getNetworkSettings
       */
      public Builder networkSettings(NetworkSettings networkSettings) {
         this.networkSettings = networkSettings;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.WindowsCustomization#getPassword
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }
      
      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.WindowsCustomization#getLicenseKey
       */
      public Builder licenseKey(String licenseKey) {
         this.licenseKey = licenseKey;
         return this;
      }
      
      public WindowsCustomization build() {
         return new WindowsCustomization(networkSettings, password, licenseKey);
      }

      public Builder fromLinuxCustomization(WindowsCustomization in) {
         return networkSettings(in.getNetworkSettings()).password(in.getPassword()).licenseKey(in.getLicenseKey());
      }
   }

   @XmlElement(name = "NetworkSettings", required = false)
   private NetworkSettings networkSettings;

   @XmlElement(name = "Password", required = false)
   private String password;

   @XmlElement(name = "LicenseKey", required = false)
   private String licenseKey;

   public WindowsCustomization(@Nullable NetworkSettings networkSettings, @Nullable String password, @Nullable String licenseKey) {
      this.networkSettings = networkSettings;
      this.password = password;
      this.licenseKey = licenseKey;
   }

   private WindowsCustomization() {
       //For JAXB
   }

   public NetworkSettings getNetworkSettings() {
      return networkSettings;
   }

   public String getPassword() {
      return password;
   }

   public String getLicenseKey() {
      return licenseKey;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      WindowsCustomization that = (WindowsCustomization) o;

      if (licenseKey != null ? !licenseKey.equals(that.licenseKey) : that.licenseKey != null)
         return false;
      if (networkSettings != null ? !networkSettings.equals(that.networkSettings) : that.networkSettings != null)
         return false;
      if (password != null ? !password.equals(that.password) : that.password != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = networkSettings != null ? networkSettings.hashCode() : 0;
      result = 31 * result + (password != null ? password.hashCode() : 0);
      result = 31 * result + (licenseKey != null ? licenseKey.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      final String pw = password==null ? null : "*******";
      final String lic = licenseKey==null ? null : "*******";
      return "[networkSettings="+networkSettings+",password="+pw+",licenseKey="+lic+"]";
   }
}