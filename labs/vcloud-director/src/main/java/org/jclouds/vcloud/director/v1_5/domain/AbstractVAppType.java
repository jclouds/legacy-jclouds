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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.dmtf.DMTFConstants;
import org.jclouds.dmtf.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * Represents a base type for VAppType and VmType.
 *
 * <pre>
 * &lt;complexType name="AbstractVAppType" &gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "AbstractVAppType")
public abstract class AbstractVAppType extends ResourceEntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromResourceEntityType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceEntityType.Builder<B> {

      private Boolean deployed;
      private Reference vAppParent;
      private List<SectionType> sections = Lists.newArrayList();

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return self();
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B deployed() {
         this.deployed = Boolean.TRUE;
         return self();
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public B notDeployed() {
         this.deployed = Boolean.FALSE;
         return self();
      }

      /**
       * @see AbstractVAppType#getVAppParent()
       */
      public B parent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return self();
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public B sections(Iterable<? extends SectionType> sections) {
         this.sections = Lists.newArrayList(checkNotNull(sections, "sections"));
         return self();
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public B section(SectionType section) {
         if (this.sections == null)
            this.sections = Lists.newArrayList();
         this.sections.add(checkNotNull(section, "section"));
         return self();
      }

      public B fromAbstractVAppType(AbstractVAppType in) {
         return fromResourceEntityType(in).parent(vAppParent).sections(sections).isDeployed(deployed);
      }
   }

   @XmlElement(name = "VAppParent")
   private Reference vAppParent;
   @XmlElementRef(namespace = DMTFConstants.OVF_NS)
   private List<? extends SectionType> sections = Lists.newArrayList();
   @XmlAttribute
   private Boolean deployed;

   protected AbstractVAppType() {
      // for JAXB and Builders
   }

   protected AbstractVAppType(Builder<?> builder) {
      super(builder);
      this.vAppParent = builder.vAppParent;
      this.sections = builder.sections;
      this.deployed = builder.deployed;
   }

   /**
    * Gets the value of the vAppParent property.
    */
   public Reference getVAppParent() {
      return vAppParent;
   }

   /**
    * Specific ovf:Section with additional information for the vApp.
    *
    * Objects of the following type(s) are allowed in the list:
    * <ul>
    * <li>SectionType
    * <li>VirtualHardwareSectionType
    * <li>LeaseSettingsSectionType
    * <li>EulaSectionType
    * <li>RuntimeInfoSectionType
    * <li>AnnotationSectionType
    * <li>DeploymentOptionSectionType
    * <li>StartupSectionType
    * <li>ResourceAllocationSectionType
    * <li>NetworkConnectionSectionType
    * <li>CustomizationSectionType
    * <li>ProductSectionType
    * <li>GuestCustomizationSectionType
    * <li>OperatingSystemSectionType
    * <li>NetworkConfigSectionType
    * <li>NetworkSectionType
    * <li>DiskSectionType
    * <li>InstallSectionType
    * </ul>
    */
   public List<? extends SectionType> getSections() {
      return this.sections;
   }

   /**
    * Gets the value of the deployed property.
    */
   public Boolean isDeployed() {
      return deployed;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AbstractVAppType that = AbstractVAppType.class.cast(o);
      return super.equals(that)
            && equal(this.vAppParent, that.vAppParent)
            && equal(this.sections, that.sections)
            && equal(this.deployed, that.deployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppParent, sections, deployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("vAppParent", vAppParent)
            .add("sections", sections)
            .add("deployed", deployed);
   }
}
