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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.ovf.Section;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkConfigSection extends Section<NetworkConfigSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromNetworkConfigSection(this);
   }

   public static class Builder extends Section.Builder<NetworkConfigSection> {
      private String network;
      private String fenceMode;
      private Boolean dhcp;
      private String gateway;
      private String netmask;
      private Map<String, String> internalToExternalNATRules = Maps.newLinkedHashMap();

      public Builder network(String network) {
         this.network = network;
         return this;
      }

      public Builder fenceMode(String fenceMode) {
         this.fenceMode = fenceMode;
         return this;
      }

      public Builder dhcp(Boolean dhcp) {
         this.dhcp = dhcp;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder internalToExternalNATRule(String internalIP, String externalIP) {
         this.internalToExternalNATRules.put(checkNotNull(internalIP, "internalIP"), checkNotNull(externalIP,
                  "externalIP"));
         return this;
      }

      public Builder internalToExternalNATRules(Map<String, String> internalToExternalNATRules) {
         this.internalToExternalNATRules.putAll(checkNotNull(internalToExternalNATRules, "internalToExternalNATRules"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public NetworkConfigSection build() {
         return new NetworkConfigSection(info, network, fenceMode, dhcp, gateway, netmask, internalToExternalNATRules);
      }

      public Builder fromNetworkConfigSection(NetworkConfigSection in) {
         return fromSection(in).network(in.getNetwork()).fenceMode(in.getFenceMode()).dhcp(in.getDhcp()).gateway(
                  in.getGateway()).netmask(in.getNetmask()).internalToExternalNATRules(
                  in.getInternalToExternalNATRules());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(Section<NetworkConfigSection> in) {
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
   private final String fenceMode;
   private final Boolean dhcp;
   private final String gateway;
   private final String netmask;
   private final Map<String, String> internalToExternalNATRules;

   public NetworkConfigSection(String info, String network, String fenceMode, Boolean dhcp, String gateway,
            String netmask, Map<String, String> internalToExternalNATRules) {
      super(info);
      this.network = network;
      this.fenceMode = fenceMode;
      this.dhcp = dhcp;
      this.gateway = gateway;
      this.netmask = netmask;
      this.internalToExternalNATRules = ImmutableMap.copyOf(checkNotNull(internalToExternalNATRules,
               "internalToExternalNATRules"));
   }

   /**
    * @return IP of the network's gateway
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * @return IP of the network's netmask
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * @return map of internal to external ip when it has any nat1to1 enabled deployed VApp
    */
   public Map<String, String> getInternalToExternalNATRules() {
      return internalToExternalNATRules;
   }

   public String getNetwork() {
      return network;
   }

   public String getFenceMode() {
      return fenceMode;
   }

   public Boolean getDhcp() {
      return dhcp;
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
      NetworkConfigSection other = (NetworkConfigSection) obj;
      if (network == null) {
         if (other.network != null)
            return false;
      } else if (!network.equals(other.network))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format(
               "[info=%s, network=%s, dhcp=%s, fenceMode=%s, gateway=%s, internalToExternalNATRules=%s, netmask=%s]",
               info, network, dhcp, fenceMode, gateway, internalToExternalNATRules, netmask);
   }

}