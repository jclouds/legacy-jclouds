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

package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.domain.OperatingSystem.CENTOS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * Generally disabled, as it incurs higher fees.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.HostingDotComVCloudComputeServiceLiveTest")
public class HostingDotComVCloudComputeServiceLiveTest {
   private static final String service = "hostingdotcom";
   private static final OperatingSystem testOS = CENTOS;

   protected SshClient.Factory sshFactory;
   private String nodeName = service;

   private RetryablePredicate<InetSocketAddress> socketTester;
   private CreateNodeResponse node;
   private ComputeServiceContext context;
   private ComputeService client;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      context = new ComputeServiceContextFactory().createContext(service, user, password,
               ImmutableSet.of(new Log4JLoggingModule()), new Properties());
      Injector injector = Guice.createInjector(new JschSshClientModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
      client = context.getComputeService();
   }

   public void testCreate() throws Exception {
      Template template = client.createTemplateInLocation("default").os(testOS).smallest();
      node = client.runNode(nodeName, template);
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
      NodeMetadata metadata = client.getNodeMetadata(node);
      assertEquals(metadata.getId(), node.getId());
      assertEquals(metadata.getLoginPort(), node.getLoginPort());
      assertEquals(metadata.getLoginType(), node.getLoginType());
      assertEquals(metadata.getName(), node.getName());
      assertEquals(metadata.getPrivateAddresses(), node.getPrivateAddresses());
      assertEquals(metadata.getPublicAddresses(), node.getPublicAddresses());
   }

   public void testList() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   public void testListTemplates() throws Exception {
      for (Template template : client.listTemplates()) {
         assert template.getImage() != null;
         System.out.println(template);
      }
   }

   public void testListSizes() throws Exception {
      for (Size size : client.listSizes()) {
         assert size.getCores() != null;
         System.out.println(size);
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
         client.destroyNode(node);
      context.close();
   }
}
