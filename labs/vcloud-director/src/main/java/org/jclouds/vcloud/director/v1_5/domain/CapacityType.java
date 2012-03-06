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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents a capacity of a given resource.
 * <p/>
 * <p/>
 * <p>Java class for Capacity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Capacity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Units" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Allocated" type="{http://www.w3.org/2001/XMLSchema}Long" minOccurs="0"/>
 *         &lt;element name="Limit" type="{http://www.w3.org/2001/XMLSchema}Long"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Capacity", propOrder = {
      "units",
      "allocated",
      "limit"
})
@XmlSeeAlso({
      CapacityWithUsage.class
})
public class CapacityType<T extends CapacityType<T>> {

   public static <T extends CapacityType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromCapacityType(this);
   }

   public static class Builder<T extends CapacityType<T>> {

      protected String units;
      protected Long allocated;
      protected Long limit;

      /**
       * @see CapacityType#getUnits()
       */
      public Builder<T> units(String units) {
         this.units = units;
         return this;
      }

      /**
       * @see CapacityType#getAllocated()
       */
      public Builder<T> allocated(Long allocated) {
         this.allocated = allocated;
         return this;
      }

      /**
       * @see CapacityType#getLimit()
       */
      public Builder<T> limit(Long limit) {
         this.limit = limit;
         return this;
      }


      public CapacityType<T> build() {
         return new CapacityType<T>(units, allocated, limit);
      }


      public Builder<T> fromCapacityType(CapacityType<T> in) {
         return units(in.getUnits())
               .allocated(in.getAllocated())
               .limit(in.getLimit());
      }
   }

   protected CapacityType(String units, Long allocated, Long limit) {
      this.units = units;
      this.allocated = allocated;
      this.limit = limit;
   }

   protected CapacityType() {
      // for JAXB
   }

   @XmlElement(name = "Units", required = true)
   protected String units;
   @XmlElement(name = "Allocated")
   protected Long allocated;
   @XmlElement(name = "Limit")
   protected Long limit;

   /**
    * Gets the value of the units property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getUnits() {
      return units;
   }

   /**
    * Gets the value of the allocated property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getAllocated() {
      return allocated;
   }

   /**
    * Gets the value of the limit property.
    */
   public Long getLimit() {
      return limit;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CapacityType<?> that = CapacityType.class.cast(o);
      return equal(units, that.units) &&
            equal(allocated, that.allocated) &&
            equal(limit, that.limit);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(units,
            allocated,
            limit);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("units", units)
            .add("allocated", allocated)
            .add("limit", limit).toString();
   }

}
