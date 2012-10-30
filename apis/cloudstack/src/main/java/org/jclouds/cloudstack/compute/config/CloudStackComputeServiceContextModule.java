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

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

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
import org.jclouds.cloudstack.domain.FirewallRule;
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
import org.jclouds.cloudstack.functions.GetFirewallRulesByVirtualMachine;
import org.jclouds.cloudstack.functions.GetIPForwardingRulesByVirtualMachine;
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
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class CloudStackComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<VirtualMachine, ServiceOffering, Template, Zone> {

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
      bind(TemplateOptions.class).to(CloudStackTemplateOptions.class);
      bind(new TypeLiteral<Function<Template, OperatingSystem>>() {
      }).to(TemplateToOperatingSystem.class);
      install(new FactoryModuleBuilder().build(StaticNATVirtualMachineInNetwork.Factory.class));
      bind(new TypeLiteral<CacheLoader<String, Set<IPForwardingRule>>>() {
      }).to(GetIPForwardingRulesByVirtualMachine.class);
      bind(new TypeLiteral<CacheLoader<String, Set<FirewallRule>>>() {
      }).to(GetFirewallRulesByVirtualMachine.class);
      bind(new TypeLiteral<CacheLoader<String, Zone>>() {
      }).to(ZoneIdToZone.class);
      bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {
      }).to(ZoneIdToZoneSupplier.class);
      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<VirtualMachine, ServiceOffering, Template, Zone>(){});
   }
   
   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<String, String>> listOSCategories(AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final CloudStackClient client) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            new Supplier<Map<String, String>>() {
               @Override
               public Map<String, String> get() {
                  GuestOSClient guestOSClient = client.getGuestOSClient();
                  return guestOSClient.listOSCategories();
               }
               @Override
               public String toString() {
                  return Objects.toStringHelper(client.getGuestOSClient()).add("method", "listOSCategories").toString();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<String, OSType>> listOSTypes(AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final CloudStackClient client) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            new Supplier<Map<String, OSType>>() {
               @Override
               public Map<String, OSType> get() {
                  GuestOSClient guestOSClient = client.getGuestOSClient();
                  return Maps.uniqueIndex(guestOSClient.listOSTypes(), new Function<OSType, String>() {

                     @Override
                     public String apply(OSType arg0) {
                        return arg0.getId();
                     }
                  });
               }
               @Override
               public String toString() {
                  return Objects.toStringHelper(client.getGuestOSClient()).add("method", "listOSTypes").toString();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<String, Network>> listNetworks(AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final NetworksForCurrentUser networksForCurrentUser) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, networksForCurrentUser,
               seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<User> getCurrentUser(AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final GetCurrentUser getCurrentUser) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, getCurrentUser,
               seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<String> jobComplete(JobComplete jobComplete) {
      // TODO: parameterize
      return new RetryablePredicate<String>(jobComplete, 1200, 1, 5, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine(
      CacheLoader<String, Set<IPForwardingRule>> getIPForwardingRules) {
      return CacheBuilder.newBuilder().build(getIPForwardingRules);
   }


   @Provides
   @Singleton
   protected LoadingCache<String, Set<FirewallRule>> getFirewallRulesByVirtualMachine(
      CacheLoader<String, Set<FirewallRule>> getFirewallRules) {
      return CacheBuilder.newBuilder().build(getFirewallRules);
   }

   @Provides
   @Singleton
   public Map<NetworkType, ? extends OptionsConverter> optionsConverters(){
      return ImmutableMap.of(
         NetworkType.ADVANCED, new AdvancedNetworkOptionsConverter(),
         NetworkType.BASIC, new BasicNetworkOptionsConverter());
   }
}
