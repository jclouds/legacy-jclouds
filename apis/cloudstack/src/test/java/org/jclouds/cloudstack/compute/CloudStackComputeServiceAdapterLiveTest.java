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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.compute.config.CloudStackComputeServiceContextModule.GetIPForwardingRulesByVirtualMachine;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.compute.strategy.CloudStackComputeServiceAdapter;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.functions.StaticNATVirtualMachineInNetwork;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.cloudstack.suppliers.GetCurrentUser;
import org.jclouds.cloudstack.suppliers.NetworksForCurrentUser;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;

@Test(groups = "live", singleThreaded = true, testName = "CloudStackComputeServiceAdapterLiveTest")
public class CloudStackComputeServiceAdapterLiveTest extends BaseCloudStackClientLiveTest {

   private CloudStackComputeServiceAdapter adapter;
   private NodeAndInitialCredentials<VirtualMachine> vm;

   private String keyPairName;
   private Map<String, String> keyPair;
   Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      Module module = new AbstractModule() {

         @Override
         protected void configure() {
            bindProperties(binder(), setupProperties());
            bind(String.class).annotatedWith(Identity.class).toInstance(identity);
            bind(new TypeLiteral<Supplier<User>>() {
            }).annotatedWith(Memoized.class).to(GetCurrentUser.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<Supplier<Map<Long, Network>>>() {
            }).annotatedWith(Memoized.class).to(NetworksForCurrentUser.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<Map<String, Credentials>>() {
            }).toInstance(credentialStore);
            bind(CloudStackClient.class).toInstance(context.getApi());
            install(new FactoryModuleBuilder().build(StaticNATVirtualMachineInNetwork.Factory.class));
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected Predicate<Long> jobComplete(JobComplete jobComplete) {
            return new RetryablePredicate<Long>(jobComplete, 1200, 1, 5, TimeUnit.SECONDS);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected Cache<Long, Set<IPForwardingRule>> getIPForwardingRuleByVirtualMachine(
               GetIPForwardingRulesByVirtualMachine getIPForwardingRule) {
            return CacheBuilder.newBuilder().build(getIPForwardingRule);
         }
      };
      adapter = Guice.createInjector(module, new Log4JLoggingModule()).getInstance(
            CloudStackComputeServiceAdapter.class);

      keyPairName = prefix + "-adapter-test-keypair";
      try {
         keyPair = ComputeTestUtils.setupKeyPair();
      } catch (IOException e) {
         fail("Unable to create keypair", e);
      }
   }

   @Test
   public void testListLocations() {
      assertFalse(Iterables.isEmpty(adapter.listLocations()));
   }

   private static final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate = new PrioritizeCredentialsFromTemplate(
         new DefaultCredentialsFromImageOrOverridingCredentials());

   @Test
   public void testCreateNodeWithGroupEncodedIntoName() throws InterruptedException {
      String group = prefix + "#foo";
      String name = group + "#node#" + new Random().nextInt();
      Template template = computeContext.getComputeService().templateBuilder().build();

      if (!client
            .getTemplateClient()
            .getTemplateInZone(Long.parseLong(template.getImage().getId()),
                  Long.parseLong(template.getLocation().getId())).isPasswordEnabled()) {

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

      assert InetAddresses.isInetAddress(address) : vm;
      IPSocket socket = new IPSocket(address, 22);
      doConnectViaSsh(socket, prioritizeCredentialsFromTemplate.apply(template, vm.getCredentials()));
   }

   protected void doConnectViaSsh(IPSocket socket, Credentials creds) {
      SshClient ssh = computeContext.utils().sshFactory().create(socket, creds);
      try {
         connectWithRetry(ssh, 5, 2000);
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         System.err.println(ssh.exec("df -k").getOutput());
         System.err.println(ssh.exec("mount").getOutput());
         System.err.println(ssh.exec("uname -a").getOutput());
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   private void connectWithRetry(SshClient ssh, int times, int delayInMilli) {
      for (int i = 0; i < times; i++) {
         try {
            ssh.connect();
            break;
         } catch (SshException e) {
            try {
               Thread.sleep(delayInMilli);
            } catch (InterruptedException e1) {
            }
         }
      }
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
   protected void tearDown() {
      if (vm != null)
         adapter.destroyNode(vm.getNodeId());
      super.tearDown();
   }
}
