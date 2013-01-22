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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.util.MachineNameOrIdAndNicSlot;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

/**
 * A {@link LoadingCache} for ip addresses. If the requested ip address has been
 * previously extracted this returns it, if not it calls vbox api.
 * 
 * @author Andrea Turli
 * 
 */
@Singleton
public class IpAddressesLoadingCache extends
      AbstractLoadingCache<MachineNameOrIdAndNicSlot, String> {
  
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<MachineNameOrIdAndNicSlot, String> masters = Maps.newHashMap();
   private final Supplier<VirtualBoxManager> manager;

   @Inject
   public IpAddressesLoadingCache(Supplier<VirtualBoxManager> manager) {
      this.manager = checkNotNull(manager, "vboxmanager");
   }

   @Override
   public synchronized String get(MachineNameOrIdAndNicSlot machineNameOrIdAndNicPort) throws ExecutionException {
      if (masters.containsKey(machineNameOrIdAndNicPort)) {
         return masters.get(machineNameOrIdAndNicPort);
      }
      String query = String.format("/VirtualBox/GuestInfo/Net/%s/V4/IP", machineNameOrIdAndNicPort.getSlotText());
      String ipAddress = Strings.nullToEmpty(manager.get().getVBox()
            .findMachine(machineNameOrIdAndNicPort.getMachineNameOrId()).getGuestPropertyValue(query));
      if(!ipAddress.isEmpty()) {
         logger.debug("<< vm(%s) has IP address(%s) at slot(%s)", machineNameOrIdAndNicPort.getMachineNameOrId(),
            ipAddress, machineNameOrIdAndNicPort.getSlotText());
      }
      masters.put(machineNameOrIdAndNicPort, ipAddress);
      return ipAddress;
   }

   @Override
   public String getIfPresent(Object key) {
      return masters.get((String) key);
   }

   @Override
   public void invalidate(Object key) {
      masters.remove(key);
   }

}
