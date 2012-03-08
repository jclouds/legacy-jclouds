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

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.ovf.SectionType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * Runtime information for a specific vm
 *
 * <pre>
 * &lt;complexType name="RuntimeInfoSection" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "RuntimeInfoSection")
public class RuntimeInfoSection extends SectionType<RuntimeInfoSection> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromRuntimeInfoSection(this);
   }

   public static class Builder extends SectionType.Builder<RuntimeInfoSection> {

      private VMWareTools vmWareTools;
      private List<Object> any = Lists.newArrayList();

      /**
       * @see RuntimeInfoSection#getVmWareTools()
       */
      public Builder vmWareTools(VMWareTools vmWareTools) {
         this.vmWareTools = vmWareTools;
         return this;
      }

      /**
       * @see RuntimeInfoSection#getAny()
       */
      public Builder any(List<Object> any) {
         this.any = any;
         return this;
      }


      @Override
      public RuntimeInfoSection build() {
         RuntimeInfoSection runtimeInfoSection = new RuntimeInfoSection(vmWareTools, any);
         return runtimeInfoSection;
      }

      @Override
      public Builder fromSectionType(SectionType<RuntimeInfoSection> in) {
          return Builder.class.cast(super.fromSectionType(in));
      }

      public Builder fromRuntimeInfoSection(RuntimeInfoSection in) {
         return fromSectionType(in)
            .vmWareTools(in.getVMWareTools())
            .any(in.getAny());
      }
   }

   protected RuntimeInfoSection() {
      // For JAXB and builder use
   }

   public RuntimeInfoSection(VMWareTools vmWareTools, List<Object> any) {
      this.vmWareTools = vmWareTools;
      this.any = any;
   }


    @XmlElement(name = "VMWareTools")
    protected VMWareTools vmWareTools;
    @XmlAnyElement(lax = true)
    protected List<Object> any = Lists.newArrayList();

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
