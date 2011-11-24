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
package org.jclouds.tmrk.enterprisecloud.domain.template;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="TemplateStorage">
 * @author Jason King
 */
public class TemplateStorage {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateStorage(this);
   }

   public static class Builder {

      private ResourceCapacity size;
      private double hourlyCost;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.TemplateStorage#getSize
       */
      public Builder size(ResourceCapacity size) {
         this.size = size;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.TemplateStorage#getHourlyCost
       */
      public Builder hourlyCost(double hourlyCost) {
         this.hourlyCost = hourlyCost;
         return this;
      }

      public TemplateStorage build() {
         return new TemplateStorage(size, hourlyCost);
      }

      public Builder fromTemplateStorage(TemplateStorage in) {
        return size(in.getSize()).hourlyCost(in.getHourlyCost());
      }
   }

   @XmlElement(name = "Size", required = false)
   private ResourceCapacity size;

   @XmlElement(name = "HourlyCost", required = false)
   private double hourlyCost;

   private TemplateStorage(@Nullable ResourceCapacity size, double hourlyCost) {
      this.size = size;
      this.hourlyCost = hourlyCost;
   }

   private TemplateStorage() {
      //For JAXB
   }

   public ResourceCapacity getSize() {
      return size;
   }

   public double getHourlyCost() {
      return hourlyCost;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateStorage that = (TemplateStorage) o;

      if (Double.compare(that.hourlyCost, hourlyCost) != 0) return false;
      if (size != null ? !size.equals(that.size) : that.size != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = size != null ? size.hashCode() : 0;
      temp = hourlyCost != +0.0d ? Double.doubleToLongBits(hourlyCost) : 0L;
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   @Override
   public String toString() {
      return "[size="+ size +", hourlyCost="+ hourlyCost +"]";
   }
}
