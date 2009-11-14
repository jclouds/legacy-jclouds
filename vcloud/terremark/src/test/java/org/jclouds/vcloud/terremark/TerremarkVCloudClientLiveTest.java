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

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.ResourceType;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
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
   private VApp vApp;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testDefaultVDC() throws Exception {
      super.testDefaultVDC();
      TerremarkVDC response = (TerremarkVDC) tmClient.getDefaultVDC().get(45, TimeUnit.SECONDS);
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
      long hardDisk = 4194304;
      String catalogOs = "Ubuntu JeOS 9.04 (32-bit)";
      String expectedOs = "Ubuntu Linux (32-bit)";

      int templateId = tmClient.getCatalog().get(45, TimeUnit.SECONDS).get(catalogOs).getId();

      vApp = tmClient.instantiateVAppTemplate(serverName, templateId).get(45, TimeUnit.SECONDS);

      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      Task instantiateTask = getLastTaskFor(vApp.getVDC().getLocation());
      assertEquals(instantiateTask.getStatus(), TaskStatus.QUEUED);

      // in terremark, this should be a no-op, as it should simply return the above task, which is
      // already deploying
      Task deployTask = tmClient.deployVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(deployTask.getLocation(), instantiateTask.getLocation());

      // check to see the result of calling deploy twice
      deployTask = tmClient.deployVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(deployTask.getLocation(), instantiateTask.getLocation());

      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      try {// per docs, this is not supported
         tmClient.cancelTask(deployTask.getLocation()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      deployTask = blockUntilSuccess(deployTask);

      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      deployTask = blockUntilSuccess(tmClient.powerOnVApp(vApp.getId()).get(45, TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

   }

   @Test
   public void testAddInternetService() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      is = tmClient.addInternetService("SSH", "TCP", 22).get(45, TimeUnit.SECONDS);
   }

   @Test(dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      node = tmClient.addNode(is.getId(), vApp.getNetworkToAddresses().values().iterator().next(),
               vApp.getName() + "-SSH", 22).get(45, TimeUnit.SECONDS);
      publicIp = is.getPublicIpAddress().getAddress();
      try {
         doCheckPass(publicIp);
      } catch (Exception e) {
         // TODO - harden this up, when we stop hanging
         System.err.printf("%s:22 -> %s:22%n%s%n", vApp.getNetworkToAddresses().values().iterator()
                  .next(), publicIp, e.getMessage());
      }
   }

   @Test(dependsOnMethods = "testPublicIp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {

      try {// per docs, this is not supported
         tmClient.undeployVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      try {// per docs, this is not supported
         tmClient.suspendVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      blockUntilSuccess(tmClient.resetVApp(vApp.getId()).get(45, TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      tmClient.shutdownVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      blockUntilSuccess(tmClient.powerOffVApp(vApp.getId()).get(45, TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);
   }

   private void verifyConfigurationOfVApp(VApp vApp, String serverName, String expectedOs,
            int processorCount, int memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.VIRTUAL_CPU)
               .getVirtualQuantity(), processorCount);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER)
               .getVirtualQuantity(), 1);
      assertEquals(
               vApp.getResourceAllocationByType().get(ResourceType.MEMORY).getVirtualQuantity(),
               memory);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.VIRTUAL_DISK)
               .getVirtualQuantity(), hardDisk);
      assertEquals(vApp.getSize(), vApp.getResourceAllocationByType()
               .get(ResourceType.VIRTUAL_DISK).getVirtualQuantity());
   }

   private Task blockUntilSuccess(Task task) throws InterruptedException, ExecutionException,
            TimeoutException {
      for (task = tmClient.getTask(task.getLocation()).get(30, TimeUnit.SECONDS); task.getStatus() != TaskStatus.SUCCESS; task = tmClient
               .getTask(task.getLocation()).get(30, TimeUnit.SECONDS)) {
         System.out.printf("%s blocking on status active: currently: %s%n", task.getOwner()
                  .getName(), task.getStatus());
         Thread.sleep(5 * 1000);
      }
      System.out.printf("%s complete%n", task.getResult().getName());
      return task;
   }

   private Task getLastTaskFor(URI owner) throws InterruptedException, ExecutionException,
            TimeoutException {
      return Iterables.getLast(tmClient.getDefaultTasksList().get(45, TimeUnit.SECONDS)
               .getTasksByOwner().get(owner));
   }

   private void doCheckPass(InetAddress address) throws IOException {
      System.out.printf("%s:%s%n", address, 22);
      SshClient connection = sshFactory.create(new InetSocketAddress(address, 22), "vcloud",
               "p4ssw0rd");
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
      if (node != null)
         tmClient.deleteNode(node.getId()).get(30, TimeUnit.SECONDS);
      if (is != null)
         tmClient.deleteInternetService(is.getId()).get(30, TimeUnit.SECONDS);
      if (vApp != null) {
         try {
            blockUntilSuccess(tmClient.powerOffVApp(vApp.getId()).get(45, TimeUnit.SECONDS));
         } catch (Exception e) {
         }
         tmClient.deleteVApp(vApp.getId()).get(45, TimeUnit.SECONDS);
      }
   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = tmClient = new TerremarkVCloudContextBuilder(
               new TerremarkVCloudPropertiesBuilder(account, key).build()).withModules(
               new Log4JLoggingModule()).buildContext().getApi();
      Injector injector = Guice.createInjector(new Log4JLoggingModule(), new JschSshClientModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService()));
      sshFactory = injector.getInstance(SshClient.Factory.class);

   }

}
