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
package org.jclouds.joyent.sdc.v6_5.features;

import static com.google.common.base.Predicates.not;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.crypto.SshKeys;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.joyent.sdc.v6_5.domain.Key;
import org.jclouds.joyent.sdc.v6_5.domain.Machine;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientLiveTest;
import org.jclouds.joyent.sdc.v6_5.options.CreateMachineOptions;
import org.jclouds.joyent.sdc.v6_5.reference.Metadata;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.InetAddresses2;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "MachineClientLiveTest")
public class MachineClientLiveTest extends BaseSDCClientLiveTest {

   @Test
   public void testListAndGetMachines() throws Exception {
      for (String datacenterId : sdcContext.getApi().getConfiguredDatacenters()) {
         MachineClient client = sdcContext.getApi().getMachineClientForDatacenter(datacenterId);
         Set<Machine> response = client.list();
         assert null != response;
         for (Machine machine : response) {
            Machine newDetails = client.get(machine.getId());
            assertEquals(newDetails.getId(), machine.getId());
            assertEquals(newDetails.getName(), machine.getName());
            assertEquals(newDetails.getType(), machine.getType());
            assertEquals(newDetails.getState(), machine.getState());
            assertEquals(newDetails.get(), machine.get());
            assertEquals(newDetails.getMemorySizeMb(), machine.getMemorySizeMb());
            assertEquals(newDetails.getDiskSizeGb(), machine.getDiskSizeGb());
            assertEquals(newDetails.getIps(), machine.getIps());
            assertEquals(newDetails.getCreated(), machine.getCreated());
            assertEquals(newDetails.getUpdated(), machine.getUpdated());
            assertEquals(newDetails.getMetadata(), machine.getMetadata());
         }
      }
   }

   private Map<String, String> key;
   private String fingerprint;
   private RetryablePredicate<HostAndPort> socketTester;
   private Predicate<Machine> machineRunning;
   private MachineClient client;
   private Machine machine;
   protected String datasetURN = System.getProperty("test." + provider + ".image-id", "sdc:sdc:ubuntu-10.04:1.0.1");
   private String name;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      key = SshKeys.generate();
      fingerprint = SshKeys.fingerprintPublicKey(key.get("public"));
      sdcContext.getApi().getKeyClient().create(Key.builder().name(fingerprint).key(key.get("public")).build());
      client = sdcContext.getApi().getMachineClientForDatacenter(
            Iterables.get(sdcContext.getApi().getConfiguredDatacenters(), 0));
      socketTester = new RetryablePredicate<HostAndPort>(new InetSocketAddressConnect(), 180, 1, 1, TimeUnit.SECONDS);
      machineRunning = new RetryablePredicate<Machine>(new Predicate<Machine>() {

         @Override
         public boolean apply(Machine input) {
            return client.get(input.getId()).getState() == Machine.State.RUNNING;
         }

      }, 600, 5, 5, TimeUnit.SECONDS);
      machineRunning = new RetryablePredicate<Machine>(new Predicate<Machine>() {

         @Override
         public boolean apply(Machine input) {
            return client.get(input.getId()).getState() == Machine.State.RUNNING;
         }

      }, 600, 5, 5, TimeUnit.SECONDS);
   }

   public void testCreateMachine() {
      Machine newMachine = client.createWithDataset(datasetURN,
            CreateMachineOptions.Builder.metadata(ImmutableMap.of("foo", "bar")));
      machine = newMachine;
      name = newMachine.getName();

      assertEquals(newMachine.getMetadata().get("foo").toString(), "bar");
      assertTrue(
            newMachine.getMetadata().get(Metadata.ROOT_AUTHORIZED_KEYS.key()).indexOf(key.get("public")) != -1,
            newMachine + "; key: " + key.get("public"));

      assertTrue(machineRunning.apply(newMachine), newMachine.toString());
      machine = client.get(newMachine.getId());

   }

   @Test(dependsOnMethods = "testCreateMachine", expectedExceptions = IllegalStateException.class)
   public void testDuplicateMachineThrowsIllegalStateException() {
      client.createWithDataset(datasetURN, CreateMachineOptions.Builder.name(name));
   }

   @Test(dependsOnMethods = "testCreateMachine")
   protected void testSsh() {
      String publicAddress = Iterables.find(machine.getIps(), not(InetAddresses2.IsPrivateIPAddress.INSTANCE));
      HostAndPort socket = HostAndPort.fromParts(publicAddress, 22);
      assertTrue(socketTester.apply(socket), socket.toString());
      SshClient client = context.utils().injector().getInstance(SshClient.Factory.class)
            .create(socket, LoginCredentials.builder().user("root").privateKey(key.get("private")).build());
      try {
         client.connect();
         ExecResponse exec = client.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @Test(dependsOnMethods = "testSsh")
   public void testDeleteMachine() {
      client.delete(machine.getId());
   }

   @Override
   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (machine != null)
         client.delete(machine.getId());
      sdcContext.getApi().getKeyClient().delete(fingerprint);
      super.tearDown();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
