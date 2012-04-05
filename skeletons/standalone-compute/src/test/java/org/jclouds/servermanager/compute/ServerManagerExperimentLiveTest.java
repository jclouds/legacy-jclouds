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
package org.jclouds.servermanager.compute;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.ServerManagerApiMetadata;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ServerManagerExperimentLiveTest")
public class ServerManagerExperimentLiveTest
      extends
      BaseComputeServiceContextLiveTest<ServerManager, ServerManager, ComputeServiceContext<ServerManager, ServerManager>> {

   public ServerManagerExperimentLiveTest() {
      provider = "servermanager";
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext<ServerManager, ServerManager> context = null;
      try {
         context = ComputeServiceContextBuilder.newBuilder(new ServerManagerApiMetadata()).build();

         context.getComputeService().listNodes();

      } finally {
         if (context != null)
            context.close();
      }
   }

}
