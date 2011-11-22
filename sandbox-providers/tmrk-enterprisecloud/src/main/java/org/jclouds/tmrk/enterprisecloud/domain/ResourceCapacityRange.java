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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="ResourceCapacityRange">
 * @author Jason King
 */
public class ResourceCapacityRange {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromResourceCapacityRange(this);
   }

   public static class Builder {

      private ResourceCapacity minimumSize;
      private ResourceCapacity maximumSize;
      private ResourceCapacity stepFactor;

      /**
       * @see ResourceCapacityRange#getMinimumSize
       */
      public Builder minimumSize(ResourceCapacity minimumSize) {
         this.minimumSize = minimumSize;
         return this;
      }

      /**
       * @see ResourceCapacityRange#getMaximumSize
       */
      public Builder maximumSize(ResourceCapacity maximumSize) {
         this.maximumSize = maximumSize;
         return this;
      }

      /**
       * @see ResourceCapacityRange#getStepFactor
       */
      public Builder stepFactor(ResourceCapacity stepFactor) {
         this.stepFactor = stepFactor;
         return this;
      }

      public ResourceCapacityRange build() {
         return new ResourceCapacityRange(minimumSize,maximumSize,stepFactor);
      }

      public Builder fromResourceCapacityRange(ResourceCapacityRange in) {
        return minimumSize(in.getMinimumSize()).maximumSize(in.getMaximumSize()).stepFactor(in.getStepFactor());
      }
   }

   @XmlElement(name = "MinimumSize", required = false)
   private ResourceCapacity minimumSize;

   @XmlElement(name = "MaximumSize", required = false)
   private ResourceCapacity maximumSize;

   @XmlElement(name = "StepFactor", required = false)
   private ResourceCapacity stepFactor;

   private ResourceCapacityRange(@Nullable ResourceCapacity minimumSize, @Nullable ResourceCapacity maximumSize, @Nullable ResourceCapacity stepFactor) {
      this.minimumSize = minimumSize;
      this.maximumSize = maximumSize;
      this.stepFactor = stepFactor;
   }

   private ResourceCapacityRange() {
      //For JAXB
   }

   public ResourceCapacity getMinimumSize() {
      return minimumSize;
   }

   public ResourceCapacity getMaximumSize() {
      return maximumSize;
   }

   public ResourceCapacity getStepFactor() {
      return stepFactor;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ResourceCapacityRange that = (ResourceCapacityRange) o;

      if (maximumSize != null ? !maximumSize.equals(that.maximumSize) : that.maximumSize != null)
         return false;
      if (minimumSize != null ? !minimumSize.equals(that.minimumSize) : that.minimumSize != null)
         return false;
      if (stepFactor != null ? !stepFactor.equals(that.stepFactor) : that.stepFactor != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = minimumSize != null ? minimumSize.hashCode() : 0;
      result = 31 * result + (maximumSize != null ? maximumSize.hashCode() : 0);
      result = 31 * result + (stepFactor != null ? stepFactor.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[minimumSize="+ minimumSize +", maximumSize="+maximumSize+", stepFactor="+stepFactor+"]";
   }
}
