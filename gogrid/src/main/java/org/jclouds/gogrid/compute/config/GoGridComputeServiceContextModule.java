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

package org.jclouds.gogrid.compute.config;

import static org.jclouds.compute.domain.OsFamily.CENTOS;

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
import org.jclouds.gogrid.compute.strategy.GoGridAddNodeWithTagStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridDestroyNodeStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridGetNodeMetadataStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridListNodesStrategy;
import org.jclouds.gogrid.compute.strategy.GoGridRebootNodeStrategy;
import org.jclouds.gogrid.compute.suppliers.GoGridDefaultLocationSupplier;
import org.jclouds.gogrid.compute.suppliers.GoGridHardwareSupplier;
import org.jclouds.gogrid.compute.suppliers.GoGridImageSupplier;
import org.jclouds.gogrid.compute.suppliers.GoGridLocationSupplier;

import com.google.common.base.Supplier;
import com.google.inject.Injector;

/**
 * @author Oleksiy Yarmula
 * @author Adrian Cole
 */
public class GoGridComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new GoGridComputeServiceDependenciesModule());
      super.configure();
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(CENTOS).imageNameMatches(".*w/ None.*");
   }

   @Override
   protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
      return GoGridAddNodeWithTagStrategy.class;
   }

   @Override
   protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
      return GoGridDestroyNodeStrategy.class;
   }

   @Override
   protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
      return GoGridGetNodeMetadataStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
      return GoGridHardwareSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
      return GoGridImageSupplier.class;
   }

   @Override
   protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
      return GoGridListNodesStrategy.class;
   }

   @Override
   protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
      return GoGridRebootNodeStrategy.class;
   }

   @Override
   protected Class<? extends Supplier<Location>> defineDefaultLocationSupplier() {
      return GoGridDefaultLocationSupplier.class;
   }

   @Override
   protected Class<? extends Supplier<Set<? extends Location>>> defineLocationSupplier() {
      return GoGridLocationSupplier.class;
   }
}
