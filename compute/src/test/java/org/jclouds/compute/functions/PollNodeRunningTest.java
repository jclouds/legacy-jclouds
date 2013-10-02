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
package org.jclouds.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.predicates.AtomicNodeRunning;
import org.jclouds.compute.reference.ComputeServiceConstants.PollPeriod;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.Atomics;

/**
 * @author Adrian Cole
 */
@Test
public class PollNodeRunningTest {

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "node\\(id\\) didn't achieve the status running; aborting after 0 seconds with final status: PENDING")
   public void testIllegalStateExceptionWhenNodeStillPending() {
      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids("id").status(Status.PENDING).build();

      // node always stays pending
      Predicate<AtomicReference<NodeMetadata>> nodeRunning = new Predicate<AtomicReference<NodeMetadata>>() {

         @Override
         public boolean apply(AtomicReference<NodeMetadata> input) {
            assertEquals(input.get(), pendingNode);
            return false;
         }

      };

      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);
      try {
         new PollNodeRunning(nodeRunning).apply(atomicNode);
      } finally {
         assertEquals(atomicNode.get(), pendingNode);
      }
   }

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "node\\(id\\) terminated")
   public void testIllegalStateExceptionWhenNodeDied() {
      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids("id").status(Status.PENDING).build();
      final NodeMetadata deadNode = new NodeMetadataBuilder().ids("id").status(Status.TERMINATED).build();

      Predicate<AtomicReference<NodeMetadata>> nodeRunning = new Predicate<AtomicReference<NodeMetadata>>() {

         @Override
         public boolean apply(AtomicReference<NodeMetadata> input) {
            assertEquals(input.get(), pendingNode);
            input.set(deadNode);
            return false;
         }

      };

      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);
      try {
         new PollNodeRunning(nodeRunning).apply(atomicNode);
      } finally {
         assertEquals(atomicNode.get(), deadNode);
      }
   }

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "api response for node\\(id\\) was null")
   public void testIllegalStateExceptionAndNodeResetWhenRefSetToNull() {
      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids("id").status(Status.PENDING).build();

      Predicate<AtomicReference<NodeMetadata>> nodeRunning = new Predicate<AtomicReference<NodeMetadata>>() {

         @Override
         public boolean apply(AtomicReference<NodeMetadata> input) {
            assertEquals(input.get(), pendingNode);
            input.set(null);
            return false;
         }

      };

      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);
      try {
         new PollNodeRunning(nodeRunning).apply(atomicNode);
      } finally {
         assertEquals(atomicNode.get(), pendingNode);
      }
   }

   public void testRecoversWhenTemporarilyNodeNotFound() {
      String nodeId = "myid";
      Timeouts timeouts = new Timeouts();

      PollPeriod period = new PollPeriod();

      final NodeMetadata pendingNode = new NodeMetadataBuilder().ids(nodeId).status(Status.PENDING).build();
      final NodeMetadata runningNode = new NodeMetadataBuilder().ids(nodeId).status(Status.RUNNING).build();
      GetNodeMetadataStrategy nodeClient = createMock(GetNodeMetadataStrategy.class);
      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(nodeClient);
      Predicate<AtomicReference<NodeMetadata>> retryableNodeRunning = new ComputeServiceTimeoutsModule() {
         public Predicate<AtomicReference<NodeMetadata>> nodeRunning(AtomicNodeRunning statusRunning,
               Timeouts timeouts, PollPeriod period) {
            return super.nodeRunning(statusRunning, timeouts, period);
         }
      }.nodeRunning(nodeRunning, timeouts, period);
      AtomicReference<NodeMetadata> atomicNode = Atomics.newReference(pendingNode);

      // Simulate transient error: first call returns null; subsequent calls
      // return the running node
      EasyMock.expect(nodeClient.getNode(nodeId)).andAnswer(new IAnswer<NodeMetadata>() {
         private int count = 0;

         @Override
         public NodeMetadata answer() throws Throwable {
            count++;
            if (count <= 1) {
               return null;
            } else {
               return runningNode;
            }
         }
      }).anyTimes();

      // replay mocks
      replay(nodeClient);

      // run
      new PollNodeRunning(retryableNodeRunning).apply(atomicNode);

      assertEquals(atomicNode.get().getStatus(), Status.RUNNING);

      // verify mocks
      verify(nodeClient);
   }
}
