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
package org.jclouds.compute.config;

import static com.google.common.base.Predicates.not;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodeRunning;
import org.jclouds.compute.predicates.NodeSuspended;
import org.jclouds.compute.predicates.NodeTerminated;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ComputeServiceTimeoutsModule extends AbstractModule {

   @Provides
   @Singleton
   @Named("NODE_RUNNING")
   protected Predicate<NodeMetadata> nodeRunning(NodeRunning stateRunning, Timeouts timeouts) {
      return timeouts.nodeRunning == 0 ? stateRunning : new RetryablePredicate<NodeMetadata>(stateRunning,
            timeouts.nodeRunning);
   }

   @Provides
   @Singleton
   @Named("NODE_TERMINATED")
   protected Predicate<NodeMetadata> serverTerminated(NodeTerminated stateTerminated, Timeouts timeouts) {
      return timeouts.nodeTerminated == 0 ? stateTerminated : new RetryablePredicate<NodeMetadata>(stateTerminated,
            timeouts.nodeTerminated);
   }
   

   @Provides
   @Singleton
   @Named("NODE_SUSPENDED")
   protected Predicate<NodeMetadata> serverSuspended(NodeSuspended stateSuspended, Timeouts timeouts) {
      return timeouts.nodeSuspended == 0 ? stateSuspended : new RetryablePredicate<NodeMetadata>(stateSuspended,
            timeouts.nodeSuspended);
   }

   @Provides
   @Singleton
   @Named("SCRIPT_COMPLETE")
   protected Predicate<CommandUsingClient> runScriptRunning(ScriptStatusReturnsZero stateRunning, Timeouts timeouts) {
      return timeouts.scriptComplete == 0 ? not(stateRunning) : new RetryablePredicate<CommandUsingClient>(
            not(stateRunning), timeouts.scriptComplete);
   }

   @Override
   protected void configure() {

   }
}