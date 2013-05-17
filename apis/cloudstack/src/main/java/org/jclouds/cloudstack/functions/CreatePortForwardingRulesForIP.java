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
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreatePortForwardingRulesForIP {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudStackClient client;
   private final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   private final LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine;

   @Inject
   public CreatePortForwardingRulesForIP(CloudStackClient client,
         BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult,
         LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine) {
      this.client = checkNotNull(client, "client");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
            "blockUntilJobCompletesAndReturnResult");
      this.getIPForwardingRulesByVirtualMachine = checkNotNull(getIPForwardingRulesByVirtualMachine,
            "getIPForwardingRulesByVirtualMachine");
   }

   public Set<IPForwardingRule> apply(PublicIPAddress ip, Iterable<Integer> ports) {
      return apply(ip, "tcp", ports);
   }

   public Set<IPForwardingRule> apply(PublicIPAddress ip, String protocol, Iterable<Integer> ports) {
      checkState(ip.getVirtualMachineId() != null,
            "ip %s should be static NATed to a virtual machine before applying rules", ip);
      if (Iterables.size(ports) == 0)
         return ImmutableSet.<IPForwardingRule> of();
      Builder<AsyncCreateResponse> responses = ImmutableSet.builder();
      for (int port : ports) {
         AsyncCreateResponse response = client.getNATClient().createIPForwardingRule(ip.getId(), protocol, port);
         logger.debug(">> creating IP forwarding rule IPAddress(%s) for protocol(%s), port(%s); response(%s)",
               ip.getId(), protocol, port, response);
         responses.add(response);
      }
      Builder<IPForwardingRule> rules = ImmutableSet.builder();
      for (AsyncCreateResponse response : responses.build()) {
         IPForwardingRule rule = blockUntilJobCompletesAndReturnResult.<IPForwardingRule> apply(response);
         rules.add(rule);
         getIPForwardingRulesByVirtualMachine.asMap().put(ip.getVirtualMachineId(), ImmutableSet.of(rule));
      }
      return rules.build();
   }
}
