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
package org.jclouds.cloudservers.compute;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.domain.LocationScope;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "CloudServersComputeServiceLiveTest")
public class CloudServersComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public CloudServersComputeServiceLiveTest() {
      provider = "cloudservers";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected void checkNodes(Iterable<? extends NodeMetadata> nodes, String group, String task) throws IOException {
      super.checkNodes(nodes, group, task);
      for (NodeMetadata node : nodes) {
         assertEquals(node.getLocation().getScope(), LocationScope.HOST);
      }
   }

   @Test(enabled = true, dependsOnMethods = "testReboot", expectedExceptions = UnsupportedOperationException.class)
   public void testSuspendResume() throws Exception {
      super.testSuspendResume();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodesByIds() throws Exception {
      super.testListNodesByIds();
   }

   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails", "listNodesByIds" })
   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }
}
