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
package org.jclouds.tmrk.enterprisecloud.domain.hardware;

import org.jclouds.tmrk.enterprisecloud.domain.ResourceCapacityRange;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="DiskConfigurationOptionRange">
 * @author Jason King
 */
public class DiskConfigurationOptionRange {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromConfigurationOptionRange(this);
   }

   public static class Builder {

      private ResourceCapacityRange resourceCapacityRange;
      private double monthlyCost;

      /**
       * @see DiskConfigurationOptionRange#getResourceCapacityRange
       */
      public Builder resourceCapacityRange(ResourceCapacityRange resourceCapacityRange) {
         this.resourceCapacityRange = resourceCapacityRange;
         return this;
      }

      /**
       * @see DiskConfigurationOptionRange#getMonthlyCost()
       */
      public Builder monthlyCost(double monthlyCost) {
         this.monthlyCost = monthlyCost;
         return this;
      }


      public DiskConfigurationOptionRange build() {
         return new DiskConfigurationOptionRange(resourceCapacityRange, monthlyCost);
      }

      public Builder fromConfigurationOptionRange(DiskConfigurationOptionRange in) {
        return resourceCapacityRange(in.getResourceCapacityRange()).monthlyCost(in.getMonthlyCost());
      }
   }

   @XmlElement(name = "ResourceCapacityRange")
   private ResourceCapacityRange resourceCapacityRange;

   @XmlElement(name = "MonthlyCost")
   private double monthlyCost;

   private DiskConfigurationOptionRange(ResourceCapacityRange resourceCapacityRange, double monthlyCost) {
      this.resourceCapacityRange = resourceCapacityRange;
      this.monthlyCost = monthlyCost;
   }

   private DiskConfigurationOptionRange() {
      //For JAXB
   }

   public ResourceCapacityRange getResourceCapacityRange() {
      return resourceCapacityRange;
   }

   public double getMonthlyCost() {
      return monthlyCost;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DiskConfigurationOptionRange that = (DiskConfigurationOptionRange) o;

      if (Double.compare(that.monthlyCost, monthlyCost) != 0) return false;
      if (resourceCapacityRange != null ? !resourceCapacityRange.equals(that.resourceCapacityRange) : that.resourceCapacityRange != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = resourceCapacityRange != null ? resourceCapacityRange.hashCode() : 0;
      temp = monthlyCost != +0.0d ? Double.doubleToLongBits(monthlyCost) : 0L;
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public String toString() {
      return "[resourceCapacityRange="+resourceCapacityRange+", monthlyCost="+monthlyCost+"]";
   }
}
