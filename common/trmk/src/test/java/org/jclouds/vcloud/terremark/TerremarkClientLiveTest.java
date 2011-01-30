/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.vcloud.options.CloneVAppOptions.Builder.deploy;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.changeNameTo;
import static org.jclouds.vcloud.terremark.domain.VAppConfiguration.Builder.deleteDiskWithAddressOnParent;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.VCloudExpressClientLiveTest;
import org.jclouds.vcloud.VCloudExpressMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.terremark.domain.CustomizationParameters;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkCatalogItem;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class TerremarkClientLiveTest extends VCloudExpressClientLiveTest {

   protected String expectedOs = "Ubuntu Linux (64-bit)";
   protected String itemName = "Ubuntu JeOS 9.10 (64-bit)";

   protected TerremarkVCloudClient tmClient;
   protected Factory sshFactory;
   private String publicIp;
   private InternetService is;
   private Node node;
   private VCloudExpressVApp vApp;
   private RetryablePredicate<IPSocket> socketTester;
   private RetryablePredicate<URI> successTester;
   private VCloudExpressVApp clone;
   private VDC vdc;
   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testGetAllInternetServices() throws Exception {
      for (InternetService service : tmClient.getAllInternetServicesInVDC(tmClient.findVDCInOrgNamed(null, null)
               .getHref())) {
         assertNotNull(tmClient.getNodes(service.getId()));
      }
   }

   @Test
   public void testGetPublicIpsAssociatedWithVDC() throws Exception {
      for (PublicIpAddress ip : tmClient
               .getPublicIpsAssociatedWithVDC(tmClient.findVDCInOrgNamed(null, null).getHref())) {
         assertNotNull(tmClient.getInternetServicesOnPublicIp(ip.getId()));
         assertNotNull(tmClient.getPublicIp(ip.getId()));
      }
   }

   @Test
   public void testGetConfigCustomizationOptions() throws Exception {
      Catalog response = connection.findCatalogInOrgNamed(null, null);
      for (ReferenceType resource : response.values()) {
         if (resource.getType().equals(VCloudExpressMediaType.CATALOGITEM_XML)) {
            TerremarkCatalogItem item = tmClient.findCatalogItemInOrgCatalogNamed(null, null, resource.getName());
            assert tmClient.getCustomizationOptions(item.getCustomizationOptions().getHref()) != null;
         }
      }
   }

   @Test
   public void testDefaultVDC() throws Exception {
      super.testDefaultVDC();
      TerremarkVDC response = (TerremarkVDC) tmClient.findVDCInOrgNamed(null, null);
      assertNotNull(response);
      assertNotNull(response.getCatalog());
      assertNotNull(response.getInternetServices());
      assertNotNull(response.getPublicIps());
   }

   @Test(enabled = true)
   public void testInstantiateAndPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      prepare();
      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("c");
      String serverName = name.toString();// "adriantest";

      long hardDisk = 4194304;

      // long hardDisk = 4194304 / 4 * 10;
      // String catalogOs = "CentOS 5.3 (64-bit)";
      // String expectedOs = "Red Hat Enterprise Linux 5 (64-bit)";

      // lookup the datacenter you are deploying into
      vdc = tmClient.findVDCInOrgNamed(null, null);

      // create an options object to collect the configuration we want.
      TerremarkInstantiateVAppTemplateOptions instantiateOptions = createInstantiateOptions();

      TerremarkCatalogItem item = tmClient.findCatalogItemInOrgCatalogNamed(null, null, itemName);

      assert item != null;

      // if this template supports setting the root password, let's add it to
      // our options
      CustomizationParameters customizationOptions = tmClient.getCustomizationOptions(item.getCustomizationOptions()
               .getHref());
      if (customizationOptions.canCustomizePassword())
         instantiateOptions.withPassword("robotsarefun");

      VCloudExpressVAppTemplate vAppTemplate = tmClient.getVAppTemplate(item.getEntity().getHref());

      assert vAppTemplate != null;

      // instantiate, noting vApp returned has minimal details
      vApp = tmClient.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName,
               instantiateOptions);

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      // in terremark, this should be a no-op, as it should simply return the
      // above task, which is
      // already deploying
      Task deployTask = tmClient.deployVApp(vApp.getHref());

      // check to see the result of calling deploy twice
      deployTask = tmClient.deployVApp(vApp.getHref());
      assertEquals(deployTask.getHref(), deployTask.getHref());

      vApp = tmClient.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      try {// per docs, this is not supported
         tmClient.cancelTask(deployTask.getHref());
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(deployTask.getHref());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getHref());

      ReferenceType vAppResource = tmClient.findVDCInOrgNamed(null, null).getResourceEntities().get(serverName);
      assertEquals(vAppResource.getHref(), vApp.getHref());

      int processorCount = 1;
      long memory = 512;
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), Status.OFF);

      assert successTester.apply(tmClient.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = tmClient.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.ON);
   }

   protected void prepare() {

   }

   abstract TerremarkInstantiateVAppTemplateOptions createInstantiateOptions();

   protected abstract Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIpForSSH(VCloudExpressVApp vApp);

   @Test(enabled = true, dependsOnMethods = "testInstantiateAndPowerOn")
   public void testAddInternetService() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      Entry<InternetService, PublicIpAddress> entry = getNewInternetServiceAndIpForSSH(vApp);
      is = entry.getKey();
      publicIp = entry.getValue().getAddress();
   }

   @Test(enabled = true, dependsOnMethods = "testInstantiateAndPowerOn")
   public void testCloneVApp() throws IOException {
      assert successTester.apply(tmClient.powerOffVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering off vApp%n", System.currentTimeMillis());

      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("b");
      String newName = name.toString();

      CloneVAppOptions options = deploy().powerOn().withDescription("The description of " + newName);

      System.out.printf("%d: cloning vApp%n", System.currentTimeMillis());
      Task task = tmClient.cloneVAppInVDC(vdc.getHref(), vApp.getHref(), newName, options);

      // wait for the task to complete
      assert successTester.apply(task.getHref());
      System.out.printf("%d: done cloning vApp%n", System.currentTimeMillis());

      assert successTester.apply(tmClient.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      // refresh task to get the new vApp location
      task = tmClient.getTask(task.getHref());

      clone = tmClient.getVApp(task.getOwner().getHref());
      assertEquals(clone.getStatus(), Status.ON);

      assertEquals(clone.getName(), newName);
      assertEquals(clone.getNetworkToAddresses().values().size(), 1);
   }

   @Test(enabled = true, dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      node = tmClient.addNode(is.getId(), Iterables.getLast(vApp.getNetworkToAddresses().values()), vApp.getName()
               + "-SSH", 22);
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

   @Test(enabled = true, dependsOnMethods = "testPublicIp")
   public void testConfigureNode() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      tmClient.configureNode(node.getId(), node.getName(), node.isEnabled(), "holy cow");
   }

   @Test(enabled = true, dependsOnMethods = "testPublicIp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      try {// per docs, this is not supported
         tmClient.undeployVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      try {// per docs, this is not supported
         tmClient.suspendVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(tmClient.resetVApp(vApp.getHref()).getHref());

      vApp = tmClient.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.ON);

      // TODO we need to determine whether shutdown is supported before invoking
      // it.
      // tmClient.shutdownVApp(vApp.getId());
      // vApp = tmClient.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(tmClient.powerOffVApp(vApp.getHref()).getHref());

      vApp = tmClient.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.OFF);
   }

   @Test(enabled = true, dependsOnMethods = "testLifeCycle")
   public void testConfigure() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      vApp = tmClient.getVApp(vApp.getHref());

      Task task = tmClient.configureVApp(vApp, changeNameTo("eduardo").changeMemoryTo(1536).changeProcessorCountTo(1)
               .addDisk(25 * 1048576).addDisk(25 * 1048576));

      assert successTester.apply(task.getHref());

      vApp = tmClient.getVApp(vApp.getHref());
      assertEquals(vApp.getName(), "eduardo");
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)).getVirtualQuantity(), 1);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity(), 1536);
      assertEquals(size(filter(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE))), 3);

      assert successTester.apply(tmClient.powerOnVApp(vApp.getHref()).getHref());

      loopAndCheckPass();

      assert successTester.apply(tmClient.powerOffVApp(vApp.getHref()).getHref());

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocation> disks = Lists.newArrayList(filter(vApp.getResourceAllocations(),
               resourceType(ResourceType.DISK_DRIVE)));

      // delete the second disk
      task = tmClient.configureVApp(vApp, deleteDiskWithAddressOnParent(disks.get(1).getAddressOnParent()));

      assert successTester.apply(task.getHref());

      assert successTester.apply(tmClient.powerOnVApp(vApp.getHref()).getHref());
      loopAndCheckPass();
   }

   private void verifyConfigurationOfVApp(VCloudExpressVApp vApp, String serverName, String expectedOs,
            int processorCount, long memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)).getVirtualQuantity(),
               processorCount);
      assertEquals(
               find(vApp.getResourceAllocations(), resourceType(ResourceType.SCSI_CONTROLLER)).getVirtualQuantity(), 1);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity(), memory);
      assertEquals(find(vApp.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity(),
               hardDisk);
      assertEquals(vApp.getSize().longValue(), find(vApp.getResourceAllocations(),
               resourceType(ResourceType.DISK_DRIVE)).getVirtualQuantity());
   }

   private void doCheckPass(String address) throws IOException {
      IPSocket socket = new IPSocket(address, 22);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(), socket);
      assert socketTester.apply(socket);
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), socket);

      SshClient connection = getConnectionFor(socket);
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

   protected abstract SshClient getConnectionFor(IPSocket socket);

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         tmClient.deleteNode(node.getId());
      if (is != null)
         tmClient.deleteInternetService(is.getId());
      if (vApp != null) {
         try {
            successTester.apply(tmClient.powerOffVApp(vApp.getHref()).getHref());
         } catch (Exception e) {

         }
         tmClient.deleteVApp(vApp.getHref());
      }
      if (clone != null) {
         try {
            successTester.apply(tmClient.powerOffVApp(clone.getHref()).getHref());
         } catch (Exception e) {

         }
         tmClient.deleteVApp(clone.getHref());
      }

   }

   protected String provider = "trmk-vcloudexpress";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      Injector injector = new RestContextFactory().createContextBuilder(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides)
               .buildInjector();

      connection = tmClient = injector.getInstance(TerremarkVCloudClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(injector.getInstance(SocketOpen.class), 130, 10, TimeUnit.SECONDS);// make
      // it
      // longer
      // then
      // default internet
      // service timeout
      successTester = new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), 650, 10, TimeUnit.SECONDS);
   }

}