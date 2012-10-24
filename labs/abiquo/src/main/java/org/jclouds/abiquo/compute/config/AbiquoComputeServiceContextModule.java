/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute.config;

import org.jclouds.abiquo.compute.functions.DatacenterToLocation;
import org.jclouds.abiquo.compute.functions.VirtualDatacenterToLocation;
import org.jclouds.abiquo.compute.functions.VirtualMachineTemplateToImage;
import org.jclouds.abiquo.compute.functions.VirtualMachineTemplateInVirtualDatacenterToHardware;
import org.jclouds.abiquo.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.abiquo.compute.options.AbiquoTemplateOptions;
import org.jclouds.abiquo.compute.strategy.AbiquoComputeServiceAdapter;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplateInVirtualDatacenter;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;

import com.google.common.base.Function;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Abiquo Compute service configuration module.
 * 
 * @author Ignasi Barrera
 */
public class AbiquoComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<VirtualMachine, VirtualMachineTemplateInVirtualDatacenter, VirtualMachineTemplate, VirtualDatacenter> {

   @Override
   protected void configure() {
      super.configure();
      bind(
            new TypeLiteral<ComputeServiceAdapter<VirtualMachine, VirtualMachineTemplateInVirtualDatacenter, VirtualMachineTemplate, VirtualDatacenter>>() {
            }).to(AbiquoComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<VirtualMachineTemplate, Image>>() {
      }).to(VirtualMachineTemplateToImage.class);
      bind(new TypeLiteral<Function<VirtualMachineTemplateInVirtualDatacenter, Hardware>>() {
      }).to(VirtualMachineTemplateInVirtualDatacenterToHardware.class);
      bind(new TypeLiteral<Function<Datacenter, Location>>() {
      }).to(DatacenterToLocation.class);
      bind(new TypeLiteral<Function<VirtualDatacenter, Location>>() {
      }).to(VirtualDatacenterToLocation.class);
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
      bind(TemplateOptions.class).to(AbiquoTemplateOptions.class);
      install(new LocationsFromComputeServiceAdapterModule<VirtualMachine, VirtualMachineTemplateInVirtualDatacenter, VirtualMachineTemplate, VirtualDatacenter>() {
      });
   }

}
