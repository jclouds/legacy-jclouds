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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.VirtualMachine.State;
import org.jclouds.cloudstack.features.VirtualMachineClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit", singleThreaded = true, testName = "VirtualMachineRunningTest")
public class VirtualMachineRunningTest {

   CloudStackClient client;
   VirtualMachineClient virtualMachineClient;

   @BeforeMethod
   public void setUp() {
      client = createMock(CloudStackClient.class);
      virtualMachineClient = createMock(VirtualMachineClient.class);

      expect(client.getVirtualMachineClient()).andReturn(virtualMachineClient);
   }

   @DataProvider(name = "virtualMachineStates")
   public Object[][] virtualMachineStates() {
      return new Object[][]{
         {State.RUNNING, true},
         {State.STARTING, false},
         {State.STOPPING, false},
         {State.STOPPED, false},
         {State.SHUTDOWNED, false},
         {State.DESTROYED, false},
         {State.EXPUNGING, false},
         {State.MIGRATING, false}
      };
   }

   @Test(dataProvider = "virtualMachineStates")
   public void testWaitForVirtualMachineToBeRunning(State state, boolean expected) {
      assertPredicateResult(state, expected);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testThrowExceptionOnErrorState() {
      assertPredicateResult(State.ERROR, true);
   }

   private void assertPredicateResult(State state, boolean expected) {
      String virtualMachineId = "229";
      VirtualMachine virtualMachine = VirtualMachine.builder().
         id(virtualMachineId).state(state).build();

      expect(virtualMachineClient.getVirtualMachine(virtualMachineId)).andReturn(virtualMachine);
      replay(client, virtualMachineClient);

      assertEquals(new VirtualMachineRunning(client).apply(virtualMachine), expected);
      verify(client, virtualMachineClient);
   }
}
