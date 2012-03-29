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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a CloudStack Cluster.
 *
 * @author Richard Downer
 */
public class Cluster implements Comparable<Cluster> {

   public enum ManagedState {
      MANAGED,
      PREPARE_UNMANAGED,
      UNMANAGED,
      PREPARE_UNMANAGED_ERROR,
      UNRECOGNIZED;

      public static ManagedState fromValue(String value) {
         try{
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

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private AllocationState allocationState;
      private Host.ClusterType clusterType;
      private String hypervisor;
      private ManagedState managedState;
      private String name;
      private long podId;
      private String podName;
      private long zoneId;
      private String zoneName;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return this;
      }

      public Builder clusterType(Host.ClusterType clusterType) {
         this.clusterType = clusterType;
         return this;
      }

      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Builder managedState(ManagedState managedState) {
         this.managedState = managedState;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder podId(long podId) {
         this.podId = podId;
         return this;
      }

      public Builder podName(String podName) {
         this.podName = podName;
         return this;
      }

      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      public Cluster build() {
         return new Cluster(id, allocationState, clusterType, hypervisor, managedState, name, podId, podName, zoneId, zoneName);
      }
   }

   private long id;
   @SerializedName("allocationstate") private AllocationState allocationState;
   @SerializedName("clustertype") private Host.ClusterType clusterType;
   @SerializedName("hypervisortype") private String hypervisor;
   @SerializedName("managedstate") private ManagedState managedState;
   private String name;
   @SerializedName("podid") private long podId;
   @SerializedName("podname") private String podName;
   @SerializedName("zoneid") private long zoneId;
   @SerializedName("zonename") private String zoneName;

   // Just for the serializer
   Cluster() {}

   public Cluster(long id, AllocationState allocationState, Host.ClusterType clusterType, String hypervisor, ManagedState managedState, String name, long podId, String podName, long zoneId, String zoneName) {
      this.id = id;
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

   public long getId() {
      return id;
   }

   public AllocationState getAllocationState() {
      return allocationState;
   }

   public Host.ClusterType getClusterType() {
      return clusterType;
   }

   public String getHypervisor() {
      return hypervisor;
   }

   public ManagedState getManagedState() {
      return managedState;
   }

   public String getName() {
      return name;
   }

   public long getPodId() {
      return podId;
   }

   public String getPodName() {
      return podName;
   }

   public long getZoneId() {
      return zoneId;
   }

   public String getZoneName() {
      return zoneName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Cluster cluster = (Cluster) o;

      if (id != cluster.id) return false;
      if (podId != cluster.podId) return false;
      if (zoneId != cluster.zoneId) return false;
      if (allocationState != cluster.allocationState) return false;
      if (clusterType != cluster.clusterType) return false;
      if (hypervisor != null ? !hypervisor.equals(cluster.hypervisor) : cluster.hypervisor != null) return false;
      if (managedState != cluster.managedState) return false;
      if (name != null ? !name.equals(cluster.name) : cluster.name != null) return false;
      if (podName != null ? !podName.equals(cluster.podName) : cluster.podName != null) return false;
      if (zoneName != null ? !zoneName.equals(cluster.zoneName) : cluster.zoneName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (allocationState != null ? allocationState.hashCode() : 0);
      result = 31 * result + (clusterType != null ? clusterType.hashCode() : 0);
      result = 31 * result + (hypervisor != null ? hypervisor.hashCode() : 0);
      result = 31 * result + (managedState != null ? managedState.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (int) (podId ^ (podId >>> 32));
      result = 31 * result + (podName != null ? podName.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Cluster{" +
         "id=" + id +
         ", allocationState=" + allocationState +
         ", clusterType=" + clusterType +
         ", hypervisor='" + hypervisor + '\'' +
         ", managedState=" + managedState +
         ", name='" + name + '\'' +
         ", podId=" + podId +
         ", podName='" + podName + '\'' +
         ", zoneId=" + zoneId +
         ", zoneName='" + zoneName + '\'' +
         '}';
   }

   @Override
   public int compareTo(Cluster other) {
      return Long.valueOf(this.id).compareTo(other.id);
   }
}
