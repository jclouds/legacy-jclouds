/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.predicates.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.cloud.VirtualMachine;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Predicate;

/**
 * Container for {@link VirtualMachine} filters.
 * 
 * @author Ignasi Barrera
 */
public class VirtualMachinePredicates {
   public static Predicate<VirtualMachine> internalName(final String... internalName) {
      checkNotNull(internalName, "names must be defined");

      return new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(final VirtualMachine virtualMachine) {
            return Arrays.asList(internalName).contains(virtualMachine.getInternalName());
         }
      };
   }

   public static Predicate<VirtualMachine> nameLabel(final String... nameLabels) {
      checkNotNull(nameLabels, "names must be defined");

      return new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(final VirtualMachine virtualMachine) {
            return Arrays.asList(nameLabels).contains(virtualMachine.getNameLabel());
         }
      };
   }

   public static Predicate<VirtualMachine> state(final VirtualMachineState... states) {
      checkNotNull(states, "states must be defined");

      return new Predicate<VirtualMachine>() {
         @Override
         public boolean apply(final VirtualMachine virtualMachine) {
            // The getState() method will generate an API call
            return Arrays.asList(states).contains(virtualMachine.getState());
         }
      };
   }
}
