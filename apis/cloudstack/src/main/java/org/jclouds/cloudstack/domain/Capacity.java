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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Information about a dimension of the capacity
 *
 * @author Richard Downer
 */
public class Capacity implements Comparable<Capacity> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long capacityTotal;
      private long capacityUsed;
      private double percentUsed;
      private long podId;
      private String podName;
      private Type type;
      private long zoneId;
      private String zoneName;

      public Builder capacityTotal(long capacityTotal) {
         this.capacityTotal = capacityTotal;
         return this;
      }

      public Builder capacityUsed(long capacityUsed) {
         this.capacityUsed = capacityUsed;
         return this;
      }

      public Builder percentUsed(double percentUsed) {
         this.percentUsed = percentUsed;
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

      public Builder type(Type type) {
         this.type = type;
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

      public Capacity build() {
         return new Capacity(capacityTotal, capacityUsed, percentUsed, podId, podName, type, zoneId, zoneName);
      }
   }

   public enum Type {
      MEMORY_ALLOCATED_BYTES(0),
      CPU_ALLOCATED_MHZ(1),
      PRIMARY_STORAGE_USED_BYTES(2),
      PRIMARY_STORAGE_ALLOCATED_BYTES(3),
      PUBLIC_IP_ADDRESSES(4),
      PRIVATE_IP_ADDRESSES(5),
      SECONDARY_STORAGE_USED_BYTES(6),
      VLANS(7),
      DIRECT_ATTACHED_PUBLIC_IP_ADDRESSES(8),
      LOCAL_STORAGE_USED_BYTES(9),
      UNRECOGNIZED(Integer.MAX_VALUE);

      private int code;

      private static final Map<Integer, Type> INDEX = Maps.uniqueIndex(ImmutableSet.copyOf(Type.values()),
         new Function<Type, Integer>() {

            @Override
            public Integer apply(Type input) {
               return input.code;
            }

         });

      Type(int code) {
         this.code = code;
      }

      @Override
      public String toString() {
         return name();
      }

      public static Type fromValue(String type) {
         Integer code = new Integer(checkNotNull(type, "type"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }
   }

   @SerializedName("capacitytotal")
   private long capacityTotal;
   @SerializedName("capacityused")
   private long capacityUsed;
   @SerializedName("percentused")
   private double percentUsed;
   @SerializedName("podid")
   private long podId = -1;
   @SerializedName("podname")
   private String podName;
   private Capacity.Type type;
   @SerializedName("zoneid")
   private long zoneId = -1;
   @SerializedName("zonename")
   private String zoneName;
   
   /* exists for the deserializer, only */
   Capacity() {
   }

   public Capacity(long capacityTotal, long capacityUsed, double percentUsed, long podId, String podName, Type type, long zoneId, String zoneName) {
      this.capacityTotal = capacityTotal;
      this.capacityUsed = capacityUsed;
      this.percentUsed = percentUsed;
      this.podId = podId;
      this.podName = podName;
      this.type = type;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   public long getCapacityTotal() {
      return capacityTotal;
   }

   public long getCapacityUsed() {
      return capacityUsed;
   }

   public double getPercentUsed() {
      return percentUsed;
   }

   public long getPodId() {
      return podId;
   }

   public String getPodName() {
      return podName;
   }

   public Type getType() {
      return type;
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

      Capacity capacity = (Capacity) o;

      if (capacityTotal != capacity.capacityTotal) return false;
      if (capacityUsed != capacity.capacityUsed) return false;
      if (Double.compare(capacity.percentUsed, percentUsed) != 0) return false;
      if (podId != capacity.podId) return false;
      if (zoneId != capacity.zoneId) return false;
      if (podName != null ? !podName.equals(capacity.podName) : capacity.podName != null) return false;
      if (type != capacity.type) return false;
      if (zoneName != null ? !zoneName.equals(capacity.zoneName) : capacity.zoneName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = (int) (capacityTotal ^ (capacityTotal >>> 32));
      result = 31 * result + (int) (capacityUsed ^ (capacityUsed >>> 32));
      temp = percentUsed != +0.0d ? Double.doubleToLongBits(percentUsed) : 0L;
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (int) (podId ^ (podId >>> 32));
      result = 31 * result + (podName != null ? podName.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
         "podId=" + podId +
         ", podName='" + podName + '\'' +
         ", zoneId=" + zoneId +
         ", zoneName='" + zoneName + '\'' +
         ", type=" + type +
         ", capacityUsed=" + capacityUsed +
         ", capacityTotal=" + capacityTotal +
         ", percentUsed=" + percentUsed +
         ']';
   }

   @Override
   public int compareTo(Capacity other) {
      int comparison = Long.valueOf(this.zoneId).compareTo(other.zoneId);
      if (comparison != 0) return comparison;
      comparison = Long.valueOf(this.podId).compareTo(other.podId);
      if (comparison != 0) return comparison;
      return Integer.valueOf(this.type.code).compareTo(other.type.code);
   }

}
