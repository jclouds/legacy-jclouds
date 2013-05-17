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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Information about a dimension of the capacity
 *
 * @author Richard Downer
 */
public class Capacity implements Comparable<Capacity> {

   /**
    */
   public static enum Type {
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
         Integer code = Integer.valueOf(checkNotNull(type, "type"));
         return INDEX.containsKey(code) ? INDEX.get(code) : UNRECOGNIZED;
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCapacity(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected long capacityTotal;
      protected long capacityUsed;
      protected double percentUsed;
      protected String podId;
      protected String podName;
      protected Capacity.Type type;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see Capacity#getCapacityTotal()
       */
      public T capacityTotal(long capacityTotal) {
         this.capacityTotal = capacityTotal;
         return self();
      }

      /**
       * @see Capacity#getCapacityUsed()
       */
      public T capacityUsed(long capacityUsed) {
         this.capacityUsed = capacityUsed;
         return self();
      }

      /**
       * @see Capacity#getPercentUsed()
       */
      public T percentUsed(double percentUsed) {
         this.percentUsed = percentUsed;
         return self();
      }

      /**
       * @see Capacity#getPodId()
       */
      public T podId(String podId) {
         this.podId = podId;
         return self();
      }

      /**
       * @see Capacity#getPodName()
       */
      public T podName(String podName) {
         this.podName = podName;
         return self();
      }

      /**
       * @see Capacity#getType()
       */
      public T type(Capacity.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see Capacity#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Capacity#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      public Capacity build() {
         return new Capacity(capacityTotal, capacityUsed, percentUsed, podId, podName, type, zoneId, zoneName);
      }

      public T fromCapacity(Capacity in) {
         return this
               .capacityTotal(in.getCapacityTotal())
               .capacityUsed(in.getCapacityUsed())
               .percentUsed(in.getPercentUsed())
               .podId(in.getPodId())
               .podName(in.getPodName())
               .type(in.getType())
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

   private final long capacityTotal;
   private final long capacityUsed;
   private final double percentUsed;
   private final String podId;
   private final String podName;
   private final Capacity.Type type;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "capacitytotal", "capacityused", "percentused", "podid", "podname", "type", "zoneid", "zonename"
   })
   protected Capacity(long capacityTotal, long capacityUsed, double percentUsed, @Nullable String podId,
                      @Nullable String podName, @Nullable Capacity.Type type, @Nullable String zoneId, @Nullable String zoneName) {
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
      return this.capacityTotal;
   }

   public long getCapacityUsed() {
      return this.capacityUsed;
   }

   public double getPercentUsed() {
      return this.percentUsed;
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
   public Capacity.Type getType() {
      return this.type;
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
      return Objects.hashCode(capacityTotal, capacityUsed, percentUsed, podId, podName, type, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Capacity that = Capacity.class.cast(obj);
      return Objects.equal(this.capacityTotal, that.capacityTotal)
            && Objects.equal(this.capacityUsed, that.capacityUsed)
            && Objects.equal(this.percentUsed, that.percentUsed)
            && Objects.equal(this.podId, that.podId)
            && Objects.equal(this.podName, that.podName)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("capacityTotal", capacityTotal).add("capacityUsed", capacityUsed).add("percentUsed", percentUsed)
            .add("podId", podId).add("podName", podName).add("type", type).add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(Capacity other) {
      int comparison = this.zoneId.compareTo(other.zoneId);
      if (comparison == 0) comparison = this.podId.compareTo(other.podId);
      if (comparison == 0) Integer.valueOf(this.type.code).compareTo(other.type.code);
      return comparison;
   }
}
