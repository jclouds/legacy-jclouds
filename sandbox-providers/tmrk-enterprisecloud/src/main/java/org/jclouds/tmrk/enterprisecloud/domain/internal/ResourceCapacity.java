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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import org.jclouds.javax.annotation.Nullable;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Jason King
 */
public class ResourceCapacity<T extends ResourceCapacity<T>> {

   public static <T extends ResourceCapacity<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResource(this);
   }

   public static class Builder<T extends ResourceCapacity<T>> {

       protected double value; //mandatory
       protected String unit;  //optional

      /**
       * @see ResourceCapacity#getValue
       */
      public Builder<T> value(double value) {
         this.value = value;
         return this;
      }

      /**
       * @see ResourceCapacity#getUnit
       */
      public Builder<T> unit(String unit) {
         this.unit = unit;
         return this;
      }

      public ResourceCapacity<T> build() {
         return new ResourceCapacity<T>(value, unit);
      }

      public Builder<T> fromResource(ResourceCapacity<T> in) {
         return value(in.getValue()).unit(in.getUnit());
      }
   }

   @XmlElement(name = "Value")
   protected double value;

   @XmlElement(name = "Unit")
   protected String unit;

   public ResourceCapacity(double value, @Nullable String unit) {
      this.value = value;
      this.unit = unit;
   }

   protected ResourceCapacity() {
      //For JAXB
   }

   public double getValue() {
      return value;
   }

    /**
     * Optional. May be null
     */
   public String getUnit() {
      return unit;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceCapacity that = (ResourceCapacity) o;

        if (Double.compare(that.value, value) != 0) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   protected String string() {
       return "value="+value+", unit="+unit;
   }
}