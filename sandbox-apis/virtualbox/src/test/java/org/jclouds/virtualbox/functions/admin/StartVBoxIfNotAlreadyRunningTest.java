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

package org.jclouds.virtualbox.functions.admin;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.ConfiguresSshClient;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

@Test(groups = "unit", singleThreaded = true, testName = "StartVBoxIfNotAlreadyRunningTest")
public class StartVBoxIfNotAlreadyRunningTest {

   @Test(expectedExceptions = IllegalStateException.class)
   public void testStartVboxThrowsIllegalStateExceptionIfTheNodeIdConfiguredIsntAround() throws Exception {

      ComputeService compute = new ComputeServiceContextFactory().createContext("stub", "foo", "bar")
            .getComputeService();

      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      @SuppressWarnings("unchecked")
      Predicate<IPSocket> socketTester = createMock(Predicate.class);
      String hostId = "hostId";
      URI endpointUri = URI.create("http://localhost:18083/");
      Credentials localhostCredentials = new Credentials("toor", "password");

      manager.connect(endpointUri.toASCIIString(), localhostCredentials.identity, localhostCredentials.credential);

      replay(socketTester);
      replay(manager);

      new StartVBoxIfNotAlreadyRunning(compute, manager, socketTester, hostId, localhostCredentials).apply(endpointUri);

   }

   @Test
   public void testStartVboxConnectsToManagerWhenPortAlreadyListening() throws Exception {

      ComputeService compute = new ComputeServiceContextFactory().createContext("stub", "foo", "bar")
            .getComputeService();

      // TODO: possibly better to use a defined name as opposed to an id, since
      // most compute services the id is not predictable.
      NodeMetadata node = Iterables.getOnlyElement(compute.createNodesInGroup("foo", 1));
      String hostId = node.getId();

      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      @SuppressWarnings("unchecked")
      Predicate<IPSocket> socketTester = createMock(Predicate.class);
      URI endpointUri = URI.create("http://localhost:18083/");
      Credentials localhostCredentials = new Credentials("toor", "password");

      expect(socketTester.apply(new IPSocket(endpointUri.getHost(), endpointUri.getPort()))).andReturn(true);
      manager.connect(endpointUri.toASCIIString(), localhostCredentials.identity, localhostCredentials.credential);

      replay(socketTester);
      replay(manager);

      assertEquals(
            new StartVBoxIfNotAlreadyRunning(compute, manager, socketTester, hostId, localhostCredentials)
                  .apply(endpointUri),
            manager);

      verify(socketTester);
      verify(manager);
   }

   @ConfiguresSshClient
   static class StartingVBoxWhenNotRunningModule extends AbstractModule {

      @Override
      protected void configure() {
         SshClient.Factory factory = createMock(SshClient.Factory.class);
         SshClient client = createMock(SshClient.class);
         // NOTE we may want to switch to a node supplier so that we can predict
         // these values. Right now, it is node 2 since the above test made node
         // 1.
         IPSocket expectedSshSockectFor2ndCreatedNode = new IPSocket("144.175.1.2", 22);
         Credentials expectedCredentialsFor2ndCreatedNode = new Credentials("root", "password2");
         expect(factory.create(expectedSshSockectFor2ndCreatedNode, expectedCredentialsFor2ndCreatedNode)).andReturn(
               client).times(2);

         expect(client.getUsername()).andReturn(expectedCredentialsFor2ndCreatedNode.identity).times(2);
         expect(client.getHostAddress()).andReturn(expectedSshSockectFor2ndCreatedNode.getAddress()).times(2);

         client.disconnect();
         client.connect();
         expect(client.exec("VBoxManage setproperty websrvauthlibrary null\n")).andReturn(new ExecResponse("", "", 0));

         client.disconnect();
         client.connect();
         expect(client.exec("vboxwebsrv -t 10000 -v -b\n")).andReturn(new ExecResponse("", "", 0));

         replay(factory);
         replay(client);
         bind(SshClient.Factory.class).toInstance(factory);

      }

   }

   @Test
   public void testStartVboxDisablesPasswordAccessOnWebsrvauthlibraryStartsVboxwebsrvInBackgroundAndConnectsManagerWhenPortIsNotListening()
         throws Exception {
      ComputeService compute = new ComputeServiceContextFactory().createContext("stub", "foo", "bar",
            ImmutableSet.<Module> of(new StartingVBoxWhenNotRunningModule())).getComputeService();
      NodeMetadata node = Iterables.getOnlyElement(compute.createNodesInGroup("foo", 1));
      String hostId = node.getId();

      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      @SuppressWarnings("unchecked")
      Predicate<IPSocket> socketTester = createMock(Predicate.class);
      Credentials localhostCredentials = new Credentials("toor", "password");
      URI endpointUri = URI.create("http://localhost:18083/");

      expect(socketTester.apply(new IPSocket(endpointUri.getHost(), endpointUri.getPort()))).andReturn(false);

      manager.connect(endpointUri.toASCIIString(), localhostCredentials.identity, localhostCredentials.credential);

      replay(socketTester);
      replay(manager);

      assertEquals(
            new StartVBoxIfNotAlreadyRunning(compute, manager, socketTester, hostId, localhostCredentials)
                  .apply(endpointUri),
            manager);

      verify(socketTester);
      verify(manager);
   }
}
