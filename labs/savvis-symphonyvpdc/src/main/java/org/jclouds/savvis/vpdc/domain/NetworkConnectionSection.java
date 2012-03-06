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
package org.jclouds.savvis.vpdc.domain;

import org.jclouds.ovf.Section;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkConnectionSection extends Section<NetworkConnectionSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromNetworkConectionSection(this);
   }

   public static class Builder extends Section.Builder<NetworkConnectionSection> {
      private String network;
      private String ipAddress;

      public Builder network(String network) {
         this.network = network;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public NetworkConnectionSection build() {
         return new NetworkConnectionSection(info, network, ipAddress);
      }

      public Builder fromNetworkConectionSection(NetworkConnectionSection in) {
         return fromSection(in).network(in.getNetwork()).ipAddress(in.getIpAddress());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<NetworkConnectionSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   private final String network;
   private final String ipAddress;

   public NetworkConnectionSection(String info, String network, String ipAddress) {
      super(info);
      this.network = network;
      this.ipAddress = ipAddress;
   }

   public String getNetwork() {
      return network;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((network == null) ? 0 : network.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      NetworkConnectionSection other = (NetworkConnectionSection) obj;
      if (network == null) {
         if (other.network != null)
            return false;
      } else if (!network.equals(other.network))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[info=%s, network=%s, ipAddress=%s]", info, network, ipAddress);
   }

}