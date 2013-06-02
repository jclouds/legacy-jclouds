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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
public class ZoneSecurityGroupNamePortsCidrs extends ZoneAndName {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromZoneSecurityGroupNamePortsCidrs(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String zoneId;
      protected String name;
      protected Set<Integer> ports = ImmutableSet.of();
      protected Set<String> cidrs = ImmutableSet.of();

      /**
       * @see ZoneSecurityGroupNamePortsCidrs#getZone()
       */
      public T zone(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see ZoneSecurityGroupNamePortsCidrs#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }
      
      /**
       * @see ZoneSecurityGroupNamePortsCidrs#getPorts()
       */
      public T ports(Set<Integer> ports) {
         this.ports = ImmutableSet.copyOf(checkNotNull(ports, "ports"));
         return self();
      }

      public T ports(Integer... in) {
         return ports(ImmutableSet.copyOf(in));
      }
      
      /**
       * @see ZoneSecurityGroupNamePortsCidrs#getCidrs()
       */
      public T cidrs(Set<String> cidrs) {
         this.cidrs = ImmutableSet.copyOf(checkNotNull(cidrs, "cidrs"));
         return self();
      }

      public T cidrs(String... in) {
         return cidrs(ImmutableSet.copyOf(in));
      }

      public ZoneSecurityGroupNamePortsCidrs build() {
         return new ZoneSecurityGroupNamePortsCidrs(zoneId, name, ports, cidrs);
      }

      public T fromZoneSecurityGroupNamePortsCidrs(ZoneSecurityGroupNamePortsCidrs in) {
         return this.zone(in.getZone())
            .name(in.getName())
            .ports(in.getPorts())
            .cidrs(in.getCidrs());
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   
   private final Set<Integer> ports;
   private final Set<String> cidrs;

   protected ZoneSecurityGroupNamePortsCidrs(String zoneId, String name, Set<Integer> ports,
                                             Set<String> cidrs) {
      super(zoneId, name);
      this.ports = ports == null ? ImmutableSet.<Integer>of() : ImmutableSet.copyOf(ports);
      this.cidrs = cidrs == null ? ImmutableSet.<String>of() : ImmutableSet.copyOf(cidrs);
   }

   /**
    *
    * @return the set of ports to open in the security group
    */
   public Set<Integer> getPorts() {
      return ports;
   }
   
   /**
    *
    * @return the set of cidrs to give access to the open ports in the security group
    */
   public Set<String> getCidrs() {
      return cidrs;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneId, name, ports, cidrs);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ZoneSecurityGroupNamePortsCidrs that = ZoneSecurityGroupNamePortsCidrs.class.cast(obj);
      return Objects.equal(this.zoneId, that.zoneId)
         && Objects.equal(this.name, that.name)
         && Objects.equal(this.ports, that.ports)
         && Objects.equal(this.cidrs, that.cidrs);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("zoneId", zoneId).add("name", name).add("ports", ports).add("cidrs", cidrs);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}

   