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
package org.jclouds.cloudstack.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.functions.ServiceOfferingToHardware;
import org.jclouds.cloudstack.compute.functions.TemplateToImage;
import org.jclouds.cloudstack.compute.functions.TemplateToOperatingSystem;
import org.jclouds.cloudstack.compute.functions.VirtualMachineToNodeMetadata;
import org.jclouds.cloudstack.compute.functions.ZoneToLocation;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.compute.strategy.AdvancedNetworkOptionsConverter;
import org.jclouds.cloudstack.compute.strategy.BasicNetworkOptionsConverter;
import org.jclouds.cloudstack.compute.strategy.CloudStackComputeServiceAdapter;
import org.jclouds.cloudstack.compute.strategy.OptionsConverter;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.features.GuestOSClient;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.functions.ZoneIdToZone;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.suppliers.GetCurrentUser;
import org.jclouds.cloudstack.suppliers.NetworksForCurrentUser;
import org.jclouds.cloudstack.suppliers.ZoneIdToZoneSupplier;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class CloudStackComputeServiceContextModule
      extends
      ComputeServiceAdapterContextModule<CloudStackClient, CloudStackAsyncClient, VirtualMachine, ServiceOffering, Template, Zone> {

   public CloudStackComputeServiceContextModule() {
      super(CloudStackClient.class, CloudStackAsyncClient.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, ServiceOffering, Template, Zone>>() {
      }).to(CloudStackComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Template, org.jclouds.compute.domain.Image>>() {
      }).to(TemplateToImage.class);
      bind(new TypeLiteral<Function<ServiceOffering, org.jclouds.compute.domain.Hardware>>() {
      }).to(ServiceOfferingToHardware.class);
      bind(new TypeLiteral<Function<Zone, Location>>() {
      }).to(ZoneToLocation.class);
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
      bind(TemplateOptions.class).to(CloudStackTemplateOptions.class);
      bind(new TypeLiteral<Function<Template, OperatingSystem>>() {
      }).to(TemplateToOperatingSystem.class);
      install(new FactoryModuleBuilder().build(StaticNATVirtualMachineInNetwork.Factory.class));
      bind(new TypeLiteral<CacheLoader<Long, Set<IPForwardingRule>>>() {
      }).to(GetIPForwardingRulesByVirtualMachine.class);
      bind(new TypeLiteral<CacheLoader<Long, Zone>>() {
      }).to(ZoneIdToZone.class);
      bind(new TypeLiteral<Supplier<LoadingCache<Long, Zone>>>() {
      }).to(ZoneIdToZoneSupplier.class);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<Long, String>> listOSCategories(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final CloudStackClient client) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<Long, String>>(authException,
            seconds, new Supplier<Map<Long, String>>() {
               @Override
               public Map<Long, String> get() {
                  GuestOSClient guestOSClient = client.getGuestOSClient();
                  return guestOSClient.listOSCategories();
               }
            });
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<Long, OSType>> listOSTypes(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final CloudStackClient client) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<Long, OSType>>(authException,
            seconds, new Supplier<Map<Long, OSType>>() {
               @Override
               public Map<Long, OSType> get() {
                  GuestOSClient guestOSClient = client.getGuestOSClient();
                  return Maps.uniqueIndex(guestOSClient.listOSTypes(), new Function<OSType, Long>() {

                     @Override
                     public Long apply(OSType arg0) {
                        return arg0.getId();
                     }
                  });
               }
            });
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<Long, Network>> listNetworks(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final NetworksForCurrentUser networksForCurrentUser) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<Long, Network>>(authException,
            seconds, networksForCurrentUser);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<User> getCurrentUser(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final GetCurrentUser getCurrentUser) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<User>(authException, seconds,
            getCurrentUser);
   }

   @Provides
   @Singleton
   protected Predicate<Long> jobComplete(JobComplete jobComplete) {
      // TODO: parameterize
      return new RetryablePredicate<Long>(jobComplete, 1200, 1, 5, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected LoadingCache<Long, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine(
      CacheLoader<Long, Set<IPForwardingRule>> getIPForwardingRules) {
      return CacheBuilder.newBuilder().build(getIPForwardingRules);
   }

   @Singleton
   public static class GetIPForwardingRulesByVirtualMachine extends CacheLoader<Long, Set<IPForwardingRule>> {
      private final CloudStackClient client;

      @Inject
      public GetIPForwardingRulesByVirtualMachine(CloudStackClient client) {
         this.client = checkNotNull(client, "client");
      }

      /**
       * @throws ResourceNotFoundException
       *            when there is no ip forwarding rule available for the VM
       */
      @Override
      public Set<IPForwardingRule> load(Long input) {
         Set<IPForwardingRule> rules = client.getNATClient().getIPForwardingRulesForVirtualMachine(input);
         return rules != null ? rules : ImmutableSet.<IPForwardingRule>of();
      }
   }

   @Provides
   @Singleton
   Map<NetworkType, ? extends OptionsConverter> optionsConverters(){
      return ImmutableMap.of(
         NetworkType.ADVANCED, new AdvancedNetworkOptionsConverter(),
         NetworkType.BASIC, new BasicNetworkOptionsConverter());
   }
}