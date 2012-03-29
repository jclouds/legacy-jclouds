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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * Represents a list of ovf:Section to configure for instantiating a VApp.
 * 
 * <pre>
 * &lt;complexType name="InstantiationParams" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "InstantiationParams")
@XmlType(name = "InstantiationParamsType")
public class InstantiationParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromInstantiationParams(this);
   }

   public static class Builder {
      private Set<? extends SectionType> sections = Sets.newLinkedHashSet();

      /**
       * @see InstantiationParams#getSections()
       */
      public Builder sections(Set<? extends SectionType> sections) {
         this.sections = checkNotNull(sections, "sections");
         return this;
      }

      public InstantiationParams build() {
         InstantiationParams instantiationParams = new InstantiationParams(sections);
         return instantiationParams;
      }

      public Builder fromInstantiationParams(InstantiationParams in) {
         return sections(in.getSections());
      }
   }

   private InstantiationParams() {
      // for JAXB
   }

   private InstantiationParams(Set<? extends SectionType> sections) {
      this.sections = sections;
   }

   @XmlElementRef
   protected Set<? extends SectionType> sections = Sets.newLinkedHashSet();

   /**
    * An {@code ovf:Section} to configure for instantiation.
    *
    * Objects of the following type(s) are allowed in the list
    * <ul>
    * <li>{@link SectionType}
    * <li>{@link VirtualHardwareSection}
    * <li>{@link LeaseSettingsSection}
    * <li>{@link EulaSection}
    * <li>{@link RuntimeInfoSection}
    * <li>{@link AnnotationSection}
    * <li>{@link DeploymentOptionSection}
    * <li>{@link StartupSection}
    * <li>{@link ResourceAllocationSection}
    * <li>{@link NetworkConnectionSection}
    * <li>{@link CustomizationSection}
    * <li>{@link ProductSection}
    * <li>{@link GuestCustomizationSection}
    * <li>{@link OperatingSystemSection}
    * <li>{@link NetworkConfigSection}
    * <li>{@link NetworkSection}
    * <li>{@link DiskSection}
    * <li>{@link InstallSection}
    * </ul>
    */
   public Set<? extends SectionType> getSections() {
      return Collections.unmodifiableSet(this.sections);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiationParams that = InstantiationParams.class.cast(o);
      return equal(sections, that.sections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("sections", sections).toString();
   }
}
