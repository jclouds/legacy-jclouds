/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute;

import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubAddNodeWithTagStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubDestroyNodeStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubGetNodeMetadataStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubHardwareSupplier;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubImageSupplier;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubListNodesStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubRebootNodeStrategy;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit")
public class ComputeServiceContextFactoryTest {

   @Test
   public void testStandalone() {
      ComputeServiceContext context = ComputeServiceContextFactory
               .createStandaloneContext(new StubComputeServiceContextModule());
      context.getComputeService().listNodes();
   }

   @Test
   public void testStandaloneWithBuilder() {
      ComputeServiceContext context = ComputeServiceContextFactory
               .createStandaloneContext(StandaloneComputeServiceContextModule.builder().install(
                        new StubComputeServiceDependenciesModule()).defineAddNodeWithTagStrategy(
                        StubAddNodeWithTagStrategy.class).defineDestroyNodeStrategy(StubDestroyNodeStrategy.class)
                        .defineGetNodeMetadataStrategy(StubGetNodeMetadataStrategy.class).defineListNodesStrategy(
                                 StubListNodesStrategy.class).defineRebootNodeStrategy(StubRebootNodeStrategy.class)
                        .defineHardwareSupplier(StubHardwareSupplier.class)
                        .defineImageSupplier(StubImageSupplier.class).build());
      context.getComputeService().listNodes();
   }
}
