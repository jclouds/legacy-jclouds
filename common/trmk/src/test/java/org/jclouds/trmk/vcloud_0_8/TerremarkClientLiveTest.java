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
package org.jclouds.trmk.vcloud_0_8;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration.Builder.changeNameTo;
import static org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration.Builder.deleteDiskWithAddressOnParent;
import static org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions.Builder.deploy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.cim.CIMPredicates;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
import org.jclouds.ssh.SshException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.CustomizationParameters;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.Protocol;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.options.AddInternetServiceOptions;
import org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.predicates.TaskSuccess;
import org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups =  "live" , singleThreaded = true)
public abstract class TerremarkClientLiveTest {

   protected String expectedOs = "Ubuntu Linux (64-bit)";
   protected String itemName = "Ubuntu JeOS 9.10 (64-bit)";

   protected Factory sshFactory;
   protected String publicIp;
   protected InternetService is;
   protected Node node;
   protected VApp vApp;
   protected RetryablePredicate<IPSocket> socketTester;
   protected RetryablePredicate<URI> successTester;
   protected Injector injector;

   protected VApp clone;
   protected VDC vdc;
   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testKeysList() throws Exception {
      for (Org org : orgs) {
         TerremarkVCloudClient vCloudExpressClient = TerremarkVCloudClient.class.cast(connection);
         Set<KeyPair> response = vCloudExpressClient.listKeyPairsInOrg(org.getHref());
         assertNotNull(response);
      }
   }

   @Test
   public void testGetAllInternetServices() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            for (InternetService service : connection.getAllInternetServicesInVDC(vdc.getHref())) {
               assertNotNull(connection.getNodes(service.getId()));
            }
         }
      }
   }

   @Test
   public void testCreateInternetServiceMonitorDisabled() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            Set<PublicIpAddress> publicIpAddresses = connection.getPublicIpsAssociatedWithVDC(vdc.getHref());
            PublicIpAddress publicIp = publicIpAddresses.iterator().next();
            InternetService service = connection.addInternetServiceToExistingIp(publicIp.getId(), PREFIX
                  + "-no-monitoring", Protocol.TCP, 1234, AddInternetServiceOptions.Builder.monitorDisabled());
            connection.deleteInternetService(service.getId());
         }
      }
   }

   @Test
   public void testGetPublicIpsAssociatedWithVDC() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            for (PublicIpAddress ip : connection.getPublicIpsAssociatedWithVDC(vdc.getHref())) {
               assertNotNull(connection.getInternetServicesOnPublicIp(ip.getId()));
               assertNotNull(connection.getPublicIp(ip.getId()));
            }
         }
      }
   }

   @Test
   public void testGetConfigCustomizationOptions() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType catalog : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(catalog.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.findCatalogItemInOrgCatalogNamed(org.getName(), catalog.getName(),
                        resource.getName());
                  assert connection.getCustomizationOptions(item.getCustomizationOptions().getHref()) != null;
               }
            }
         }
      }
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
      vdc = connection.findVDCInOrgNamed(null, null);

      // create an options object to collect the configuration we want.
      InstantiateVAppTemplateOptions instantiateOptions = createInstantiateOptions();

      CatalogItem item = connection.findCatalogItemInOrgCatalogNamed(null, null, itemName);

      assert item != null;

      // if this template supports setting the root password, let's add it to
      // our options
      CustomizationParameters customizationOptions = connection.getCustomizationOptions(item.getCustomizationOptions()
            .getHref());
      if (customizationOptions.canCustomizePassword())
         instantiateOptions.withPassword("robotsarefun");

      VAppTemplate vAppTemplate = connection.getVAppTemplate(item.getEntity().getHref());

      assert vAppTemplate != null;

      // instantiate, noting vApp returned has minimal details
      vApp = connection.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName,
            instantiateOptions);

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      // in terremark, this should be a no-op, as it should simply return the
      // above task, which is
      // already deploying
      Task deployTask = connection.deployVApp(vApp.getHref());

      // check to see the result of calling deploy twice
      deployTask = connection.deployVApp(vApp.getHref());
      assertEquals(deployTask.getHref(), deployTask.getHref());

      vApp = connection.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      try {// per docs, this is not supported
         connection.cancelTask(deployTask.getHref());
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(deployTask.getHref());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = connection.getVApp(vApp.getHref());

      ReferenceType vAppResource = connection.findVDCInOrgNamed(null, null).getResourceEntities().get(serverName);
      assertEquals(vAppResource.getHref(), vApp.getHref());

      int processorCount = 1;
      long memory = 512;
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), Status.OFF);

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = connection.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.ON);
   }

   protected abstract InstantiateVAppTemplateOptions createInstantiateOptions();

   protected void prepare() {

   }

   protected abstract Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIpForSSH(VApp vApp);

   @Test(enabled = true, dependsOnMethods = "testInstantiateAndPowerOn")
   public void testAddInternetService() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      Entry<InternetService, PublicIpAddress> entry = getNewInternetServiceAndIpForSSH(vApp);
      is = entry.getKey();
      publicIp = entry.getValue().getAddress();
   }

   @Test(enabled = true, dependsOnMethods = "testInstantiateAndPowerOn")
   public void testCloneVApp() throws IOException {
      assert successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering off vApp%n", System.currentTimeMillis());

      StringBuffer name = new StringBuffer();
      for (int i = 0; i < 15; i++)
         name.append("b");
      String newName = name.toString();

      CloneVAppOptions options = deploy().powerOn().withDescription("The description of " + newName);

      System.out.printf("%d: cloning vApp%n", System.currentTimeMillis());
      Task task = connection.cloneVAppInVDC(vdc.getHref(), vApp.getHref(), newName, options);

      // wait for the task to complete
      assert successTester.apply(task.getHref());
      System.out.printf("%d: done cloning vApp%n", System.currentTimeMillis());

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      // refresh task to get the new vApp location
      task = connection.getTask(task.getHref());

      clone = connection.getVApp(task.getOwner().getHref());
      assertEquals(clone.getStatus(), Status.ON);

      assertEquals(clone.getName(), newName);
      assertEquals(clone.getNetworkToAddresses().values().size(), 1);
   }

   @Test(enabled = true, dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      node = connection.addNode(is.getId(), Iterables.getLast(vApp.getNetworkToAddresses().values()), vApp.getName()
            + "-SSH", 22);
      loopAndCheckPass();
   }

   protected void loopAndCheckPass() throws IOException {
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
      connection.configureNode(node.getId(), node.getName(), node.isEnabled(), "holy cow");
   }

   @Test(enabled = true, dependsOnMethods = "testPublicIp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      try {// per docs, this is not supported
         connection.undeployVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      try {// per docs, this is not supported
         connection.suspendVApp(vApp.getHref());
         assert false;
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(connection.resetVApp(vApp.getHref()).getHref());

      vApp = connection.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.ON);

      // TODO we need to determine whether shutdown is supported before invoking
      // it.
      // connection.shutdownVApp(vApp.getId());
      // vApp = connection.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());

      vApp = connection.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.OFF);
   }

   @Test(enabled = true, dependsOnMethods = "testLifeCycle")
   public void testConfigure() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      vApp = connection.getVApp(vApp.getHref());

      Task task = connection.configureVApp(vApp, changeNameTo("eduardo").changeMemoryTo(1536).changeProcessorCountTo(1)
            .addDisk(25 * 1048576).addDisk(25 * 1048576));

      assert successTester.apply(task.getHref());

      vApp = connection.getVApp(vApp.getHref());
      assertEquals(vApp.getName(), "eduardo");
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR))
            .getVirtualQuantity().longValue(), 1);
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.MEMORY))
            .getVirtualQuantity().longValue(), 1536);
      assertEquals(size(filter(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE))),
            3);

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());

      loopAndCheckPass();

      assert successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocationSettingData> disks = Lists.newArrayList(filter(vApp.getResourceAllocations(),
            CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE)));

      // delete the second disk
      task = connection.configureVApp(vApp,
            deleteDiskWithAddressOnParent(Integer.parseInt(disks.get(1).getAddressOnParent())));

      assert successTester.apply(task.getHref());

      assert successTester.apply(connection.powerOnVApp(vApp.getHref()).getHref());
      loopAndCheckPass();
   }

   protected void verifyConfigurationOfVApp(VApp vApp, String serverName, String expectedOs, int processorCount,
         long memory, long hardDisk) {
      assertEquals(vApp.getName(), serverName);
      assertEquals(vApp.getOperatingSystemDescription(), expectedOs);
      assertEquals((int) find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR))
            .getVirtualQuantity().longValue(), processorCount);
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.MEMORY))
            .getVirtualQuantity().longValue(), memory);
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE))
            .getVirtualQuantity().longValue(), hardDisk);
      assertEquals(vApp.getSize().longValue(),
            find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE))
                  .getVirtualQuantity().longValue());
   }

   protected void doCheckPass(String address) throws IOException {
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
         connection.deleteNode(node.getId());
      if (is != null)
         connection.deleteInternetService(is.getId());
      if (vApp != null) {
         try {
            successTester.apply(connection.powerOffVApp(vApp.getHref()).getHref());
         } catch (Exception e) {

         }
         connection.deleteVApp(vApp.getHref());
      }
      if (clone != null) {
         try {
            successTester.apply(connection.powerOffVApp(clone.getHref()).getHref());
         } catch (Exception e) {

         }
         connection.deleteVApp(clone.getHref());
      }

   }

   protected String provider = "trmk-vcloudexpress";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      injector = new RestContextFactory().createContextBuilder(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), overrides).buildInjector();

      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(injector.getInstance(SocketOpen.class), 300, 10, TimeUnit.SECONDS);// make
      // it
      // longer
      // then
      // default internet
      // service timeout
      successTester = new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), 650, 10, TimeUnit.SECONDS);
      context = createContextWithProperties(setupProperties());
      connection = context.getApi();
      orgs = listOrgs();
   }

   protected TerremarkVCloudClient connection;
   protected RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> context;

   @Test
   public void testOrg() throws Exception {
      for (Org org : orgs) {
         assertNotNull(org);
         assertNotNull(org.getName());
         assert org.getCatalogs().size() >= 1;
         assert org.getTasksLists().size() >= 1;
         assert org.getVDCs().size() >= 1;
         assertEquals(connection.findOrgNamed(org.getName()), org);
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultOrg() throws Exception {
      for (Org org : orgs) {
         RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> newContext = null;
         try {
            newContext = createContextWithProperties(overrideDefaults(ImmutableMap.of(
                  VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName())));
            assertEquals(newContext.getApi().findOrgNamed(null), org);
         } finally {
            newContext.close();
         }
      }
   }

   public Properties overrideDefaults(Map<String, String> overrides) {
      Properties properties = setupProperties();
      properties.putAll(overrides);
      return properties;
   }

   @Test
   public void testCatalog() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            assertNotNull(response);
            assertNotNull(response.getName());
            assertNotNull(response.getHref());
            assertEquals(connection.findCatalogInOrgNamed(org.getName(), response.getName()), response);
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultCatalog() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> newContext = null;
            try {
               newContext = createContextWithProperties(overrideDefaults(ImmutableMap.of(
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_CATALOG, cat.getName())));
               assertEquals(newContext.getApi().findCatalogInOrgNamed(null, null), connection.getCatalog(cat.getHref()));
            } finally {
               newContext.close();
            }
         }
      }
   }

   @Test
   public void testGetVDCNetwork() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType resource : response.getAvailableNetworks().values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.NETWORK_XML)) {
                  try {
                     Network net = connection.getNetwork(resource.getHref());
                     assertNotNull(net);
                     assertNotNull(net.getName());
                     assertNotNull(net.getHref());
                     assertEquals(
                           connection.findNetworkInOrgVDCNamed(org.getName(), response.getName(), net.getName()), net);
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultNetwork() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType net : response.getAvailableNetworks().values()) {
               RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> newContext = null;
               try {
                  newContext = createContextWithProperties(overrideDefaults(ImmutableMap.of(
                        VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                        VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC, vdc.getName(),
                        VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK, net.getName())));
                  assertEquals(newContext.getApi().findNetworkInOrgVDCNamed(null, null, net.getName()),
                        connection.getNetwork(net.getHref()));
               } finally {
                  newContext.close();
               }
            }
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.getCatalogItem(resource.getHref());
                  verifyCatalogItem(item);
               }
            }
         }
      }
   }

   protected void verifyCatalogItem(CatalogItem item) {
      assertNotNull(item);
      assertNotNull(item);
      assertNotNull(item.getEntity());
      assertNotNull(item.getHref());
      assertNotNull(item.getProperties());
      assertNotNull(item.getType());
   }

   @Test
   public void testFindCatalogItem() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.findCatalogItemInOrgCatalogNamed(org.getName(), response.getName(),
                        resource.getName());
                  verifyCatalogItem(item);
               }
            }
         }
      }
   }

   @Test
   public void testDefaultVDC() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            assertNotNull(response);
            assertNotNull(response.getName());
            assertNotNull(response.getHref());
            assertNotNull(response.getResourceEntities());
            assertNotNull(response.getAvailableNetworks());
            assertNotNull(response.getCatalog());
            assertNotNull(response.getInternetServices());
            assertNotNull(response.getPublicIps());
            assertEquals(connection.getVDC(response.getHref()), response);
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultVDC() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> newContext = null;
            try {
               newContext = createContextWithProperties(overrideDefaults(ImmutableMap.of(
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC, vdc.getName())));
               assertEquals(newContext.getApi().findVDCInOrgNamed(null, null), connection.getVDC(vdc.getHref()));
            } finally {
               newContext.close();
            }
         }
      }
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            org.jclouds.trmk.vcloud_0_8.domain.TasksList response = connection.findTasksListInOrgNamed(org.getName(),
                  tasksList.getName());
            assertNotNull(response);
            assertNotNull(response.getLocation());
            assertNotNull(response.getTasks());
            assertEquals(connection.getTasksList(response.getLocation()).getLocation(), response.getLocation());
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultTasksList() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> newContext = null;
            try {
               newContext = createContextWithProperties(overrideDefaults(ImmutableMap.of(
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                     VCloudConstants.PROPERTY_VCLOUD_DEFAULT_TASKSLIST, tasksList.getName())));
               assertEquals(newContext.getApi().findTasksListInOrgNamed(null, null),
                     connection.getTasksList(tasksList.getHref()));
            } finally {
               newContext.close();
            }
         }
      }
   }

   @Test
   public void testGetTask() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            org.jclouds.trmk.vcloud_0_8.domain.TasksList response = connection.findTasksListInOrgNamed(org.getName(),
                  tasksList.getName());
            assertNotNull(response);
            assertNotNull(response.getLocation());
            assertNotNull(response.getTasks());
            if (response.getTasks().size() > 0) {
               Task task = response.getTasks().last();
               assertEquals(connection.getTask(task.getHref()).getHref(), task.getHref());
            }
         }
      }
   }

   protected Iterable<Org> orgs;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".identity");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   public RestContext<TerremarkVCloudClient, TerremarkVCloudAsyncClient> createContextWithProperties(
         Properties overrides) {
      return new ComputeServiceContextFactory(setupRestProperties()).createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
   }

   @AfterGroups(groups = { "live" })
   public void teardownClient() {
      context.close();
   }

   protected Iterable<Org> listOrgs() {
      return Iterables.transform(connection.listOrgs().values(), new Function<ReferenceType, Org>() {

         @Override
         public Org apply(ReferenceType arg0) {
            return connection.getOrg(arg0.getHref());
         }

      });
   }

   @Test
   public void testGetVAppTemplate() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(TerremarkVCloudMediaType.VAPPTEMPLATE_XML)) {
                     assertNotNull(connection.getVAppTemplate(item.getEntity().getHref()));
                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      for (Org org : listOrgs()) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType item : response.getResourceEntities().values()) {
               if (item.getType().equals(TerremarkVCloudMediaType.VAPP_XML)) {
                  try {
                     VApp app = connection.getVApp(item.getHref());
                     assertNotNull(app);
                  } catch (RuntimeException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testFindVAppTemplate() throws Exception {
      for (Org org : listOrgs()) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(TerremarkVCloudMediaType.VAPPTEMPLATE_XML)) {
                     assertNotNull(connection.findVAppTemplateInOrgCatalogNamed(org.getName(), response.getName(), item
                           .getEntity().getName()));
                  }
               }
            }
         }
      }
   }
}