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
 * <xs:complexType name="DnsSettingsType">
 * @author Jason King
 */
public class DnsSettings {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromDnsSettings(this);
   }

   public static class Builder {

      private String primaryDns;
      private String secondaryDns;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.DnsSettings#getPrimaryDns
       */
      public Builder primaryDns(String primaryDns) {
         this.primaryDns = primaryDns;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.network.DnsSettings#getSecondaryDns
       */
      public Builder secondaryDns(String secondaryDns) {
         this.secondaryDns = secondaryDns;
         return this;
      }

      public DnsSettings build() {
         return new DnsSettings(primaryDns, secondaryDns);
      }

      public Builder fromDnsSettings(DnsSettings in) {
         return primaryDns(in.getPrimaryDns()).secondaryDns(in.getSecondaryDns());
      }
   }

   @XmlElement(name = "PrimaryDns", required = false)
   private String primaryDns;

   @XmlElement(name = "SecondaryDns", required = false)
   private String secondaryDns;

   public DnsSettings(@Nullable String primaryDns, @Nullable String secondaryDns) {
      this.primaryDns = primaryDns;
      this.secondaryDns = secondaryDns;
   }

   protected DnsSettings() {
       //For JAXB
   }

   @Nullable
   public String getPrimaryDns() {
       return primaryDns;
   }

   @Nullable
   public String getSecondaryDns() {
       return secondaryDns;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DnsSettings that = (DnsSettings) o;

      if (primaryDns != null ? !primaryDns.equals(that.primaryDns) : that.primaryDns != null)
         return false;
      if (secondaryDns != null ? !secondaryDns.equals(that.secondaryDns) : that.secondaryDns != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = primaryDns != null ? primaryDns.hashCode() : 0;
      result = 31 * result + (secondaryDns != null ? secondaryDns.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[primaryDns="+ primaryDns +",secondaryDns="+ secondaryDns +"]";
   }
}