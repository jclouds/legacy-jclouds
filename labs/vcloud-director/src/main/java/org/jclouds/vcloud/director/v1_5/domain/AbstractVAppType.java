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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a base type for VAppType and VmType.
 *
 * <pre>
 * &lt;complexType name="AbstractVAppType" &gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
public abstract class AbstractVAppType<T extends AbstractVAppType<T>> extends ResourceEntityType<T> {

   public static abstract class Builder<T extends AbstractVAppType<T>> extends ResourceEntityType.Builder<T> {

      protected Boolean deployed;
      protected Reference vAppParent;
      protected List<SectionType<?>> sections = Lists.newArrayList();

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public Builder<T> isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return this;
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public Builder<T> deployed() {
         this.deployed = Boolean.TRUE;
         return this;
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      public Builder<T> notDeployed() {
         this.deployed = Boolean.FALSE;
         return this;
      }

      /**
       * @see AbstractVAppType#getVAppParent()
       */
      public Builder<T> parent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public Builder<T> sections(List<SectionType<?>> sections) {
         if (checkNotNull(sections, "sections").size() > 0)
            this.sections = Lists.newArrayList(sections);
         return this;
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      public Builder<T> section(SectionType<?> section) {
         if (this.sections == null)
            this.sections = Lists.newArrayList();
         this.sections.add(checkNotNull(section, "section"));
         return this;
      }

      /**
       * @see ResourceEntityType#getFiles()
       */
      @Override
      public Builder<T> files(FilesList files) {
         this.files = files;
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      @Override
      public Builder<T> status(Integer status) {
         this.status = status;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }
      
      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder<T> tasks(Set<Task> tasks) {
         if (checkNotNull(tasks, "tasks").size() > 0)
            this.tasks = Sets.newLinkedHashSet(tasks);
         return this;
      }

      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder<T> task(Task task) {
         if (tasks == null)
            tasks = Sets.newLinkedHashSet();
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder<T> fromResourceEntityType(ResourceEntityType<T> in) {
         return Builder.class.cast(super.fromResourceEntityType(in));
      }

      public Builder<T> fromAbstractVAppType(AbstractVAppType<T> in) {
         return fromResourceEntityType(in).parent(vAppParent).sections(sections).isDeployed(deployed);
      }
   }

   @XmlElement(name = "VAppParent")
   protected Reference vAppParent;
   @XmlElementRef(name = "Section", namespace = VCloudDirectorConstants.VCLOUD_OVF_NS)
   protected List<SectionType<?>> sections = Lists.newArrayList();
   @XmlAttribute
   protected Boolean deployed;

   protected AbstractVAppType() {
      // for JAXB and Builders
   }

   public AbstractVAppType(URI href, String type, @Nullable Set<Link> links, String description, @Nullable Set<Task> tasks, String id, String name, FilesList files, Integer status, Reference vAppParent,
                           @Nullable List<SectionType<?>> sections, Boolean deployed) {
      super(href, type, links, description, tasks, id, name, files, status);
      this.vAppParent = vAppParent;
      this.sections = sections;
      this.deployed = deployed;
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
   public List<SectionType<?>> getSections() {
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
      Vm that = Vm.class.cast(o);
      return super.equals(that) &&
            equal(this.vAppParent, that.vAppParent) && equal(this.sections, that.sections) && equal(this.deployed, that.deployed);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppParent, sections, deployed);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("vAppParent", vAppParent).add("sections", sections).add("deployed", deployed);
   }
}
