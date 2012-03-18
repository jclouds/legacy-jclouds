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
package org.jclouds.openstack.nova.v1_1.compute.config;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.openstack.nova.v1_1.NovaAsyncClient;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v1_1.compute.domain.RegionAndName;
import org.jclouds.openstack.nova.v1_1.compute.functions.FlavorToHardware;
import org.jclouds.openstack.nova.v1_1.compute.functions.NovaImageToImage;
import org.jclouds.openstack.nova.v1_1.compute.functions.NovaImageToOperatingSystem;
import org.jclouds.openstack.nova.v1_1.compute.functions.ServerToNodeMetadata;
import org.jclouds.openstack.nova.v1_1.compute.loaders.LoadFloatingIpsForInstance;
import org.jclouds.openstack.nova.v1_1.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v1_1.domain.Flavor;
import org.jclouds.openstack.nova.v1_1.domain.Server;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Module for building a compute service context for Nova
 * 
 * @author Matt Stephenson
 */
public class NovaComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<NovaClient, NovaAsyncClient, Server, Flavor, org.jclouds.openstack.nova.v1_1.domain.Image, Location> {
   public NovaComputeServiceContextModule() {
      super(NovaClient.class, NovaAsyncClient.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(
            new TypeLiteral<ComputeServiceAdapter<Server, Flavor, org.jclouds.openstack.nova.v1_1.domain.Image, Location>>() {
            }).to(NovaComputeServiceAdapter.class);

      bind(new TypeLiteral<Function<Server, NodeMetadata>>() {
      }).to(ServerToNodeMetadata.class);

      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.v1_1.domain.Image, Image>>() {
      }).to(NovaImageToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem>>() {
      }).to(NovaImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<Flavor, Hardware>>() {
      }).to(FlavorToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);

      bind(TemplateOptions.class).to(NovaTemplateOptions.class);

      bind(new TypeLiteral<CacheLoader<RegionAndName, Iterable<String>>>() {
      }).annotatedWith(Names.named("FLOATINGIP")).to(LoadFloatingIpsForInstance.class);

   }

   @Provides
   @Singleton
   @Named("FLOATINGIP")
   protected LoadingCache<RegionAndName, Iterable<String>> instanceToFloatingIps(
         @Named("FLOATINGIP") CacheLoader<RegionAndName, Iterable<String>> in) {
      return CacheBuilder.newBuilder().build(in);
   }
}