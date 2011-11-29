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
package org.jclouds.cloudstack.predicates;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.VirtualMachine.State;
import org.jclouds.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * Tests to see if a virtualMachine is expunged from the system
 * 
 * @author Andrei Savu
 */
@Singleton
public class VirtualMachineExpunged implements Predicate<VirtualMachine> {

   private final CloudStackClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VirtualMachineExpunged(CloudStackClient client) {
      this.client = client;
   }

   public boolean apply(VirtualMachine virtualMachine) {
      logger.trace("looking for state on virtualMachine %s", checkNotNull(virtualMachine, "virtualMachine"));
      return refresh(virtualMachine) == null;
   }

   private VirtualMachine refresh(VirtualMachine virtualMachine) {
      return client.getVirtualMachineClient().getVirtualMachine(virtualMachine.getId());
   }
}
