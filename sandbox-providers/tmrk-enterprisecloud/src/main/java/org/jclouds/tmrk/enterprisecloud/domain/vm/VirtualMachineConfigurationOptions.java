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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.ResourceCapacityRange;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * <xs:complexType name="VirtualMachineConfigurationOptions">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "VirtualMachineConfigurationOptions")
public class VirtualMachineConfigurationOptions extends BaseResource<VirtualMachineConfigurationOptions> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachineConfigurationOptions(this);
   }

   public static class Builder extends BaseResource.Builder<VirtualMachineConfigurationOptions> {
      //TODO There are additional fields
      protected ResourceCapacityRange memory;

      /**
       * @see VirtualMachineConfigurationOptions#getMemory
       */
      public Builder memory(ResourceCapacityRange memory) {
         this.memory = memory;
         return this;
      }

      @Override
      public VirtualMachineConfigurationOptions build() {
         return new VirtualMachineConfigurationOptions(href, type, memory);
      }

      public Builder fromVirtualMachineConfigurationOptions(VirtualMachineConfigurationOptions in) {
         return fromResource(in).memory(in.getMemory());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<VirtualMachineConfigurationOptions> in) {
         return Builder.class.cast(super.fromResource(in));
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
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

   }

   @XmlElement(name = "Memory", required = false)
   private ResourceCapacityRange memory;

   private VirtualMachineConfigurationOptions(URI href, String type, @Nullable ResourceCapacityRange memory) {
      super(href, type);
      this.memory = memory;
   }

   private VirtualMachineConfigurationOptions() {
       //For JAXB
   }

   /**
    *
    * @return memory capacity range
    */
   public ResourceCapacityRange getMemory() {
      return memory;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineConfigurationOptions that = (VirtualMachineConfigurationOptions) o;

      if (memory != null ? !memory.equals(that.memory) : that.memory != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", memory="+memory;
   }
}