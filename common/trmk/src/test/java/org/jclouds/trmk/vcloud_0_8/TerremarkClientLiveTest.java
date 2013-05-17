/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.size;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration.Builder.changeNameTo;
import static org.jclouds.trmk.vcloud_0_8.domain.VAppConfiguration.Builder.deleteDiskWithAddressOnParent;
import static org.jclouds.trmk.vcloud_0_8.options.CloneVAppOptions.Builder.deploy;
import static org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions.Builder.processorCount;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.cim.CIMPredicates;
import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
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
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true)
public abstract class TerremarkClientLiveTest extends BaseApiLiveTest<TerremarkVCloudClient> {

   protected String expectedOs = "Ubuntu Linux (64-bit)";
   protected String itemName = "Ubuntu JeOS 9.10 (64-bit)";

   protected Factory sshFactory;
   protected String publicIp;
   protected InternetService is;
   protected Node node;
   protected VApp vApp;
   protected Predicate<HostAndPort> socketTester;
   protected Predicate<URI> successTester;
   protected Injector injector;

   protected VApp clone;
   protected VDC vdc;
   protected String serverName;
   protected KeyPair key;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   public TerremarkClientLiveTest() {
      this.provider = "trmk-vcloudexpress";
      StringBuilder name = new StringBuilder();
      for (int i = 0; i < 15; i++)
         name.append("d");
      serverName = name.toString();// "adriantest";
   }

   @Test
   public void testKeysList() throws Exception {
      for (Org org : orgs) {
         TerremarkVCloudClient vCloudExpressClient = TerremarkVCloudClient.class.cast(api);
         Set<KeyPair> response = vCloudExpressClient.listKeyPairsInOrg(org.getHref());
         assertNotNull(response);
      }
   }

   @Test
   public void testGetAllInternetServices() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            for (InternetService service : api.getAllInternetServicesInVDC(vdc.getHref())) {
               assertNotNull(api.getNodes(service.getId()));
            }
         }
      }
   }

   @Test
   public void testCreateInternetServiceMonitorDisabled() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            Set<PublicIpAddress> publicIpAddresses = api.getPublicIpsAssociatedWithVDC(vdc.getHref());
            PublicIpAddress publicIp = publicIpAddresses.iterator().next();
            InternetService service = api.addInternetServiceToExistingIp(publicIp.getId(), PREFIX
                  + "-no-monitoring", Protocol.TCP, 1234, AddInternetServiceOptions.Builder.monitorDisabled());
            api.deleteInternetService(service.getId());
         }
      }
   }

   @Test
   public void testGetPublicIpsAssociatedWithVDC() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            for (PublicIpAddress ip : api.getPublicIpsAssociatedWithVDC(vdc.getHref())) {
               assertNotNull(api.getInternetServicesOnPublicIp(ip.getId()));
               assertNotNull(api.getPublicIp(ip.getId()));
            }
         }
      }
   }

   @Test
   public void testGetConfigCustomizationOptions() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType catalog : org.getCatalogs().values()) {
            Catalog response = api.getCatalog(catalog.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = api.findCatalogItemInOrgCatalogNamed(org.getName(), catalog.getName(),
                        resource.getName());
                  assert api.getCustomizationOptions(item.getCustomizationOptions().getHref()) != null;
               }
            }
         }
      }
   }

   @Test(enabled = true)
   public void testInstantiateAndPowerOn() throws InterruptedException, ExecutionException, TimeoutException,
         IOException {
      prepare();

      long hardDisk = 4194304;

      // long hardDisk = 4194304 / 4 * 10;
      // String catalogOs = "CentOS 5.3 (64-bit)";
      // String expectedOs = "Red Hat Enterprise Linux 5 (64-bit)";

      // lookup the datacenter you are deploying into
      vdc = api.findVDCInOrgNamed(null, null);

      // create an options object to collect the configuration we want.
      InstantiateVAppTemplateOptions instantiateOptions = createInstantiateOptions().sshKeyFingerprint(
            key.getFingerPrint());

      CatalogItem item = api.findCatalogItemInOrgCatalogNamed(null, null, itemName);

      assert item != null;

      // if this template supports setting the root password, let's add it to
      // our options
      CustomizationParameters customizationOptions = api.getCustomizationOptions(item.getCustomizationOptions()
            .getHref());

      if (customizationOptions.canCustomizePassword())
         instantiateOptions.withPassword("robotsarefun");

      VAppTemplate vAppTemplate = api.getVAppTemplate(item.getEntity().getHref());

      assert vAppTemplate != null;

      // instantiate, noting vApp returned has minimal details
      vApp = api.instantiateVAppTemplateInVDC(vdc.getHref(), vAppTemplate.getHref(), serverName,
            instantiateOptions);

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      // in terremark, this should be a no-op, as it should simply return the
      // above task, which is
      // already deploying
      Task deployTask = api.deployVApp(vApp.getHref());

      // check to see the result of calling deploy twice
      deployTask = api.deployVApp(vApp.getHref());
      assertEquals(deployTask.getHref(), deployTask.getHref());

      vApp = api.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.RESOLVED);

      try {// per docs, this is not supported
         api.cancelTask(deployTask.getHref());
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(deployTask.getHref());
      System.out.printf("%d: done deploying vApp%n", System.currentTimeMillis());

      vApp = api.getVApp(vApp.getHref());

      ReferenceType vAppResource = api.findVDCInOrgNamed(null, null).getResourceEntities().get(serverName);
      assertEquals(vAppResource.getHref(), vApp.getHref());

      int processorCount = 1;
      long memory = 512;
      verifyConfigurationOfVApp(vApp, serverName, expectedOs, processorCount, memory, hardDisk);
      assertEquals(vApp.getStatus(), Status.OFF);

      assert successTester.apply(api.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      vApp = api.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.ON);
   }

   protected InstantiateVAppTemplateOptions createInstantiateOptions() {
      return processorCount(1).memory(512).sshKeyFingerprint(key.getFingerPrint());
   }

   protected void prepare() {
      Org org = api.findOrgNamed(null);
      try {
         key = api.generateKeyPairInOrg(org.getHref(), "livetest", false);
      } catch (IllegalStateException e) {
         key = api.findKeyPairInOrg(org.getHref(), "livetest");
         api.deleteKeyPair(key.getId());
         key = api.generateKeyPairInOrg(org.getHref(), "livetest", false);
      }
      assertNotNull(key);
      assertEquals(key.getName(), "livetest");
      assertNotNull(key.getPrivateKey());
      assertNotNull(key.getFingerPrint());
      assertEquals(key.isDefault(), false);
      assertEquals(key.getFingerPrint(), api.findKeyPairInOrg(org.getHref(), key.getName()).getFingerPrint());
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
      assert successTester.apply(api.powerOffVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering off vApp%n", System.currentTimeMillis());

      StringBuilder name = new StringBuilder();
      for (int i = 0; i < 15; i++)
         name.append("b");
      String newName = name.toString();

      CloneVAppOptions options = deploy().powerOn().withDescription("The description of " + newName);

      System.out.printf("%d: cloning vApp%n", System.currentTimeMillis());
      Task task = api.cloneVAppInVDC(vdc.getHref(), vApp.getHref(), newName, options);

      // wait for the task to complete
      assert successTester.apply(task.getHref());
      System.out.printf("%d: done cloning vApp%n", System.currentTimeMillis());

      assert successTester.apply(api.powerOnVApp(vApp.getHref()).getHref());
      System.out.printf("%d: done powering on vApp%n", System.currentTimeMillis());

      // refresh task to get the new vApp location
      task = api.getTask(task.getHref());

      clone = api.getVApp(task.getOwner().getHref());
      assertEquals(clone.getStatus(), Status.ON);

      assertEquals(clone.getName(), newName);
      assertEquals(clone.getNetworkToAddresses().values().size(), 1);
   }

   @Test(enabled = true, dependsOnMethods = { "testInstantiateAndPowerOn", "testAddInternetService" })
   public void testPublicIp() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      node = api.addNode(is.getId(), Iterables.getLast(vApp.getNetworkToAddresses().values()), vApp.getName()
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
      api.configureNode(node.getId(), node.getName(), node.isEnabled(), "holy cow");
   }

   @Test(enabled = true, dependsOnMethods = "testPublicIp")
   public void testLifeCycle() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      try {// per docs, this is not supported
         api.undeployVApp(vApp.getHref());
         fail("Expected UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
      }

      try {// per docs, this is not supported
         api.suspendVApp(vApp.getHref());
         fail("Expected UnsupportedOperationException");
      } catch (UnsupportedOperationException e) {
      }

      assert successTester.apply(api.resetVApp(vApp.getHref()).getHref());

      vApp = api.getVApp(vApp.getHref());

      assertEquals(vApp.getStatus(), Status.ON);

      // TODO we need to determine whether shutdown is supported before invoking
      // it.
      // api.shutdownVApp(vApp.getId());
      // vApp = api.getVApp(vApp.getId());
      // assertEquals(vApp.getStatus(), VAppStatus.ON);

      assert successTester.apply(api.powerOffVApp(vApp.getHref()).getHref());

      vApp = api.getVApp(vApp.getHref());
      assertEquals(vApp.getStatus(), Status.OFF);
   }

   @Test(enabled = true, dependsOnMethods = "testLifeCycle")
   public void testConfigure() throws InterruptedException, ExecutionException, TimeoutException, IOException {

      vApp = api.getVApp(vApp.getHref());

      Task task = api.configureVApp(vApp, changeNameTo("eduardo").changeMemoryTo(1536).changeProcessorCountTo(1)
            .addDisk(25 * 1048576).addDisk(25 * 1048576));

      assert successTester.apply(task.getHref());

      vApp = api.getVApp(vApp.getHref());
      assertEquals(vApp.getName(), "eduardo");
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.PROCESSOR))
            .getVirtualQuantity().longValue(), 1);
      assertEquals(find(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.MEMORY))
            .getVirtualQuantity().longValue(), 1536);
      assertEquals(size(filter(vApp.getResourceAllocations(), CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE))),
            3);

      assert successTester.apply(api.powerOnVApp(vApp.getHref()).getHref());

      loopAndCheckPass();

      assert successTester.apply(api.powerOffVApp(vApp.getHref()).getHref());

      // extract the disks on the vApp sorted by addressOnParent
      List<ResourceAllocationSettingData> disks = Lists.newArrayList(filter(vApp.getResourceAllocations(),
            CIMPredicates.resourceTypeIn(ResourceType.DISK_DRIVE)));

      // delete the second disk
      task = api.configureVApp(vApp,
            deleteDiskWithAddressOnParent(Integer.parseInt(disks.get(1).getAddressOnParent())));

      assert successTester.apply(task.getHref());

      assert successTester.apply(api.powerOnVApp(vApp.getHref()).getHref());
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
      HostAndPort socket = HostAndPort.fromParts(address, 22);

      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(), socket);
      assert socketTester.apply(socket);
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), socket);

      SshClient ssh = getConnectionFor(socket);
      try {
         ssh.connect();
         System.out.printf("%d: %s ssh ssh made%n", System.currentTimeMillis(), socket);
         System.out.println(ssh.exec("df -h"));
         System.out.println(ssh.exec("ls -al /dev/sd*"));
         System.out.println(ssh.exec("echo '$Ep455l0ud!2'|sudo -S fdisk -l"));
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   protected abstract SshClient getConnectionFor(HostAndPort socket);

   @AfterGroups(groups = { "live" })
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         api.deleteNode(node.getId());
      if (is != null)
         api.deleteInternetService(is.getId());
      if (key != null)
         api.deleteKeyPair(key.getId());
      if (vApp != null) {
         try {
            successTester.apply(api.powerOffVApp(vApp.getHref()).getHref());
         } catch (Exception e) {

         }
         api.deleteVApp(vApp.getHref());
      }
      if (clone != null) {
         try {
            successTester.apply(api.powerOffVApp(clone.getHref()).getHref());
         } catch (Exception e) {

         }
         api.deleteVApp(clone.getHref());
      }
   }

   @Override
   protected TerremarkVCloudClient create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      sshFactory = injector.getInstance(SshClient.Factory.class);

      // longer than default internet service timeout
      socketTester = retry(injector.getInstance(SocketOpen.class), 300, 10, SECONDS);
      successTester = retry(injector.getInstance(TaskSuccess.class), 650, 10, SECONDS);
      api = injector.getInstance(TerremarkVCloudClient.class);
      orgs = listOrgs();
      return api;
   }

   @Test
   public void testOrg() throws Exception {
      for (Org org : orgs) {
         assertNotNull(org);
         assertNotNull(org.getName());
         assert org.getCatalogs().size() >= 1;
         assert org.getTasksLists().size() >= 1;
         assert org.getVDCs().size() >= 1;
         assertEquals(api.findOrgNamed(org.getName()), org);
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultOrg() throws Exception {
      for (Org org : orgs) {
         TerremarkVCloudClient newApi = null;
         try {
            newApi = create(
                  overrideDefaults(ImmutableMap.of(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName())),
                  setupModules());
            assertEquals(newApi.findOrgNamed(null), org);
         } finally {
            newApi.close();
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
            Catalog response = api.getCatalog(cat.getHref());
            assertNotNull(response);
            assertNotNull(response.getName());
            assertNotNull(response.getHref());
            assertEquals(api.findCatalogInOrgNamed(org.getName(), response.getName()), response);
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultCatalog() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            TerremarkVCloudClient newApi = null;
            try {
               newApi = create(
                     overrideDefaults(ImmutableMap.of(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                           VCloudConstants.PROPERTY_VCLOUD_DEFAULT_CATALOG, cat.getName())), setupModules());
               assertEquals(newApi.findCatalogInOrgNamed(null, null), api.getCatalog(cat.getHref()));
            } finally {
               newApi.close();
            }
         }
      }
   }

   @Test
   public void testGetVDCNetwork() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = api.getVDC(vdc.getHref());
            for (ReferenceType resource : response.getAvailableNetworks().values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.NETWORK_XML)) {
                  try {
                     Network net = api.getNetwork(resource.getHref());
                     assertNotNull(net);
                     assertNotNull(net.getName());
                     assertNotNull(net.getHref());
                     assertEquals(
                           api.findNetworkInOrgVDCNamed(org.getName(), response.getName(), net.getName()), net);
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
            VDC response = api.getVDC(vdc.getHref());
            for (ReferenceType net : response.getAvailableNetworks().values()) {
               TerremarkVCloudClient newApi = null;
               try {
                  newApi = create(
                        overrideDefaults(ImmutableMap.of(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                              VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC, vdc.getName(),
                              VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK, net.getName())), setupModules());
                  assertEquals(newApi.findNetworkInOrgVDCNamed(null, null, net.getName()),
                        api.getNetwork(net.getHref()));
               } finally {
                  newApi.close();
               }
            }
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = api.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = api.getCatalogItem(resource.getHref());
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
            Catalog response = api.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = api.findCatalogItemInOrgCatalogNamed(org.getName(), response.getName(),
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
            VDC response = api.getVDC(vdc.getHref());
            assertNotNull(response);
            assertNotNull(response.getName());
            assertNotNull(response.getHref());
            assertNotNull(response.getResourceEntities());
            assertNotNull(response.getAvailableNetworks());
            assertNotNull(response.getCatalog());
            assertNotNull(response.getInternetServices());
            assertNotNull(response.getPublicIps());
            assertEquals(api.getVDC(response.getHref()), response);
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultVDC() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            TerremarkVCloudClient newApi = null;
            try {
               newApi = create(
                     overrideDefaults(ImmutableMap.of(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                           VCloudConstants.PROPERTY_VCLOUD_DEFAULT_VDC, vdc.getName())), setupModules());
               assertEquals(newApi.findVDCInOrgNamed(null, null), api.getVDC(vdc.getHref()));
            } finally {
               newApi.close();
            }
         }
      }
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            org.jclouds.trmk.vcloud_0_8.domain.TasksList response = api.findTasksListInOrgNamed(org.getName(),
                  tasksList.getName());
            assertNotNull(response);
            assertNotNull(response.getLocation());
            assertNotNull(response.getTasks());
            assertEquals(api.getTasksList(response.getLocation()).getLocation(), response.getLocation());
         }
      }
   }

   @Test
   public void testPropertiesCanOverrideDefaultTasksList() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            TerremarkVCloudClient newApi = null;
            try {
               newApi = create(
                     overrideDefaults(ImmutableMap.of(VCloudConstants.PROPERTY_VCLOUD_DEFAULT_ORG, org.getName(),
                           VCloudConstants.PROPERTY_VCLOUD_DEFAULT_TASKSLIST, tasksList.getName())), setupModules());
               assertEquals(newApi.findTasksListInOrgNamed(null, null),
                     api.getTasksList(tasksList.getHref()));
            } finally {
               newApi.close();
            }
         }
      }
   }

   @Test
   public void testGetTask() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType tasksList : org.getTasksLists().values()) {
            org.jclouds.trmk.vcloud_0_8.domain.TasksList response = api.findTasksListInOrgNamed(org.getName(),
                  tasksList.getName());
            assertNotNull(response);
            assertNotNull(response.getLocation());
            assertNotNull(response.getTasks());
            if (response.getTasks().size() > 0) {
               Task task = response.getTasks().last();
               assertEquals(api.getTask(task.getHref()).getHref(), task.getHref());
            }
         }
      }
   }

   protected Iterable<Org> orgs;

   protected Iterable<Org> listOrgs() {
      return Iterables.transform(api.listOrgs().values(), new Function<ReferenceType, Org>() {

         @Override
         public Org apply(ReferenceType arg0) {
            return api.getOrg(arg0.getHref());
         }

      });
   }

   @Test
   public void testGetVAppTemplate() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = api.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = api.getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(TerremarkVCloudMediaType.VAPPTEMPLATE_XML)) {
                     assertNotNull(api.getVAppTemplate(item.getEntity().getHref()));
                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = api.getVDC(vdc.getHref());
            for (ReferenceType item : response.getResourceEntities().values()) {
               if (item.getType().equals(TerremarkVCloudMediaType.VAPP_XML)) {
                  try {
                     VApp app = api.getVApp(item.getHref());
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
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = api.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(TerremarkVCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = api.getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(TerremarkVCloudMediaType.VAPPTEMPLATE_XML)) {
                     assertNotNull(api.findVAppTemplateInOrgCatalogNamed(org.getName(), response.getName(), item
                           .getEntity().getName()));
                  }
               }
            }
         }
      }
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule());
   }
}
