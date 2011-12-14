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
import org.jclouds.tmrk.enterprisecloud.domain.internal.AnonymousResource;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="LinuxCustomizationType">
 * @author Jason King
 */
public class LinuxCustomization {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromLinuxCustomization(this);
   }

   public static class Builder {

      private NetworkSettings networkSettings;
      private AnonymousResource sshKey;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.LinuxCustomization#getNetworkSettings
       */
      public Builder networkSettings(NetworkSettings networkSettings) {
         this.networkSettings = networkSettings;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.LinuxCustomization#getSshKey
       */
      public Builder sshKey(AnonymousResource sshKey) {
         this.sshKey = sshKey;
         return this;
      }

      public LinuxCustomization build() {
         return new LinuxCustomization(networkSettings, sshKey);
      }

      public Builder fromLinuxCustomization(LinuxCustomization in) {
         return networkSettings(in.getNetworkSettings()).sshKey(in.getSshKey());
      }
   }

   @XmlElement(name = "NetworkSettings", required = false)
   private NetworkSettings networkSettings;

   @XmlElement(name = "SshKey", required = false)
   private AnonymousResource sshKey;

   public LinuxCustomization(@Nullable NetworkSettings networkSettings, @Nullable AnonymousResource sshKey) {
      this.networkSettings = networkSettings;
      this.sshKey = sshKey;
   }

   private LinuxCustomization() {
       //For JAXB
   }

   public NetworkSettings getNetworkSettings() {
      return networkSettings;
   }

   public AnonymousResource getSshKey() {
      return sshKey;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      LinuxCustomization that = (LinuxCustomization) o;

      if (networkSettings != null ? !networkSettings.equals(that.networkSettings) : that.networkSettings != null)
         return false;
      if (sshKey != null ? !sshKey.equals(that.sshKey) : that.sshKey != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = networkSettings != null ? networkSettings.hashCode() : 0;
      result = 31 * result + (sshKey != null ? sshKey.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[networkSettings="+ networkSettings +",sshKey="+ sshKey +"]";
   }
}