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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.VirtualMachine.State;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a virtualMachine is running
 * 
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineRunning implements Predicate<VirtualMachine> {

   private final CloudStackApi client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VirtualMachineRunning(CloudStackApi client) {
      this.client = client;
   }

   public boolean apply(VirtualMachine virtualMachine) {
      logger.trace("looking for state on virtualMachine %s", checkNotNull(virtualMachine, "virtualMachine"));
      virtualMachine = refresh(virtualMachine);
      if (virtualMachine == null)
         return false;
      logger.trace("%s: looking for virtualMachine state %s: currently: %s", virtualMachine.getId(), State.RUNNING,
            virtualMachine.getState());
      if (virtualMachine.getState() == State.ERROR)
         throw new IllegalStateException("virtualMachine in error state: " + virtualMachine);
      return virtualMachine.getState() == State.RUNNING;
   }

   private VirtualMachine refresh(VirtualMachine virtualMachine) {
      return client.getVirtualMachineApi().getVirtualMachine(virtualMachine.getId());
   }
}
