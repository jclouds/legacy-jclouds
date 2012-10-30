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
package org.jclouds.vcloud.compute.functions;

import javax.inject.Singleton;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.functions.HardwareBuilderFromResourceAllocations;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudHardwareBuilderFromResourceAllocations extends HardwareBuilderFromResourceAllocations {
   @Override
   public Volume apply(ResourceAllocationSettingData from) {
      if (from instanceof VCloudHardDisk) {
         VCloudHardDisk vDisk = VCloudHardDisk.class.cast(from);
         return new VolumeImpl(from.getAddressOnParent() + "", Volume.Type.LOCAL, vDisk.getCapacity() / 1024f, null,
                  "0".equals(from.getAddressOnParent()), true);
      } else {
         return super.apply(from);
      }

   }
}
