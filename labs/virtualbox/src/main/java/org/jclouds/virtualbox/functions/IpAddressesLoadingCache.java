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
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Supplier;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

/**
 * A {@link LoadingCache} for ip addresses. If the requested ip address has been
 * previously extracted this returns it, if not it calls vbox api.
 * 
 * @author andrea turli
 * 
 */
@Singleton
public class IpAddressesLoadingCache extends
      AbstractLoadingCache<String, String> {
  
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, String> masters = Maps.newHashMap();
   private final Supplier<VirtualBoxManager> manager;

   @Inject
   public IpAddressesLoadingCache(Supplier<VirtualBoxManager> manager) {
      this.manager = checkNotNull(manager, "vboxmanager");
   }

   @Override
   public synchronized String get(String idOrName) throws ExecutionException {
      if (masters.containsKey(idOrName)) {
         return masters.get(idOrName);
      }
     
      String currentIp = "", previousIp = "";
      int count = 0;
      while (count < 3) {
         currentIp = "";
         while (!MachineUtils.isIpv4(currentIp)) {
            currentIp = manager.get().getVBox().findMachine(idOrName)
                  .getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP");
         }

         if (previousIp.equals(currentIp)) {
             count++;
          }
         previousIp = currentIp;
      }

      masters.put(idOrName, currentIp);
      return currentIp;
   }

   @Override
   public String getIfPresent(Object key) {
      return masters.get((String) key);
   }

}
