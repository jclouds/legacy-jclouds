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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Maps.newHashMap;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.compute.internal.VCloudExpressComputeClientImpl;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkAdapter;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires
 * {@link VCloudExpressComputeClientImpl} bound.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetExtraFromVApp implements Function<VApp, Map<String, String>> {

   @Resource
   protected Logger logger = Logger.NULL;

   public Map<String, String> apply(VApp vApp) {
      Map<String, String> extra = newHashMap();
      try {
         // TODO make this work with composite vApps
         Vm vm = Iterables.get(vApp.getChildren(), 0);
         extra.put("memory/mb", find(vm.getHardware().getResourceAllocations(), resourceType(ResourceType.MEMORY))
                  .getVirtualQuantity()
                  + "");
         extra.put("processor/count", find(vm.getHardware().getResourceAllocations(),
                  resourceType(ResourceType.PROCESSOR)).getVirtualQuantity()
                  + "");
         for (ResourceAllocation disk : filter(vm.getHardware().getResourceAllocations(),
                  resourceType(ResourceType.DISK_DRIVE))) {
            if (disk instanceof VCloudHardDisk) {
               VCloudHardDisk vDisk = VCloudHardDisk.class.cast(disk);
               extra.put(String.format("disk_drive/%s/kb", disk.getAddressOnParent()), vDisk.getCapacity() + "");
            } else {
               extra.put(String.format("disk_drive/%s/kb", disk.getAddressOnParent()), disk.getVirtualQuantity() + "");
            }
         }
         for (ResourceAllocation net : filter(vm.getHardware().getResourceAllocations(),
                  resourceType(ResourceType.ETHERNET_ADAPTER))) {
            if (net instanceof VCloudNetworkAdapter) {
               VCloudNetworkAdapter vNet = VCloudNetworkAdapter.class.cast(net);
               extra.put(String.format("network/%s/ip", net.getAddressOnParent()), vNet.getIpAddress());
            }
         }
      } catch (Exception e) {
         logger.error(e, "error getting extra data for vApp: %s", vApp);
      }
      return extra;
   }
}