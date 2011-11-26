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
package org.jclouds.tmrk.enterprisecloud.domain.template;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.*;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.software.OperatingSystem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * <xs:complexType name="Template">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "Template")
public class Template extends Resource<Template> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends Resource.Builder<Template> {
      //TODO There are additional fields
      protected OperatingSystem operatingSystem;
      protected String description;
      //protected ComputeMatrix computeMatrix;
      protected ConfigurationOptionRange processor;
      protected ResourceCapacityRange memory;
      protected TemplateStorage storage;
      protected int networkAdapters;
      protected CustomizationOption customization;
      //protected DeviceLicensedSoftware licensedSoftware;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getOperatingSystem
       */
      public Builder operatingSystem(OperatingSystem operatingSystem) {
         this.operatingSystem = operatingSystem;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getProcessor
       */
      public Builder processor(ConfigurationOptionRange processor) {
         this.processor = processor;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getMemory
       */
      public Builder memory(ResourceCapacityRange memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getStorage
       */
      public Builder storage(TemplateStorage storage) {
         this.storage = storage;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getNetworkAdapters
       */
      public Builder networkAdapters(int networkAdapters) {
         this.networkAdapters = networkAdapters;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.template.Template#getCustomization
       */
      public Builder customization(CustomizationOption customization) {
         this.customization = customization;
         return this;
      }

      @Override
      public Template build() {
         return new Template(href, type, links, actions, name, operatingSystem, description, processor, memory, storage, networkAdapters, customization);
      }

      public Builder fromTask(Template in) {
         return fromResource(in).description(in.getDescription())
                 .operatingSystem(in.getOperatingSystem()).processor(in.getProcessor())
                 .memory(in.getMemory()).storage(in.getStorage()).networkAdapters(in.getNetworkAdapters())
                 .customization(in.getCustomization());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<Template> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<Template> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.type(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "OperatingSystem", required = false)
   protected OperatingSystem operatingSystem;

   @XmlElement(name = "Description", required = false)
   protected String description;

   //protected ComputeMatrix computeMatrix;

   @XmlElement(name = "Processor", required = false)
   protected ConfigurationOptionRange processor;

   @XmlElement(name = "Memory", required = false)
   protected ResourceCapacityRange memory;

   @XmlElement(name = "Storage", required = false)
   protected TemplateStorage storage;

   @XmlElement(name = "NetworkAdapters", required = false)
   protected int networkAdapters;

   @XmlElement(name = "Customization", required = false)
   protected CustomizationOption customization;

   //protected DeviceLicensedSoftware licensedSoftware;

   private Template(URI href, String type, Set<Link> links, Set<Action> actions,  String name, @Nullable OperatingSystem operatingSystem, @Nullable String description,
                    @Nullable ConfigurationOptionRange processor, @Nullable ResourceCapacityRange memory,
                    @Nullable TemplateStorage storage, @Nullable int networkAdapters, @Nullable CustomizationOption customization) {
      super(href, type, name, links, actions);
      this.operatingSystem = operatingSystem;
      this.description = description;
      this.processor = processor;
      this.memory = memory;
      this.storage = storage;
      this.networkAdapters = networkAdapters;
      this.customization = customization;
   }

   private Template() {
       //For JAXB
   }

   public String getDescription() {
      return description;
   }

   public ConfigurationOptionRange getProcessor() {
      return processor;
   }

   public ResourceCapacityRange getMemory() {
      return memory;
   }

   public TemplateStorage getStorage() {
      return storage;
   }

   public int getNetworkAdapters() {
      return networkAdapters;
   }

   public CustomizationOption getCustomization() {
      return customization;
   }

   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Template template = (Template) o;

      if (networkAdapters != template.networkAdapters) return false;
      if (customization != null ? !customization.equals(template.customization) : template.customization != null)
         return false;
      if (description != null ? !description.equals(template.description) : template.description != null)
         return false;
      if (memory != null ? !memory.equals(template.memory) : template.memory != null)
         return false;
      if (operatingSystem != null ? !operatingSystem.equals(template.operatingSystem) : template.operatingSystem != null)
         return false;
      if (processor != null ? !processor.equals(template.processor) : template.processor != null)
         return false;
      if (storage != null ? !storage.equals(template.storage) : template.storage != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (operatingSystem != null ? operatingSystem.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (processor != null ? processor.hashCode() : 0);
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      result = 31 * result + (storage != null ? storage.hashCode() : 0);
      result = 31 * result + networkAdapters;
      result = 31 * result + (customization != null ? customization.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", operatingSystem="+ operatingSystem+
            ", description="+ description+", processor="+ processor+
            ", memory="+ memory+", storage="+ storage+
            ", networkAdapters="+ networkAdapters+", customization="+ customization;
   }

}