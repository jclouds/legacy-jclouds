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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents a capacity and usage of a given resource.
 * <p/>
 * <p/>
 * <p>Java class for CapacityWithUsage complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="CapacityWithUsage">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}CapacityType">
 *       &lt;sequence>
 *         &lt;element name="Used" type="{http://www.w3.org/2001/XMLSchema}Long" minOccurs="0"/>
 *         &lt;element name="Overhead" type="{http://www.w3.org/2001/XMLSchema}Long" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "CapacityWithUsage", propOrder = {
      "used",
      "overhead"
})
public class CapacityWithUsage extends CapacityType<CapacityWithUsage>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromCapacityWithUsage(this);
   }

   public static class Builder extends CapacityType.Builder<CapacityWithUsage> {

      private Long used;
      private Long overhead;

      /**
       * @see CapacityWithUsage#getUsed()
       */
      public Builder used(Long used) {
         this.used = used;
         return this;
      }

      /**
       * @see CapacityWithUsage#getOverhead()
       */
      public Builder overhead(Long overhead) {
         this.overhead = overhead;
         return this;
      }


      @Override
      public CapacityWithUsage build() {
         return new CapacityWithUsage(units, allocated, limit, used, overhead);
      }

      /**
       * @see CapacityType#getUnits()
       */
      @Override
      public Builder units(String units) {
         this.units = units;
         return this;
      }

      /**
       * @see CapacityType#getAllocated()
       */
      @Override
      public Builder allocated(Long allocated) {
         this.allocated = allocated;
         return this;
      }

      /**
       * @see CapacityType#getLimit()
       */
      @Override
      public Builder limit(Long limit) {
         this.limit = limit;
         return this;
      }


      @Override
      public Builder fromCapacityType(CapacityType<CapacityWithUsage> in) {
         return Builder.class.cast(super.fromCapacityType(in));
      }

      public Builder fromCapacityWithUsage(CapacityWithUsage in) {
         return fromCapacityType(in)
               .used(in.getUsed())
               .overhead(in.getOverhead());
      }
   }

   private CapacityWithUsage(String units, Long allocated, Long limit, Long used, Long overhead) {
      super(units, allocated, limit);
      this.used = used;
      this.overhead = overhead;
   }

   private CapacityWithUsage() {
      // for JAXB
   }

   @XmlElement(name = "Used")
   protected Long used;
   @XmlElement(name = "Overhead")
   protected Long overhead;

   /**
    * Gets the value of the used property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getUsed() {
      return used;
   }

   /**
    * Gets the value of the overhead property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getOverhead() {
      return overhead;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CapacityWithUsage that = CapacityWithUsage.class.cast(o);
      return equal(used, that.used) &&
            equal(overhead, that.overhead);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(used,
            overhead);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("used", used)
            .add("overhead", overhead).toString();
   }

}
