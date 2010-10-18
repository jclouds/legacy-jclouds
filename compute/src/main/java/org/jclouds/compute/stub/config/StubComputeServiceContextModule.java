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

package org.jclouds.compute.stub.config;

import java.util.Set;

import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubAddNodeWithTagStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubDestroyNodeStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubGetNodeMetadataStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubHardwareSupplier;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubImageSupplier;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubListNodesStrategy;
import org.jclouds.compute.stub.config.StubComputeServiceDependenciesModule.StubRebootNodeStrategy;
import org.jclouds.concurrent.SingleThreaded;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@SingleThreaded
public class StubComputeServiceContextModule extends StandaloneComputeServiceContextModule {
   @Override
   protected void configure() {
      install(new StubComputeServiceDependenciesModule());
      super.configure();
   }

   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return StubAddNodeWithTagStrategy.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return StubDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return StubGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return StubListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return StubRebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return StubHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return StubImageSupplier.class;
   }

}
