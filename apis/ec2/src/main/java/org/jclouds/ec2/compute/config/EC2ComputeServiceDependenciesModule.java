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

import java.security.SecureRandom;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.CreateSecurityGroupIfNeeded;
import org.jclouds.ec2.compute.functions.CreateUniqueKeyPair;
import org.jclouds.ec2.compute.functions.CredentialsForInstance;
import org.jclouds.ec2.compute.functions.RegionAndIdToImage;
import org.jclouds.ec2.compute.functions.RunningInstanceToNodeMetadata;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceDependenciesModule extends AbstractModule {

   public static final Map<InstanceState, NodeState> instanceToNodeState = ImmutableMap
         .<InstanceState, NodeState> builder().put(InstanceState.PENDING, NodeState.PENDING)
         .put(InstanceState.RUNNING, NodeState.RUNNING).put(InstanceState.SHUTTING_DOWN, NodeState.PENDING)
         .put(InstanceState.TERMINATED, NodeState.TERMINATED).put(InstanceState.STOPPING, NodeState.PENDING)
         .put(InstanceState.STOPPED, NodeState.SUSPENDED).put(InstanceState.UNRECOGNIZED, NodeState.UNRECOGNIZED)
         .build();

   @Singleton
   @Provides
   Map<InstanceState, NodeState> provideServerToNodeState() {
      return instanceToNodeState;
   }

   @Override
   protected void configure() {
      bind(TemplateBuilder.class).to(EC2TemplateBuilderImpl.class);
      bind(TemplateOptions.class).to(EC2TemplateOptions.class);
      bind(ComputeService.class).to(EC2ComputeService.class);
      bind(new TypeLiteral<Function<RunningInstance, NodeMetadata>>() {
      }).to(RunningInstanceToNodeMetadata.class);
      bind(new TypeLiteral<CacheLoader<RunningInstance, Credentials>>() {
      }).to(CredentialsForInstance.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, String>>() {
      }).to(CreateSecurityGroupIfNeeded.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, KeyPair>>() {
      }).to(CreateUniqueKeyPair.class);
      bind(new TypeLiteral<CacheLoader<RegionAndName, Image>>() {
      }).to(RegionAndIdToImage.class);
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<EC2Client, EC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
      bind(new TypeLiteral<RestContext<EC2Client, EC2AsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<EC2Client, EC2AsyncClient>>() {
      }).in(Scopes.SINGLETON);
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
   protected Cache<RunningInstance, Credentials> credentialsMap(CacheLoader<RunningInstance, Credentials> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Provides
   @Singleton
   protected Cache<RegionAndName, KeyPair> keypairMap(CacheLoader<RegionAndName, KeyPair> in) {
      return CacheBuilder.newBuilder().build(in);
   }
   
   @Provides
   @Singleton
   // keys that we know about.
   protected Map<RegionAndName, KeyPair> knownKeys(){
      return Maps.newConcurrentMap();
   }
   
   @Provides
   @Singleton
   @Named("SECURITY")
   protected Cache<RegionAndName, String> securityGroupMap(CacheLoader<RegionAndName, String> in) {
      return CacheBuilder.newBuilder().build(in);
   }
   
   @Provides
   @Singleton
   // keys that we know about.
   protected Map<RegionAndName, Image> knownImages(){
      return Maps.newConcurrentMap();
   }
   
}
