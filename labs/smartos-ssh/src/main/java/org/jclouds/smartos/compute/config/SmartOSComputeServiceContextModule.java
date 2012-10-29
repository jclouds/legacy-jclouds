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
package org.jclouds.smartos.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.smartos.SmartOSHostController;
import org.jclouds.smartos.compute.domain.DataSet;
import org.jclouds.smartos.compute.domain.VM;
import org.jclouds.smartos.compute.domain.VmSpecification;
import org.jclouds.smartos.compute.functions.DataSetToImage;
import org.jclouds.smartos.compute.functions.DatacenterToLocation;
import org.jclouds.smartos.compute.functions.VMToNodeMetadata;
import org.jclouds.smartos.compute.functions.VmSpecificationToHardware;
import org.jclouds.smartos.compute.strategy.SmartOSComputeServiceAdapter;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Nigel Magnay
 */
public class SmartOSComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<VM, VmSpecification, DataSet, SmartOSHostController> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VM, VmSpecification, DataSet, SmartOSHostController>>() {
      }).to(SmartOSComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VM, NodeMetadata>>() {
      }).to(VMToNodeMetadata.class);
      bind(new TypeLiteral<Function<DataSet, org.jclouds.compute.domain.Image>>() {
      }).to(DataSetToImage.class);
      bind(new TypeLiteral<Function<VmSpecification, org.jclouds.compute.domain.Hardware>>() {
      }).to(VmSpecificationToHardware.class);
      bind(new TypeLiteral<Function<SmartOSHostController, Location>>() {
      }).to(DatacenterToLocation.class);
      // to have the compute service adapter override default locations
      // install(new LocationsFromComputeServiceAdapterModule<VM, VmSpecification, DataSet,
      // SmartOSHost>(){});

   }
}
