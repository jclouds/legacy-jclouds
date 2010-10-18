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

package org.jclouds.rimuhosting.miro.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Set;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingAddNodeWithTagStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingDestroyNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingGetNodeMetadataStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingListNodesStrategy;
import org.jclouds.rimuhosting.miro.compute.strategy.RimuHostingRebootNodeStrategy;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingDefaultLocationSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingHardwareSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingImageSupplier;
import org.jclouds.rimuhosting.miro.compute.suppliers.RimuHostingLocationSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Injector;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires
 * {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new RimuHostingComputeServiceDependenciesModule());
      super.configure();
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.hardwareId("MIRO1B").osFamily(UBUNTU).os64Bit(false).imageNameMatches(".*10\\.?04.*");
   }

   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return RimuHostingAddNodeWithTagStrategy.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return RimuHostingDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return RimuHostingGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return RimuHostingHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return RimuHostingImageSupplier.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return RimuHostingListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return RimuHostingRebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return RimuHostingLocationSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return RimuHostingDefaultLocationSupplier.class;
   }

}
