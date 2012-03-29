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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.VCloudOperatingSystemSection;
import org.jclouds.vcloud.domain.ovf.VCloudVirtualHardwareSection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class VmImpl extends ReferenceTypeImpl implements Vm {

   @Nullable
   private final Status status;
   private final ReferenceType vApp;
   @Nullable
   private final String description;
   private final List<Task> tasks = Lists.newArrayList();
   @Nullable
   private final VCloudVirtualHardwareSection hardware;
   private final String vAppScopedLocalId;
   private final VCloudOperatingSystemSection os;
   @Nullable
   private final GuestCustomizationSection guestCustomization;
   @Nullable
   private final NetworkConnectionSection networkConnectionSection;

   public VmImpl(String name, String type, URI id, @Nullable Status status, ReferenceType vApp,
            @Nullable String description, Iterable<Task> tasks, @Nullable VCloudVirtualHardwareSection hardware,
            @Nullable VCloudOperatingSystemSection os, @Nullable NetworkConnectionSection networkConnectionSection,
            @Nullable GuestCustomizationSection guestCustomization, @Nullable String vAppScopedLocalId) {
      super(name, type, id);
      this.status = status;
      this.vApp = vApp;// TODO: once <1.0 is killed check not null
      this.description = description;
      Iterables.addAll(this.tasks, checkNotNull(tasks, "tasks"));
      this.hardware = hardware;
      this.os = os;
      this.networkConnectionSection = networkConnectionSection;
      this.guestCustomization = guestCustomization;
      this.vAppScopedLocalId = vAppScopedLocalId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @Nullable
   public Status getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ReferenceType getParent() {
      return vApp;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Task> getTasks() {
      return tasks;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VCloudVirtualHardwareSection getVirtualHardwareSection() {
      return hardware;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public VCloudOperatingSystemSection getOperatingSystemSection() {
      return os;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NetworkConnectionSection getNetworkConnectionSection() {
      return networkConnectionSection;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GuestCustomizationSection getGuestCustomizationSection() {
      return guestCustomization;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((guestCustomization == null) ? 0 : guestCustomization.hashCode());
      result = prime * result + ((hardware == null) ? 0 : hardware.hashCode());
      result = prime * result + ((networkConnectionSection == null) ? 0 : networkConnectionSection.hashCode());
      result = prime * result + ((os == null) ? 0 : os.hashCode());
      result = prime * result + ((vApp == null) ? 0 : vApp.hashCode());
      result = prime * result + ((vAppScopedLocalId == null) ? 0 : vAppScopedLocalId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VmImpl other = (VmImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (guestCustomization == null) {
         if (other.guestCustomization != null)
            return false;
      } else if (!guestCustomization.equals(other.guestCustomization))
         return false;
      if (hardware == null) {
         if (other.hardware != null)
            return false;
      } else if (!hardware.equals(other.hardware))
         return false;
      if (networkConnectionSection == null) {
         if (other.networkConnectionSection != null)
            return false;
      } else if (!networkConnectionSection.equals(other.networkConnectionSection))
         return false;
      if (os == null) {
         if (other.os != null)
            return false;
      } else if (!os.equals(other.os))
         return false;
      if (vApp == null) {
         if (other.vApp != null)
            return false;
      } else if (!vApp.equals(other.vApp))
         return false;
      if (vAppScopedLocalId == null) {
         if (other.vAppScopedLocalId != null)
            return false;
      } else if (!vAppScopedLocalId.equals(other.vAppScopedLocalId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", description=" + description
               + ", status=" + status + ", tasks=" + tasks + ", vApp=" + vApp + ", hardware=" + hardware + ", os=" + os
               + ", network=" + networkConnectionSection + ", vAppScopedLocalId=" + vAppScopedLocalId + "]";
   }

}