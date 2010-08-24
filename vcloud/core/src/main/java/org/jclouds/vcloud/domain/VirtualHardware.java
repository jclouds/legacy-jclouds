/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.domain;

import java.net.URI;
import java.util.Set;

import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A description of the virtual hardware supported by a virtual machine.
 */
public class VirtualHardware extends ReferenceTypeImpl {
   private final String info;
   private final VirtualSystem virtualSystem;
   private final Set<ResourceAllocation> resourceAllocations = Sets.newLinkedHashSet();

   public VirtualHardware(String name, String type, URI href, String info, VirtualSystem virtualSystem,
            Iterable<? extends ResourceAllocation> resourceAllocations) {
      super(name, type, href);
      this.info = info;
      this.virtualSystem = virtualSystem;
      Iterables.addAll(this.resourceAllocations, resourceAllocations);
   }

   public String getInfo() {
      return info;
   }

   public VirtualSystem getSystem() {
      return virtualSystem;
   }

   public Set<? extends ResourceAllocation> getResourceAllocations() {
      return resourceAllocations;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", info=" + getInfo()
               + ", virtualSystem=" + getSystem() + "]";
   }
}