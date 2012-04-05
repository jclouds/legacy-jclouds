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
package org.jclouds.opsource.servers.internal;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.opsource.servers.OpSourceServersAsyncClient;
import org.jclouds.opsource.servers.OpSourceServersClient;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link OpSourceServersClient} and acts as parent for other
 * client live tests.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public abstract class BaseOpSourceServersClientLiveTest
      extends
      BaseComputeServiceContextLiveTest<OpSourceServersClient, OpSourceServersAsyncClient, ComputeServiceContext<OpSourceServersClient, OpSourceServersAsyncClient>> {

   protected BaseOpSourceServersClientLiveTest() {
      provider = "opsource-servers";
   }

   protected RestContext<OpSourceServersClient, OpSourceServersAsyncClient> restContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      restContext = context.getProviderSpecificContext();
   }

}
