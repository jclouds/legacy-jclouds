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
package org.jclouds.ec2.compute.config;

import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.extensions.EC2ImageExtension;
import org.jclouds.ec2.compute.functions.AddElasticIpsToNodemetadata;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.functions.CredentialsForInstance;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.compute.functions.WindowsLoginCredentialsFromEncryptedData;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.ec2.compute.loaders.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.loaders.LoadPublicIpForInstanceOrNull;
import org.jclouds.ec2.compute.loaders.RegionAndIdToImage;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.compute.predicates.GetImageWhenStatusAvailablePredicateWithResult;
import org.jclouds.ec2.compute.predicates.SecurityGroupPresent;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Image.ImageState;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceDependenciesModule extends AbstractModule {

   public static final Map<InstanceState, Status> toPortableNodeStatus = ImmutableMap
            .<InstanceState, Status> builder()
            .put(InstanceState.PENDING, Status.PENDING)
            .put(InstanceState.RUNNING, Status.RUNNING)
            .put(InstanceState.SHUTTING_DOWN, Status.PENDING)
            .put(InstanceState.TERMINATED, Status.TERMINATED)
            .put(InstanceState.STOPPING, Status.PENDING)
            .put(InstanceState.STOPPED, Status.SUSPENDED)
            .put(InstanceState.UNRECOGNIZED, Status.UNRECOGNIZED)
            .build();
   
   @Singleton
   @Provides
   protected Map<InstanceState, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
   
   @VisibleForTesting
   public static final Map<ImageState, Image.Status> toPortableImageStatus = ImmutableMap
            .<ImageState, Image.Status> builder()
            .put(ImageState.AVAILABLE, Image.Status.AVAILABLE)
            .put(ImageState.DEREGISTERED, Image.Status.DELETED)
            .put(ImageState.UNRECOGNIZED, Image.Status.UNRECOGNIZED).build();

   @Singleton
   @Provides
   protected Map<ImageState, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }
   
   @Override
   protected void configure() {
      bind(TemplateBuilder.class).to(EC2TemplateBuilderImpl.class);
      bind(TemplateOptions.class).to(EC2TemplateOptions.class);
      bind(ComputeService.class).to(EC2ComputeService.class);
      bind(new TypeLiteral<CacheLoader<RunningInstance, Credentials>>() {
      }).to(CredentialsForInstance.class);
      bind(new TypeLiteral<Function<RegionAndName, KeyPair>>() {
      }).to(CreateUniqueKeyPair.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, Image>>() {
      }).to(RegionAndIdToImage.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).annotatedWith(Names.named("SECURITY")).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).annotatedWith(Names.named("ELASTICIP")).to(LoadPublicIpForInstanceOrNull.class);      
      bind(WindowsLoginCredentialsFromEncryptedData.class);
      bind(new TypeLiteral<Function<org.jclouds.ec2.domain.Image, Image>>() {
      }).to(EC2ImageParser.class);
      bind(new TypeLiteral<ImageExtension>() {
      }).to(EC2ImageExtension.class);
      bind(new TypeLiteral<PredicateWithResult<String, Image>>() {
      }).to(GetImageWhenStatusAvailablePredicateWithResult.class);
   }

   /**
    * only add the overhead of looking up ips when we have enabled the auto-allocate functionality
    */
   @Provides
   @Singleton
   public Function<RunningInstance, NodeMetadata> bindNodeConverter(RunningInstanceToNodeMetadata baseConverter,
            AddElasticIpsToNodemetadata addElasticIpsToNodemetadata,
            @Named(EC2Constants.PROPERTY_EC2_AUTO_ALLOCATE_ELASTIC_IPS) boolean autoAllocateElasticIps) {
      if (!autoAllocateElasticIps)
         return baseConverter;
      return Functions.compose(addElasticIpsToNodemetadata, baseConverter);
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

   @Provides
   @Singleton
   protected LoadingCache<RunningInstance, Credentials> credentialsMap(CacheLoader<RunningInstance, Credentials> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected ConcurrentMap<RegionAndName, KeyPair> keypairMap(Injector i) {
      return Maps.newConcurrentMap();
   }

   @Provides
   @Singleton
   @Named("SECURITY")
   protected LoadingCache<RegionAndName, String> securityGroupMap(
            @Named("SECURITY") CacheLoader<RegionAndName, String> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   @Named("ELASTICIP")
   protected LoadingCache<RegionAndName, String> instanceToElasticIp(
            @Named("ELASTICIP") CacheLoader<RegionAndName, String> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   @Named("SECURITY")
   protected Predicate<RegionAndName> securityGroupEventualConsistencyDelay(SecurityGroupPresent in,
            @Named(PROPERTY_EC2_TIMEOUT_SECURITYGROUP_PRESENT) long msDelay) {
      return new RetryablePredicate<RegionAndName>(in, msDelay, 100l, TimeUnit.MILLISECONDS);
   }

}
