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

import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.VirtualBox;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.testng.annotations.Test;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.VirtualBoxManager;

import java.util.Map;
import java.util.Set;

//@Test(groups = "live")
public class IMachineToNodeMetadataTest {

   @Test
   public void testCreate() throws Exception {

      Credentials creds = new Credentials("admin", "123456");
      VirtualBoxManager manager = VirtualBoxManager.createInstance("");

      Map<MachineState, NodeState> machineToNodeState = VirtualBoxComputeServiceContextModule.machineToNodeState;
      Set<Image> images = ImmutableSet.of();
      Set<org.jclouds.compute.domain.Hardware> hardwares = ImmutableSet.of();

      VirtualBox virtualBox = new VirtualBox();
      IMachineToNodeMetadata parser = new IMachineToNodeMetadata();
      IMachineToHardware hwParser = new IMachineToHardware(virtualBox);

//      hwParser.apply()

   }
}
