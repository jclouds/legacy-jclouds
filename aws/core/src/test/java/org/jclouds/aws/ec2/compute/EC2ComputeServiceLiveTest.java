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
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.ec2.EC2ContextBuilder;
import org.jclouds.aws.ec2.EC2PropertiesBuilder;
import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.ServerIdentity;
import org.jclouds.compute.domain.ServerMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = false, sequential = true, testName = "ec2.EC2ComputeServiceLiveTest")
public class EC2ComputeServiceLiveTest {

   private EC2ComputeService client;
   protected SshClient.Factory sshFactory;
   private String serverPrefix = System.getProperty("user.name") + ".ec2";

   private RetryablePredicate<InetSocketAddress> socketTester;
   private CreateServerResponse server;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new EC2ContextBuilder(new EC2PropertiesBuilder(user, password).build())
               .withModules(new Log4JLoggingModule(), new JschSshClientModule()).buildInjector();
      client = injector.getInstance(EC2ComputeService.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   public void testCreate() throws Exception {
      server = client.createServer(serverPrefix, Profile.SMALLEST, Image.RHEL_53);
      assertNotNull(server.getId());
      assertEquals(server.getLoginPort(), 22);
      assertEquals(server.getLoginType(), LoginType.SSH);
      assertNotNull(server.getName());
      assertEquals(server.getPrivateAddresses().size(), 1);
      assertEquals(server.getPublicAddresses().size(), 1);
      assertNotNull(server.getCredentials());
      assertNotNull(server.getCredentials().account);
      assertNotNull(server.getCredentials().key);
      sshPing();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() throws Exception {
      ServerMetadata metadata = client.getServerMetadata(server.getId());
      assertEquals(metadata.getId(), server.getId());
      assertEquals(metadata.getLoginPort(), server.getLoginPort());
      assertEquals(metadata.getLoginType(), server.getLoginType());
      assertEquals(metadata.getName(), server.getName());
      assertEquals(metadata.getPrivateAddresses(), server.getPrivateAddresses());
      assertEquals(metadata.getPublicAddresses(), server.getPublicAddresses());
   }

   public void testList() throws Exception {
      for (ServerIdentity server : client.listServers()) {
         assert server.getId() != null;
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
      InetSocketAddress socket = new InetSocketAddress(server.getPublicAddresses().last(), server
               .getLoginPort());
      socketTester.apply(socket);
      SshClient connection = sshFactory.create(socket, server.getCredentials().account, server
               .getCredentials().key.getBytes());
      try {
         connection.connect();
         InputStream etcPasswd = connection.get("/etc/passwd");
         Utils.toStringAndClose(etcPasswd);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (server != null)
         client.destroyServer(server.getId());
   }

}
