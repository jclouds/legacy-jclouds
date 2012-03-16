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

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * Runtime information for a specific vm
 *
 * <pre>
 * &lt;complexType name="RuntimeInfoSectionType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "RuntimeInfoSection")
@XmlType(name = "RuntimeInfoSectionType")
public class RuntimeInfoSection extends SectionType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromRuntimeInfoSection(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends SectionType.Builder<B> {

      private VMWareTools vmWareTools;
      private List<Object> any = Lists.newArrayList();

      /**
       * @see RuntimeInfoSection#getVmWareTools()
       */
      public B vmWareTools(VMWareTools vmWareTools) {
         this.vmWareTools = vmWareTools;
         return self();
      }

      /**
       * @see RuntimeInfoSection#getAny()
       */
      public B any(List<Object> any) {
         this.any = any;
         return self();
      }

      @Override
      public RuntimeInfoSection build() {
         RuntimeInfoSection runtimeInfoSection = new RuntimeInfoSection(this);
         return runtimeInfoSection;
      }

      public B fromRuntimeInfoSection(RuntimeInfoSection in) {
         return fromSectionType(in)
            .vmWareTools(in.getVMWareTools())
            .any(in.getAny());
      }
   }

   protected RuntimeInfoSection() {
      // For JAXB and B use
   }

   protected RuntimeInfoSection(Builder<?> builder) {
      super(builder);
      this.vmWareTools = builder.vmWareTools;
      this.any = builder.any;
   }


    @XmlElement(name = "VMWareTools")
    private VMWareTools vmWareTools;
    @XmlAnyElement(lax = true)
    private List<Object> any = Lists.newArrayList();

    /**
     * Gets the value of the vmWareTools property.
     */
    public VMWareTools getVMWareTools() {
        return vmWareTools;
    }

    /**
     * Gets the value of the any property.
     */
    public List<Object> getAny() {
        return this.any;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RuntimeInfoSection that = RuntimeInfoSection.class.cast(o);
      return super.equals(that) &&
            equal(vmWareTools, that.vmWareTools) && equal(any, that.any);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vmWareTools, any);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("vmWareTools", vmWareTools).add("any", any);
   }

}
