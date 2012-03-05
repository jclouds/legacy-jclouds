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

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class NodeCreator implements Function<NodeSpec, NodeAndInitialCredentials<IMachine>> {

  private final Supplier<VirtualBoxManager>   manager;
  private final Function<CloneSpec, IMachine> cloner;

  @Inject
  public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner) {
    this.manager = manager;
    this.cloner = cloner;
  }

  @Override
  public NodeAndInitialCredentials<IMachine> apply(NodeSpec nodeSpec) {
    
    Master master = nodeSpec.getMaster();
    
    if (master.getMachine().getCurrentSnapshot() != null) {
      ISession session = manager.get().openMachineSession(master.getMachine());
      session.getConsole().deleteSnapshot(master.getMachine().getCurrentSnapshot().getId());
      session.unlockMachine();
    }
    
    
    
  }
}
