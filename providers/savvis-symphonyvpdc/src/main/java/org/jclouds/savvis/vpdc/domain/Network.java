/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Various network features such NAT Public IP, Gateway and Netmask.
 * 
 * @author Adrian Cole
 */
public class Network extends ResourceImpl {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends ResourceImpl.Builder {
      private String gateway;
      private String netmask;
      private Map<String, String> internalToExternalNATRules = Maps.newLinkedHashMap();

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder internalToExternalNATRule(String internalIP, String externalIP) {
         this.internalToExternalNATRules.put(checkNotNull(internalIP, "internalIP"),
               checkNotNull(externalIP, "externalIP"));
         return this;
      }

      public Builder internalToExternalNATRules(Map<String, String> internalToExternalNATRules) {
         this.internalToExternalNATRules.putAll(checkNotNull(internalToExternalNATRules, "internalToExternalNATRules"));
         return this;
      }

      @Override
      public Network build() {
         return new Network(id, name, type, href, gateway, netmask, internalToExternalNATRules);
      }

      public static Builder fromNetwork(Network in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
               .gateway(in.getGateway()).internalToExternalNATRules(in.getInternalToExternalNATRules())
               .netmask(in.getNetmask());
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

   }

   @Nullable
   private final String gateway;
   private final String netmask;
   private final Map<String, String> internalToExternalNATRules;

   public Network(String id, String name, String type, URI href, @Nullable String gateway, String netmask,
         Map<String, String> internalToExternalNATRules) {
      super(id, name, type, href);
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

   @Override
   public Builder toBuilder() {
      return Builder.fromNetwork(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", gateway=" + gateway
            + ", netmask=" + netmask + ", internalToExternalNATRules=" + internalToExternalNATRules + "]";
   }

}