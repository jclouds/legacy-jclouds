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
package org.jclouds.glesys.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.GleSYSProviderMetadata;
import org.testng.annotations.Test;

import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "GleSYSExperimentLiveTest")
public class GleSYSExperimentLiveTest
      extends
      BaseComputeServiceContextLiveTest<GleSYSClient, GleSYSAsyncClient, ComputeServiceContext<GleSYSClient, GleSYSAsyncClient>> {

   public GleSYSExperimentLiveTest() {
      provider = "glesys";
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext<GleSYSClient, GleSYSAsyncClient> context = null;
      try {

         context = ComputeServiceContextBuilder
               .newBuilder(new GleSYSProviderMetadata())
               .overrides(setupProperties())
               .modules(setupModules()).build();

         assertEquals(context.getComputeService().listAssignableLocations().size(), 4);

      } finally {
         Closeables.closeQuietly(context);
      }
   }

}
