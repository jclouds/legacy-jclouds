/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Profile;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewServerData;
import org.jclouds.rimuhosting.miro.domain.IpAddresses;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Ivan Meredith
 */
@Test(groups = "live", sequential = true, testName = "rimuhosting.RimuHostingNodeServiceLiveTest")
public class RimuHostingComputeServiceLiveTest {

   protected SshClient.Factory sshFactory;
   private String nodePrefix = System.getProperty("user.name") + ".rimuhosting";

   private RetryablePredicate<InetSocketAddress> socketTester;
   private CreateNodeResponse node;
   private ComputeServiceContext context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws IOException {
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      context = new ComputeServiceContextFactory().createContext("rimuhosting", key, key,
               ImmutableSet.of(new Log4JLoggingModule()), new Properties());
      Injector injector = Guice.createInjector(new JschSshClientModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   public void testCreate() throws Exception {
      node = context.getComputeService().startNodeInLocation("default", nodePrefix,
               Profile.SMALLEST, Image.CENTOS_53);
      assertNotNull(node.getId());
      assertEquals(node.getLoginPort(), 22);
      assertEquals(node.getLoginType(), LoginType.SSH);
      assertNotNull(node.getName());
      assertEquals(node.getPrivateAddresses().size(), 1);
      assertEquals(node.getPublicAddresses().size(), 1);
      assertNotNull(node.getCredentials());
      assertNotNull(node.getCredentials().account);
      assertNotNull(node.getCredentials().key);
      sshPing();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() throws Exception {
      NodeMetadata metadata = context.getComputeService().getNodeMetadata(node);
      assertEquals(metadata.getId(), node.getId());
      assertEquals(metadata.getLoginPort(), node.getLoginPort());
      assertEquals(metadata.getLoginType(), node.getLoginType());
      assertEquals(metadata.getName(), node.getName());
      assertEquals(metadata.getPrivateAddresses(), node.getPrivateAddresses());
      assertEquals(metadata.getPublicAddresses(), node.getPublicAddresses());
   }

   public void testList() throws Exception {
      for (ComputeMetadata node : context.getComputeService().listNodes()) {
         assert node.getId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   private void sshPing() throws IOException {
      try {
         doCheckKey();
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCheckKey();
      }
   }

   private void doCheckKey() throws IOException {
      InetSocketAddress socket = new InetSocketAddress(node.getPublicAddresses().last(), node
               .getLoginPort());
      socketTester.apply(socket);
      SshClient ssh = node.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----") ? sshFactory
               .create(socket, node.getCredentials().account, node.getCredentials().key.getBytes())
               : sshFactory
                        .create(socket, node.getCredentials().account, node.getCredentials().key);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         context.getComputeService().destroyNode(node);
      context.close();
   }

   public void testParseInetAddress() throws UnknownHostException {

      Server rhServer = createMock(Server.class);
      IpAddresses addresses = createMock(IpAddresses.class);
      expect(rhServer.getIpAddresses()).andReturn(addresses).atLeastOnce();
      expect(addresses.getPrimaryIp()).andReturn("127.0.0.1");
      expect(addresses.getSecondaryIps()).andReturn(ImmutableSortedSet.of("www.yahoo.com"));
      replay(rhServer);
      replay(addresses);

      // assertEquals(Sets
      // .newLinkedHashSet(RimuHostingCreateNodeResponse.getPublicAddresses(rhServer)),
      // ImmutableSet.of(InetAddress.getByName("127.0.0.1"), InetAddress
      // .getByName("www.yahoo.com")));
   }

   @Test(enabled = false)
   public void test() throws UnknownHostException {

      NewServerResponse nsResponse = createMock(NewServerResponse.class);
      Server rhServer = createMock(Server.class);

      expect(nsResponse.getServer()).andReturn(rhServer).atLeastOnce();

      expect(rhServer.getId()).andReturn(new Long(1));
      expect(rhServer.getName()).andReturn("name");

      IpAddresses addresses = createMock(IpAddresses.class);
      expect(rhServer.getIpAddresses()).andReturn(addresses).atLeastOnce();

      expect(addresses.getPrimaryIp()).andReturn("127.0.0.1");
      expect(addresses.getSecondaryIps()).andReturn(ImmutableSortedSet.<String> of());

      NewServerData data = createMock(NewServerData.class);

      expect(nsResponse.getNewInstanceRequest()).andReturn(data).atLeastOnce();
      CreateOptions options = createMock(CreateOptions.class);
      expect(data.getCreateOptions()).andReturn(options);
      expect(options.getPassword()).andReturn("password");

      replay(nsResponse);
      replay(rhServer);
      replay(addresses);
      replay(data);
      replay(options);

      // RimuHostingCreateNodeResponse response = new RimuHostingCreateNodeResponse(nsResponse);
      // assertEquals(response.getId(), "1");
      // assertEquals(response.getName(), "name");
      // assertEquals(response.getPublicAddresses(), ImmutableSet.<InetAddress> of(InetAddress
      // .getByName("127.0.0.1")));
      // assertEquals(response.getPrivateAddresses(), ImmutableSet.<InetAddress> of());
      // assertEquals(response.getLoginPort(), 22);
      // assertEquals(response.getLoginType(), LoginType.SSH);
      // assertEquals(response.getCredentials(), new Credentials("root", "password"));

   }
}
