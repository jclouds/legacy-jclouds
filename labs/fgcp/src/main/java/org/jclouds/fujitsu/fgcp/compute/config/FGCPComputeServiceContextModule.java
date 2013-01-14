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
package org.jclouds.fujitsu.fgcp.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.fujitsu.fgcp.compute.functions.CPUToProcessor;
import org.jclouds.fujitsu.fgcp.compute.functions.DiskImageToImage;
import org.jclouds.fujitsu.fgcp.compute.functions.DiskImageToOperatingSystem;
import org.jclouds.fujitsu.fgcp.compute.functions.DiskToVolume;
import org.jclouds.fujitsu.fgcp.compute.functions.ServerTypeToHardware;
import org.jclouds.fujitsu.fgcp.compute.functions.VServerMetadataToNodeMetadata;
import org.jclouds.fujitsu.fgcp.compute.options.FGCPTemplateOptions;
import org.jclouds.fujitsu.fgcp.compute.strategy.FGCPComputeServiceAdapter;
import org.jclouds.fujitsu.fgcp.compute.strategy.VServerMetadata;
import org.jclouds.fujitsu.fgcp.domain.CPU;
import org.jclouds.fujitsu.fgcp.domain.Disk;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.functions.IdentityFunction;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
//import org.jclouds.fujitsu.fgcp.compute.predicates.ServerStopped;

/**
 * Added in FGCPContextBuilder
 * 
 * @author Dies Koper
 */
public class FGCPComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<VServerMetadata, ServerType, DiskImage, Location> {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
      // installDependencies();

      bind(
            new TypeLiteral<ComputeServiceAdapter<VServerMetadata, ServerType, DiskImage, Location>>() {
            }).to(FGCPComputeServiceAdapter.class);

      // the following bind functions that map FGCP domain specific resources
      // to jclouds'
      bind(new TypeLiteral<Function<VServerMetadata, NodeMetadata>>() {
      }).to(VServerMetadataToNodeMetadata.class);
      bind(new TypeLiteral<Function<DiskImage, Image>>() {
      }).to(DiskImageToImage.class);
      bind(new TypeLiteral<Function<DiskImage, OperatingSystem>>() {
      }).to(DiskImageToOperatingSystem.class);
      bind(new TypeLiteral<Function<ServerType, Hardware>>() {
      }).to(ServerTypeToHardware.class);
      bind(new TypeLiteral<Function<Disk, Volume>>() {
      }).to(DiskToVolume.class);
      bind(new TypeLiteral<Function<CPU, Processor>>() {
      }).to(CPUToProcessor.class);

      // we aren't converting hardware from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to((Class) IdentityFunction.class);

      bind(TemplateOptions.class).to(FGCPTemplateOptions.class);


      // bind(new TypeLiteral<Predicate<String>>() {
      // }).to((Class) ServerStopped.class);

      // need to look into the following later for to map (create) jclouds'
      // location to FGCP.
      // see LocationScope:
      // PROVIDER: FGCP
      // REGION: country?/country+state?
      // ZONE: virtual DC: contractId
      // NETWORK: VSYS? DMZ/SECURE1/SECURE2?
      // RACK: N/A?
      // HOST: N/A?
      // there are no locations except the provider
      // bind(new TypeLiteral<Supplier<Location>>() {
      // }).to(OnlyLocationOrFirstZone.class);

      // install(new FGCPBindComputeStrategiesByClass());
      // install(new FGCPBindComputeSuppliersByClass());
      // bind(ReviseParsedImage.class).to(AWSEC2ReviseParsedImage.class);
      // bind(CreateKeyPairAndSecurityGroupsAsNeededAndReturnRunOptions.class).to(
      // CreateKeyPairPlacementAndSecurityGroupsAsNeededAndReturnRunOptions.class);
      // bind(EC2HardwareSupplier.class).to(AWSEC2HardwareSupplier.class);
      // bind(EC2TemplateBuilderImpl.class).to(AWSEC2TemplateBuilderImpl.class);
      // bind(EC2GetNodeMetadataStrategy.class).to(AWSEC2GetNodeMetadataStrategy.class);
      // bind(InstancePresent.class).to(AWSEC2InstancePresent.class);
      // bind(EC2CreateNodesInGroupThenAddToSet.class).to(AWSEC2CreateNodesInGroupThenAddToSet.class);
      // bind(RunningInstanceToNodeMetadata.class).to(AWSRunningInstanceToNodeMetadata.class);
   }

   // @Provides
   // @Singleton
   // @Named("SECURITY")
   // protected Predicate<String> provideServerStopped(ServerStopped
   // serverStopped, Timeouts timeouts) {
   // return RetryablePredicate.create(serverStopped,
   // timeouts.nodeSuspended);
   // }

   protected void installDependencies() {
      // install(new FGCPComputeServiceDependenciesModule());
   }

   /*
    * @Override protected TemplateBuilder provideTemplate(Injector injector,
    * TemplateBuilder template) { return
    * template.osFamily(CENTOS).os64Bit(true); }
    */
}
