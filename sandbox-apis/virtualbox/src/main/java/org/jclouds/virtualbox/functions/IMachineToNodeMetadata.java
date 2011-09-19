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

import com.google.common.base.Function;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.*;

import javax.annotation.Resource;
import javax.inject.Named;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IMachineToNodeMetadata implements Function<IMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public NodeMetadata apply(@Nullable IMachine vm) {

      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      String s = vm.getName();
      nodeMetadataBuilder.name(s);

      // TODO Set up location properly
      LocationBuilder locationBuilder = new LocationBuilder();
      locationBuilder.description("");
      locationBuilder.id("");
      locationBuilder.scope(LocationScope.HOST);
      nodeMetadataBuilder.location(locationBuilder.build());
      HardwareBuilder hardwareBuilder = new HardwareBuilder();
      hardwareBuilder.ram(vm.getMemorySize().intValue());

      // TODO: Get more processor information
      Set<Processor> processors = new HashSet<Processor>();
      for (int i = 0; i < vm.getCPUCount(); i++) {
         Processor processor = new Processor(1, 0);
         processors.add(processor);
      }
      hardwareBuilder.processors(processors);

      // TODO: How to get this?
      hardwareBuilder.is64Bit(false);

      nodeMetadataBuilder.hostname(vm.getName());
      nodeMetadataBuilder.loginPort(18083);

      Map<MachineState, NodeState> nodeStateMap = new HashMap<MachineState, NodeState>();
      nodeStateMap.put(MachineState.Running, NodeState.RUNNING);

      nodeStateMap.put(MachineState.PoweredOff, NodeState.SUSPENDED);
      nodeStateMap.put(MachineState.DeletingSnapshot, NodeState.PENDING);
      nodeStateMap.put(MachineState.DeletingSnapshotOnline, NodeState.PENDING);
      nodeStateMap.put(MachineState.DeletingSnapshotPaused, NodeState.PENDING);
      nodeStateMap.put(MachineState.FaultTolerantSyncing, NodeState.PENDING);
      nodeStateMap.put(MachineState.LiveSnapshotting, NodeState.PENDING);
      nodeStateMap.put(MachineState.SettingUp, NodeState.PENDING);
      nodeStateMap.put(MachineState.Starting, NodeState.PENDING);
      nodeStateMap.put(MachineState.Stopping, NodeState.PENDING);
      nodeStateMap.put(MachineState.Restoring, NodeState.PENDING);

      // TODO What to map these states to?
      nodeStateMap.put(MachineState.FirstOnline, NodeState.PENDING);
      nodeStateMap.put(MachineState.FirstTransient, NodeState.PENDING);
      nodeStateMap.put(MachineState.LastOnline, NodeState.PENDING);
      nodeStateMap.put(MachineState.LastTransient, NodeState.PENDING);
      nodeStateMap.put(MachineState.Teleported, NodeState.PENDING);
      nodeStateMap.put(MachineState.TeleportingIn, NodeState.PENDING);
      nodeStateMap.put(MachineState.TeleportingPausedVM, NodeState.PENDING);


      nodeStateMap.put(MachineState.Aborted, NodeState.ERROR);
      nodeStateMap.put(MachineState.Stuck, NodeState.ERROR);

      nodeStateMap.put(MachineState.Null, NodeState.UNRECOGNIZED);

      MachineState vmState = vm.getState();
      NodeState nodeState = nodeStateMap.get(vmState);
      if(nodeState == null)
         nodeState = NodeState.UNRECOGNIZED;
      nodeMetadataBuilder.state(nodeState);

      logger.debug("Setting virtualbox node to: " + nodeState + " from machine state: " + vmState);

//      nodeMetadataBuilder.imageId("");
//      nodeMetadataBuilder.group("");

      String provider = "virtualbox";
      String identity = System.getProperty("test." + provider + ".identity", "administrator");
      String credential = System.getProperty("test." + provider + ".credential", "12345");

      nodeMetadataBuilder.credentials(new Credentials(identity, credential));
      nodeMetadataBuilder.id(vm.getId());
      return nodeMetadataBuilder.build();

   }
}
