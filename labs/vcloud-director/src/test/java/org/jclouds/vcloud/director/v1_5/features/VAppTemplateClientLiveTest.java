/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests the request/response behavior of {@link org.jclouds.vcloud.director.v1_5.features.VAppTemplateClient}
 * 
 * NOTE The environment MUST have at least one template configured
 *
 * @author Aled Sage
 */
@Test(groups = {"live", "unit", "user"}, testName = "VAppTemplateClientLiveTest")
public class VAppTemplateClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   private VAppTemplateClient vappTemplateClient;
   private VdcClient vdcClient;
   private VAppClient vappClient;
   
   private VApp vApp;
   private Vm vm;
   
   @BeforeClass(inheritGroups = true)
   @Override
   public void setupRequiredClients() throws Exception {
      vappTemplateClient = context.getApi().getVAppTemplateClient();
      vdcClient = context.getApi().getVdcClient();
      vappClient = context.getApi().getVAppClient();
   }

   // TODO remove duplication from other tests
   @AfterClass(groups = { "live" })
   public void cleanUp() throws Exception {
      if (vApp != null) cleanUpVApp(vApp);
   }

   // FIXME cloneVAppTemplate is giving back 500 error
   private VAppTemplate cloneVAppTemplate(boolean waitForTask) throws Exception {
      CloneVAppTemplateParams cloneVAppTemplateParams = CloneVAppTemplateParams.builder()
               .source(Reference.builder().href(vAppTemplateURI).build())
               .build();
      VAppTemplate clonedVappTemplate = vdcClient.cloneVAppTemplate(vdcURI, cloneVAppTemplateParams);
      
      if (waitForTask) {
         Task cloneTask = Iterables.getFirst(clonedVappTemplate.getTasks(), null);
         assertNotNull(cloneTask, "vdcClient.cloneVAppTemplate returned VAppTemplate that did not contain any tasks");
         retryTaskSuccess.apply(cloneTask);
      }

      return clonedVappTemplate;
   }

   @Test
   public void testInstantiateAndStartVApp() throws Exception {
      vApp = instantiateVApp();
      Task instantiateTask = Iterables.getFirst(vApp.getTasks(), null);
      if (instantiateTask != null) {
         assertTaskSucceedsLong(instantiateTask);
      }

      // Start the vApp so that it has VMs
      Task task = vappClient.powerOn(vApp.getHref());
      assertTaskSucceedsLong(task);

      // Get a VM
      vApp = vappClient.getVApp(vApp.getHref()); // refresh
      List<Vm> vms = vApp.getChildren().getVms();
      vm = Iterables.getFirst(vms, null);
      assertNotNull(vm, "started vApp "+vApp+" must have at least one VM");
   }

   @Test
   public void testGetVAppTemplate() {
      VAppTemplate template = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      
      Checks.checkVAppTemplate(template);
      assertEquals(template.getHref(), vAppTemplateURI);
   }
   
   @Test
   public void testGetOwner() {
      Owner owner = vappTemplateClient.getOwnerOfVAppTemplate(vAppTemplateURI);
      
      Checks.checkOwner(owner);
      assertEquals(owner.getUser(), vappTemplateClient.getVAppTemplate(vAppTemplateURI).getOwner().getUser());
   }
   
   @Test
   public void testGetCustomizationSection() {
      CustomizationSection customizationSection = vappTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI);
      
      Checks.checkCustomizationSection(customizationSection);
   }
   
   @Test
   public void testGetProductSections() {
      ProductSectionList productSectionList = vappTemplateClient.getProductSectionsForVAppTemplate(vAppTemplateURI);
      
      Checks.checkProductSectionList(productSectionList);
   }
   
   @Test( dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testGetGuestCustomizationSection() {
      GuestCustomizationSection guestCustomizationSection = vappTemplateClient.getVAppTemplateGuestCustomizationSection(vm.getHref());
      
      Checks.checkGuestCustomizationSection(guestCustomizationSection);
   }
   
   @Test
   public void testGetLeaseSettingsSection() {
      // FIXME Wrong case for Vapp
      LeaseSettingsSection leaseSettingsSection = vappTemplateClient.getVappTemplateLeaseSettingsSection(vAppTemplateURI);
      
      Checks.checkLeaseSettingsSection(leaseSettingsSection);
   }
   
   @Test
   public void testGetMetadata() {
      Metadata metadata = vappTemplateClient.getVAppTemplateMetadata(vAppTemplateURI);
      
      Checks.checkMetadata(metadata);
   }

   @Test // implicitly tested by testEditVAppTemplateMetadataValue, which first creates the metadata entry; otherwise no entry may exist
   public void testGetMetadataValue() {
      Metadata metadata = vappTemplateClient.getVAppTemplateMetadata(vAppTemplateURI);
      MetadataEntry entry = Iterables.get(metadata.getMetadataEntries(), 0);
      
      MetadataValue val = vappTemplateClient.getVAppTemplateMetadataValue(vAppTemplateURI, entry.getKey());
      
      Checks.checkMetadataValue(val);
      assertEquals(val.getValue(), entry.getValue());
   }
   
   @Test
   public void testGetNetworkConfigSection() {
      NetworkConfigSection networkConfigSection = vappTemplateClient.getVAppTemplateNetworkConfigSection(vAppTemplateURI);
      
      Checks.checkNetworkConfigSection(networkConfigSection);
   }
   
   @Test( dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testGetNetworkConnectionSection() {
      NetworkConnectionSection networkConnectionSection = vappTemplateClient.getVAppTemplateNetworkConnectionSection(vm.getHref());

      Checks.checkNetworkConnectionSection(networkConnectionSection);
   }

   @Test
   public void testGetNetworkSection() {
      NetworkSection networkSection = vappTemplateClient.getVAppTemplateNetworkSection(vAppTemplateURI);

      Checks.checkOvfNetworkSection(networkSection);
   }

   @Test
   public void testGetOvf() {
      Envelope envelope = vappTemplateClient.getVAppTemplateOvf(vAppTemplateURI);
      
      Checks.checkOvfEnvelope(envelope);
   }

   @Test
   public void testEditVAppTemplate() {
      String uid = ""+random.nextInt();
      String name = "myname-"+uid;
      String description = "mydescr-"+uid;
      VAppTemplate template = VAppTemplate.builder()
               .name(name)
               .description(description)
               .build();
      
      final Task task = vappTemplateClient.editVAppTemplate(vAppTemplateURI, template);
      retryTaskSuccess.apply(task);

      VAppTemplate newTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      assertEquals(newTemplate.getName(), name);
      assertEquals(newTemplate.getDescription(), description);
   }

   @Test
   public void testEditMetadata() {
      // TODO Cleanup after ourselves..
      
      Metadata oldMetadata = vappTemplateClient.getVAppTemplateMetadata(vAppTemplateURI);
      Map<String,String> oldMetadataMap = Checks.metadataToMap(oldMetadata);

      String uid = ""+random.nextInt();
      String key = "mykey-"+uid;
      String val = "myval-"+uid;
      MetadataEntry metadataEntry = MetadataEntry.builder().entry(key, val).build();
      Metadata metadata = Metadata.builder().fromMetadata(oldMetadata).entry(metadataEntry).build();
      
      final Task task = vappTemplateClient.editVAppTemplateMetadata(vAppTemplateURI, metadata);
      retryTaskSuccess.apply(task);

      Metadata newMetadata = vappTemplateClient.getVAppTemplateMetadata(vAppTemplateURI);
      Map<String,String> expectedMetadataMap = ImmutableMap.<String,String>builder()
               .putAll(oldMetadataMap)
               .put(key, val)
               .build();
      Checks.checkMetadataFor("vAppTemplate", newMetadata, expectedMetadataMap);
   }
   
   @Test
   public void testEditMetadataValue() {
      // TODO Cleanup after ourselves..
      
      String uid = ""+random.nextInt();
      String key = "mykey-"+uid;
      String val = "myval-"+uid;
      MetadataValue metadataValue = MetadataValue.builder().value(val).build();
      
      final Task task = vappTemplateClient.editVAppTemplateMetadataValue(vAppTemplateURI, key, metadataValue);
      retryTaskSuccess.apply(task);

      MetadataValue newMetadataValue = vappTemplateClient.getVAppTemplateMetadataValue(vAppTemplateURI, key);
      assertEquals(newMetadataValue.getValue(), metadataValue.getValue());
   }

   @Test
   public void testDeleteVAppTemplateMetadataValue() {
      // First store a value
      String key = "mykey-"+random.nextInt();
      MetadataValue metadataValue = MetadataValue.builder().value("myval").build();
      final Task task = vappTemplateClient.editVAppTemplateMetadataValue(vAppTemplateURI, key, metadataValue);
      retryTaskSuccess.apply(task);
      
      // Then delete the entry
      final Task deletionTask = vappTemplateClient.deleteVAppTemplateMetadataValue(vAppTemplateURI, key);
      retryTaskSuccess.apply(deletionTask);

      // Then confirm the entry is not there
      Metadata newMetadata = vappTemplateClient.getVAppTemplateMetadata(vAppTemplateURI);
      Checks.checkMetadataKeyAbsentFor("vAppTemplate", newMetadata, key);
   }

   @Test( dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testEditGuestCustomizationSection() {
      String computerName = "a"+random.nextInt(Integer.MAX_VALUE);
      GuestCustomizationSection newSection = GuestCustomizationSection.builder()
               .info("my info")
               .computerName(computerName)
               .build();
      
      final Task task = vappTemplateClient.editVAppTemplateGuestCustomizationSection(vm.getHref(), newSection);
      assertTaskSucceeds(task);

      GuestCustomizationSection modified = vappTemplateClient.getVAppTemplateGuestCustomizationSection(vm.getHref());
      
      Checks.checkGuestCustomizationSection(modified);
      assertEquals(modified.getComputerName(), computerName);
   }
   
   @Test
   public void testEditCustomizationSection() {
      boolean oldVal = vappTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI).isCustomizeOnInstantiate();
      boolean newVal = !oldVal;
      
      CustomizationSection customizationSection = CustomizationSection.builder()
               .info("my info")
               .customizeOnInstantiate(newVal)
               .build();
      
      final Task task = vappTemplateClient.editVAppTemplateCustomizationSection(vAppTemplateURI, customizationSection);
      retryTaskSuccess.apply(task);

      CustomizationSection newCustomizationSection = vappTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI);
      assertEquals(newCustomizationSection.isCustomizeOnInstantiate(), newVal);
   }

   @Test // FIXME deploymentLeaseInSeconds returned is null 
   public void testEditLeaseSettingsSection() throws Exception {
      // Note: use smallish number for storageLeaseInSeconds; it seems to be capped at 5184000?
      int storageLeaseInSeconds = random.nextInt(10000)+1;
      int deploymentLeaseInSeconds = random.nextInt(10000)+1;
      LeaseSettingsSection leaseSettingSection = LeaseSettingsSection.builder()
               .info("my info")
               .storageLeaseInSeconds(storageLeaseInSeconds)
               .deploymentLeaseInSeconds(deploymentLeaseInSeconds)
               .build();
      
      final Task task = vappTemplateClient.editVappTemplateLeaseSettingsSection(vAppTemplateURI, leaseSettingSection);
      retryTaskSuccess.apply(task);
      
      LeaseSettingsSection newLeaseSettingsSection = vappTemplateClient.getVappTemplateLeaseSettingsSection(vAppTemplateURI);
      assertEquals(newLeaseSettingsSection.getStorageLeaseInSeconds(), (Integer)storageLeaseInSeconds);
      assertEquals(newLeaseSettingsSection.getDeploymentLeaseInSeconds(), (Integer)deploymentLeaseInSeconds);
   }

   @Test( dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testEditNetworkConfigSection() {
      // TODO What to modify?
      
      NetworkConfigSection oldSection = vappTemplateClient.getVAppTemplateNetworkConfigSection(vApp.getHref());
      NetworkConfigSection newSection = oldSection.toBuilder().build();
      
//      String networkName = ""+random.nextInt();
//      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
//               .fenceMode("isolated")
//               .build();
//      VAppNetworkConfiguration vappNetworkConfiguration = VAppNetworkConfiguration.builder()
//               .networkName(networkName)
//               .configuration(networkConfiguration)
//               .build();
//      Set<VAppNetworkConfiguration> vappNetworkConfigurations = ImmutableSet.of(vappNetworkConfiguration);
//      NetworkConfigSection networkConfigSection = NetworkConfigSection.builder()
//               .info("my info")
//               .networkConfigs(vappNetworkConfigurations)
//               .build();
      
      final Task task = vappTemplateClient.editVAppTemplateNetworkConfigSection(vApp.getHref(), newSection);
      assertTaskSucceeds(task);

      NetworkConfigSection modified = vappTemplateClient.getVAppTemplateNetworkConfigSection(vAppTemplateURI);
      Checks.checkNetworkConfigSection(modified);
      
//      assertEquals(modified§.getNetworkConfigs().size(), 1);
//      
//      VAppNetworkConfiguration newVAppNetworkConfig = Iterables.get(modified§.getNetworkConfigs(), 0);
//      assertEquals(newVAppNetworkConfig.getNetworkName(), networkName);
   }

   @Test( dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testEditNetworkConnectionSection() {
      // TODO Modify a field so can assert that the change really took effect
      
      NetworkConnectionSection oldSection = vappTemplateClient.getVAppTemplateNetworkConnectionSection(vm.getHref());
      NetworkConnectionSection newSection = oldSection.toBuilder()
               .build();
      
      final Task task = vappTemplateClient.editVAppTemplateNetworkConnectionSection(vm.getHref(), newSection);
      assertTaskSucceeds(task);

      NetworkConnectionSection modified = vappTemplateClient.getVAppTemplateNetworkConnectionSection(vm.getHref());
      Checks.checkNetworkConnectionSection(modified);
   }
   
   @Test // FIXME cloneVAppTemplate is giving back 500 error
   public void testDeleteVAppTemplate() throws Exception {
      VAppTemplate clonedVappTemplate = cloneVAppTemplate(true);

      // Confirm that "get" works pre-delete
      VAppTemplate vAppTemplatePreDelete = vappTemplateClient.getVAppTemplate(clonedVappTemplate.getHref());
      Checks.checkVAppTemplate(vAppTemplatePreDelete);
      
      // Delete the template
      final Task task = vappTemplateClient.deleteVappTemplate(clonedVappTemplate.getHref());
      retryTaskSuccess.apply(task);

      // Confirm that can't access post-delete, i.e. template has been deleted
      try {
         vappTemplateClient.getVAppTemplate(clonedVappTemplate.getHref());
      } catch (VCloudDirectorException e) {
         // success; should get a 403 because vAppTemplate no longer exists
      }
   }

   @Test
   public void testDisableVAppTemplateDownload() throws Exception {
      vappTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      
      // TODO Check that it really is disabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      //
      // For example:
      //    VAppTemplate vAppTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      //    Set<Link> links = vAppTemplate.getLinks();
      //    assertFalse(hasLinkMatchingRel(links, "download.*"), "Should not offer download link after disabling download: "+vAppTemplate);
   }
   
   @Test
   public void testEnableVAppTemplateDownload() throws Exception {
      // First disable so that enable really has some work to do...
      vappTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      final Task task = vappTemplateClient.enableDownloadVappTemplate(vAppTemplateURI);
      retryTaskSuccess.apply(task);
      
      // TODO Check that it really is enabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      //
      // For example:
      //    VAppTemplate vAppTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      //    Set<Link> links = vAppTemplate.getLinks();
      //    assertTrue(hasLinkMatchingRel(links, "download.*"), "Should offer download link after enabling download: "+vAppTemplate);
   }
   
   @SuppressWarnings("unused")
   private boolean hasLinkMatchingRel(Set<Link> links, String regex) {
      for (Link link : links) {
         if (link.getRel() != null && link.getRel().matches(regex)) {
            return true;
         }
      }
      return false;
   }
   
   @Test(dependsOnMethods = { "testInstantiateAndStartVApp" } )
   public void testConsolidateVAppTemplate() throws Exception {
      // TODO Need assertion that command had effect
      
      System.out.println("About to try to consolidate "+vm);
      final Task task = vappTemplateClient.consolidateVappTemplate(vm.getHref());
      assertTaskSucceedsLong(task);
   }
   
   @Test // FIXME Need a datastore reference
   public void testRelocateVAppTemplate() throws Exception {
      // TODO Need assertion that command had effect
      Reference dataStore = null; // FIXME
      RelocateParams relocateParams = RelocateParams.builder()
               .datastore(dataStore)
               .build();
      
      final Task task = vappTemplateClient.relocateVappTemplate(vAppTemplateURI, relocateParams);
      assertTaskSucceedsLong(task);
   }
   
   // This failed previously, but is passing now. 
   // However, it's not part of the official API so not necessary to assert it.
   @Test 
   public void testCompletedTaskNotIncludedInVAppTemplate() throws Exception {
      // Kick off a task, and wait for it to complete
      vappTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      final Task task = vappTemplateClient.enableDownloadVappTemplate(vAppTemplateURI);
      retryTaskSuccess.apply(task);

      // Ask the VAppTemplate for its tasks, and the status of the matching task if it exists
      VAppTemplate vAppTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      Set<Task> tasks = vAppTemplate.getTasks();
      if (tasks != null) {
         for (Task contender : tasks) {
            if (task.getId().equals(contender.getId())) {
               String status = contender.getStatus();
               if (status.equals(Task.Status.QUEUED) || status.equals(Task.Status.PRE_RUNNING) || status.equals(Task.Status.RUNNING)) {
                  fail("Task "+contender+" reported complete, but is included in VAppTemplate in status "+status);
               }
            }
         }
      }
   }
}
