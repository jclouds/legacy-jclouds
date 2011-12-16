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
package org.jclouds.tmrk.enterprisecloud.domain.layout;

import com.google.common.collect.Sets;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReference;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineReferences;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * LayoutGroup is more than a simple wrapper as it extends Resource.
 * <xs:complexType name="LayoutGroupType">
 * @author Jason King
 * 
 */
public class LayoutGroup extends Resource<LayoutGroup> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromLayoutGroup(this);
   }

   public static class Builder extends Resource.Builder<LayoutGroup> {
      private int index;
      private Set<VirtualMachineReference> virtualMachineReferences = Sets.newLinkedHashSet();

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutGroup#getIndex
       */
      public Builder index(int index) {
         this.index = index;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.layout.LayoutGroup#getVirtualMachineReferences()
       */
      public Builder virtualMachineReferences(Set<VirtualMachineReference> virtualMachineReferences) {
         this.virtualMachineReferences = Sets.newLinkedHashSet(checkNotNull(virtualMachineReferences, "virtualMachineReferences"));
         return this;
      }

      
      @Override
      public LayoutGroup build() {
         return new LayoutGroup(href, type, name, links, actions, index,virtualMachineReferences);
      }

      public Builder fromLayoutGroup(LayoutGroup in) {
         return fromResource(in).index(in.getIndex());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<LayoutGroup> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource<LayoutGroup> in) {
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

   @XmlElement(name = "Index", required = false)
   private int index;

   @XmlElement(name = "VirtualMachines", required = false)
   private VirtualMachineReferences virtualMachineReferences;
   
   //TODO: PhysicalDevices

   private LayoutGroup(URI href, String type, String name, Set<Link> links, Set<Action> actions, 
                       int index, Set<VirtualMachineReference> virtualMachineReferences) {
      super(href, type, name, links, actions);
      this.index = index;
      this.virtualMachineReferences = VirtualMachineReferences.builder().virtualMachineReferences(virtualMachineReferences).build();
   }

   private LayoutGroup() {
       //For JAXB
   }

   public int getIndex() {
      return index;
   }

   public Set<VirtualMachineReference> getVirtualMachineReferences() {
      return virtualMachineReferences.getVirtualMachineReferences();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      LayoutGroup that = (LayoutGroup) o;

      if (index != that.index) return false;
      if (!virtualMachineReferences.equals(that.virtualMachineReferences))
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + index;
      result = 31 * result + virtualMachineReferences.hashCode();
      return result;
   }

   @Override
   public String string() {
      return super.string()+", index="+index+", virtualMachineReferences="+virtualMachineReferences;
   }
}