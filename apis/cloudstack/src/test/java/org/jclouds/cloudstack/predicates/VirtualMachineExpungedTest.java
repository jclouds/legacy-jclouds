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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.VirtualMachineApi;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit", singleThreaded = true, testName = "VirtualMachineExpungedTest")
public class VirtualMachineExpungedTest {

   CloudStackApi client;
   VirtualMachineApi virtualMachineClient;

   @BeforeMethod
   public void setUp() {
      client = createMock(CloudStackApi.class);
      virtualMachineClient = createMock(VirtualMachineApi.class);
      expect(client.getVirtualMachineApi()).andReturn(virtualMachineClient);
   }

   @Test
   public void testWaitForVirtualMachineToBeExpunged() {
      VirtualMachine virtualMachine = VirtualMachine.builder().id("229").build();
      expect(virtualMachineClient.getVirtualMachine(virtualMachine.getId())).andReturn(null);

      replay(client, virtualMachineClient);
      assertTrue(new VirtualMachineExpunged(client).apply(virtualMachine));
      verify(client, virtualMachineClient);
   }

   @Test
   public void testNoRemovedYet() {
      VirtualMachine virtualMachine = VirtualMachine.builder().id("229").build();
      expect(virtualMachineClient.getVirtualMachine(virtualMachine.getId())).andReturn(virtualMachine);

      replay(client, virtualMachineClient);
      assertFalse(new VirtualMachineExpunged(client).apply(virtualMachine));
      verify(client, virtualMachineClient);
   }
}
