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
package org.jclouds.tmrk.enterprisecloud.domain.resource.cpu;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ComputePoolCpuUsageDetail is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="ComputePoolCpuUsageDetail">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolCpuUsageDetail")
public class ComputePoolCpuUsageDetail extends Resource<ComputePoolCpuUsageDetail> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromComputePoolCpuUsage(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolCpuUsageDetail> {
      private Date time;
      private ResourceCapacity value;
      private VirtualMachinesCpuUsage virtualMachinesCpuUsage;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail#getTime
       */
      public Builder time(Date time) {
         this.time =(checkNotNull(time,"time"));
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail#getValue
       */
      public Builder value(ResourceCapacity value) {
         this.value =(checkNotNull(value,"value"));
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.cpu.ComputePoolCpuUsageDetail#getVirtualMachinesCpuUsage
       */
      public Builder virtualMachines(VirtualMachinesCpuUsage virtualMachinesCpuUsage) {
         this.virtualMachinesCpuUsage =(checkNotNull(virtualMachinesCpuUsage,"virtualMachinesCpuUsage"));
         return this;
      }

      @Override
      public ComputePoolCpuUsageDetail build() {
         return new ComputePoolCpuUsageDetail(href, type, name, links, actions, time, value, virtualMachinesCpuUsage);
      }

      public Builder fromComputePoolCpuUsage(ComputePoolCpuUsageDetail in) {
         return fromResource(in).time(in.getTime()).value(in.getValue()).virtualMachines(in.getVirtualMachinesCpuUsage());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolCpuUsageDetail> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolCpuUsageDetail> in) {
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
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   @XmlElement(name = "Time", required = true)
   private Date time;

   @XmlElement(name = "Value", required = false)
   private ResourceCapacity value;

   @XmlElement(name = "VirtualMachines", required = false)
   private VirtualMachinesCpuUsage virtualMachinesCpuUsage;

   private ComputePoolCpuUsageDetail(URI href, String type, String name, Set<Link> links, Set<Action> actions, Date time, @Nullable ResourceCapacity value, @Nullable VirtualMachinesCpuUsage virtualMachinesCpuUsage) {
      super(href, type, name, links, actions);
      this.time = checkNotNull(time, "time");
      this.value = value;
      this.virtualMachinesCpuUsage = virtualMachinesCpuUsage;
   }

   private ComputePoolCpuUsageDetail() {
       //For JAXB
   }

   public Date getTime() {
      return time;
   }

   public ResourceCapacity getValue() {
      return value;
   }

   public VirtualMachinesCpuUsage getVirtualMachinesCpuUsage() {
      return virtualMachinesCpuUsage;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      ComputePoolCpuUsageDetail that = (ComputePoolCpuUsageDetail) o;

      if (!time.equals(that.time)) return false;
      if (value != null ? !value.equals(that.value) : that.value != null)
         return false;
      if (virtualMachinesCpuUsage != null ? !virtualMachinesCpuUsage.equals(that.virtualMachinesCpuUsage) : that.virtualMachinesCpuUsage != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + time.hashCode();
      result = 31 * result + (value != null ? value.hashCode() : 0);
      result = 31 * result + (virtualMachinesCpuUsage != null ? virtualMachinesCpuUsage.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", time="+ time +", value="+value+", virtualMachinesCpuUsage="+ virtualMachinesCpuUsage;
   }
}