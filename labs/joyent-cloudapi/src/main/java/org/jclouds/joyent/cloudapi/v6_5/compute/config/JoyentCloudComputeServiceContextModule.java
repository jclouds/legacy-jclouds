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
package org.jclouds.joyent.cloudapi.v6_5.compute.config;

import static org.jclouds.joyent.cloudapi.v6_5.config.JoyentCloudProperties.AUTOGENERATE_KEYS;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.joyent.cloudapi.v6_5.compute.JoyentCloudComputeService;
import org.jclouds.joyent.cloudapi.v6_5.compute.JoyentCloudComputeServiceAdapter;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.DatasetInDatacenterToImage;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.DatasetToOperatingSystem;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.MachineInDatacenterToNodeMetadata;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.OrphanedGroupsByDatacenterId;
import org.jclouds.joyent.cloudapi.v6_5.compute.functions.PackageInDatacenterToHardware;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.compute.loaders.CreateUniqueKey;
import org.jclouds.joyent.cloudapi.v6_5.compute.options.JoyentCloudTemplateOptions;
import org.jclouds.joyent.cloudapi.v6_5.compute.strategy.ApplyJoyentCloudTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatasetInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.MachineInDatacenter;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.PackageInDatacenter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Module for building a compute service context for Cloud Api
 * 
 * @author Adrian Cole
 */
public class JoyentCloudComputeServiceContextModule extends
      ComputeServiceAdapterContextModule<MachineInDatacenter, PackageInDatacenter, DatasetInDatacenter, Location> {

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(
            new TypeLiteral<ComputeServiceAdapter<MachineInDatacenter, PackageInDatacenter, DatasetInDatacenter, Location>>() {
            }).to(JoyentCloudComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<MachineInDatacenter, NodeMetadata>>() {
      }).to(MachineInDatacenterToNodeMetadata.class);

      bind(new TypeLiteral<Function<DatasetInDatacenter, Image>>() {
      }).to(DatasetInDatacenterToImage.class);
      bind(new TypeLiteral<Function<Dataset, OperatingSystem>>() {
      }).to(DatasetToOperatingSystem.class);

      bind(new TypeLiteral<Function<PackageInDatacenter, Hardware>>() {
      }).to(PackageInDatacenterToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));

      // how to figure out if a group in a datacenter is no longer in use
      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>, Multimap<String, String>>>() {
      }).to(OrphanedGroupsByDatacenterId.class);

      bind(ComputeService.class).to(JoyentCloudComputeService.class);
      bind(TemplateOptions.class).to(JoyentCloudTemplateOptions.class);

      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
      ApplyJoyentCloudTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);
      
      bind(new TypeLiteral<CacheLoader<DatacenterAndName, KeyAndPrivateKey>>() {
      }).to(CreateUniqueKey.class);
   }
   
   @Override
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      boolean generateKey = injector.getInstance(com.google.inject.Key.get(boolean.class,
               Names.named(AUTOGENERATE_KEYS)));
      return options.as(JoyentCloudTemplateOptions.class).generateKey(generateKey);
   }

   @Provides
   @Singleton
   protected LoadingCache<DatacenterAndName, KeyAndPrivateKey> keyMap(
         CacheLoader<DatacenterAndName, KeyAndPrivateKey> in) {
      return CacheBuilder.newBuilder().build(in);
   }
   
   @Provides
   @Singleton
   protected Supplier<Map<String, Location>> createLocationIndexedById(
         @Memoized Supplier<Set<? extends Location>> locations) {
      return Suppliers.compose(new Function<Set<? extends Location>, Map<String, Location>>() {

         @Override
         public Map<String, Location> apply(Set<? extends Location> arg0) {
            return Maps.uniqueIndex(ImmutableSet.<Location> copyOf(arg0), new Function<Location, String>() {

               @Override
               public String apply(Location arg0) {
                  return arg0.getId();
               }

            });
         }
      }, locations);

   }

   @VisibleForTesting
   public static final Map<Machine.State, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
         .<Machine.State, NodeMetadata.Status> builder()
         .put(Machine.State.PROVISIONING, NodeMetadata.Status.PENDING)
         .put(Machine.State.RUNNING, NodeMetadata.Status.RUNNING)
         .put(Machine.State.STOPPING, NodeMetadata.Status.PENDING)
         .put(Machine.State.OFFLINE, NodeMetadata.Status.PENDING)
         .put(Machine.State.STOPPED, NodeMetadata.Status.SUSPENDED)
         .put(Machine.State.DELETED, NodeMetadata.Status.TERMINATED)
         .put(Machine.State.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   protected Map<Machine.State, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }

}
