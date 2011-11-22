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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.VirtualMachine;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.inject.assistedinject.Assisted;

import java.util.Set;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class StaticNATVirtualMachineInNetwork implements Function<VirtualMachine, PublicIPAddress> {
   public static interface Factory {
      StaticNATVirtualMachineInNetwork create(Network in);
   }

   private final CloudStackClient client;
   private final ReuseOrAssociateNewPublicIPAddress reuseOrAssociate;
   private final Network network;
   private final Predicate<Long> jobComplete;
   private final Cache<Long, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine;

   @Inject
   public StaticNATVirtualMachineInNetwork(CloudStackClient client,
         ReuseOrAssociateNewPublicIPAddress reuseOrAssociate, Predicate<Long> jobComplete,
         Cache<Long, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine, @Assisted Network network) {
      this.client = checkNotNull(client, "client");
      this.reuseOrAssociate = checkNotNull(reuseOrAssociate, "reuseOrAssociate");
      this.jobComplete = checkNotNull(jobComplete, "jobComplete");
      this.getIPForwardingRulesByVirtualMachine = checkNotNull(getIPForwardingRulesByVirtualMachine,
            "getIPForwardingRulesByVirtualMachine");
      this.network = checkNotNull(network, "network");
   }

   public PublicIPAddress apply(VirtualMachine vm) {
      PublicIPAddress ip;
      for (ip = reuseOrAssociate.apply(network); (!ip.isStaticNAT() || ip.getVirtualMachineId() != vm.getId()); ip = reuseOrAssociate
            .apply(network)) {
         // check to see if someone already grabbed this ip
         if (ip.getVirtualMachineId() > 0 && ip.getVirtualMachineId() != vm.getId())
            continue;
         try {
            client.getNATClient().enableStaticNATForVirtualMachine(vm.getId(), ip.getId());
            ip = client.getAddressClient().getPublicIPAddress(ip.getId());
            if (ip.isStaticNAT() && ip.getVirtualMachineId() == vm.getId())
               break;
         } catch (IllegalStateException e) {
            // very likely an ip conflict, so retry;
         }
         return ip;
      }
      AsyncCreateResponse job = client.getNATClient().createIPForwardingRule(ip.getId(), "tcp", 22);
      checkState(jobComplete.apply(job.getJobId()), "Timeout creating IP forwarding rule: ", job);
      AsyncJob<IPForwardingRule> response = client.getAsyncJobClient().getAsyncJob(job.getJobId());
      checkState(response.getResult() != null, "No result after creating IP forwarding rule: ", response);
      getIPForwardingRulesByVirtualMachine.asMap().put(vm.getId(), ImmutableSet.of(response.getResult()));
      return ip;
   }
}