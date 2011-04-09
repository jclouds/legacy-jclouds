/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.deltacloud.compute.config;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.deltacloud.DeltacloudAsyncClient;
import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.compute.functions.DeltacloudImageToImage;
import org.jclouds.deltacloud.compute.functions.HardwareProfileToHardware;
import org.jclouds.deltacloud.compute.functions.InstanceToNodeMetadata;
import org.jclouds.deltacloud.compute.functions.RealmToLocation;
import org.jclouds.deltacloud.compute.strategy.DeltacloudComputeServiceAdapter;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class DeltacloudComputeServiceContextModule
         extends
         ComputeServiceAdapterContextModule<DeltacloudClient, DeltacloudAsyncClient, Instance, HardwareProfile, org.jclouds.deltacloud.domain.Image, Realm> {

   public DeltacloudComputeServiceContextModule() {
      super(DeltacloudClient.class, DeltacloudAsyncClient.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(
               new TypeLiteral<ComputeServiceAdapter<Instance, HardwareProfile, org.jclouds.deltacloud.domain.Image, Realm>>() {
               }).to(DeltacloudComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);
      bind(new TypeLiteral<Function<org.jclouds.deltacloud.domain.Image, org.jclouds.compute.domain.Image>>() {
      }).to(DeltacloudImageToImage.class);
      bind(new TypeLiteral<Function<HardwareProfile, org.jclouds.compute.domain.Hardware>>() {
      }).to(HardwareProfileToHardware.class);
      bind(new TypeLiteral<Function<Realm, Location>>() {
      }).to(RealmToLocation.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
   }
}
