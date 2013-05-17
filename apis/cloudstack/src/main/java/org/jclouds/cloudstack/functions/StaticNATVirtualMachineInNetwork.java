/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class StaticNATVirtualMachineInNetwork implements Function<VirtualMachine, PublicIPAddress> {
   public static interface Factory {
      StaticNATVirtualMachineInNetwork create(Network in);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudStackClient client;
   private final ReuseOrAssociateNewPublicIPAddress reuseOrAssociate;
   private final Network network;

   @Inject
   public StaticNATVirtualMachineInNetwork(CloudStackClient client,
         ReuseOrAssociateNewPublicIPAddress reuseOrAssociate, @Assisted Network network) {
      this.client = checkNotNull(client, "client");
      this.reuseOrAssociate = checkNotNull(reuseOrAssociate, "reuseOrAssociate");
      this.network = checkNotNull(network, "network");
   }

   public PublicIPAddress apply(VirtualMachine vm) {
      PublicIPAddress ip;
      for (ip = reuseOrAssociate.apply(network); !ip.isStaticNAT() || ip.getVirtualMachineId() != vm.getId(); ip = reuseOrAssociate
            .apply(network)) {
         // check to see if someone already grabbed this ip
         if (ip.getVirtualMachineId() != null && ip.getVirtualMachineId() != vm.getId())
            continue;
         try {
            logger.debug(">> static NATing IPAddress(%s) to virtualMachine(%s)", ip.getId(), vm.getId());
            client.getNATClient().enableStaticNATForVirtualMachine(vm.getId(), ip.getId());
            ip = client.getAddressClient().getPublicIPAddress(ip.getId());
            if (ip.isStaticNAT() && ip.getVirtualMachineId().equals(vm.getId()))
               break;
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
         return ip;
      }
      return ip;
   }
}
