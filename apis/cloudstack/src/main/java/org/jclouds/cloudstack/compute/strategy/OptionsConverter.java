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
 * Convert template options into DeployVirtualMachineOptions. Expressed as an interface, because in
 * CloudStack different zone network types have different requirements when it comes to networks and
 * security groups.
 *
 * @author Richard Downer
 */
public interface OptionsConverter {

   /**
    * Convert a CloudStackTemplateOptions and apply to a DeployVirtualMachineOptions instance.
    *
    * @param templateOptions the input set of options
    * @param networks the networks available
    * @param zoneId the zone of the new virtual machine
    * @param options where the resulting set of options will be applied
    * @return same as "options" parameter
    */
   DeployVirtualMachineOptions apply(CloudStackTemplateOptions templateOptions, Map<String, Network> networks, String zoneId, DeployVirtualMachineOptions options);

}
