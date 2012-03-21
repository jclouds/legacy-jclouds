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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
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
import org.jclouds.openstack.nova.v1_1.NovaAsyncClient;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.NovaComputeService;
import org.jclouds.openstack.nova.v1_1.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v1_1.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.openstack.nova.v1_1.compute.functions.FlavorInZoneToHardware;
import org.jclouds.openstack.nova.v1_1.compute.functions.ImageInZoneToImage;
import org.jclouds.openstack.nova.v1_1.compute.functions.NovaImageToOperatingSystem;
import org.jclouds.openstack.nova.v1_1.compute.functions.OrphanedGroupsByZoneId;
import org.jclouds.openstack.nova.v1_1.compute.functions.ServerInZoneToNodeMetadata;
import org.jclouds.openstack.nova.v1_1.compute.loaders.FindSecurityGroupOrCreate;
import org.jclouds.openstack.nova.v1_1.compute.loaders.LoadFloatingIpsForInstance;
import org.jclouds.openstack.nova.v1_1.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v1_1.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.FlavorInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ServerInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v1_1.predicates.FindSecurityGroupWithNameAndReturnTrue;
import org.jclouds.openstack.nova.v1_1.reference.NovaConstants;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Key;
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
         ComputeServiceAdapterContextModule<NovaClient, NovaAsyncClient, ServerInZone, FlavorInZone, ImageInZone, Location> {
   public NovaComputeServiceContextModule() {
      super(NovaClient.class, NovaAsyncClient.class);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<ServerInZone, FlavorInZone, ImageInZone, Location>>() {
      }).to(NovaComputeServiceAdapter.class);
     
      bind(ComputeService.class).to(NovaComputeService.class);
      
      bind(new TypeLiteral<Function<ServerInZone, NodeMetadata>>() {
      }).to(ServerInZoneToNodeMetadata.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>,  Multimap<String, String>>>() {
      }).to(OrphanedGroupsByZoneId.class);

      bind(new TypeLiteral<Function<ImageInZone, Image>>() {
      }).to(ImageInZoneToImage.class);
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem>>() {
      }).to(NovaImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<FlavorInZone, Hardware>>() {
      }).to(FlavorInZoneToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);

      bind(TemplateOptions.class).to(NovaTemplateOptions.class);

      bind(new TypeLiteral<CacheLoader<ZoneAndId, Iterable<String>>>() {
      }).annotatedWith(Names.named("FLOATINGIP")).to(LoadFloatingIpsForInstance.class);

      bind(new TypeLiteral<Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone>>() {
      }).to(CreateSecurityGroupIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<ZoneAndName, SecurityGroupInZone>>() {
      }).to(FindSecurityGroupOrCreate.class);

      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
               ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

   }

   @Override
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      return options.as(NovaTemplateOptions.class)
            .autoAssignFloatingIp(injector.getInstance(
                  Key.get(boolean.class, Names.named(NovaConstants.PROPERTY_NOVA_AUTO_ALLOCATE_FLOATING_IPS))))
            .generateKeyPair(injector.getInstance(
                  Key.get(boolean.class, Names.named(NovaConstants.PROPERTY_NOVA_AUTO_GENERATE_KEYPAIRS))));
   }

   @Provides
   @Singleton
   @Named("FLOATINGIP")
   protected LoadingCache<ZoneAndId, Iterable<String>> instanceToFloatingIps(
            @Named("FLOATINGIP") CacheLoader<ZoneAndId, Iterable<String>> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected LoadingCache<ZoneAndName, SecurityGroupInZone> securityGroupMap(
            CacheLoader<ZoneAndName, SecurityGroupInZone> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   @Named("SECURITY")
   protected Predicate<AtomicReference<ZoneAndName>> securityGroupEventualConsistencyDelay(
            FindSecurityGroupWithNameAndReturnTrue in,
            @Named(NovaConstants.PROPERTY_NOVA_TIMEOUT_SECURITYGROUP_PRESENT) long msDelay) {
      return new RetryablePredicate<AtomicReference<ZoneAndName>>(in, msDelay, 100l, TimeUnit.MILLISECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Location>> createLocationIndexedById(
            @Memoized Supplier<Set<? extends Location>> locations) {
      return Suppliers.compose(new Function<Set<? extends Location>, Map<String, Location>>() {

         @SuppressWarnings("unchecked")
         @Override
         public Map<String, Location> apply(Set<? extends Location> arg0) {
            // TODO: find a nice way to get rid of this cast.
            Iterable<Location> locations = (Iterable<Location>) arg0;
            return Maps.uniqueIndex(locations, new Function<Location, String>() {

               @Override
               public String apply(Location arg0) {
                  return arg0.getId();
               }

            });
         }
      }, locations);

   }
}