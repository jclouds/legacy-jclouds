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

import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.compute.suppliers.CallForImages;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.aws.ec2.domain.RegionNameAndPublicKeyMaterial;
import org.jclouds.aws.ec2.functions.CreatePlacementGroupIfNeeded;
import org.jclouds.aws.ec2.functions.ImportOrReturnExistingKeypair;
import org.jclouds.aws.ec2.predicates.PlacementGroupAvailable;
import org.jclouds.aws.ec2.predicates.PlacementGroupDeleted;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.extensions.EC2ImageExtension;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.functions.CredentialsForInstance;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.ec2.compute.loaders.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.loaders.LoadPublicIpForInstanceOrNull;
import org.jclouds.ec2.compute.loaders.RegionAndIdToImage;
import org.jclouds.ec2.compute.predicates.GetImageWhenStatusAvailablePredicateWithResult;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.predicates.PredicateWithResult;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

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
      bind(new TypeLiteral<CacheLoader<RunningInstance, Credentials>>() {
      }).to(CredentialsForInstance.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).annotatedWith(Names.named("SECURITY")).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).annotatedWith(Names.named("ELASTICIP")).to(LoadPublicIpForInstanceOrNull.class);    
      bind(new TypeLiteral<Function<RegionAndName, KeyPair>>() {
      }).to(CreateUniqueKeyPair.class);
      bind(new TypeLiteral<Function<RegionNameAndPublicKeyMaterial, KeyPair>>() {
      }).to(ImportOrReturnExistingKeypair.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, Image>>() {
      }).to(RegionAndIdToImage.class);
      install(new FactoryModuleBuilder().build(CallForImages.Factory.class));
      bind(new TypeLiteral<Function<org.jclouds.ec2.domain.Image, Image>>() {
      }).to(EC2ImageParser.class);
      bind(new TypeLiteral<ImageExtension>() {
      }).to(EC2ImageExtension.class);
      bind(new TypeLiteral<PredicateWithResult<String, Image>>() {
      }).to(GetImageWhenStatusAvailablePredicateWithResult.class);
   }

   @Provides
   @Singleton
   @ImageQuery
   protected Map<String, String> imageQuery(ValueOfConfigurationKeyOrNull config) {
      String amiQuery = Strings.emptyToNull(config.apply(PROPERTY_EC2_AMI_QUERY));
      String owners = config.apply(PROPERTY_EC2_AMI_OWNERS);
      if ("".equals(owners)) {
         amiQuery = null;
      } else if (owners != null) {
         StringBuilder query = new StringBuilder();
         if ("*".equals(owners))
            query.append("state=available;image-type=machine");
         else
            query.append("owner-id=").append(owners).append(";state=available;image-type=machine");
         Logger.getAnonymousLogger().warning(
               String.format("Property %s is deprecated, please use new syntax: %s=%s", PROPERTY_EC2_AMI_OWNERS,
                     PROPERTY_EC2_AMI_QUERY, query.toString()));
         amiQuery = query.toString();
      }
      Builder<String, String> builder = ImmutableMap.<String, String> builder();
      if (amiQuery != null)
         builder.put(PROPERTY_EC2_AMI_QUERY, amiQuery);
      String ccQuery = Strings.emptyToNull(config.apply(PROPERTY_EC2_CC_AMI_QUERY));
      if (ccQuery != null)
         builder.put(PROPERTY_EC2_CC_AMI_QUERY, ccQuery);
      return builder.build();
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
   protected LoadingCache<RegionAndName, String> placementGroupMap(CreatePlacementGroupIfNeeded in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @ClusterCompute
   @Singleton
   protected Set<String> provideClusterComputeIds() {
      return Sets.newLinkedHashSet();
   }

}
