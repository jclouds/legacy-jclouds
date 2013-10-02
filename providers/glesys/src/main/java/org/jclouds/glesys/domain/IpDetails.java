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
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * Represents detailed information about an IP address.
 */
public class IpDetails {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromIpDetails(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String datacenter;
      protected int ipversion;
      protected String ptr;
      protected String platform;
      protected String address;
      protected String netmask;
      protected String broadcast;
      protected String gateway;
      protected List<String> nameServers = ImmutableList.of();
      protected String serverId;
      protected Cost cost;
      protected boolean reserved;

      /**
       * @see IpDetails#getDatacenter()
       */
      public T datacenter(String datacenter) {
         this.datacenter = checkNotNull(datacenter, "datacenter");
         return self();
      }

      protected T version(int ipversion) {
         this.ipversion = ipversion;
         return self();
      }

      /*
      * @see IpDetails#getVersion()
      */
      public T version4() {
         return version(4);
      }

      /*
      * @see IpDetails#getVersion()
      */
      public T version6() {
         return version(6);
      }

      /**
       * @see IpDetails#getPtr()
       */
      public T ptr(String ptr) {
         this.ptr = checkNotNull(ptr, "ptr");
         return self();
      }

      /**
       * @see IpDetails#getPlatform()
       */
      public T platform(String platform) {
         this.platform = checkNotNull(platform, "platform");
         return self();
      }

      /**
       * @see IpDetails#getAddress()
       */
      public T address(String address) {
         this.address = address;
         return self();
      }

      /**
       * @see IpDetails#getNetmask()
       */
      public T netmask(String netmask) {
         this.netmask = netmask;
         return self();
      }

      /**
       * @see IpDetails#getBroadcast()
       */
      public T broadcast(String broadcast) {
         this.broadcast = broadcast;
         return self();
      }

      /**
       * @see IpDetails#getGateway()
       */
      public T gateway(String gateway) {
         this.gateway = gateway;
         return self();
      }

      /**
       * @see IpDetails#getNameServers()
       */
      public T nameServers(List<String> nameservers) {
         this.nameServers = ImmutableList.copyOf(checkNotNull(nameservers, "nameServers"));
         return self();
      }

      public T nameServers(String... in) {
         return nameServers(ImmutableList.copyOf(in));
      }

      /**
       * @see IpDetails#getServerId()
       */
      public T serverId(String serverId) {
         this.serverId = serverId;
         return self();
      }

      /**
       * @see IpDetails#getCost()
       */
      public T cost(Cost cost) {
         this.cost = cost;
         return self();
      }

      /**
       * @see IpDetails#isReserved()
       */
      public T reserved(boolean reserved) {
         this.reserved = reserved;
         return self();
      }

      public IpDetails build() {
         return new IpDetails(datacenter, ipversion, ptr, platform, address, netmask, broadcast, gateway, nameServers,
               serverId, cost, new GleSYSBoolean(reserved));
      }

      public T fromIpDetails(IpDetails in) {
         return this.datacenter(in.getDatacenter())
               .version(in.getVersion())
               .ptr(in.getPtr())
               .platform(in.getPlatform())
               .address(in.getAddress())
               .netmask(in.getNetmask())
               .broadcast(in.getBroadcast())
               .gateway(in.getGateway())
               .nameServers(in.getNameServers())
               .serverId(in.getServerId())
               .cost(in.getCost())
               .reserved(in.isReserved());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String datacenter;
   private final int version;
   private final String ptr;
   private final String platform;
   private final String address;
   private final String netmask;
   private final String broadcast;
   private final String gateway;
   private final List<String> nameServers;
   private final String serverId;
   private final Cost cost;
   private final boolean reserved;

   @ConstructorProperties({
         "datacenter", "ipversion", "ptr", "platform", "ipaddress", "netmask", "broadcast", "gateway", "nameservers",
         "serverid", "cost", "reserved"
   })
   protected IpDetails(String datacenter, int version, String ptr, String platform, String address,
                       @Nullable String netmask, @Nullable String broadcast, @Nullable String gateway,
                       List<String> nameServers, @Nullable String serverId, Cost cost, GleSYSBoolean reserved) {
      this.datacenter = checkNotNull(datacenter, "datacenter");
      this.version = checkNotNull(version, "version");
      this.ptr = checkNotNull(ptr, "ptr");
      this.platform = checkNotNull(platform, "platform");
      this.address = address;
      this.netmask = netmask;
      this.broadcast = broadcast;
      this.gateway = gateway;
      this.nameServers = ImmutableList.copyOf(nameServers);
      this.serverId = serverId;
      this.cost = checkNotNull(cost, "cost");
      this.reserved = checkNotNull(reserved, "reserved").getValue();
   }

   public String getDatacenter() {
      return this.datacenter;
   }

   /**
    * @return the IP version, ex. 4
    */
   public int getVersion() {
      return this.version;
   }

   public String getPtr() {
      return this.ptr;
   }

   public String getPlatform() {
      return this.platform;
   }

   public String getAddress() {
      return this.address;
   }

   @Nullable
   public String getNetmask() {
      return this.netmask;
   }

   @Nullable
   public String getBroadcast() {
      return this.broadcast;
   }

   @Nullable
   public String getGateway() {
      return this.gateway;
   }

   public List<String> getNameServers() {
      return this.nameServers;
   }

   @Nullable
   public String getServerId() {
      return serverId;
   }

   public Cost getCost() {
      return cost;
   }

   public boolean isReserved() {
      return reserved;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(datacenter, version, ptr, platform, address, netmask, broadcast, gateway, nameServers,
            serverId, cost, reserved);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      IpDetails that = IpDetails.class.cast(obj);
      return Objects.equal(this.datacenter, that.datacenter)
            && Objects.equal(this.version, that.version)
            && Objects.equal(this.ptr, that.ptr)
            && Objects.equal(this.platform, that.platform)
            && Objects.equal(this.address, that.address)
            && Objects.equal(this.netmask, that.netmask)
            && Objects.equal(this.broadcast, that.broadcast)
            && Objects.equal(this.gateway, that.gateway)
            && Objects.equal(this.nameServers, that.nameServers)
            && Objects.equal(this.serverId, that.serverId)
            && Objects.equal(this.cost, that.cost)
            && Objects.equal(this.reserved, that.reserved);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("datacenter", datacenter).add("ipversion", version).add("ptr", ptr).add("platform", platform)
            .add("address", address).add("netmask", netmask).add("broadcast", broadcast).add("gateway", gateway)
            .add("nameServers", nameServers).add("serverId", serverId).add("cost", cost).add("reserved", reserved);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
