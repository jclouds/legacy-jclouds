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
package org.jclouds.joyent.cloudapi.v6_5.features;

import static com.google.common.base.Predicates.not;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.internal.BaseJoyentCloudApiLiveTest;
import org.jclouds.joyent.cloudapi.v6_5.options.CreateMachineOptions;
import org.jclouds.joyent.cloudapi.v6_5.reference.Metadata;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshKeys;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.InetAddresses2;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "MachineApiLiveTest")
public class MachineApiLiveTest extends BaseJoyentCloudApiLiveTest {

   @Test
   public void testListAndGetMachines() throws Exception {
      for (String datacenterId : cloudApiContext.getApi().getConfiguredDatacenters()) {
         MachineApi api = cloudApiContext.getApi().getMachineApiForDatacenter(datacenterId);
         Set<Machine> response = api.list();
         assert null != response;
         for (Machine machine : response) {
            Machine newDetails = api.get(machine.getId());
            assertEquals(newDetails.getId(), machine.getId());
            assertEquals(newDetails.getName(), machine.getName());
            assertEquals(newDetails.getType(), machine.getType());
            assertEquals(newDetails.getState(), machine.getState());
            assertEquals(newDetails.getDatasetURN(), machine.getDatasetURN());
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
   private Predicate<HostAndPort> socketTester;
   private Predicate<Machine> machineRunning;
   private MachineApi api;
   private Machine machine;
   protected String datasetURN = System.getProperty("test." + provider + ".image-id", "sdc:sdc:ubuntu-10.04:1.0.1");
   private String name;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      key = SshKeys.generate();
      fingerprint = SshKeys.fingerprintPublicKey(key.get("public"));
      cloudApiContext.getApi().getKeyApi().create(Key.builder().name(fingerprint).key(key.get("public")).build());
      api = cloudApiContext.getApi().getMachineApiForDatacenter(
            Iterables.get(cloudApiContext.getApi().getConfiguredDatacenters(), 0));
      SocketOpen socketOpen = context.utils().injector().getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 180, 1, 1, SECONDS);
      machineRunning = retry(new Predicate<Machine>() {
         public boolean apply(Machine input) {
            return api.get(input.getId()).getState() == Machine.State.RUNNING;
         }
      }, 600, 5, 5, SECONDS);
   }

   public void testCreateMachine() {
      Machine newMachine = api.createWithDataset(datasetURN,
            CreateMachineOptions.Builder.metadata(ImmutableMap.of("foo", "bar")));
      machine = newMachine;
      name = newMachine.getName();

      assertEquals(newMachine.getMetadata().get("foo").toString(), "bar");
      assertTrue(
            newMachine.getMetadata().get(Metadata.ROOT_AUTHORIZED_KEYS.key()).indexOf(key.get("public")) != -1,
            newMachine + "; key: " + key.get("public"));

      assertTrue(machineRunning.apply(newMachine), newMachine.toString());
      machine = api.get(newMachine.getId());

   }

   @Test(dependsOnMethods = "testCreateMachine", expectedExceptions = IllegalStateException.class)
   public void testDuplicateMachineThrowsIllegalStateException() {
      api.createWithDataset(datasetURN, CreateMachineOptions.Builder.name(name));
   }

   @Test(dependsOnMethods = "testCreateMachine")
   protected void testSsh() {
      String publicAddress = Iterables.find(machine.getIps(), not(InetAddresses2.IsPrivateIPAddress.INSTANCE));
      HostAndPort socket = HostAndPort.fromParts(publicAddress, 22);
      assertTrue(socketTester.apply(socket), socket.toString());
      SshClient api = context.utils().injector().getInstance(SshClient.Factory.class)
            .create(socket, LoginCredentials.builder().user("root").privateKey(key.get("private")).build());
      try {
         api.connect();
         ExecResponse exec = api.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (api != null)
            api.disconnect();
      }
   }

   @Test(dependsOnMethods = "testSsh")
   public void testDeleteMachine() {
      api.delete(machine.getId());
   }


   @AfterGroups(groups = "live")
   @Override
   protected void tearDownContext() {
      if (machine != null)
         api.delete(machine.getId());
      cloudApiContext.getApi().getKeyApi().delete(fingerprint);
      super.tearDownContext();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
