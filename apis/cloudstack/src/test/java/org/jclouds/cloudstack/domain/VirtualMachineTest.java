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
package org.jclouds.cloudstack.domain;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for the VirtualMachine class.
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "VirtualMachineTest")
public class VirtualMachineTest {

   @Test(groups = "unit", enabled = true)
   public void testCpuUsed() {
      // Class under test should detect if the % is missing off the end
      boolean caught = false;
      try { VirtualMachine.builder().cpuUsed("23.4").build(); } catch (Exception e) { caught = true; }
      assertTrue(caught);

      // If CpuUsed is not specified at all, that's OK
      caught = false;
      try { VirtualMachine.builder().build(); } catch (Exception e) { caught = true; }
      assertFalse(caught);

      // Retrieving CpuUsed should just give us a straightforward float
      VirtualMachine vm = VirtualMachine.builder().cpuUsed("23.4%").build();
      assertEquals(vm.getCpuUsed(), 23.4, 0.01);
   }

}
