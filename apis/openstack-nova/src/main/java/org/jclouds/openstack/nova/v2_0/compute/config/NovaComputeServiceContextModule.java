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
package org.jclouds.openstack.nova.v2_0.compute.config;

import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_ALLOCATE_FLOATING_IPS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.AUTO_GENERATE_KEYPAIRS;
import static org.jclouds.openstack.nova.v2_0.config.NovaProperties.TIMEOUT_SECURITYGROUP_PRESENT;

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
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeService;
import org.jclouds.openstack.nova.v2_0.compute.NovaComputeServiceAdapter;
import org.jclouds.openstack.nova.v2_0.compute.extensions.NovaImageExtension;
import org.jclouds.openstack.nova.v2_0.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.openstack.nova.v2_0.compute.functions.FlavorInZoneToHardware;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageInZoneToImage;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageToOperatingSystem;
import org.jclouds.openstack.nova.v2_0.compute.functions.OrphanedGroupsByZoneId;
import org.jclouds.openstack.nova.v2_0.compute.functions.ServerInZoneToNodeMetadata;
import org.jclouds.openstack.nova.v2_0.compute.loaders.CreateUniqueKeyPair;
import org.jclouds.openstack.nova.v2_0.compute.loaders.FindSecurityGroupOrCreate;
import org.jclouds.openstack.nova.v2_0.compute.loaders.LoadFloatingIpsForInstance;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.compute.predicates.GetImageWhenImageInZoneHasActiveStatusPredicateWithResult;
import org.jclouds.openstack.nova.v2_0.compute.strategy.ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.FlavorInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ImageInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ServerInZone;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneSecurityGroupNameAndPorts;
import org.jclouds.openstack.nova.v2_0.predicates.FindSecurityGroupWithNameAndReturnTrue;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.util.Suppliers2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
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
public class NovaComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<ServerInZone, FlavorInZone, ImageInZone, Location> {

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
      bind(new TypeLiteral<Function<org.jclouds.openstack.nova.v2_0.domain.Image, OperatingSystem>>() {
      }).to(ImageToOperatingSystem.class);

      bind(new TypeLiteral<Function<FlavorInZone, Hardware>>() {
      }).to(FlavorInZoneToHardware.class);

      // we aren't converting location from a provider-specific type
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));

      bind(TemplateOptions.class).to(NovaTemplateOptions.class);

      bind(new TypeLiteral<CacheLoader<ZoneAndId, Iterable<String>>>() {
      }).annotatedWith(Names.named("FLOATINGIP")).to(LoadFloatingIpsForInstance.class);

      bind(new TypeLiteral<Function<ZoneSecurityGroupNameAndPorts, SecurityGroupInZone>>() {
      }).to(CreateSecurityGroupIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<ZoneAndName, SecurityGroupInZone>>() {
      }).to(FindSecurityGroupOrCreate.class);

      bind(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class).to(
               ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(new TypeLiteral<CacheLoader<ZoneAndName, KeyPair>>() {
      }).to(CreateUniqueKeyPair.class);
      
      bind(new TypeLiteral<ImageExtension>() {
      }).to(NovaImageExtension.class);
      
      bind(new TypeLiteral<PredicateWithResult<ZoneAndId, Image>>() {
      }).to(GetImageWhenImageInZoneHasActiveStatusPredicateWithResult.class);
   }

   @Override
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      return options.as(NovaTemplateOptions.class)
            .autoAssignFloatingIp(injector.getInstance(
                  Key.get(boolean.class, Names.named(AUTO_ALLOCATE_FLOATING_IPS))))
            .generateKeyPair(injector.getInstance(
                  Key.get(boolean.class, Names.named(AUTO_GENERATE_KEYPAIRS))));
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
   
   @Override
   protected Map<OsFamily, LoginCredentials> osFamilyToCredentials(Injector injector) {
      return ImmutableMap.of(OsFamily.WINDOWS, LoginCredentials.builder().user("Administrator").build(),
               OsFamily.UBUNTU, LoginCredentials.builder().user("ubuntu").build());
   }

   @Provides
   @Singleton
   @Named(TIMEOUT_SECURITYGROUP_PRESENT)
   protected Predicate<AtomicReference<ZoneAndName>> securityGroupEventualConsistencyDelay(
            FindSecurityGroupWithNameAndReturnTrue in,
            @Named(TIMEOUT_SECURITYGROUP_PRESENT) long msDelay) {
      return new RetryablePredicate<AtomicReference<ZoneAndName>>(in, msDelay, 100l, TimeUnit.MILLISECONDS);
   }

   @Provides
   @Singleton
   protected LoadingCache<ZoneAndName, KeyPair> keyPairMap(
         CacheLoader<ZoneAndName, KeyPair> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Location>> createLocationIndexedById(
            @Memoized Supplier<Set<? extends Location>> locations) {
      return Suppliers2.compose(new Function<Set<? extends Location>, Map<String, Location>>() {

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

   @VisibleForTesting
   public static final Map<Server.Status, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<Server.Status, NodeMetadata.Status> builder().put(Server.Status.ACTIVE, NodeMetadata.Status.RUNNING)//
            .put(Server.Status.SUSPENDED, NodeMetadata.Status.SUSPENDED)//
            .put(Server.Status.DELETED, NodeMetadata.Status.TERMINATED)//
            .put(Server.Status.PAUSED, NodeMetadata.Status.SUSPENDED)//
            .put(Server.Status.RESIZE, NodeMetadata.Status.PENDING)//
            .put(Server.Status.VERIFY_RESIZE, NodeMetadata.Status.PENDING)//
            .put(Server.Status.REVERT_RESIZE, NodeMetadata.Status.PENDING)//
            .put(Server.Status.BUILD, NodeMetadata.Status.PENDING)//
            .put(Server.Status.PASSWORD, NodeMetadata.Status.PENDING)//
            .put(Server.Status.REBUILD, NodeMetadata.Status.PENDING)//
            .put(Server.Status.ERROR, NodeMetadata.Status.ERROR)//
            .put(Server.Status.REBOOT, NodeMetadata.Status.PENDING)//
            .put(Server.Status.HARD_REBOOT, NodeMetadata.Status.PENDING)//
            .put(Server.Status.UNKNOWN, NodeMetadata.Status.UNRECOGNIZED)//
            .put(Server.Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   protected Map<Server.Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
   
   @VisibleForTesting
   public static final Map<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> toPortableImageStatus = ImmutableMap
            .<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> builder()
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.ACTIVE, Image.Status.AVAILABLE)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.SAVING, Image.Status.PENDING)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.DELETED, Image.Status.DELETED)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.ERROR, Image.Status.ERROR)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.UNKNOWN, Image.Status.UNRECOGNIZED)
            .put(org.jclouds.openstack.nova.v2_0.domain.Image.Status.UNRECOGNIZED, Image.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   protected Map<org.jclouds.openstack.nova.v2_0.domain.Image.Status, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }
   
   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }
}