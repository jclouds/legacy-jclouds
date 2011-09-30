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
package org.jclouds.compute.predicates;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests possible uses of OperatingSystemPredicates
 * 
 * @author Aled Sage
 */
@Test
public class NodePredicatesTest {

   private NodeMetadata node;
   private ComputeService computeService;

   @BeforeMethod
   public void setUp() throws Exception {
      node = createMock(NodeMetadata.class);
      computeService = createMock(ComputeService.class);
      
      expect(node.getId()).andReturn("myid").anyTimes();
      expect(computeService.getNodeMetadata("myid")).andReturn(node).anyTimes();
      expect(node.getLocation()).andReturn(null).anyTimes();
   }
   
   @Test
   public void testNodeRunningReturnsTrueWhenRunning() {
       expect(node.getState()).andReturn(NodeState.RUNNING).atLeastOnce();
       replay(node);
       replay(computeService);
	   
	   NodeRunning nodeRunning = new NodeRunning(computeService);
	   Assert.assertTrue(nodeRunning.apply(node));
   }

   @Test(expectedExceptions=IllegalStateException.class)
   public void testNodeRunningFailsOnTerminated() {
	   expect(node.getState()).andReturn(NodeState.TERMINATED).atLeastOnce();
	   replay(node);
       replay(computeService);
	   
	   NodeRunning nodeRunning = new NodeRunning(computeService);
	   nodeRunning.apply(node);
   }
   
   @Test(expectedExceptions=IllegalStateException.class)
   public void testNodeRunningFailsOnError() {
       expect(node.getState()).andReturn(NodeState.ERROR).atLeastOnce();
       replay(node);
       replay(computeService);

       NodeRunning nodeRunning = new NodeRunning(computeService);
       nodeRunning.apply(node);
   }
}
