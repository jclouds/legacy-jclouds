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

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a Pod in CloudStack.
 *
 * @author Richard Downer
 */
public class Pod implements Comparable<Pod> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromPod(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String zoneId;
      protected String zoneName;
      protected String gateway;
      protected String netmask;
      protected String startIp;
      protected String endIp;
      protected AllocationState allocationState;

      /**
       * @see Pod#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Pod#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Pod#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Pod#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      /**
       * @see Pod#getGateway()
       */
      public T gateway(String gateway) {
         this.gateway = gateway;
         return self();
      }

      /**
       * @see Pod#getNetmask()
       */
      public T netmask(String netmask) {
         this.netmask = netmask;
         return self();
      }

      /**
       * @see Pod#getStartIp()
       */
      public T startIp(String startIp) {
         this.startIp = startIp;
         return self();
      }

      /**
       * @see Pod#getEndIp()
       */
      public T endIp(String endIp) {
         this.endIp = endIp;
         return self();
      }

      /**
       * @see Pod#getAllocationState()
       */
      public T allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return self();
      }

      public Pod build() {
         return new Pod(id, name, zoneId, zoneName, gateway, netmask, startIp, endIp, allocationState);
      }

      public T fromPod(Pod in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName())
               .gateway(in.getGateway())
               .netmask(in.getNetmask())
               .startIp(in.getStartIp())
               .endIp(in.getEndIp())
               .allocationState(in.getAllocationState());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String zoneId;
   private final String zoneName;
   private final String gateway;
   private final String netmask;
   private final String startIp;
   private final String endIp;
   private final AllocationState allocationState;

   @ConstructorProperties({
         "id", "name", "zoneid", "zonename", "gateway", "netmask", "startip", "endip", "allocationstate"
   })
   protected Pod(String id, @Nullable String name, @Nullable String zoneId, @Nullable String zoneName, @Nullable String gateway, @Nullable String netmask, @Nullable String startIp, @Nullable String endIp, @Nullable AllocationState allocationState) {
      this.id = checkNotNull(id, "id");
      this.name = name;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
      this.gateway = gateway;
      this.netmask = netmask;
      this.startIp = startIp;
      this.endIp = endIp;
      this.allocationState = allocationState;
   }

   /**
    * @return id the ID of the Pod
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return name the name of the Pod
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return zoneId the Zone ID of the Pod
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return zoneName the Zone name of the Pod
    */
   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   /**
    * @return gateway the gateway of the Pod
    */
   @Nullable
   public String getGateway() {
      return this.gateway;
   }

   /**
    * @return netmask the netmask of the Pod
    */
   @Nullable
   public String getNetmask() {
      return this.netmask;
   }

   /**
    * @return startIp the starting IP for the Pod
    */
   @Nullable
   public String getStartIp() {
      return this.startIp;
   }

   /**
    * @return endIp the ending IP for the Pod
    */
   @Nullable
   public String getEndIp() {
      return this.endIp;
   }

   /**
    * @return the allocation state of the cluster
    */
   @Nullable
   public AllocationState getAllocationState() {
      return this.allocationState;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, zoneId, zoneName, gateway, netmask, startIp, endIp, allocationState);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Pod that = Pod.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName)
            && Objects.equal(this.gateway, that.gateway)
            && Objects.equal(this.netmask, that.netmask)
            && Objects.equal(this.startIp, that.startIp)
            && Objects.equal(this.endIp, that.endIp)
            && Objects.equal(this.allocationState, that.allocationState);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("zoneId", zoneId).add("zoneName", zoneName).add("gateway", gateway).add("netmask", netmask).add("startIp", startIp).add("endIp", endIp).add("allocationState", allocationState);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Pod other) {
      return this.id.compareTo(other.id);
   }
}
