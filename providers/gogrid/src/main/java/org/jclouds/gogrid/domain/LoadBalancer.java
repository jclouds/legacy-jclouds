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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Longs;

/**
 * Class LoadBalancer
 * 
 * @author Oleksiy Yarmula
*/
public class LoadBalancer implements Comparable<LoadBalancer> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromLoadBalancer(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected String name;
      protected String description;
      protected IpPortPair virtualIp;
      protected Set<IpPortPair> realIpList = ImmutableSet.of();
      protected LoadBalancerType type;
      protected LoadBalancerPersistenceType persistence;
      protected LoadBalancerOs os;
      protected LoadBalancerState state;
      protected Option datacenter;
   
      /** 
       * @see LoadBalancer#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see LoadBalancer#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see LoadBalancer#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /** 
       * @see LoadBalancer#getVirtualIp()
       */
      public T virtualIp(IpPortPair virtualIp) {
         this.virtualIp = virtualIp;
         return self();
      }

      /** 
       * @see LoadBalancer#getRealIpList()
       */
      public T realIpList(Set<IpPortPair> realIpList) {
         this.realIpList = ImmutableSet.copyOf(checkNotNull(realIpList, "realIpList"));      
         return self();
      }

      public T realIpList(IpPortPair... in) {
         return realIpList(ImmutableSet.copyOf(in));
      }

      /** 
       * @see LoadBalancer#getType()
       */
      public T type(LoadBalancerType type) {
         this.type = type;
         return self();
      }

      /** 
       * @see LoadBalancer#getPersistence()
       */
      public T persistence(LoadBalancerPersistenceType persistence) {
         this.persistence = persistence;
         return self();
      }

      /** 
       * @see LoadBalancer#getOs()
       */
      public T os(LoadBalancerOs os) {
         this.os = os;
         return self();
      }

      /** 
       * @see LoadBalancer#getState()
       */
      public T state(LoadBalancerState state) {
         this.state = state;
         return self();
      }

      /** 
       * @see LoadBalancer#getDatacenter()
       */
      public T datacenter(Option datacenter) {
         this.datacenter = datacenter;
         return self();
      }

      public LoadBalancer build() {
         return new LoadBalancer(id, name, description, virtualIp, realIpList, type, persistence, os, state, datacenter);
      }
      
      public T fromLoadBalancer(LoadBalancer in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .description(in.getDescription())
                  .virtualIp(in.getVirtualIp())
                  .realIpList(in.getRealIpList())
                  .type(in.getType())
                  .persistence(in.getPersistence())
                  .os(in.getOs())
                  .state(in.getState())
                  .datacenter(in.getDatacenter());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final String name;
   private final String description;
   private final IpPortPair virtualIp;
   private final Set<IpPortPair> realIpList;
   private final LoadBalancerType type;
   private final LoadBalancerPersistenceType persistence;
   private final LoadBalancerOs os;
   private final LoadBalancerState state;
   private final Option datacenter;

   @ConstructorProperties({
      "id", "name", "description", "virtualip", "realiplist", "type", "persistence", "os", "state", "datacenter"
   })
   protected LoadBalancer(long id, String name, @Nullable String description, IpPortPair virtualIp, Set<IpPortPair> realIpList, LoadBalancerType type, LoadBalancerPersistenceType persistence, LoadBalancerOs os, LoadBalancerState state, Option datacenter) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.description = description;
      this.virtualIp = checkNotNull(virtualIp, "virtualIp");
      this.realIpList = ImmutableSet.copyOf(checkNotNull(realIpList, "realIpList"));      
      this.type = checkNotNull(type, "type");
      this.persistence = checkNotNull(persistence, "persistence");
      this.os = checkNotNull(os, "os");
      this.state = checkNotNull(state, "state");
      this.datacenter = checkNotNull(datacenter, "datacenter");
   }

   public long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public IpPortPair getVirtualIp() {
      return this.virtualIp;
   }

   public Set<IpPortPair> getRealIpList() {
      return this.realIpList;
   }

   public LoadBalancerType getType() {
      return this.type;
   }

   public LoadBalancerPersistenceType getPersistence() {
      return this.persistence;
   }

   public LoadBalancerOs getOs() {
      return this.os;
   }

   public LoadBalancerState getState() {
      return this.state;
   }

   public Option getDatacenter() {
      return this.datacenter;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, description, virtualIp, realIpList, type, persistence, os, state, datacenter);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      LoadBalancer that = LoadBalancer.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.virtualIp, that.virtualIp)
               && Objects.equal(this.realIpList, that.realIpList)
               && Objects.equal(this.type, that.type)
               && Objects.equal(this.persistence, that.persistence)
               && Objects.equal(this.os, that.os)
               && Objects.equal(this.state, that.state)
               && Objects.equal(this.datacenter, that.datacenter);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("description", description).add("virtualIp", virtualIp).add("realIpList", realIpList).add("type", type).add("persistence", persistence).add("os", os).add("state", state).add("datacenter", datacenter);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(LoadBalancer o) {
      return Longs.compare(id, o.getId());
   }
}
