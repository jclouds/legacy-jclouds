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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.terremark.VCloudComputeClient.Image;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.ImmutableMap;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class TerremarkVCloudComputeClientLiveTest {
   VCloudComputeClient client;
   TerremarkVCloudClient tmClient;

   private String id;
   private InetAddress privateAddress;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   private static class Expectation {
      final long hardDisk;
      final String os;

      public Expectation(long hardDisk, String os) {
         this.hardDisk = hardDisk;
         this.os = os;
      }
   }

   private Map<Image, Expectation> expectationMap = ImmutableMap.<Image, Expectation> builder()
            .put(Image.CENTOS_53,
                     new Expectation(4194304 / 4 * 10, "Red Hat Enterprise Linux 5 (64-bit)")).put(
                     Image.RHEL_53,
                     new Expectation(4194304 / 4 * 10, "Red Hat Enterprise Linux 5 (64-bit)")).put(
                     Image.UMBUNTU_90, new Expectation(4194304, "Ubuntu Linux (64-bit)")).put(
                     Image.UMBUNTU_JEOS, new Expectation(4194304, "Ubuntu Linux (32-bit)")).build();

   private InternetService is;
   private Node node;
   private InetAddress publicIp;
   private Predicate<InetAddress> addressTester;

   @Test
   public void testPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      Image toTest = Image.CENTOS_53;

      String serverName = getCompatibleServerName(toTest);
      int processorCount = 1;
      int memory = 512;

      id = client.start(serverName, processorCount, memory, toTest);
      Expectation expectation = expectationMap.get(toTest);

      TerremarkVApp vApp = tmClient.getVApp(id);
      verifyConfigurationOfVApp(vApp, serverName, expectation.os, processorCount, memory,
               expectation.hardDisk);
      assertEquals(vApp.getStatus(), VAppStatus.ON);
   }

   private String getCompatibleServerName(Image toTest) {
      String serverName = toTest.toString().toLowerCase().replaceAll("_", "-").substring(0,
               toTest.toString().length() <= 15 ? toTest.toString().length() : 14);
      return serverName;
   }

   @Test(dependsOnMethods = "testPowerOn")
   public void testGetAnyPrivateAddress() {
      privateAddress = client.getAnyPrivateAddress(id);
      assert !addressTester.apply(privateAddress);
   }

   @Test(dependsOnMethods = "testGetAnyPrivateAddress")
   public void testSshLoadBalanceIp() {
      is = tmClient.addInternetService("SSH", "TCP", 22);
      node = tmClient.addNode(is.getId(), privateAddress, id + "-SSH", 22);
      publicIp = is.getPublicIpAddress().getAddress();
      // assert addressTester.apply(publicIp);
      client.exec(publicIp, "uname -a");
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
      assertEquals(vApp.getSize(), vApp.getResourceAllocationByType().get(ResourceType.DISK_DRIVE)
               .getVirtualQuantity());
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         tmClient.deleteNode(node.getId());
      if (is != null)
         tmClient.deleteInternetService(is.getId());
      if (id != null)
         client.stop(id);
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(
               account, key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule(),
               new JschSshClientModule(), new AbstractModule() {

                  @Override
                  protected void configure() {
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<InetSocketAddress> socketTester(SocketOpen open) {
                     return new RetryablePredicate<InetSocketAddress>(open, 130, 10,
                              TimeUnit.SECONDS);// make it longer then
                     // default internet
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<InetAddress> addressTester(AddressReachable reachable) {
                     return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
                  }

                  @SuppressWarnings("unused")
                  @Provides
                  private Predicate<URI> successTester(TaskSuccess success) {
                     return new RetryablePredicate<URI>(success, 300, 10, TimeUnit.SECONDS);
                  }

               }).buildInjector();
      client = injector.getInstance(VCloudComputeClient.class);
      tmClient = injector.getInstance(TerremarkVCloudClient.class);
      addressTester = injector.getInstance(Key.get(new TypeLiteral<Predicate<InetAddress>>() {
      }));
   }

}
