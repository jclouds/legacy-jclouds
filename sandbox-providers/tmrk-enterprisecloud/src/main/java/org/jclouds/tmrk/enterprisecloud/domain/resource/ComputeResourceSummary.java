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
 * <xs:complexType name="ComputeResourceSummary">
 * @author Jason King
 */
public class ComputeResourceSummary {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromComputeResourceSummary(this);
   }

   public static class Builder {
      protected ResourceCapacity allocated;
      protected ResourceCapacity consumed;
      protected ResourceCapacity purchased;
      protected int utilization;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputeResourceSummary#getAllocated
       */
      public Builder allocated(ResourceCapacity allocated) {
         this.allocated = allocated;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputeResourceSummary#getConsumed
       */
      public Builder consumed(ResourceCapacity consumed) {
         this.consumed = consumed;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputeResourceSummary#getPurchased
       */
      public Builder purchased(ResourceCapacity purchased) {
         this.purchased = purchased;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.ComputeResourceSummary#getUtilization
       */
      public Builder utilization(int utilization) {
         this.utilization = utilization;
         return this;
      }

      public ComputeResourceSummary build() {
         return new ComputeResourceSummary(allocated,consumed,purchased,utilization);
      }

      public Builder fromComputeResourceSummary(ComputeResourceSummary in) {
         return allocated(in.getAllocated()).consumed(in.getConsumed()).purchased(in.getPurchased()).utilization(in.getUtilization());
      }
   }

   @XmlElement(name = "Allocated", required = false)
   private ResourceCapacity allocated;

   @XmlElement(name = "Consumed", required = false)
   private ResourceCapacity consumed;

   @XmlElement(name = "Purchased", required = false)
   private ResourceCapacity purchased;

   @XmlElement(name = "Utilization", required = false)
   private int utilization;

   protected ComputeResourceSummary(@Nullable ResourceCapacity allocated, @Nullable ResourceCapacity consumed, @Nullable ResourceCapacity purchased, int utilization) {
      this.allocated = allocated;
      this.consumed = consumed;
      this.purchased = purchased;
      this.utilization = utilization;
   }

   protected ComputeResourceSummary() {
      //For JAXB
   }

   public ResourceCapacity getAllocated() {
      return allocated;
   }

   public ResourceCapacity getConsumed() {
      return consumed;
   }

   public ResourceCapacity getPurchased() {
      return purchased;
   }

   public int getUtilization() {
      return utilization;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ComputeResourceSummary that = (ComputeResourceSummary) o;

      if (utilization != that.utilization) return false;
      if (allocated != null ? !allocated.equals(that.allocated) : that.allocated != null)
         return false;
      if (consumed != null ? !consumed.equals(that.consumed) : that.consumed != null)
         return false;
      if (purchased != null ? !purchased.equals(that.purchased) : that.purchased != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = allocated != null ? allocated.hashCode() : 0;
      result = 31 * result + (consumed != null ? consumed.hashCode() : 0);
      result = 31 * result + (purchased != null ? purchased.hashCode() : 0);
      result = 31 * result + utilization;
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   protected String string() {
       return "allocated="+ allocated +", consumed="+consumed+", purchased="+purchased+", utilization="+utilization;
   }
}
