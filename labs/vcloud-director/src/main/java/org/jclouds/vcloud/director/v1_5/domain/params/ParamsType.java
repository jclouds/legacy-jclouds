/*
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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A basic type used to specify parameters for operations.
 *
 * @see <a href="http://www.vmware.com/support/vcd/doc/rest-api-doc-1.5-html/types/ParamsType.html">
 *    vCloud REST API - ParamsType</a>
 * @since 0.9
 */
@XmlType(name = "ParamsType")
public class ParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromParamsType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> {

      private String description;
      private String name;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }
      
      /**
       * @see ParamsType#getDescription()
       */
      public B description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see ParamsType#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      public ParamsType build() {
         return new ParamsType(this);
      }

      public B fromParamsType(ParamsType in) {
         return description(in.getDescription())
               .name(in.getName());
      }
   }

   protected ParamsType(Builder<?> builder) {
      this.description = builder.description;
      this.name = builder.name;
   }

   protected ParamsType() {
      // for JAXB
   }


   @XmlElement(name = "Description")
   protected String description;
   @XmlAttribute
   protected String name;

   /**
    * Optional description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * A name as parameter.
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ParamsType that = ParamsType.class.cast(o);
      return equal(this.description, that.description) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(description, name);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public ToStringHelper string() {
      return Objects.toStringHelper("").add("description", description).add("name", name);
   }
}
