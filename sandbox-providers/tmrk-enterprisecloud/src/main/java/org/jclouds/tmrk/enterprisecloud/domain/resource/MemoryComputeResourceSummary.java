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

/**
 * <xs:complexType name="MemoryComputeResourceSummary">
 * @author Jason King
 */
public class MemoryComputeResourceSummary extends ComputeResourceSummary {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMemoryComputeResourceSummary(this);
   }

   public static class Builder extends ComputeResourceSummary.Builder {

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

      public MemoryComputeResourceSummary build() {
         return new MemoryComputeResourceSummary(allocated,consumed,purchased,utilization);
      }

      public Builder fromMemoryComputeResourceSummary(MemoryComputeResourceSummary in) {
         return allocated(in.getAllocated()).consumed(in.getConsumed()).purchased(in.getPurchased()).utilization(in.getUtilization());
      }
   }

   private MemoryComputeResourceSummary(@Nullable ResourceCapacity allocated, @Nullable ResourceCapacity consumed, @Nullable ResourceCapacity purchased, int utilization) {
      super(allocated,consumed,purchased,utilization);
   }

   private MemoryComputeResourceSummary() {
      //For JAXB
   }
}
