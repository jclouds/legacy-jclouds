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
import javax.xml.bind.annotation.XmlType;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;
import org.jclouds.vcloud.director.v1_5.domain.ovf.environment.EnvironmentType;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a VM.
 *
 * <pre>
 * &lt;complexType name="Vm" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "Vm")
public class Vm extends AbstractVAppType<Vm> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromVm(this);
   }

   public static class Builder extends AbstractVAppType.Builder<Vm> {

      private String vAppScopedLocalId;
      private EnvironmentType environment;
      private Boolean needsCustomization;

      /**
       * @see Vm#getVAppScopedLocalId()
       */
      public Builder vAppScopedLocalId(String vAppScopedLocalId) {
         this.vAppScopedLocalId = vAppScopedLocalId;
         return this;
      }

      /**
       * @see Vm#getEnvironment()
       */
      public Builder environment(EnvironmentType environment) {
         this.environment = environment;
         return this;
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public Builder isNeedsCustomization(Boolean needsCustomization) {
         this.needsCustomization = needsCustomization;
         return this;
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public Builder needsCustomization() {
         this.needsCustomization = Boolean.TRUE;
         return this;
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public Builder notNeedsCustomization() {
         this.needsCustomization = Boolean.FALSE;
         return this;
      }

      @Override
      public Vm build() {
         Vm vm = new Vm(href, type, links, description, tasks, id, name, files, status, vAppParent, sections, deployed, vAppScopedLocalId, environment, needsCustomization);
         return vm;
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
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder fromAbstractVAppType(AbstractVAppType<Vm> in) {
         return Builder.class.cast(super.fromAbstractVAppType(in));
      }

      public Builder fromVm(Vm in) {
         return fromAbstractVAppType(in).vAppScopedLocalId(in.getVAppScopedLocalId()).environment(in.getEnvironment()).isNeedsCustomization(in.isNeedsCustomization());
      }
   }

   protected Vm() {
      // for JAXB and Builders
   }

   public Vm(URI href, String type, @Nullable Set<Link> links, String description, @Nullable Set<Task> tasks, String id, String name, FilesList files, Integer status, Reference vAppParent,
             @Nullable List<SectionType<?>> sections, Boolean deployed, String vAppScopedlocalId, EnvironmentType environment, Boolean needsCustomization) {
      super(href, type, links, description, tasks, id, name, files, status, vAppParent, sections, deployed);
      this.vAppScopedLocalId = vAppScopedlocalId;
      this.environment = environment;
      this.needsCustomization = needsCustomization;
   }

   @XmlElement(name = "VAppScopedLocalId")
   protected String vAppScopedLocalId;
   @XmlElement(name = "Environment", namespace = "http://schemas.dmtf.org/ovf/environment/1")
   protected EnvironmentType environment;
   @XmlAttribute
   protected Boolean needsCustomization;

   /**
    * Gets the value of the vAppScopedLocalId property.
    *
    * @return possible object is {@link String }
    */
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * OVF environment section
    *
    * @return possible object is {@link Environment }
    */
   public EnvironmentType getEnvironment() {
      return environment;
   }

   /**
    * Gets the value of the needsCustomization property.
    *
    * @return possible object is {@link Boolean }
    */
   public Boolean isNeedsCustomization() {
      return needsCustomization;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Vm that = Vm.class.cast(o);
      return super.equals(that) &&
            equal(this.vAppScopedLocalId, that.vAppScopedLocalId) && equal(this.environment, that.environment) && equal(this.needsCustomization, that.needsCustomization);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppScopedLocalId, environment, needsCustomization);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("vAppScopedLocalId", vAppScopedLocalId).add("environment", environment).add("needsCustomization", needsCustomization);
   }
}
