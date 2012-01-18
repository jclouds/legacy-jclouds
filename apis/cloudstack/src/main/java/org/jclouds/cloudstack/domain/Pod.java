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

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Pod in CloudStack.
 *
 * @author Richard Downer
 */
public class Pod implements Comparable<Pod> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String name;
      private long zoneId;
      private String zoneName;
      private String gateway;
      private String netmask;
      private String startIp;
      private String endIp;
      private AllocationState allocationState;

      private Builder() {}

      /**
       * @param id the ID of the Pod
       */
      public Builder id(long id) {
         this.id  = id;
         return this;
      }

      /**
       * @param name the name of the Pod
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param zoneId the Zone ID of the Pod
       */
      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @param zoneName the Zone name of the Pod
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      /**
       * @param gateway the gateway of the Pod
       */
      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      /**
       * @param netmask the netmask of the Pod
       */
      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      /**
       * @param startIp the starting IP for the Pod
       */
      public Builder startIp(String startIp) {
         this.startIp = startIp;
         return this;
      }

      /**
       * @param endIp the ending IP for the Pod
       */
      public Builder endIp(String endIp) {
         this.endIp = endIp;
         return this;
      }

      /**
       * @param allocationState the allocation state of the cluster
       */
      public Builder allocationState(AllocationState allocationState) {
         this.allocationState = allocationState;
         return this;
      }

      /**
       * Build the Pod object
       * @return the Pod object
       */
      public Pod build() {
         return new Pod(id, name, zoneId, zoneName, gateway, netmask, startIp, endIp, allocationState);
      }
   }

   private long id;
   private String name;
   @SerializedName("zoneid") private long zoneId;
   @SerializedName("zonename") private String zoneName;
   private String gateway;
   private String netmask;
   @SerializedName("startip") private String startIp;
   @SerializedName("endip") private String endIp;
   @SerializedName("allocationstate") private AllocationState allocationState;

   /* Just for the serializer */
   Pod() {}

   public Pod(long id, String name, long zoneId, String zoneName, String gateway, String netmask, String startIp, String endIp, AllocationState allocationState) {
      this.id = id;
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
   public long getId() {
      return id;
   }

   /**
    * @return name the name of the Pod
    */
   public String getName() {
      return name;
   }

   /**
    * @return zoneId the Zone ID of the Pod
    */
   public long getZoneId() {
      return zoneId;
   }

   /**
    * @return zoneName the Zone name of the Pod
    */
   public String getZoneName() {
      return zoneName;
   }

   /**
    * @return gateway the gateway of the Pod
    */
   public String getGateway() {
      return gateway;
   }

   /**
    * @return netmask the netmask of the Pod
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * @return startIp the starting IP for the Pod
    */
   public String getStartIp() {
      return startIp;
   }

   /**
    * @return endIp the ending IP for the Pod
    */
   public String getEndIp() {
      return endIp;
   }

   /**
    * @param allocationState the allocation state of the cluster
    */
   public AllocationState getAllocationState() {
      return allocationState;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Pod pod = (Pod) o;

      if (id != pod.id) return false;
      if (zoneId != pod.zoneId) return false;
      if (allocationState != pod.allocationState) return false;
      if (endIp != null ? !endIp.equals(pod.endIp) : pod.endIp != null) return false;
      if (gateway != null ? !gateway.equals(pod.gateway) : pod.gateway != null) return false;
      if (name != null ? !name.equals(pod.name) : pod.name != null) return false;
      if (netmask != null ? !netmask.equals(pod.netmask) : pod.netmask != null) return false;
      if (startIp != null ? !startIp.equals(pod.startIp) : pod.startIp != null) return false;
      if (zoneName != null ? !zoneName.equals(pod.zoneName) : pod.zoneName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      result = 31 * result + (gateway != null ? gateway.hashCode() : 0);
      result = 31 * result + (netmask != null ? netmask.hashCode() : 0);
      result = 31 * result + (startIp != null ? startIp.hashCode() : 0);
      result = 31 * result + (endIp != null ? endIp.hashCode() : 0);
      result = 31 * result + (allocationState != null ? allocationState.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Pod{" +
         "id=" + id +
         ", name='" + name + '\'' +
         ", zoneId=" + zoneId +
         ", zoneName='" + zoneName + '\'' +
         ", gateway='" + gateway + '\'' +
         ", netmask='" + netmask + '\'' +
         ", startIp='" + startIp + '\'' +
         ", endIp='" + endIp + '\'' +
         ", allocationState=" + allocationState +
         '}';
   }

   @Override
   public int compareTo(Pod other) {
      return Long.valueOf(this.id).compareTo(other.id);
   }
}
