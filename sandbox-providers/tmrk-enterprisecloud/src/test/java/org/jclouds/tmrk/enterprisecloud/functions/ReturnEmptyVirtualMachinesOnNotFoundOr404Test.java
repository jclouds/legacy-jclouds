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
package org.jclouds.tmrk.enterprisecloud.functions;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachines;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 * @author Jason King
 */
@Test(groups = "unit", testName = "ReturnEmptyVirtualMachinesOnNotFoundOr404Test")
public class ReturnEmptyVirtualMachinesOnNotFoundOr404Test {

   private ReturnEmptyVirtualMachinesOnNotFoundOr404 function;
   private VirtualMachines expected;

   @BeforeMethod
   public void setUp() {
      function = new ReturnEmptyVirtualMachinesOnNotFoundOr404(new ReturnTrueOn404());
      expected = VirtualMachines.builder().build();
   }

   public void testOn404() {
      VirtualMachines expected = VirtualMachines.builder().build();
      assertEquals(function.apply(new HttpResponseException("response exception", null, new HttpResponse(404, "404 message", null))), expected);
   }

   public void testOnNotFound() {
      VirtualMachines expected = VirtualMachines.builder().build();
      assertEquals(function.apply(new ResourceNotFoundException()),expected);
   }

   public void testOnNotFoundChained() {
      VirtualMachines expected = VirtualMachines.builder().build();
      assertEquals(function.apply(new RuntimeException(new ResourceNotFoundException())),expected);
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testOn500() {
      function.apply(new HttpResponseException("response exception", null, new HttpResponse(500, "500 message", null)));
   }
}
