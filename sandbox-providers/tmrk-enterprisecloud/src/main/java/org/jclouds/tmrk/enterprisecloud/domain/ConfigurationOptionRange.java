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

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="ConfigurationOptionRange">
 * @author Jason King
 */
public class ConfigurationOptionRange {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromConfigurationOptionRange(this);
   }

   public static class Builder {

      private int minimum;
      private int maximum;
      private int stepFactor;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.ConfigurationOptionRange#getMinimum
       */
      public Builder minimum(int minimum) {
         this.minimum = minimum;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.ConfigurationOptionRange#getMaximum
       */
      public Builder maximum(int maximum) {
         this.maximum = maximum;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.ConfigurationOptionRange#getStepFactor
       */
      public Builder stepFactor(int stepFactor) {
         this.stepFactor = stepFactor;
         return this;
      }

      public ConfigurationOptionRange build() {
         return new ConfigurationOptionRange(minimum, maximum,stepFactor);
      }

      public Builder fromConfigurationOptionRange(ConfigurationOptionRange in) {
        return minimum(in.getMinimum()).maximum(in.getMaximum()).stepFactor(in.getStepFactor());
      }
   }

   @XmlElement(name = "Minimum")
   private int minimum;

   @XmlElement(name = "Maximum")
   private int maximum;

   @XmlElement(name = "StepFactor")
   private int stepFactor;

   private ConfigurationOptionRange(int minimum, int maximum, int stepFactor) {
      this.minimum = minimum;
      this.maximum = maximum;
      this.stepFactor = stepFactor;
   }

   private ConfigurationOptionRange() {
      //For JAXB
   }

   public int getMinimum() {
      return minimum;
   }

   public int getMaximum() {
      return maximum;
   }

   public int getStepFactor() {
      return stepFactor;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ConfigurationOptionRange that = (ConfigurationOptionRange) o;

      if (maximum != that.maximum) return false;
      if (minimum != that.minimum) return false;
      if (stepFactor != that.stepFactor) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = minimum;
      result = 31 * result + maximum;
      result = 31 * result + stepFactor;
      return result;
   }

   @Override
   public String toString() {
      return "[minimum="+ minimum +", maximum="+ maximum +", stepFactor="+stepFactor+"]";
   }
}
