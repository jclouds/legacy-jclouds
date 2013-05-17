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

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a CloudStack Cluster.
 *
 * @author Richard Downer
 */
public class Cluster implements Comparable<Cluster> {

   /**
    */
   public static enum ManagedState {
      MANAGED,
      PREPARE_UNMANAGED,
      UNMANAGED,
      PREPARE_UNMANAGED_ERROR,
      UNRECOGNIZED;

      public static ManagedState fromValue(String value) {
         try {
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, value));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

      @Override
      public String toString() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCluster(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected AllocationState allocationState;
      protected Host.ClusterType clusterType;
      protected String hypervisor;
      protected Cluster.ManagedState managedState;
      protected String name;
      protected String podId;
      protected String podName;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see Cluster#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Cluster#getAllocationState()
       */
      public T allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return self();
      }

      /**
       * @see Cluster#getClusterType()
       */
      public T clusterType(Host.ClusterType clusterType) {
         this.clusterType = clusterType;
         return self();
      }

      /**
       * @see Cluster#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see Cluster#getManagedState()
       */
      public T managedState(Cluster.ManagedState managedState) {
         this.managedState = managedState;
         return self();
      }

      /**
       * @see Cluster#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Cluster#getPodId()
       */
      public T podId(String podId) {
         this.podId = podId;
         return self();
      }

      /**
       * @see Cluster#getPodName()
       */
      public T podName(String podName) {
         this.podName = podName;
         return self();
      }

      /**
       * @see Cluster#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Cluster#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      public Cluster build() {
         return new Cluster(id, allocationState, clusterType, hypervisor, managedState, name, podId, podName, zoneId, zoneName);
      }

      public T fromCluster(Cluster in) {
         return this
               .id(in.getId())
               .allocationState(in.getAllocationState())
               .clusterType(in.getClusterType())
               .hypervisor(in.getHypervisor())
               .managedState(in.getManagedState())
               .name(in.getName())
               .podId(in.getPodId())
               .podName(in.getPodName())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final AllocationState allocationState;
   private final Host.ClusterType clusterType;
   private final String hypervisor;
   private final Cluster.ManagedState managedState;
   private final String name;
   private final String podId;
   private final String podName;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "id", "allocationstate", "clustertype", "hypervisortype", "managedstate", "name", "podid", "podname", "zoneid", "zonename"
   })
   protected Cluster(String id, @Nullable AllocationState allocationState, @Nullable Host.ClusterType clusterType,
                     @Nullable String hypervisor, @Nullable Cluster.ManagedState managedState, @Nullable String name,
                     @Nullable String podId, @Nullable String podName, @Nullable String zoneId, @Nullable String zoneName) {
      this.id = checkNotNull(id, "id");
      this.allocationState = allocationState;
      this.clusterType = clusterType;
      this.hypervisor = hypervisor;
      this.managedState = managedState;
      this.name = name;
      this.podId = podId;
      this.podName = podName;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public AllocationState getAllocationState() {
      return this.allocationState;
   }

   @Nullable
   public Host.ClusterType getClusterType() {
      return this.clusterType;
   }

   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   @Nullable
   public Cluster.ManagedState getManagedState() {
      return this.managedState;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getPodId() {
      return this.podId;
   }

   @Nullable
   public String getPodName() {
      return this.podName;
   }

   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, allocationState, clusterType, hypervisor, managedState, name, podId, podName, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Cluster that = Cluster.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.allocationState, that.allocationState)
            && Objects.equal(this.clusterType, that.clusterType)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.managedState, that.managedState)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.podId, that.podId)
            && Objects.equal(this.podName, that.podName)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("allocationState", allocationState).add("clusterType", clusterType).add("hypervisor", hypervisor)
            .add("managedState", managedState).add("name", name).add("podId", podId).add("podName", podName).add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Cluster other) {
      return this.id.compareTo(other.id);
   }
}
