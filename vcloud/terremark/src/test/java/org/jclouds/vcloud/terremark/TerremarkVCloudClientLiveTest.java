/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class TerremarkVCloudClientLiveTest extends VCloudClientLiveTest {
   TerremarkVCloudClient tmClient;

   private Factory sshFactory;

   private InetAddress publicIp;
   private InternetService is;
   private Node node;
   private TerremarkVApp vApp;

   private RetryablePredicate<InetSocketAddress> socketTester;

   private RetryablePredicate<URI> successTester;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testDefaultVDC() throws Exception {
      super.testDefaultVDC();
      TerremarkVDC response = (TerremarkVDC) tmClient.getDefaultVDC();
      assertNotNull(response);
      assertNotNull(response.getCatalog());
      assertNotNull(response.getInternetServices());
      assertNotNull(response.getPublicIps());
   }

   @Test
   public void testInstantiateAndPowerOn() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      String serverName = "adriantest";
      int processorCount = 1;
      int memory = 512;
      // long hardDisk = 4194304;
      // String catalogOs = "Ubuntu JeOS 9.04 (32-bit)";
      // String expectedOs = "Ubuntu Linux (32-bit)";
      long hardDisk = 4194304 / 4 * 10;
      String catalogOs = "CentOS 5.3 (32-bit)";
      String expectedOs = "Red Hat Enterprise Linux 5 (32-bit)";

      String templateId = tmClient.getCatalog().get(catalogOs).getId();

      System.out.printf("%d: instantiating vApp%n", System.currentTimeMillis());
      vApp = tmClient.instantiateVAppTemplate(serverName, templateId);

      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      // in terremark, this should be a no-op, as it should simply return the above task, which is
      // already deploying
      Task deployTask = tmClient.deployVApp(vApp.getId());

      // check to see the result of calling deploy twice
      deployTask = tmClient.deployVApp(vApp.getId());
      assertEquals(deployTask.getLocation(), deployTask.getLocation());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      try {// per docs, this is not supported
         tmClient.cancelTask(deployTask.getLocation());
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 501);
      }

      assert successTester.apply(deployTask.getLocation());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getId());
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      assert successTester.apply(tmClient.powerOnVApp(vApp.getId()).getLocation());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.ON);

   }

   @Test
   public void testAddInternetService() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      is = tmClient.addInternetService("SSH", "TCP", 22);
   }

   @Test(dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      node = tmClient.addNode(is.getId(), vApp.getNetworkToAddresses().values().iterator().next(),
               vApp.getName() + "-SSH", 22);
      publicIp = is.getPublicIpAddress().getAddress();
      doCheckPass(publicIp);
   }

   @Test(dependsOnMethods = "testPublicIp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {

      try {// per docs, this is not supported
         tmClient.undeployVApp(vApp.getId());
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 501);
      }

      try {// per docs, this is not supported
         tmClient.suspendVApp(vApp.getId());
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 501);
      }

      assert successTester.apply(tmClient.resetVApp(vApp.getId()).getLocation());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      // TODO we need to determine whether shutdown is supported before invoking it.
      // tmClient.shutdownVApp(vApp.getId());
      // vApp = tmClient.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(tmClient.powerOffVApp(vApp.getId()).getLocation());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.OFF);
   }

   private void verifyConfigurationOfVApp(TerremarkVApp vApp, String serverName, String expectedOs,
            int processorCount, int memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR)
               .getVirtualQuantity(), processorCount);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER)
               .getVirtualQuantity(), 1);
      assertEquals(
               vApp.getResourceAllocationByType().get(ResourceType.MEMORY).getVirtualQuantity(),
               memory);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.DISK_DRIVE)
               .getVirtualQuantity(), hardDisk);
      assertEquals(vApp.getSize().longValue(), vApp.getResourceAllocationByType().get(
               ResourceType.DISK_DRIVE).getVirtualQuantity());
   }

   private void doCheckPass(InetAddress address) throws IOException {
      InetSocketAddress socket = new InetSocketAddress(address, 22);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               socket);
      assert socketTester.apply(socket);
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), socket);

      SshClient connection = sshFactory.create(socket, "vcloud", "p4ssw0rd");
      try {
         connection.connect();
         System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), socket);
         InputStream etcPasswd = connection.get("/etc/passwd");
         Utils.toStringAndClose(etcPasswd);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         tmClient.deleteNode(node.getId());
      if (is != null)
         tmClient.deleteInternetService(is.getId());
      if (vApp != null) {
         try {
            successTester.apply(tmClient.powerOffVApp(vApp.getId()).getLocation());
         } catch (Exception e) {

         }
         tmClient.deleteVApp(vApp.getId());
      }
   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(
               account, key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule(),
               new JschSshClientModule()).buildInjector();

      connection = tmClient = injector.getInstance(TerremarkVCloudClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(injector
               .getInstance(SocketOpen.class), 130, 10, TimeUnit.SECONDS);// make it longer then
      // default internet
      // service timeout
      successTester = new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), 300, 10,
               TimeUnit.SECONDS);
   }

}
