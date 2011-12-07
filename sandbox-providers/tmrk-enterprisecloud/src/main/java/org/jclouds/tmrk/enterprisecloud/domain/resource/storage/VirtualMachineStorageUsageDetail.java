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
package org.jclouds.tmrk.enterprisecloud.domain.resource.storage;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * VirtualMachineStorageUsageDetail is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="VirtualMachineStorageUsageDetail">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "VirtualMachineStorageUsageDetail")
public class VirtualMachineStorageUsageDetail extends Resource<VirtualMachineStorageUsageDetail> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromVirtualMachineStorageUsageDetail(this);
   }

   public static class Builder extends Resource.Builder<VirtualMachineStorageUsageDetail> {
      private int diskCount;
      private ResourceCapacity allocated;

      /**
       * @see VirtualMachineStorageUsageDetail#getDiskCount
       */
      public Builder diskCount(int diskCount) {
         this.diskCount =(checkNotNull(diskCount,"diskCount"));
         return this;
      }

      /**
       * @see VirtualMachineStorageUsageDetail#getAllocated
       */
      public Builder allocated(ResourceCapacity allocated) {
         this.allocated =(checkNotNull(allocated,"allocated"));
         return this;
      }

      @Override
      public VirtualMachineStorageUsageDetail build() {
         return new VirtualMachineStorageUsageDetail(href, type, name, links, actions, diskCount, allocated);
      }

      public Builder fromVirtualMachineStorageUsageDetail(VirtualMachineStorageUsageDetail in) {
         return fromResource(in).diskCount(in.getDiskCount()).allocated(in.getAllocated());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<VirtualMachineStorageUsageDetail> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<VirtualMachineStorageUsageDetail> in) {
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

   @XmlElement(name = "DiskCount", required = true)
   private int diskCount;

   @XmlElement(name = "Allocated", required = false)
   private ResourceCapacity allocated;

   private VirtualMachineStorageUsageDetail(URI href, String type, String name, Set<Link> links, Set<Action> actions, int diskCount, @Nullable ResourceCapacity allocated) {
      super(href, type, name, links, actions);
      this.diskCount = diskCount;
      this.allocated = allocated;
   }

   private VirtualMachineStorageUsageDetail() {
       //For JAXB
   }

   public int getDiskCount() {
      return diskCount;
   }

   public ResourceCapacity getAllocated() {
      return allocated;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      VirtualMachineStorageUsageDetail that = (VirtualMachineStorageUsageDetail) o;

      if (diskCount != that.diskCount) return false;
      if (allocated != null ? !allocated.equals(that.allocated) : that.allocated != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + diskCount;
      result = 31 * result + (allocated != null ? allocated.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", diskCount="+ diskCount +", allocated="+ allocated;
   }
}