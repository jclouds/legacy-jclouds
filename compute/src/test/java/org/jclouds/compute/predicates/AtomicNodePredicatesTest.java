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
package org.jclouds.compute.predicates;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.concurrent.atomic.AtomicReference;

import com.google.common.util.concurrent.Atomics;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.domain.LoginCredentials;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests possible uses of NodePredicates
 * 
 * @author Aled Sage, Adrian Cole
 */
@Test(singleThreaded = true, testName = "AtomicNodePredicatesTest")
public class AtomicNodePredicatesTest {

   private NodeMetadata node;
   private GetNodeMetadataStrategy computeService;

   @Test
   public void testNoUpdatesAtomicReferenceOnPass() {
      NodeMetadata running = new NodeMetadataBuilder().id("myid").status(Status.RUNNING).build();
      GetNodeMetadataStrategy computeService = createMock(GetNodeMetadataStrategy.class);

      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(running);
      Assert.assertTrue(nodeRunning.apply(reference));
      Assert.assertEquals(reference.get(), running);

      verify(computeService);

   }

   @Test
   public void testRefreshUpdatesAtomicReferenceOnRecheckPending() {
      NodeMetadata pending = new NodeMetadataBuilder().id("myid").status(Status.PENDING).build();
      GetNodeMetadataStrategy computeService = createMock(GetNodeMetadataStrategy.class);

      expect(computeService.getNode("myid")).andReturn(pending);

      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(pending);
      Assert.assertFalse(nodeRunning.apply(reference));
      Assert.assertEquals(reference.get(), pending);

      verify(computeService);

   }

   @Test
   public void testRefreshUpdatesAtomicReferenceOnRecheckPendingAcceptsNewCredentials() {
      LoginCredentials creds = LoginCredentials.builder().user("user").password("password").build();
      NodeMetadata newNode = new NodeMetadataBuilder().id("myid").status(Status.UNRECOGNIZED).credentials(creds).build();

      LoginCredentials creds2 = LoginCredentials.builder().user("user").password("password2").build();

      NodeMetadata pending = new NodeMetadataBuilder().id("myid").status(Status.PENDING).credentials(creds2).build();
      
      GetNodeMetadataStrategy computeService = createMock(GetNodeMetadataStrategy.class);

      expect(computeService.getNode("myid")).andReturn(pending);

      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(newNode);
      Assert.assertFalse(nodeRunning.apply(reference));
      Assert.assertEquals(reference.get(), pending);

      verify(computeService);
   }
   
   @Test
   public void testRefreshUpdatesAtomicReferenceOnRecheckRunning() {
      NodeMetadata running = new NodeMetadataBuilder().id("myid").status(Status.RUNNING).build();
      NodeMetadata pending = new NodeMetadataBuilder().id("myid").status(Status.PENDING).build();
      GetNodeMetadataStrategy computeService = createMock(GetNodeMetadataStrategy.class);

      expect(computeService.getNode("myid")).andReturn(running);

      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(pending);
      Assert.assertTrue(nodeRunning.apply(reference));
      Assert.assertEquals(reference.get(), running);

      verify(computeService);

   }

   @BeforeMethod
   public void setUp() throws Exception {
      node = createMock(NodeMetadata.class);
      computeService = createMock(GetNodeMetadataStrategy.class);

      expect(node.getId()).andReturn("myid").anyTimes();
      expect(computeService.getNode("myid")).andReturn(node).anyTimes();
      expect(node.getLocation()).andReturn(null).anyTimes();
   }

   @Test
   public void testNodeRunningReturnsTrueWhenRunning() {
      expect(node.getStatus()).andReturn(Status.RUNNING).atLeastOnce();
      expect(node.getBackendStatus()).andReturn(null).atLeastOnce();
      replay(node);
      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(node);
      Assert.assertTrue(nodeRunning.apply(reference));
      Assert.assertEquals(reference.get(), node);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNodeRunningFailsOnTerminated() {
      expect(node.getStatus()).andReturn(Status.TERMINATED).atLeastOnce();
      expect(node.getBackendStatus()).andReturn(null).atLeastOnce();
      replay(node);
      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(node);
      nodeRunning.apply(reference);
      Assert.assertEquals(reference.get(), node);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNodeRunningFailsOnError() {
      expect(node.getStatus()).andReturn(Status.ERROR).atLeastOnce();
      expect(node.getBackendStatus()).andReturn(null).atLeastOnce();
      replay(node);
      replay(computeService);

      AtomicNodeRunning nodeRunning = new AtomicNodeRunning(computeService);
      AtomicReference<NodeMetadata> reference = Atomics.newReference(node);
      nodeRunning.apply(reference);
      Assert.assertEquals(reference.get(), node);
   }
}
