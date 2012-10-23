/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;

/**
 * Represents a disk attached to a virtual machine.
 * <p>
 * This disks will be created when a virtual machine is deployed, and will be
 * destroyed when it is undeployed. If there is a need to use persistent
 * storage, a persistent {@link Volume} should be used instead.
 * 
 * @author Ignasi Barrera
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Hard+Disks+Resource">
 *      http://community.abiquo.com/display/ABI20/Hard+Disks+Resource</a>
 */
public class HardDisk extends DomainWrapper<DiskManagementDto> {
   /** The virtual datacenter where the hard disk belongs. */
   private VirtualDatacenter virtualDatacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected HardDisk(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final DiskManagementDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Creates the hard disk in the selected virtual datacenter.
    * <p>
    * Once the hard disk has been created it can be attached to a virtual
    * machine of the virtual datacenter.
    */
   public void save() {
      target = context.getApi().getCloudApi().createHardDisk(virtualDatacenter.unwrap(), target);
   }

   /**
    * Deletes the hard disk.
    */
   public void delete() {
      context.getApi().getCloudApi().deleteHardDisk(target);
      target = null;
   }

   // Parent access

   /**
    * Gets the virtual datacenter where the hard disk belongs to.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Virtual+Datacenter+Resource#VirtualDatacenterResource-RetrieveaVirtualDatacenter"
    *      > http://community.abiquo.com/display/ABI20/Virtual+Datacenter+
    *      Resource# VirtualDatacenterResource-RetrieveaVirtualDatacenter</a>
    */
   public VirtualDatacenter getVirtualDatacenter() {
      Integer virtualDatacenterId = target.getIdFromLink(ParentLinkName.VIRTUAL_DATACENTER);
      VirtualDatacenterDto dto = context.getApi().getCloudApi().getVirtualDatacenter(virtualDatacenterId);
      virtualDatacenter = wrap(context, VirtualDatacenter.class, dto);
      return virtualDatacenter;
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final VirtualDatacenter virtualDatacenter) {
      return new Builder(context, virtualDatacenter);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Long sizeInMb;

      private VirtualDatacenter virtualDatacenter;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final VirtualDatacenter virtualDatacenter) {
         super();
         checkNotNull(virtualDatacenter, ValidationErrors.NULL_RESOURCE + VirtualDatacenter.class);
         this.context = context;
         this.virtualDatacenter = virtualDatacenter;
      }

      public Builder sizeInMb(final long sizeInMb) {
         this.sizeInMb = sizeInMb;
         return this;
      }

      public HardDisk build() {
         DiskManagementDto dto = new DiskManagementDto();
         dto.setSizeInMb(sizeInMb);

         HardDisk hardDisk = new HardDisk(context, dto);
         hardDisk.virtualDatacenter = virtualDatacenter;

         return hardDisk;
      }
   }

   // Delegate methods. Since a hard disk cannot be edited, setters are not
   // visible

   /**
    * Returns the id of the hard disk.
    */
   public Integer getId() {
      // TODO: DiskManagementDto does not have an id field
      return target.getEditLink() == null ? null : target.getIdFromLink("edit");
   }

   /**
    * Returns the size of the hard disk in MB.
    */
   public Long getSizeInMb() {
      return target.getSizeInMb();
   }

   /**
    * Returns the sequence number of the hard disk.
    * <p>
    * It will be computed when attaching the hard disk to a virtual machine and
    * will determine the attachment order of the disk in the virtual machine.
    */
   public Integer getSequence() {
      return target.getSequence();
   }

   @Override
   public String toString() {
      return "HardDisk [id=" + getId() + ", sizeInMb=" + getSizeInMb() + ", sequence=" + getSequence() + "]";
   }

}
