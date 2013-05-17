/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Objects.equal;

import com.google.common.base.Objects;

/**
 * @author Seshu Pasam
 */
public class NetworkAdapter implements Comparable<NetworkAdapter> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromNetworkAdapter(this);
   }

   public static class Builder {
      private String macAddress;
      private String name;
      private Subnet subnet;

      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder subnet(Subnet subnet) {
         this.subnet = subnet;
         return this;
      }

      public NetworkAdapter build() {
         return new NetworkAdapter(macAddress, name, subnet);
      }

      public Builder fromNetworkAdapter(NetworkAdapter in) {
         return macAddress(in.getMacAddress()).name(in.getName()).subnet(in.getSubnet());
      }

   }

   private final String macAddress;
   private final String name;
   private final Subnet subnet;

   public NetworkAdapter(String macAddress, String name, Subnet subnet) {
      this.macAddress = macAddress;
      this.name = name;
      this.subnet = subnet;
   }

   public int compareTo(NetworkAdapter that) {
      return (this == that) ? 0 : getMacAddress().compareTo(that.getMacAddress());
   }

   public String getMacAddress() {
      return macAddress;
   }

   public String getName() {
      return name;
   }

   public Subnet getSubnet() {
      return subnet;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkAdapter that = NetworkAdapter.class.cast(o);
      return equal(this.macAddress, that.macAddress) && equal(this.name, that.name) && equal(this.subnet, that.subnet);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(macAddress, name, subnet);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("macAddress", macAddress).add("name", name).add("subnet", subnet)
               .toString();
   }
}
