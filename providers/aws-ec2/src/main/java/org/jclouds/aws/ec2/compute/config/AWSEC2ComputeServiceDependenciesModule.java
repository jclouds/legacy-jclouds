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
package org.jclouds.aws.ec2.compute.config;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.compute.suppliers.CallForImages;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.RegionNameAndPublicKeyMaterial;
import org.jclouds.aws.ec2.functions.ImportOrReturnExistingKeypair;
import org.jclouds.aws.ec2.predicates.PlacementGroupAvailable;
import org.jclouds.aws.ec2.predicates.PlacementGroupDeleted;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.functions.CredentialsForInstance;
import org.jclouds.ec2.compute.functions.RegionAndIdToImage;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class AWSEC2ComputeServiceDependenciesModule extends EC2ComputeServiceDependenciesModule {
   @Override
   protected void configure() {
      bind(TemplateBuilder.class).to(EC2TemplateBuilderImpl.class);
      bind(TemplateOptions.class).to(AWSEC2TemplateOptions.class);
      bind(ComputeService.class).to(AWSEC2ComputeService.class);
      bind(new TypeLiteral<Function<RunningInstance, NodeMetadata>>() {
      }).to(RunningInstanceToNodeMetadata.class);
      bind(new TypeLiteral<CacheLoader<RunningInstance, Credentials>>() {
      }).to(CredentialsForInstance.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, KeyPair>>() {
      }).to(CreateUniqueKeyPair.class);
      bind(new TypeLiteral<Function<RegionNameAndPublicKeyMaterial, KeyPair>>() {
      }).to(ImportOrReturnExistingKeypair.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, Image>>() {
      }).to(RegionAndIdToImage.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<AWSEC2Client, AWSEC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<AWSEC2Client, AWSEC2AsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<AWSEC2Client, AWSEC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      install(new FactoryModuleBuilder().build(CallForImages.Factory.class));
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

   @Provides
   @Singleton
   @Named("PLACEMENT")
   protected Cache<RegionAndName, String> placementGroupMap(CreateSecurityGroupIfNeeded in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @ClusterCompute
   @Singleton
   protected Set<String> provideClusterComputeIds() {
      return Sets.newLinkedHashSet();
   }

}
