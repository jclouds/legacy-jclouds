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

import static org.jclouds.cloudstack.predicates.PublicIPAddressPredicates.available;

import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PublicIPAddressPredicatesTest {

   public void testIsAvailableWhenAllocated() {
      PublicIPAddress address = PublicIPAddress.builder().state(PublicIPAddress.State.ALLOCATED).id("204").build();

      assert available().apply(address);

   }

   public void testIsNotAvailableWhenNotAllocated() {
      PublicIPAddress address = PublicIPAddress.builder().state(PublicIPAddress.State.ALLOCATING).id("204").build();

      assert !available().apply(address);

   }

   public void testIsNotAvailableWhenAssignedToVM() {
      PublicIPAddress address = PublicIPAddress.builder().state(PublicIPAddress.State.ALLOCATED).virtualMachineId("1")
            .id("204").build();

      assert !available().apply(address);

   }

   public void testIsNotAvailableWhenSourceNAT() {
      PublicIPAddress address = PublicIPAddress.builder().state(PublicIPAddress.State.ALLOCATED).isSourceNAT(true)
            .id("204").build();

      assert !available().apply(address);

   }

   public void testIsNotAvailableWhenStaticNAT() {
      PublicIPAddress address = PublicIPAddress.builder().state(PublicIPAddress.State.ALLOCATED).isStaticNAT(true)
            .id("204").build();

      assert !available().apply(address);

   }
}
