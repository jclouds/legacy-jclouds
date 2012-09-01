/*
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

package org.jclouds.nodepool;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import static org.jclouds.nodepool.config.NodePoolProperties.BASEDIR;
import static org.jclouds.nodepool.config.NodePoolProperties.MAX_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.MIN_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.POOL_ADMIN_ACCESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Properties;

import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.inject.Module;

public class NodePoolComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   private final String basedir = "target/" + this.getClass().getSimpleName().toLowerCase();

   public NodePoolComputeServiceLiveTest() {
      provider = "nodepool";
   }

   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty(BASEDIR, basedir);
      contextProperties.setProperty("nodepool.identity", "pooluser");
      contextProperties.setProperty(POOL_ADMIN_ACCESS, "adminUsername=pooluser,adminPassword=poolpassword");
      contextProperties.setProperty(TIMEOUT_SCRIPT_COMPLETE, (1200 * 1000) + "");
      contextProperties.setProperty(TIMEOUT_PORT_OPEN, (1200 * 1000) + "");
      contextProperties.setProperty(BASEDIR, basedir);
      contextProperties.setProperty(POOL_ADMIN_ACCESS, "adminUsername=pooluser,adminPassword=poolpassword");
      contextProperties.setProperty(MAX_SIZE, 2 + "");
      contextProperties.setProperty(MIN_SIZE, 1 + "");
      return contextProperties;
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      Closeables.closeQuietly(context);
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   @Test(enabled = true, groups = "live")
   public void testCreateAndRunAService() throws Exception {
      createAndRunAServiceInGroup(group);
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testCreateAndRunAService")
   public void testRebuildPoolStateFromStore() {
      tearDownContext();
      setupContext();
      assertSame(client.listNodes().size(), 1);
      assertEquals(((NodeMetadata) Iterables.get(client.listNodes(), 0)).getGroup(), this.group);
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testRebuildPoolStateFromStore")
   public void testIncreasePoolAllowed() throws RunNodesException {
      client.createNodesInGroup(this.group, 1);
      assertSame(client.listNodes().size(), 2);
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testIncreasePoolAllowed")
   public void testIncreasePoolNotAllowed() throws RunNodesException {
      boolean caughtException = false;
      try {
         client.createNodesInGroup(this.group, 1);
      } catch (Exception e) {
         caughtException = true;
      }
      assertTrue(caughtException, "expected an exception to be thrown");
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testIncreasePoolNotAllowed")
   public void testGetBackendComputeServiceContext() {
      NodePoolComputeServiceContext ctx = context.utils().injector().getInstance(NodePoolComputeServiceContext.class);
      assertNotNull(ctx.getBackendContext());
      assertSame(
               Sets.filter(ctx.getBackendContext().getComputeService().listNodesDetailsMatching(NodePredicates.all()),
                        NodePredicates.inGroup(ctx.getPoolGroupName())).size(), 2);
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testGetBackendComputeServiceContext")
   public void testDestroyPoolNodes() {
      client.destroyNodesMatching(NodePredicates.inGroup(this.group));
      // after we destroy all nodes we should still have minsize nodes in the pool
      NodePoolComputeServiceContext ctx = context.utils().injector().getInstance(NodePoolComputeServiceContext.class);
      assertSame(ctx.getPoolStats().currentSize(), 1);
   }

   @Test(enabled = true, groups = "live", dependsOnMethods = "testDestroyPoolNodes")
   public void testDestroyPool() {
      // TODO get the ctx without the injector
      NodePoolComputeServiceContext ctx = context.utils().injector().getInstance(NodePoolComputeServiceContext.class);
      ctx.destroyPool();
      assertSame(
               Sets.filter(ctx.getBackendContext().getComputeService().listNodesDetailsMatching(NodePredicates.all()),
                        NodePredicates.inGroup(ctx.getPoolGroupName())).size(), 0);
   }

   @Override
   @Test(enabled = false)
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testCompareSizes() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
   }

   @Override
   @Test(enabled = false, expectedExceptions = AuthorizationException.class)
   public void testCorrectAuthException() throws Exception {
   }

   @Override
   @Test(enabled = false, expectedExceptions = NoSuchElementException.class)
   public void testCorrectExceptionRunningNodesNotFound() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testCreateTwoNodesWithRunScript")
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testConcurrentUseOfComputeServiceToCreateNodes")
   public void testCreateTwoNodesWithRunScript() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testCredentialsCache() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails" })
   public void testDestroyNodes() {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
   }

   @Override
   @Test(enabled = false, groups = { "integration", "live" })
   public void testGetAssignableLocations() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testImageById() {
   }

   @Override
   @Test(enabled = false)
   public void testImagesCache() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testListImages() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testListSizes() throws Exception {
   }

   @Override
   @Test(enabled = false)
   public void testOptionToNotBlock() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
   }

   @Override
   @Test(enabled = false, dependsOnMethods = "testImagesCache")
   public void testTemplateMatch() throws Exception {
   }

}
