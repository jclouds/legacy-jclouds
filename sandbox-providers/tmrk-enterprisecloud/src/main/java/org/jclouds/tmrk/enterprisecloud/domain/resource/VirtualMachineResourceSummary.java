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
package org.jclouds.tmrk.enterprisecloud.domain.resource;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="VirtualMachineResourceSummary">
 * @author Jason King
 */
public class VirtualMachineResourceSummary extends ComputeResourceSummary {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVirtualMachineResourceSummary(this);
   }

   public static class Builder extends ComputeResourceSummary.Builder {

      protected int count;
      protected int poweredOnCount;

      public Builder allocated(ResourceCapacity allocated) {
         return Builder.class.cast(super.allocated(allocated));
      }

      public Builder consumed(ResourceCapacity consumed) {
         return Builder.class.cast(super.consumed(consumed));
      }

      public Builder purchased(ResourceCapacity purchased) {
         return Builder.class.cast(super.purchased(purchased));
      }

      public Builder utilization(int utilization) {
        return Builder.class.cast(super.utilization(utilization));
      }

      public Builder count(int count) {
         this.count = count;
         return this;
      }

      public Builder poweredOnCount(int poweredOnCount) {
         this.poweredOnCount = poweredOnCount;
         return this;
      }

      public VirtualMachineResourceSummary build() {
         return new VirtualMachineResourceSummary(allocated,consumed,purchased,utilization,count,poweredOnCount);
      }

      public Builder fromVirtualMachineResourceSummary(VirtualMachineResourceSummary in) {
         return allocated(in.getAllocated()).consumed(in.getConsumed()).purchased(in.getPurchased()).utilization(in.getUtilization())
                .count(in.getCount()).poweredOnCount(in.getPoweredOnCount());
      }
   }

   @XmlElement(name = "Count", required = false)
   private int count;

   @XmlElement(name = "PoweredOnCount", required = false)
   private int poweredOnCount;

   private VirtualMachineResourceSummary(@Nullable ResourceCapacity allocated, @Nullable ResourceCapacity consumed, @Nullable ResourceCapacity purchased, int utilization, int count, int poweredOnCount) {
      super(allocated,consumed,purchased,utilization);
      this.count = count;
      this.poweredOnCount = poweredOnCount;
   }

   private VirtualMachineResourceSummary() {
      //For JAXB
   }

   public int getCount() {
      return count;
   }

   public int getPoweredOnCount() {
      return poweredOnCount;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineResourceSummary that = (VirtualMachineResourceSummary) o;

      if (count != that.count) return false;
      if (poweredOnCount != that.poweredOnCount) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + count;
      result = 31 * result + poweredOnCount;
      return result;
   }

   @Override
   public String string() {
      return super.string()+", count="+count+", poweredOnCount="+poweredOnCount;
   }
}
