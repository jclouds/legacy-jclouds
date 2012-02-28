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
package org.jclouds.vcloud.director.v1_5.domain.ovf;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_OVF_NS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;

import com.google.common.base.Objects;

/**
 * Metadata about a virtual machine or grouping of them.
 * <p/>
 * Base type for Sections, subclassing this is the most common form of extensibility. Subtypes define more specific
 * elements.
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */

// TODO why do I have to declare these?
@XmlSeeAlso(
     {CustomizationSection.class,
      DeploymentOptionSection.class,
      DiskSection.class,     
      LeaseSettingsSection.class,
      GuestCustomizationSection.class,
      NetworkSection.class,
      NetworkConfigSection.class,
      NetworkConnectionSection.class,
      ProductSection.class,
      VirtualHardwareSection.class,
      VirtualSystem.class })
public abstract class SectionType<T extends SectionType<T>> {

   public abstract Builder<T> toBuilder();

   public static abstract class Builder<T extends SectionType<T>> {
      protected String info;
      
      public abstract SectionType<T> build();

      /**
       * @see SectionType#getInfo
       */
      public Builder<T> info(String info) {
         this.info = info;
         return this;
      }

      public Builder<T> fromSection(SectionType<T> in) {
         return info(in.getInfo());
      }
   }

   @XmlElement(name = "Info", namespace = VCLOUD_OVF_NS)
   protected String info;

   public SectionType(@Nullable String info) {
      this.info = info;
   }

   protected SectionType() {
      // For Builders and JAXB
   }

   /**
    * Info element describes the meaning of the Section, this is typically shown if the Section is not understood by an
    * application
    *
    * @return ovf info
    */
   public String getInfo() {
      return info;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(info);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SectionType<?> other = (SectionType<?>) obj;
      return Objects.equal(info, other.info);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("info", info);
   }

}