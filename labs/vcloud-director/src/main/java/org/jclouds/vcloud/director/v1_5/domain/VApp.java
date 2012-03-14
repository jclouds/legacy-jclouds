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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.EntityType.Builder;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a vApp.
 *
 * <pre>
 * &lt;complexType name="VApp" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "VApp")
@XmlType(name = "VAppType")
public class VApp extends AbstractVAppType<VApp> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVApp(this);
   }

   public static class Builder extends AbstractVAppType.Builder<VApp> {

      private Owner owner;
      private Boolean inMaintenanceMode;
      private VAppChildren children;
      private Boolean ovfDescriptorUploaded;

      /**
       * @see VApp#getOwner()
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public Builder isInMaintenanceMode(Boolean inMaintenanceMode) {
         this.inMaintenanceMode = inMaintenanceMode;
         return this;
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public Builder inMaintenanceMode() {
         this.inMaintenanceMode = Boolean.TRUE;
         return this;
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public Builder notInMaintenanceMode() {
         this.inMaintenanceMode = Boolean.FALSE;
         return this;
      }

      /**
       * @see VApp#getChildren()
       */
      public Builder children(VAppChildren children) {
         this.children = children;
         return this;
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public Builder isOvfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return this;
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public Builder ovfDescriptorUploaded() {
         this.ovfDescriptorUploaded = Boolean.TRUE;
         return this;
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public Builder ovfDescriptorNotUploaded() {
         this.ovfDescriptorUploaded = Boolean.FALSE;
         return this;
      }

      @Override
      public VApp build() {
         VApp vApp = new VApp(href, type, links, description, tasks, id, name, files, status, vAppParent, sections, inMaintenanceMode,
               owner, inMaintenanceMode, children, ovfDescriptorUploaded);
         return vApp;
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      @Override
      public Builder isDeployed(Boolean deployed) {
         this.deployed = deployed;
         return this;
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      @Override
      public Builder deployed() {
         this.deployed = Boolean.TRUE;
         return this;
      }

      /**
       * @see AbstractVAppType#isDeployed()
       */
      @Override
      public Builder notDeployed() {
         this.deployed = Boolean.FALSE;
         return this;
      }

      /**
       * @see AbstractVAppType#getVAppParent()
       */
      @Override
      public Builder parent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      @Override
      public Builder sections(List<SectionType<?>> sections) {
         if (checkNotNull(sections, "sections").size() > 0)
            this.sections = Lists.newArrayList(sections);
         return this;
      }

      /**
       * @see AbstractVAppType#getSections()
       */
      @Override
      public Builder section(SectionType<?> section) {
         if (this.sections == null)
            this.sections = Lists.newArrayList();
         this.sections.add(checkNotNull(section, "section"));
         return this;
      }

      /**
       * @see ResourceEntityType#getFiles()
       */
      @Override
      public Builder files(FilesList files) {
         this.files = files;
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      @Override
      public Builder status(Integer status) {
         this.status = status;
         return this;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }
      
      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         if (checkNotNull(tasks, "tasks").size() > 0)
            this.tasks = Sets.newLinkedHashSet(tasks);
         return this;
      }

      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder task(Task task) {
         if (tasks == null)
            tasks = Sets.newLinkedHashSet();
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromAbstractVAppType(AbstractVAppType<VApp> in) {
         return Builder.class.cast(super.fromAbstractVAppType(in));
      }

      public Builder fromVApp(VApp in) {
         return fromAbstractVAppType(in)
               .owner(in.getOwner()).isInMaintenanceMode(in.isInMaintenanceMode())
               .children(in.getChildren()).isOvfDescriptorUploaded(in.isOvfDescriptorUploaded());
      }
   }

   protected VApp() {
      // For JAXB and builder use
   }

   public VApp(URI href, String type, @Nullable Set<Link> links, String description, @Nullable Set<Task> tasks, String id, String name, FilesList files, Integer status, Reference vAppParent,
                           @Nullable List<SectionType<?>> sections, Boolean deployed, Owner owner, Boolean inMaintenanceMode, VAppChildren children, Boolean ovfDescriptorUploaded) {
      super(href, type, links, description, tasks, id, name, files, status, vAppParent, sections, deployed);
      this.owner = owner;
      this.inMaintenanceMode = inMaintenanceMode;
      this.children = children;
      this.ovfDescriptorUploaded = ovfDescriptorUploaded;
   }

   @XmlElement(name = "Owner")
   protected Owner owner;
   @XmlElement(name = "InMaintenanceMode")
   protected Boolean inMaintenanceMode;
   @XmlElement(name = "Children")
   protected VAppChildren children;
   @XmlAttribute
   protected Boolean ovfDescriptorUploaded;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the inMaintenanceMode property.
    */
   public Boolean isInMaintenanceMode() {
      return inMaintenanceMode;
   }

   /**
    * Gets the value of the children property.
    */
   public VAppChildren getChildren() {
      return children;
   }

   /**
    * Gets the value of the ovfDescriptorUploaded property.
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VApp that = VApp.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && equal(this.inMaintenanceMode, that.inMaintenanceMode) &&
            equal(this.children, that.children) && equal(this.ovfDescriptorUploaded, that.ovfDescriptorUploaded);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, inMaintenanceMode, children, ovfDescriptorUploaded);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner).add("inMaintenanceMode", inMaintenanceMode)
            .add("children", children).add("ovfDescriptorUploaded", ovfDescriptorUploaded);
   }
}
