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
package org.jclouds.tmrk.enterprisecloud.domain.vm;

import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.LayoutRequest;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

/**
 * <xs:complexType name="CreateVirtualMachineRequestType">
 * @author Jason King
 * 
 */
public class CreateVirtualMachineRequest<T extends CreateVirtualMachineRequest<T>> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCreateVirtualMachineRequest(this);
   }

   public static class Builder<T extends CreateVirtualMachineRequest<T>> {

      protected String name;
      protected String description;
      protected int processorCount;
      protected ResourceCapacity memory;
      protected LayoutRequest layout;
      protected Set<String> tags = Sets.newLinkedHashSet();

      /**
       * @see CreateVirtualMachineRequest#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getProcessorCount()
       */
      public Builder processorCount(int processorCount) {
         this.processorCount = processorCount;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getMemory()
       */
      public Builder memory(ResourceCapacity memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getLayout()
       */
      public Builder layout(LayoutRequest layout) {
         this.layout = layout;
         return this;
      }

      /**
       * @see CreateVirtualMachineRequest#getTags()
       */
      public Builder tags(Set<String> tags) {
         this.tags = tags;
         return this;
      }

      public CreateVirtualMachineRequest build() {
         return new CreateVirtualMachineRequest(name,processorCount,memory,description,layout,tags);
      }

      public Builder fromCreateVirtualMachineRequest(CreateVirtualMachineRequest in) {
         return name(in.getName())
               .processorCount(in.getProcessorCount())
               .memory(in.getMemory())
               .description(in.getDescription())
               .layout(in.getLayout())
               .tags(in.getTags());
      }
   }

   @XmlAttribute(name = "name", required = true)
   private String name;

   @XmlElement(name = "ProcessorCount", required = true)
   private int processorCount;

   @XmlElement(name = "Memory", required = true) //Docs/Schema are contradictory - required
   private ResourceCapacity memory;

   @XmlElement(name = "Layout", required = true) //Docs/Schema are contradictory - required
   private LayoutRequest layout;

   @XmlElement(name = "Description", required = false)
   private String description;
   
   @XmlElement(name = "Tags", required = true) //Might need empty element
   private Set<String> tags = Sets.newLinkedHashSet();
   
   protected CreateVirtualMachineRequest(String name, int processorCount, ResourceCapacity memory,
                                       @Nullable String description,@Nullable LayoutRequest layout,@Nullable Set<String> tags) {
      this.name  = name;
      this.description = description;
      this.processorCount = processorCount;
      this.memory = memory;
      this.layout = layout;
      this.tags = tags;
   }

   protected CreateVirtualMachineRequest() {
       //For JAXB
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public int getProcessorCount() {
      return processorCount;
   }

   public ResourceCapacity getMemory() {
      return memory;
   }

   public LayoutRequest getLayout() {
      return layout;
   }

   public Set<String> getTags() {
      return Collections.unmodifiableSet(tags);
   }

   @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   public String string() {
      return "name="+name+", description="+description+", processorCount="+processorCount+", memory="+memory+", layout="+layout+", tags="+tags;
   }
}