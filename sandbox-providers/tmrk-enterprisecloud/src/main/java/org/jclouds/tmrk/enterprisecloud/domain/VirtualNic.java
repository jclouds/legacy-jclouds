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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.javax.annotation.Nullable;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="VirtualNic">
 * @author Jason King
 */
public class VirtualNic {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVirtualNic(this);
   }

   public static class Builder {

      private String name;
      private String macAddress;
      private int unitNumber;
      private NetworkReference network;

      /**
       * @see VirtualNic#getName
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see VirtualNic#getMacAddress
       */
      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      /**
       * @see VirtualNic#getUnitNumber
       */
      public Builder unitNumber(int unitNumber) {
         this.unitNumber = unitNumber;
         return this;
      }

      /**
       * @see VirtualNic#getNetwork
       */
      public Builder network(NetworkReference network) {
         this.network = network;
         return this;
      }

      public VirtualNic build() {
         return new VirtualNic(name, macAddress, unitNumber, network);
      }

      public Builder fromVirtualNic(VirtualNic in) {
         return name(in.getName())
                .macAddress(in.getMacAddress())
                .unitNumber(in.getUnitNumber())
                .network(in.getNetwork());
      }
   }

   @XmlElement(name = "Name")
   private String name;

   @XmlElement(name = "MacAddress")
   private String macAddress;

   @XmlElement(name = "UnitNumber")
   private int unitNumber;

   @XmlElement(name = "Network")
   private NetworkReference network;

   public VirtualNic(@Nullable String name, @Nullable String macAddress, int unitNumber, @Nullable NetworkReference network) {
      this.name = name;
      this.macAddress = macAddress;
      this.unitNumber = unitNumber;
      this.network = network;
   }

   protected VirtualNic() {
       //For JAXB
   }

   @Nullable
   public String getName() {
       return name;
   }

   @Nullable
   public String getMacAddress() {
       return macAddress;
   }

   public int getUnitNumber() {
       return unitNumber;
   }

   @Nullable
   public NetworkReference getNetwork() {
      return network;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualNic that = (VirtualNic) o;

        if (unitNumber != that.unitNumber) return false;
        if (macAddress != null ? !macAddress.equals(that.macAddress) : that.macAddress != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (network != null ? !network.equals(that.network) : that.network != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + unitNumber;
        result = 31 * result + (network != null ? network.hashCode() : 0);
        return result;
    }

    @Override
   public String toString() {
      return "[name="+name+",macAddress="+macAddress+",unitNumber="+unitNumber+",network="+network+"]";
   }
}