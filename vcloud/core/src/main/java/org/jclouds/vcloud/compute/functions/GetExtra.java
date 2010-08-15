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
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VApp;

import com.google.common.base.Function;

/**
 * Configures the {@link VCloudComputeServiceContext}; requires
 * {@link BaseVCloudComputeClient} bound.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetExtra implements Function<VApp, Map<String, String>> {

   @Resource
   protected Logger logger = Logger.NULL;

   public Map<String, String> apply(VApp vApp) {
      Map<String, String> extra = newHashMap();
      try {
         extra.put("memory/mb", find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY))
               .getVirtualQuantity()
               + "");
         extra.put("processor/count", find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR))
               .getVirtualQuantity()
               + "");
         for (ResourceAllocation disk : filter(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE))) {
            extra.put(String.format("disk_drive/%s/kb", disk.getAddressOnParent()), disk.getVirtualQuantity() + "");
         }

         for (Entry<String, String> net : vApp.getNetworkToAddresses().entries()) {
            extra.put(String.format("network/%s/ip", net.getKey()), net.getValue());
         }
      } catch (Exception e) {
         logger.error(e, "error getting extra data for vApp: %s", vApp);
      }
      return extra;
   }
}