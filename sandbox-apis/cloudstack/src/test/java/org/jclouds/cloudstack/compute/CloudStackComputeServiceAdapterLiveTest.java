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

import static com.google.inject.name.Names.bindProperties;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackPropertiesBuilder;
import org.jclouds.cloudstack.compute.options.CloudStackTemplateOptions;
import org.jclouds.cloudstack.compute.strategy.CloudStackComputeServiceAdapter;
import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.features.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.predicates.JobComplete;
import org.jclouds.cloudstack.predicates.TemplatePredicates;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provides;

@Test(groups = "live", singleThreaded = true, testName = "CloudStackComputeServiceAdapterLiveTest")
public class CloudStackComputeServiceAdapterLiveTest extends BaseCloudStackClientLiveTest {

   private CloudStackComputeServiceAdapter adapter;
   private VirtualMachine vm;

   private String keyPairName;
   private Map<String, String> keyPair;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      Module module = new AbstractModule() {

         @Override
         protected void configure() {
            bindProperties(binder(), new CloudStackPropertiesBuilder(new Properties()).build());
            bind(CloudStackClient.class).toInstance(context.getApi());
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected Predicate<Long> jobComplete(JobComplete jobComplete) {
            return new RetryablePredicate<Long>(jobComplete, 1200, 1, 5, TimeUnit.SECONDS);
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

   @Test
   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentialsWithSecurityGroup() {
      String group = "foo";
      String name = "node" + new Random().nextInt();
      Template template = computeContext.getComputeService().templateBuilder().build();

      client.getSSHKeyPairClient().deleteSSHKeyPair(keyPairName);
      client.getSSHKeyPairClient().registerSSHKeyPair(keyPairName, keyPair.get("public"));

      Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();
      credentialStore.put("keypair#" + keyPairName, new Credentials("root", keyPair.get("private")));

      // TODO: look at SecurityGroupClientLiveTest for how to do this
      template.getOptions().as(CloudStackTemplateOptions.class).keyPair(keyPairName);

      vm = adapter.createNodeWithGroupEncodedIntoNameThenStoreCredentials(group, name, template, credentialStore);

      // TODO: check security groups vm.getSecurityGroups(),
      // check other things, like cpu correct, mem correct, image/os is correct
      // (as possible)

      assert credentialStore.containsKey("node#" + vm.getId()) : "credentials to log into vm not found " + vm;
      assert InetAddresses.isInetAddress(vm.getIPAddress()) : vm;

      doConnectViaSsh(vm, credentialStore.get("node#" + vm.getId()));
   }

   protected void doConnectViaSsh(VirtualMachine vm, Credentials creds) {
      SshClient ssh = computeContext.utils().sshFactory().create(new IPSocket(vm.getIPAddress(), 22), creds);
      try {
         ssh.connect();
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
         adapter.destroyNode(vm.getId() + "");
      super.tearDown();
   }
}
