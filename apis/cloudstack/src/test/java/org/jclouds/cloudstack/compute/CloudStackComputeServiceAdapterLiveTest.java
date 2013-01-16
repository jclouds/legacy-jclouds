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
package org.jclouds.cloudstack.compute;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.inject.name.Names.bindProperties;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.config.CloudStackComputeServiceContextModule;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.compute.strategy.CloudStackComputeServiceAdapter;
import org.jclouds.cloudstack.compute.strategy.OptionsConverter;
import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.functions.GetFirewallRulesByVirtualMachine;
import org.jclouds.cloudstack.functions.GetIPForwardingRulesByVirtualMachine;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.functions.ZoneIdToZone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.cloudstack.suppliers.GetCurrentUser;
import org.jclouds.cloudstack.suppliers.NetworksForCurrentUser;
import org.jclouds.cloudstack.suppliers.ZoneIdToZoneSupplier;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

@Test(groups = "live", singleThreaded = true, testName = "CloudStackComputeServiceAdapterLiveTest")
public class CloudStackComputeServiceAdapterLiveTest extends BaseCloudStackClientLiveTest {

   private CloudStackComputeServiceAdapter adapter;
   private NodeAndInitialCredentials<VirtualMachine> vm;

   private String keyPairName;
   private Map<String, String> keyPair;
   Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();

   @BeforeGroups(groups = { "live" })
   public void setupContext() {
      super.setupContext();
      Module module = new AbstractModule() {

         @Override
         protected void configure() {
            bindProperties(binder(), setupProperties());
            bind(new TypeLiteral<Supplier<User>>() {
            }).annotatedWith(Memoized.class).to(GetCurrentUser.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<Supplier<Map<String, Network>>>() {
            }).annotatedWith(Memoized.class).to(NetworksForCurrentUser.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<Map<String, Credentials>>() {
            }).toInstance(credentialStore);
            bind(CloudStackClient.class).toInstance(cloudStackContext.getApi());
            bind(new TypeLiteral<Map<NetworkType, ? extends OptionsConverter>>() {}).
               toInstance(new CloudStackComputeServiceContextModule().optionsConverters());
            bind(String.class).annotatedWith(Names.named(PROPERTY_SESSION_INTERVAL)).toInstance("60");
            bind(new TypeLiteral<CacheLoader<String, Set<IPForwardingRule>>>() {
            }).to(GetIPForwardingRulesByVirtualMachine.class);
            bind(new TypeLiteral<CacheLoader<String, Set<FirewallRule>>>() {
            }).to(GetFirewallRulesByVirtualMachine.class);
            bind(new TypeLiteral<CacheLoader<String, Zone>>() {}).
               to(ZoneIdToZone.class);
            bind(new TypeLiteral<Supplier<LoadingCache<String, Zone>>>() {}).
               to(ZoneIdToZoneSupplier.class);
            install(new FactoryModuleBuilder().build(StaticNATVirtualMachineInNetwork.Factory.class));
         }
         
         @Provides
         @Singleton
         Supplier<Credentials> supplyCredentials(){
            return Suppliers.ofInstance(new Credentials(identity, credential));
         }

         @Provides
         @Singleton
         protected Predicate<String> jobComplete(JobComplete jobComplete) {
            return retry(jobComplete, 1200, 1, 5, SECONDS);
         }

         @Provides
         @Singleton
         protected LoadingCache<String, Set<IPForwardingRule>> getIPForwardingRulesByVirtualMachine(
               GetIPForwardingRulesByVirtualMachine getIPForwardingRules) {
            return CacheBuilder.newBuilder().build(getIPForwardingRules);
         }


         @Provides
         @Singleton
         protected LoadingCache<String, Set<FirewallRule>> getFirewallRulesByVirtualMachine(
            GetFirewallRulesByVirtualMachine getFirewallRules) {
            return CacheBuilder.newBuilder().build(getFirewallRules);
         }
      };
      adapter = Guice.createInjector(module, new SLF4JLoggingModule()).getInstance(
            CloudStackComputeServiceAdapter.class);

      keyPairName = prefix + "-adapter-test-keypair";
      keyPair = ComputeTestUtils.setupKeyPair();

      client.getSSHKeyPairClient().deleteSSHKeyPair(keyPairName);
      client.getSSHKeyPairClient().registerSSHKeyPair(keyPairName, keyPair.get("public"));
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   private static final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate = new PrioritizeCredentialsFromTemplate(
         new DefaultCredentialsFromImageOrOverridingCredentials());

   @Test
   public void testCreateNodeWithGroupEncodedIntoName() {
      String group = prefix + "-foo";
      String name = group + "-node-" + new Random().nextInt();
      Template template = view.getComputeService().templateBuilder().build();

      if (!client
            .getTemplateClient()
            .getTemplateInZone(template.getImage().getId(),
                  template.getLocation().getId()).isPasswordEnabled()) {

         // TODO: look at SecurityGroupClientLiveTest for how to do this
         template.getOptions().as(CloudStackTemplateOptions.class).keyPair(keyPairName);
      }
      vm = adapter.createNodeWithGroupEncodedIntoName(group, name, template);

      assertEquals(vm.getNode().getDisplayName(), name);
      // check to see if we setup a NAT rule (conceding we could check this from
      // cache)
      IPForwardingRule rule = getFirst(
         client.getNATClient().getIPForwardingRulesForVirtualMachine(vm.getNode().getId()), null);

      String address = rule != null ? rule.getIPAddress() : vm.getNode().getIPAddress();

      loginCredentials = prioritizeCredentialsFromTemplate.apply(template, vm.getCredentials());

      assert InetAddresses.isInetAddress(address) : vm;
      HostAndPort socket = HostAndPort.fromParts(address, 22);
      checkSSH(socket);
   }

   @Test
   public void testListHardwareProfiles() {
      Iterable<ServiceOffering> profiles = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(profiles));

      for (ServiceOffering profile : profiles) {
         // TODO: check that the results are valid
      }
   }

   @Test
   public void testListImages() {
      Iterable<org.jclouds.cloudstack.domain.Template> templates = adapter.listImages();
      assertFalse(Iterables.isEmpty(templates));

      for (org.jclouds.cloudstack.domain.Template template : templates) {
         assert TemplatePredicates.isReady().apply(template) : template;
      }
   }

   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (vm != null)
         adapter.destroyNode(vm.getNodeId());
      super.tearDownContext();
   }
}
