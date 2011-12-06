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
package org.jclouds.tmrk.enterprisecloud.domain.resource.memory;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;

/**
 * <xs:complexType name="VirtualMachineMemoryUsageDetail">
 * @author Jason King
 * 
 */
public class VirtualMachineMemoryUsageDetail extends BaseNamedResource<VirtualMachineMemoryUsageDetail> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachineMemoryUsageDetail(this);
   }

   public static class Builder extends BaseNamedResource.Builder<VirtualMachineMemoryUsageDetail> {
      private ResourceCapacity usage;
      private int utilization;
      private boolean deleted;

      /**
       * @see VirtualMachineMemoryUsageDetail#getUsage
       */
      public Builder usage(ResourceCapacity usage) {
         this.usage = usage;
         return this;
      }

      /**
       * @see VirtualMachineMemoryUsageDetail#getUtilization
       */
      public Builder utilization(int utilization) {
         this.utilization = utilization;
         return this;
      }

      /**
       * @see VirtualMachineMemoryUsageDetail#isDeleted
       */
      public Builder deleted(boolean deleted) {
         this.deleted = deleted;
         return this;
      }

      @Override
      public VirtualMachineMemoryUsageDetail build() {
         return new VirtualMachineMemoryUsageDetail(href, type, name, usage, utilization, deleted);
      }

      public Builder fromVirtualMachineMemoryUsageDetail(VirtualMachineMemoryUsageDetail in) {
         return fromNamedResource(in).usage(in.getUsage());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<VirtualMachineMemoryUsageDetail> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<VirtualMachineMemoryUsageDetail> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
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
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "Usage", required = false)
   private ResourceCapacity usage;

   @XmlElement(name = "Utilization", required = true)
   private int utilization;

   @XmlElement(name = "Deleted", required = false)
   private boolean deleted;

   private VirtualMachineMemoryUsageDetail(URI href, String type, String name,
                                           @Nullable ResourceCapacity usage, int utilization, boolean deleted) {
      super(href, type, name);
      this.usage = usage;
      this.utilization = utilization;
      this.deleted = deleted;
   }

   private VirtualMachineMemoryUsageDetail() {
       //For JAXB
   }

   public ResourceCapacity getUsage() {
      return usage;
   }

   public int getUtilization() {
      return utilization;
   }

   public boolean isDeleted() {
      return deleted;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineMemoryUsageDetail that = (VirtualMachineMemoryUsageDetail) o;

      if (deleted != that.deleted) return false;
      if (utilization != that.utilization) return false;
      if (usage != null ? !usage.equals(that.usage) : that.usage != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (usage != null ? usage.hashCode() : 0);
      result = 31 * result + utilization;
      result = 31 * result + (deleted ? 1 : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", usage="+usage+", utilization="+utilization+", deleted="+deleted;
   }
}