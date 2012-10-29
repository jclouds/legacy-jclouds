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
package org.jclouds.dmtf.ovf;

import static com.google.common.base.Objects.equal;
import static org.jclouds.dmtf.DMTFConstants.OVF_NS;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Metadata about a virtual machine or grouping of them.
 *
 * Base type for Sections, subclassing this is the most common form of extensibility. Subtypes define more specific elements.
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlType(name = "Section_Type")
public abstract class SectionType {

   public static abstract class Builder<B extends Builder<B>> {
      private String info;
      private Boolean required;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      public abstract SectionType build();

      /**
       * @see SectionType#getInfo()
       */
      public B info(String info) {
         this.info = info;
         return self();
      }

      /**
       * @see SectionType#isRequired()
       */
      public B required(Boolean required) {
         this.required = required;
         return self();
      }

      /**
       * @see SectionType#isRequired()
       */
      public B required() {
         this.required = Boolean.TRUE;
         return self();
      }

      /**
       * @see SectionType#isRequired()
       */
      public B notRequired() {
         this.required = Boolean.FALSE;
         return self();
      }

      public B fromSectionType(SectionType in) {
         return info(in.getInfo()).required(in.isRequired());
      }
   }

   @XmlElement(name = "Info", required = true)
   private String info;
   @XmlAttribute(namespace = OVF_NS)
   private Boolean required;

   protected SectionType(Builder<?> builder) {
      this.info = builder.info;
      this.required = builder.required;
   }

   protected SectionType() {
      // For JAXB
   }

   /**
    * Info element describes the meaning of the Section, this is typically shown if the Section is not understood by an application
    * 
    * @return ovf info
    */
   public String getInfo() {
      return info;
   }

   public void setInfo(String info) {
      this.info = info;
   }

   public Boolean isRequired() {
      return required;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(info, required);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SectionType other = (SectionType) obj;
      return equal(this.info, other.info) && equal(this.required, other.required);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("info", info).add("required", required);
   }

}
