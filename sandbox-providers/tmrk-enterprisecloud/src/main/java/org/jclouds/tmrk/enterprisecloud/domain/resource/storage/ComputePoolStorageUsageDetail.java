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
 * ComputePoolStorageUsageDetail is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="ComputePoolStorageUsageDetail">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "ComputePoolStorageUsageDetail")
public class ComputePoolStorageUsageDetail extends Resource<ComputePoolStorageUsageDetail> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromComputePoolStorageUsageDetail(this);
   }

   public static class Builder extends Resource.Builder<ComputePoolStorageUsageDetail> {
      private ResourceCapacity allocated;
      private VirtualMachinesStorageDetails virtualMachinesStorageDetails;

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.storage.ComputePoolStorageUsageDetail#getAllocated
       */
      public Builder allocated(ResourceCapacity allocated) {
         this.allocated =(checkNotNull(allocated,"allocated"));
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.storage.ComputePoolStorageUsageDetail#getVirtualMachinesStorageDetails
       */
      public Builder virtualMachines(VirtualMachinesStorageDetails virtualMachinesStorageDetails) {
         this.virtualMachinesStorageDetails =(checkNotNull(virtualMachinesStorageDetails,"virtualMachinesStorageDetails"));
         return this;
      }

      @Override
      public ComputePoolStorageUsageDetail build() {
         return new ComputePoolStorageUsageDetail(href, type, name, links, actions, allocated, virtualMachinesStorageDetails);
      }

      public Builder fromComputePoolStorageUsageDetail(ComputePoolStorageUsageDetail in) {
         return fromResource(in).allocated(in.getAllocated()).virtualMachines(in.getVirtualMachinesStorageDetails());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<ComputePoolStorageUsageDetail> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<ComputePoolStorageUsageDetail> in) {
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

   @XmlElement(name = "Allocated", required = false)
   private ResourceCapacity allocated;

   @XmlElement(name = "VirtualMachines", required = false)
   private VirtualMachinesStorageDetails virtualMachinesStorageDetails;

   private ComputePoolStorageUsageDetail(URI href, String type, String name, Set<Link> links, Set<Action> actions, @Nullable ResourceCapacity allocated, @Nullable VirtualMachinesStorageDetails virtualMachinesStorageDetails) {
      super(href, type, name, links, actions);
      this.allocated = allocated;
      this.virtualMachinesStorageDetails = virtualMachinesStorageDetails;
   }

   private ComputePoolStorageUsageDetail() {
       //For JAXB
   }

   public ResourceCapacity getAllocated() {
      return allocated;
   }

   public VirtualMachinesStorageDetails getVirtualMachinesStorageDetails() {
      return virtualMachinesStorageDetails;
   }



   @Override
   public String string() {
      return super.string()+", allocated="+ allocated +", virtualMachinesStorageDetails="+ virtualMachinesStorageDetails;
   }
}