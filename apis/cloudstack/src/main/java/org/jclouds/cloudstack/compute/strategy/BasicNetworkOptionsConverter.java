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

import java.util.Map;

import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;

/**
 * Convert template options into DeployVirtualMachineOptions, when the target zone has basic networking.
 *
 * @author Richard Downer
 */
public class BasicNetworkOptionsConverter implements OptionsConverter {
   @Override
   public DeployVirtualMachineOptions apply(CloudStackTemplateOptions templateOptions, Map<String, Network> networks, String zoneId, DeployVirtualMachineOptions options) {
      // both security groups and networks are optional, and CloudStack will
      // use the zone/user's default network/security group if none given
      if (templateOptions.getSecurityGroupIds().size() > 0) {
         options.securityGroupIds(templateOptions.getSecurityGroupIds());
      }
      if (templateOptions.getNetworkIds().size() > 0) {
         options.networkIds(templateOptions.getNetworkIds());
      }
      return options;
   }
}
