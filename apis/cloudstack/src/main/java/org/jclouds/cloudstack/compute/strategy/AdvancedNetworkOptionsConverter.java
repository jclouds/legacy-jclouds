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
package org.jclouds.cloudstack.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.defaultNetworkInZone;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.isIsolatedNetwork;
import static org.jclouds.cloudstack.predicates.NetworkPredicates.supportsStaticNAT;

import java.util.Map;

import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;

import com.google.common.collect.Iterables;

/**
 * Convert template options into DeployVirtualMachineOptions, when the target zone has advanced networking.
 *
 * @author Richard Downer
 */
public class AdvancedNetworkOptionsConverter implements OptionsConverter {
   @Override
   public DeployVirtualMachineOptions apply(CloudStackTemplateOptions templateOptions, Map<String, Network> networks, String zoneId, DeployVirtualMachineOptions options) {
      // security groups not allowed.
      // at least one network must be given to CloudStack,
      // but jclouds will try to autodetect an appropriate network if none given.
      checkArgument(templateOptions.getSecurityGroupIds().isEmpty(), "security groups cannot be specified for locations (zones) that use advanced networking");
      if (templateOptions.getNetworkIds().size() > 0) {
         options.networkIds(templateOptions.getNetworkIds());
      } else if (templateOptions.getIpsToNetworks().isEmpty()) {
         checkArgument(!networks.isEmpty(), "please setup a network for zone: " + zoneId);
         Network defaultNetworkInZone = Iterables.getFirst(filter(networks.values(), and(defaultNetworkInZone(zoneId), supportsStaticNAT())), null);
         if(defaultNetworkInZone == null) {
             defaultNetworkInZone = Iterables.getFirst(filter(networks.values(), isIsolatedNetwork()), null);
         }
         if (defaultNetworkInZone == null) {
             throw new IllegalArgumentException("please choose a specific network in zone " + zoneId + ": " + networks);
         } else {
             options.networkId(defaultNetworkInZone.getId());
         }
      }
      return options;
   }
}
