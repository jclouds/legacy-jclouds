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

package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.internal.Nullable;

/**
 * Defines the mapping of volumes for
 * {@link org.jclouds.aws.ec2.services.InstanceClient#setBlockDeviceMappingForInstanceInRegion}.
 * 
 * @author Oleksiy Yarmula
 */
public class BlockDeviceMapping {

   private final Multimap<String, RunningInstance.EbsBlockDevice> ebsBlockDevices = LinkedHashMultimap
            .create();

   public BlockDeviceMapping() {
   }

   /**
    * Creates block device mapping from the list of {@link RunningInstance.EbsBlockDevice devices}.
    * 
    * This method copies the values of the list.
    * 
    * @param ebsBlockDevices
    *           devices to be changed for the volume. This cannot be null.
    */
   public BlockDeviceMapping(Multimap<String, RunningInstance.EbsBlockDevice> ebsBlockDevices) {
      this.ebsBlockDevices.putAll(checkNotNull(ebsBlockDevices,
      /* or throw */"EbsBlockDevices can't be null"));
   }

   /**
    * Adds a {@link RunningInstance.EbsBlockDevice} to the mapping.
    * 
    * @param deviceName
    *           name of the device to apply the mapping. Can be null.
    * @param ebsBlockDevice
    *           ebsBlockDevice to be added. This cannot be null.
    * @return the same instance for method chaining purposes
    */
   public BlockDeviceMapping addEbsBlockDevice(@Nullable String deviceName,
            RunningInstance.EbsBlockDevice ebsBlockDevice) {
      this.ebsBlockDevices.put(deviceName, checkNotNull(ebsBlockDevice,
      /* or throw */"EbsBlockDevice can't be null"));
      return this;
   }

   public Multimap<String, RunningInstance.EbsBlockDevice> getEbsBlockDevices() {
      return ImmutableMultimap.<String, RunningInstance.EbsBlockDevice> builder().putAll(
               ebsBlockDevices).build();
   }
}
