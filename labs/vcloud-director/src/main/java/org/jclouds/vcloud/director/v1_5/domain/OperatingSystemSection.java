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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * An OperatingSystemSection specifies the operating system installed on a virtual machine.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "VirtualHardwareSection")
public class OperatingSystemSection extends SectionType<OperatingSystemSection> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return builder().fromOperatingSystemSection(this);
   }

   public static class Builder extends SectionType.Builder<OperatingSystemSection> {
      private Integer id;
      private String description;
      private String version;

      /**
       * @see OperatingSystemSection#getId()
       */
      public Builder id(Integer id) {
         this.id = id;
         return this;
      }

      /**
       * @see OperatingSystemSection#getVersion()
       */
      public Builder version(String version) {
         this.version = version;
         return this;
      }

      /**
       * @see OperatingSystemSection#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public OperatingSystemSection build() {
         return new OperatingSystemSection(info, id, version, description);
      }

      public Builder fromOperatingSystemSection(OperatingSystemSection in) {
         return id(in.getId()).info(in.getInfo()).description(in.getDescription());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromSection(SectionType<OperatingSystemSection> in) {
         return Builder.class.cast(super.fromSection(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder info(String info) {
         return Builder.class.cast(super.info(info));
      }

   }

   @XmlAttribute
   protected Integer id;
   @XmlAttribute
   protected String version;
   @XmlElement
   protected String description;

   public OperatingSystemSection(@Nullable String info, @Nullable Integer id, @Nullable String version, @Nullable String description) {
      super(info);
      this.id = id;
      this.description = description;
      this.version = version;
   }

   protected OperatingSystemSection() {
      // For Builders and JAXB
   }

   /**
    * 
    * @return ovf id
    * @see org.jclouds.vcloud.director.v1_5.domain.cim.OSType#getCode()
    */
   public Integer getId() {
      return id;
   }

   public String getVersion() {
      return version;
   }

   /**
    * 
    * @return description or null
    */
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      
      OperatingSystemSection other = (OperatingSystemSection) obj;
      return super.equals(other) && Objects.equal(description, other.description);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("description", description);
   }

}