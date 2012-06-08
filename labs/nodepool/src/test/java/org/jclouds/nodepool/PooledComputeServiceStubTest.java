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

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.StubComputeServiceIntegrationTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;

/**
 * This tests the operation of the {@link PooledComputeService} implementation using the jclouds
 * stub provider as the backing {@link ComputeService}.
 * 
 * TODO live tests backed by actual cloud providers
 * 
 * @author Andrew Kennedy
 * @see AppTest
 */
@Test(singleThreaded = true, testName = "PooledComputeServiceStubTest")
public class PooledComputeServiceStubTest extends StubComputeServiceIntegrationTest {

   protected PooledComputeService pool;

   public PooledComputeServiceStubTest() {
      provider = "stub";
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      ComputeServiceContext pooledCtx = ContextBuilder.newBuilder("pooled").build(ComputeServiceContext.class);

      // pool = new EagerFixedSizePooledComputeService(client,
      // client.templateBuilder().any().build());
      try {
         ((PooledComputeService) client).startPool();
      } catch (RunNodesException e) {
         Throwables.propagate(e);
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      try {
         pool.close();
      } catch (Exception e) {
         Throwables.propagate(e);
      }

      super.tearDownContext();
   }
}