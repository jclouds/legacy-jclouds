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

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.domain.ResourceType;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.domain.VApp;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class TerremarkVCloudClientLiveTest extends VCloudClientLiveTest {
   TerremarkVCloudClient tmClient;

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
   public void testInstantiate() throws InterruptedException, ExecutionException, TimeoutException {
      String serverName = "adriantest4";
      int processorCount = 1;
      int memory = 512;
      String catalogOs = "Ubuntu Server 9.04 (32-bit)";
      String expectedOs = "Ubuntu Linux (32-bit)";

      URI template = tmClient.getCatalog().get(45, TimeUnit.SECONDS).get(catalogOs).getLocation();

      URI network = tmClient.getDefaultVDC().get(45, TimeUnit.SECONDS).getAvailableNetworks()
               .values().iterator().next().getLocation();

      VApp vApp = tmClient.instantiateVAppTemplate(serverName, template, processorCount, memory,
               network).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      Task instantiateTask = getLastTaskFor(vApp.getVDC().getLocation());
      assertEquals(instantiateTask.getStatus(), TaskStatus.QUEUED);

      // in terremark, this should be a no-op, as it should simply return the above task, which is
      // already deploying
      Task deployTask = tmClient.deploy(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(deployTask.getLocation(), instantiateTask.getLocation());

      // check to see the result of calling deploy twice
      deployTask = tmClient.deploy(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(deployTask.getLocation(), instantiateTask.getLocation());

      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.CREATING);

      try {// per docs, this is not supported
         tmClient.cancelTask(deployTask.getLocation()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      deployTask = blockUntilSuccess(deployTask);

      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      try {// per docs, this is not supported
         tmClient.undeploy(deployTask.getResult().getLocation()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      deployTask = blockUntilSuccess(tmClient.powerOn(deployTask.getResult().getLocation()).get(45,
               TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      try {// per docs, this is not supported
         tmClient.suspend(deployTask.getResult().getLocation()).get(45, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 501);
      }

      deployTask = blockUntilSuccess(tmClient.reset(deployTask.getResult().getLocation()).get(45,
               TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      tmClient.shutdown(deployTask.getResult().getLocation()).get(45, TimeUnit.SECONDS);
      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.ON);

      deployTask = blockUntilSuccess(tmClient.powerOff(deployTask.getResult().getLocation()).get(
               45, TimeUnit.SECONDS));
      vApp = tmClient.getVApp(vApp.getLocation()).get(45, TimeUnit.SECONDS);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      tmClient.delete(deployTask.getResult().getLocation()).get(45, TimeUnit.SECONDS);
      //TODO verify not present anymore
   }

   private void verifyConfigurationOfVApp(VApp vApp, String serverName, String expectedOs,
            int processorCount, int memory) {
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
               .getVirtualQuantity(), memory * 8192);
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

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = tmClient = new TerremarkVCloudContextBuilder(
               new TerremarkVCloudPropertiesBuilder(account, key).build()).withModules(
               new Log4JLoggingModule()).buildContext().getApi();
   }

}
