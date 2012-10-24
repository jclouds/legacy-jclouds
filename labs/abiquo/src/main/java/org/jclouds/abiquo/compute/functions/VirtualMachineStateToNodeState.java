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

package org.jclouds.abiquo.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata.Status;

import com.abiquo.server.core.cloud.VirtualMachineState;
import com.google.common.base.Function;

/**
 * Converts a {@link VirtualMachineState} object to a {@link Status} one.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class VirtualMachineStateToNodeState implements Function<VirtualMachineState, Status> {

   @Override
   public Status apply(final VirtualMachineState state) {
      switch (state) {
         case ALLOCATED:
         case LOCKED:
         case CONFIGURED:
         case NOT_ALLOCATED:
            return Status.PENDING;
         case ON:
            return Status.RUNNING;
         case OFF:
         case PAUSED:
            return Status.SUSPENDED;
         case UNKNOWN:
         default:
            return Status.UNRECOGNIZED;
      }
   }
}
