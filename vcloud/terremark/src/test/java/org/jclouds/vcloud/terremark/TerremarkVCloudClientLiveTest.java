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
package org.jclouds.vcloud.terremark;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.options.CloneVAppOptions.Builder.deploy;
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.changeNameTo;
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.deleteDiskWithAddressOnParent;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudClientLiveTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.terremark.domain.ComputeOptions;
import org.jclouds.vcloud.terremark.domain.CustomizationParameters;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.InternetServiceConfiguration;
import org.jclouds.vcloud.terremark.domain.IpAddress;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

   private RetryablePredicate<InetSocketAddress> socketTester;

   private RetryablePredicate<String> successTester;

   private VApp clone;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testGetIpAddressesForNetwork() throws Exception {
      VDC response = tmClient.getDefaultVDC();
      for (NamedResource resource : response.getAvailableNetworks().values()) {
         if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
            SortedSet<IpAddress> addresses = tmClient.getIpAddressesForNetwork(resource.getId());
            assertNotNull(addresses);
         }
      }
   }

   @Test
   public void testGetAllInternetServices() throws Exception {
      for (InternetService service : tmClient.getAllInternetServicesInVDC(tmClient.getDefaultVDC()
               .getId())) {
         assertNotNull(tmClient.getNodes(service.getId()));
      }
   }

   @Test
   public void testGetPublicIpsAssociatedWithVDC() throws Exception {
      for (PublicIpAddress ip : tmClient.getPublicIpsAssociatedWithVDC(tmClient.getDefaultVDC()
               .getId())) {
         assertNotNull(tmClient.getInternetServicesOnPublicIp(ip.getId()));
         assertNotNull(tmClient.getPublicIp(ip.getId()));
      }
   }

   @Test
   public void testGetConfigCustomizationOptions() throws Exception {
      Catalog response = connection.getDefaultCatalog();
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            CatalogItem item = connection.getCatalogItem(resource.getId());
            SortedSet<ComputeOptions> options = tmClient.getComputeOptionsOfCatalogItem(item
                     .getId());
            assert options.size() == 32 || options.size() == 20 : item.getId() + ": "
                     + options.size();
            assert tmClient.getCustomizationOptionsOfCatalogItem(item.getId()) != null;
         }
      }
   }

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
      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("a");
      String serverName = name.toString();// "adriantest";

      long hardDisk = 4194304;

      String expectedOs = "Ubuntu Linux (32-bit)";
      // long hardDisk = 4194304 / 4 * 10;
      // String catalogOs = "CentOS 5.3 (32-bit)";
      // String expectedOs = "Red Hat Enterprise Linux 5 (32-bit)";

      // lookup the id of the datacenter you are deploying into
      String vDCId = tmClient.getDefaultVDC().getId();

      // lookup the id of the item in the catalog you wish to deploy by name
      Catalog catalog = tmClient.getDefaultCatalog();
      String itemId = catalog.get("Ubuntu JeOS 9.10 (32-bit)").getId();

      // determine the cheapest configuration size
      SortedSet<ComputeOptions> sizeOptions = tmClient.getComputeOptionsOfCatalogItem(itemId);

      ComputeOptions cheapestOption = Iterables.find(sizeOptions, new Predicate<ComputeOptions>() {

         @Override
         public boolean apply(ComputeOptions arg0) {
            return arg0.getProcessorCount() == 2;
         }

      });

      // create an options object to collect the configuration we want.
      TerremarkInstantiateVAppTemplateOptions instantiateOptions = processorCount(
               cheapestOption.getProcessorCount()).memory(cheapestOption.getMemory());

      // if this template supports setting the root password, let's add it to our options
      CustomizationParameters customizationOptions = tmClient
               .getCustomizationOptionsOfCatalogItem(itemId);
      if (customizationOptions.canCustomizePassword())
         instantiateOptions.withPassword("robotsarefun");

      // the vAppTemplateId tends to be the same as the itemId, but just in case, convert
      String vAppTemplateId = tmClient.getCatalogItem(itemId).getEntity().getId();

      // instantiate, noting vApp returned has minimal details
      vApp = tmClient.instantiateVAppTemplateInVDC(vDCId, serverName, vAppTemplateId,
               instantiateOptions);

      assertEquals(vApp.getStatus(), VAppStatus.RESOLVED);

      // in terremark, this should be a no-op, as it should simply return the above task, which is
      // already deploying
      Task deployTask = tmClient.deployVApp(vApp.getId());

      // check to see the result of calling deploy twice
      deployTask = tmClient.deployVApp(vApp.getId());
      assertEquals(deployTask.getLocation(), deployTask.getLocation());

      vApp = tmClient.getVApp(vApp.getId());

      assertEquals(vApp.getStatus(), VAppStatus.RESOLVED);

      try {// per docs, this is not supported
         tmClient.cancelTask(deployTask.getId());
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 501);
      }

      assert successTester.apply(deployTask.getId());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getId());

      NamedResource vAppResource = tmClient.getDefaultVDC().getResourceEntities().get(serverName);
      assertEquals(vAppResource.getId(), vApp.getId());

      int processorCount = cheapestOption.getProcessorCount();
      long memory = cheapestOption.getMemory();
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), VAppStatus.OFF);

      assert successTester.apply(tmClient.powerOnVApp(vApp.getId()).getId());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.ON);
      System.out.println(tmClient.getComputeOptionsOfVApp(vApp.getId()));
      System.out.println(tmClient.getCustomizationOptionsOfVApp(vApp.getId()));
   }

   @Test
   public void testAddInternetService() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      is = tmClient.addInternetServiceToVDC(tmClient.getDefaultVDC().getId(), "SSH", Protocol.TCP,
               22);
      publicIp = is.getPublicIpAddress().getAddress();
   }

   // 400 errors
   @Test(dependsOnMethods = { "testAddInternetService" }, expectedExceptions = HttpResponseException.class)
   public void testConfigureInternetService() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      is = tmClient.configureInternetService(is.getId(), new InternetServiceConfiguration()
               .changeDescriptionTo("holy cow"));
      assertEquals(is.getDescription(), "holy cow");
   }

   @Test(dependsOnMethods = { "testInstantiateAndPowerOn" })
   public void testCloneVApp() throws IOException {
      assert successTester.apply(tmClient.powerOffVApp(vApp.getId()).getId());
      System.out.printf("%d: done powering off vApp%n", System.currentTimeMillis());

      // lookup the id of the datacenter you are deploying into
      String vDCId = tmClient.getDefaultVDC().getId();

      String vAppIdToClone = vApp.getId();

      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("b");
      String newName = name.toString();

      CloneVAppOptions options = deploy().powerOn()
               .withDescription("The description of " + newName);

      System.out.printf("%d: cloning vApp%n", System.currentTimeMillis());
      Task task = tmClient.cloneVAppInVDC(vDCId, vAppIdToClone, newName, options);

      // wait for the task to complete
      assert successTester.apply(task.getId());
      System.out.printf("%d: done cloning vApp%n", System.currentTimeMillis());

      assert successTester.apply(tmClient.powerOnVApp(vApp.getId()).getId());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      // refresh task to get the new vApp location
      task = tmClient.getTask(task.getId());

      clone = tmClient.getVApp(task.getResult().getId());
      assertEquals(clone.getStatus(), VAppStatus.ON);

      assertEquals(clone.getName(), newName);
      assertEquals(clone.getNetworkToAddresses().values().size(), 1);
   }

   @Test(dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      node = tmClient.addNode(is.getId(), Iterables.getLast(vApp.getNetworkToAddresses().values()),
               vApp.getName() + "-SSH", 22);
      loopAndCheckPass();
   }

   private void loopAndCheckPass() throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            doCheckPass(publicIp);
            return;
         } catch (SshException e) {
            try {
               Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
            }
            continue;
         }
      }
   }

   // 400 errors
   @Test(dependsOnMethods = { "testPublicIp" }, expectedExceptions = HttpResponseException.class)
   public void testConfigureNode() throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      node = tmClient.configureNode(node.getId(), new NodeConfiguration()
               .changeDescriptionTo("holy cow"));
      assertEquals(node.getDescription(), "holy cow");
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

      assert successTester.apply(tmClient.resetVApp(vApp.getId()).getId());

      vApp = tmClient.getVApp(vApp.getId());

      assertEquals(vApp.getStatus(), VAppStatus.ON);

      // TODO we need to determine whether shutdown is supported before invoking it.
      // tmClient.shutdownVApp(vApp.getId());
      // vApp = tmClient.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(tmClient.powerOffVApp(vApp.getId()).getId());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getStatus(), VAppStatus.OFF);
   }

   @Test(dependsOnMethods = "testLifeCycle")
   public void testConfigure() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {

      vApp = tmClient.getVApp(vApp.getId());

      Task task = tmClient.configureVApp(vApp, changeNameTo("eduardo").changeMemoryTo(1536)
               .changeProcessorCountTo(1).addDisk(25 * 1048576).addDisk(25 * 1048576));

      assert successTester.apply(task.getId());

      vApp = tmClient.getVApp(vApp.getId());
      assertEquals(vApp.getName(), "eduardo");
      assertEquals(
               Iterables.getOnlyElement(
                        vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR))
                        .getVirtualQuantity(), 1);
      assertEquals(Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.MEMORY)).getVirtualQuantity(),
               1536);
      assertEquals(vApp.getResourceAllocationByType().get(ResourceType.DISK_DRIVE).size(), 3);

      assert successTester.apply(tmClient.powerOnVApp(vApp.getId()).getId());

      loopAndCheckPass();

      assert successTester.apply(tmClient.powerOffVApp(vApp.getId()).getId());

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocation> disks = Lists.newArrayList(vApp.getResourceAllocationByType().get(
               ResourceType.DISK_DRIVE));

      // delete the second disk
      task = tmClient.configureVApp(vApp, deleteDiskWithAddressOnParent(disks.get(1)
               .getAddressOnParent()));

      assert successTester.apply(task.getId());

      assert successTester.apply(tmClient.powerOnVApp(vApp.getId()).getId());
      loopAndCheckPass();
   }

   private void verifyConfigurationOfVApp(VApp vApp, String serverName, String expectedOs,
            int processorCount, long memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals(
               Iterables.getOnlyElement(
                        vApp.getResourceAllocationByType().get(ResourceType.PROCESSOR))
                        .getVirtualQuantity(), processorCount);
      assertEquals(Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.SCSI_CONTROLLER))
               .getVirtualQuantity(), 1);
      assertEquals(Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.MEMORY)).getVirtualQuantity(),
               memory);
      assertEquals(Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.DISK_DRIVE))
               .getVirtualQuantity(), hardDisk);
      assertEquals(vApp.getSize().longValue(), Iterables.getOnlyElement(
               vApp.getResourceAllocationByType().get(ResourceType.DISK_DRIVE))
               .getVirtualQuantity());
   }

   private void doCheckPass(InetAddress address) throws IOException {
      InetSocketAddress socket = new InetSocketAddress(address, 22);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               socket);
      assert socketTester.apply(socket);
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), socket);

      SshClient connection = sshFactory.create(socket, "vcloud", "$Ep455l0ud!2");
      try {
         connection.connect();
         System.out.printf("%d: %s ssh connection made%n", System.currentTimeMillis(), socket);
         System.out.println(connection.exec("df -h"));
         System.out.println(connection.exec("ls -al /dev/sd*"));
         System.out.println(connection.exec("echo '$Ep455l0ud!2'|sudo -S fdisk -l"));
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
            successTester.apply(tmClient.powerOffVApp(vApp.getId()).getId());
         } catch (Exception e) {

         }
         tmClient.deleteVApp(vApp.getId());
      }
      if (clone != null) {
         try {
            successTester.apply(tmClient.powerOffVApp(clone.getId()).getId());
         } catch (Exception e) {

         }
         tmClient.deleteVApp(clone.getId());
      }

   }

   @BeforeGroups(groups = { "live" })
   @Override
   public void setupClient() {
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new TerremarkVCloudContextBuilder("terremark",
               new TerremarkVCloudPropertiesBuilder(account, key).build()).withModules(
               new Log4JLoggingModule(), new JschSshClientModule()).buildInjector();

      connection = tmClient = injector.getInstance(TerremarkVCloudClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(injector
               .getInstance(SocketOpen.class), 130, 10, TimeUnit.SECONDS);// make it longer then
      // default internet
      // service timeout
      successTester = new RetryablePredicate<String>(injector.getInstance(TaskSuccess.class), 650,
               10, TimeUnit.SECONDS);
   }

}
