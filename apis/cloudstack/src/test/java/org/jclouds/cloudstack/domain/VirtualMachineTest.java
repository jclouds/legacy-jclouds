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
package org.jclouds.cloudstack.domain;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

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
      try { 
         VirtualMachine.builder().id("1").cpuUsed("23.4").build();
         fail("Should have thrown an exception due to % being missing!");
      } catch (Exception e) { 
      }

      // If CpuUsed is not specified at all, that's OK
      VirtualMachine vm = VirtualMachine.builder().id("2").build();
      assertEquals(vm.getCpuUsed(), 0.0f);

      // Retrieving CpuUsed should just give us a straightforward float
      vm = VirtualMachine.builder().id("3").cpuUsed("23.4%").build();
      assertEquals(vm.getCpuUsed(), 23.4, 0.01);
      
      //Allow ',' as decimal separator
      vm = VirtualMachine.builder().id("4").cpuUsed("23,4%").build();
      assertEquals(vm.getCpuUsed(), 23.4, 0.01);
   }

}
