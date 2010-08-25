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

package org.jclouds.aws.ec2.compute.config;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.jclouds.aws.ec2.reference.EC2Constants.PROPERTY_EC2_CC_AMIs;
import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.Region;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.EC2ComputeService;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.functions.CreatePlacementGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.aws.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.aws.ec2.compute.functions.RegionAndIdToImage;
import org.jclouds.aws.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.compute.strategy.EC2DestroyLoadBalancerStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2DestroyNodeStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2GetNodeMetadataStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2ListNodesStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2LoadBalanceNodesStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2RebootNodeStrategy;
import org.jclouds.aws.ec2.compute.strategy.EC2RunNodesAndAddToSetStrategy;
import org.jclouds.aws.ec2.compute.suppliers.EC2LocationSupplier;
import org.jclouds.aws.ec2.compute.suppliers.EC2SizeSupplier;
import org.jclouds.aws.ec2.compute.suppliers.RegionAndNameToImageSupplier;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.functions.RunningInstanceToStorageMappingUnix;
import org.jclouds.aws.ec2.predicates.InstancePresent;
import org.jclouds.aws.ec2.predicates.PlacementGroupAvailable;
import org.jclouds.aws.ec2.predicates.PlacementGroupDeleted;
import org.jclouds.aws.suppliers.DefaultLocationSupplier;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.DestroyLoadBalancerStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.LoadBalanceNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.domain.Location;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Configures the {@link ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Provides
   @Singleton
   @Named("PRESENT")
   protected Predicate<RunningInstance> instancePresent(InstancePresent present) {
      return new RetryablePredicate<RunningInstance>(present, 5000, 200, TimeUnit.MILLISECONDS);
   }

   @Provides
   @Singleton
   @Named("AVAILABLE")
   protected Predicate<PlacementGroup> placementGroupAvailable(PlacementGroupAvailable available) {
      return new RetryablePredicate<PlacementGroup>(available, 60, 1, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named("DELETED")
   protected Predicate<PlacementGroup> placementGroupDeleted(PlacementGroupDeleted deleted) {
      return new RetryablePredicate<PlacementGroup>(deleted, 60, 1, TimeUnit.SECONDS);
   }

   @Override
   protected void configure() {
      install(new ComputeServiceTimeoutsModule());
      bind(TemplateBuilder.class).to(EC2TemplateBuilderImpl.class);
      bind(TemplateOptions.class).to(EC2TemplateOptions.class);
      bind(ComputeService.class).to(EC2ComputeService.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<EC2Client, EC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<EC2Client, EC2AsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<EC2Client, EC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(LoadBalanceNodesStrategy.class).to(EC2LoadBalanceNodesStrategy.class);
      bind(DestroyLoadBalancerStrategy.class).to(EC2DestroyLoadBalancerStrategy.class);
      bind(RunNodesAndAddToSetStrategy.class).to(EC2RunNodesAndAddToSetStrategy.class);
      bind(ListNodesStrategy.class).to(EC2ListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(EC2GetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(EC2RebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(EC2DestroyNodeStrategy.class);
      bind(new TypeLiteral<Function<RunningInstance, Map<String, String>>>() {
      }).annotatedWith(Names.named("volumeMapping")).to(RunningInstanceToStorageMappingUnix.class).in(Scopes.SINGLETON);
   }

   @Provides
   @Singleton
   Supplier<String> provideSuffix() {
      return new Supplier<String>() {
         final SecureRandom random = new SecureRandom();

         @Override
         public String get() {
            return random.nextInt(100) + "";
         }
      };

   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      String region = injector.getInstance(Key.get(String.class, Region.class));
      return "Eucalyptus".equals(region) ? template.osFamily(CENTOS).smallest() : template.os64Bit(false).osFamily(
               UBUNTU).osVersionMatches(".*10\\.?04.*").osDescriptionMatches("^ubuntu-images.*");
   }

   @Provides
   @Singleton
   protected final Map<RegionAndName, KeyPair> credentialsMap(CreateUniqueKeyPair in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return newLinkedHashMap();
   }

   @Provides
   @Singleton
   @Named("SECURITY")
   protected final Map<RegionAndName, String> securityGroupMap(CreateSecurityGroupIfNeeded in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return newLinkedHashMap();
   }

   @Provides
   @Singleton
   @Named("PLACEMENT")
   protected final Map<RegionAndName, String> placementGroupMap(CreatePlacementGroupIfNeeded in) {
      // doesn't seem to clear when someone issues remove(key)
      // return new MapMaker().makeComputingMap(in);
      return newLinkedHashMap();
   }

   @Provides
   @Singleton
   @Named(PROPERTY_EC2_AMI_OWNERS)
   String[] amiOwners(@Named(PROPERTY_EC2_AMI_OWNERS) String amiOwners) {
      if (amiOwners.trim().equals(""))
         return new String[] {};
      return toArray(Splitter.on(',').split(amiOwners), String.class);
   }

   @Provides
   @Singleton
   @Named(PROPERTY_EC2_CC_AMIs)
   String[] ccAmis(@Named(PROPERTY_EC2_CC_AMIs) String ccAmis) {
      if (ccAmis.trim().equals(""))
         return new String[] {};
      return toArray(Splitter.on(',').split(ccAmis), String.class);
   }

   @Provides
   @Singleton
   protected ConcurrentMap<RegionAndName, Image> provideImageMap(RegionAndIdToImage regionAndIdToImage) {
      return new MapMaker().makeComputingMap(regionAndIdToImage);
   }

   @Provides
   @Singleton
   protected Supplier<Map<RegionAndName, ? extends Image>> provideRegionAndNameToImageSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final RegionAndNameToImageSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<RegionAndName, ? extends Image>>(
               authException, seconds, new Supplier<Map<RegionAndName, ? extends Image>>() {
                  @Override
                  public Map<RegionAndName, ? extends Image> get() {
                     return supplier.get();
                  }
               });
   }

   @Override
   protected Supplier<Set<? extends Image>> getSourceImageSupplier(Injector injector) {
      Supplier<Map<RegionAndName, ? extends Image>> map = injector.getInstance(Key
               .get(new TypeLiteral<Supplier<Map<RegionAndName, ? extends Image>>>() {
               }));
      return Suppliers.compose(new Function<Map<RegionAndName, ? extends Image>, Set<? extends Image>>() {
         @Override
         public Set<? extends Image> apply(Map<RegionAndName, ? extends Image> from) {
            return Sets.newLinkedHashSet(from.values());
         }
      }, map);
   }

   @Override
   protected Supplier<Set<? extends Location>> getSourceLocationSupplier(Injector injector) {
      return injector.getInstance(EC2LocationSupplier.class);
   }

   @Override
   protected Supplier<Set<? extends Size>> getSourceSizeSupplier(Injector injector) {
      return injector.getInstance(EC2SizeSupplier.class);
   }

   @Override
   protected Supplier<Location> supplyDefaultLocation(Injector injector, Supplier<Set<? extends Location>> locations) {
      return injector.getInstance(DefaultLocationSupplier.class);
   }
}
