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

import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkGuestCustomizationSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkLeaseSettingsSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadata;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataKeyAbsentFor;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkMetadataValue;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConfigSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkNetworkConnectionSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOvfEnvelope;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOvfNetworkSection;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkOwner;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkProductSectionList;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkVAppTemplate;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.metadataToMap;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Map;
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
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnection.IpAddressAllocationMode;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests the request/response behavior of {@link VAppTemplateClient}
 * 
 * NOTE The environment MUST have at least one template configured
 *
 * @author Aled Sage
 */
@Test(groups = { "live", "user", "vapptemplate" }, singleThreaded = true, testName = "VAppTemplateClientLiveTest")
public class VAppTemplateClientLiveTest extends AbstractVAppClientLiveTest {

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

   @Test(testName = "GET /vAppTemplate/{id}")
   public void testGetVAppTemplate() {
      vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      
      checkVAppTemplate(vAppTemplate);
      assertEquals(vAppTemplate.getHref(), vAppTemplateURI);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/owner")
   public void testGetVAppTemplateOwner() {
      Owner owner = vAppTemplateClient.getOwnerOfVAppTemplate(vAppTemplateURI);
      
      checkOwner(owner);
      assertEquals(owner.getUser(), vAppTemplateClient.getVAppTemplate(vAppTemplateURI).getOwner().getUser());
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/customizationSection")
   public void testGetCustomizationSection() {
      CustomizationSection customizationSection = vAppTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI);
      
      checkCustomizationSection(customizationSection);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/productSections")
   public void testGetProductSections() {
      ProductSectionList productSectionList = vAppTemplateClient.getProductSectionsForVAppTemplate(vAppTemplateURI);
      
      checkProductSectionList(productSectionList);
   }
   
   @Test(testName = "PUT /vAppTemplate/{id}/productSections")
   public void testEditProductSections() {
      // TODO make a real modification
      
      ProductSectionList origSections = vAppTemplateClient.getProductSectionsForVAppTemplate(vApp.getHref());
      ProductSectionList newSections = origSections.toBuilder().build();
      
      Task task = vAppTemplateClient.editProductSectionsForVAppTemplate(vApp.getHref(), newSections);
      assertTaskSucceeds(task);

      ProductSectionList modified = vAppTemplateClient.getProductSectionsForVAppTemplate(vApp.getHref());
      checkProductSectionList(modified);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/guestCustomizationSection")
   public void testGetGuestCustomizationSection() {
      getGuestCustomizationSection(new Function<URI, GuestCustomizationSection>() {
         @Override
         public GuestCustomizationSection apply(URI uri) {
            return vAppTemplateClient.getVAppTemplateGuestCustomizationSection(uri);
         }
      });
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/leaseSettingsSection")
   public void testGetLeaseSettingsSection() {
      // FIXME Wrong case for Vapp
      LeaseSettingsSection leaseSettingsSection = vAppTemplateClient.getVappTemplateLeaseSettingsSection(vAppTemplateURI);
      
      checkLeaseSettingsSection(leaseSettingsSection);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/metadata", dependsOnMethods = { "testEditMetadataValue" })
   public void testGetVAppTemplateMetadata() {
      Metadata metadata = vAppTemplateClient.getMetadataClient().getMetadata(vAppTemplateURI);
      
      checkMetadata(metadata);
   }

   // implicitly tested by testEditVAppTemplateMetadataValue, which first creates the metadata entry; otherwise no entry may exist
   @Test(testName = "GET /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testGetVAppTemplateMetadata" })
   public void testGetMetadataValue() {
      Metadata metadata = vAppTemplateClient.getMetadataClient().getMetadata(vAppTemplateURI);
      MetadataEntry entry = Iterables.get(metadata.getMetadataEntries(), 0);
      
      MetadataValue val = vAppTemplateClient.getMetadataClient().getMetadataValue(vAppTemplateURI, entry.getKey());
      
      checkMetadataValue(val);
      assertEquals(val.getValue(), entry.getValue());
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/networkConfigSection")
   public void testGetVAppTemplateNetworkConfigSection() {
      NetworkConfigSection networkConfigSection = vAppTemplateClient.getVAppTemplateNetworkConfigSection(vAppTemplateURI);
      
      checkNetworkConfigSection(networkConfigSection);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/networkConnectionSection")
   public void testGetNetworkConnectionSection() {
      getNetworkConnectionSection(new Function<URI, NetworkConnectionSection>() {
         @Override
         public NetworkConnectionSection apply(URI uri) {
            return vAppTemplateClient.getVAppTemplateNetworkConnectionSection(uri);
         }
      });
   }

   @Test(testName = "GET /vAppTemplate/{id}/networkSection")
   public void testGetVAppTemplateNetworkSection() {
      NetworkSection networkSection = vAppTemplateClient.getVAppTemplateNetworkSection(vAppTemplateURI);

      checkOvfNetworkSection(networkSection);
   }

   @Test(testName = "GET /vAppTemplate/{id}/ovf")
   public void testGetVAppTemplateOvf() {
      Envelope envelope = vAppTemplateClient.getVAppTemplateOvf(vAppTemplateURI);
      
      checkOvfEnvelope(envelope);
   }

   @Test(testName = "PUT /vAppTemplate/{id}")
   public void testEditVAppTemplate() {
      String name = name("myname-");
      String description = name("Description ");
      VAppTemplate template = VAppTemplate.builder()
               .name(name)
               .description(description)
               .build();
      
      final Task task = vAppTemplateClient.editVAppTemplate(vAppTemplateURI, template);
      retryTaskSuccess.apply(task);

      VAppTemplate newTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      assertEquals(newTemplate.getName(), name);
      assertEquals(newTemplate.getDescription(), description);
   }

   @Test(testName = "POST /vAppTemplate/{id}/metadata", dependsOnMethods = { "testGetVAppTemplate" })
   public void testEditMetadata() {
      // TODO Cleanup after ourselves..
      
      Metadata oldMetadata = vAppTemplateClient.getMetadataClient().getMetadata(vAppTemplateURI);
      Map<String,String> oldMetadataMap = metadataToMap(oldMetadata);

      String key = name("key-");
      String val = name("value-");
      MetadataEntry metadataEntry = MetadataEntry.builder().entry(key, val).build();
      Metadata metadata = Metadata.builder().fromMetadata(oldMetadata).entry(metadataEntry).build();
      
      final Task task = vAppTemplateClient.getMetadataClient().mergeMetadata(vAppTemplateURI, metadata);
      retryTaskSuccess.apply(task);

      Metadata newMetadata = vAppTemplateClient.getMetadataClient().getMetadata(vAppTemplateURI);
      Map<String,String> expectedMetadataMap = ImmutableMap.<String,String>builder()
               .putAll(oldMetadataMap)
               .put(key, val)
               .build();
      checkMetadataFor("vAppTemplate", newMetadata, expectedMetadataMap);
   }
   
   @Test(testName = "PUT /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testEditMetadata" })
   public void testEditMetadataValue() {
      // TODO Cleanup after ourselves..
      
      String key = name("key-");
      String val = name("value-");
      MetadataValue metadataValue = MetadataValue.builder().value(val).build();
      
      final Task task = vAppTemplateClient.getMetadataClient().setMetadata(vAppTemplateURI, key, metadataValue);
      retryTaskSuccess.apply(task);

      MetadataValue newMetadataValue = vAppTemplateClient.getMetadataClient().getMetadataValue(vAppTemplateURI, key);
      assertEquals(newMetadataValue.getValue(), metadataValue.getValue());
   }

   @Test(testName = "DELETE /vAppTemplate/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadataValue" })
   public void testDeleteVAppTemplateMetadataValue() {
      // First store a value
      String key = name("key-");
      MetadataValue metadataValue = MetadataValue.builder().value("myval").build();
      final Task task = vAppTemplateClient.getMetadataClient().setMetadata(vAppTemplateURI, key, metadataValue);
      retryTaskSuccess.apply(task);
      
      // Then delete the entry
      final Task deletionTask = vAppTemplateClient.getMetadataClient().deleteMetadataEntry(vAppTemplateURI, key);
      retryTaskSuccess.apply(deletionTask);

      // Then confirm the entry is not there
      Metadata newMetadata = vAppTemplateClient.getMetadataClient().getMetadata(vAppTemplateURI);
      checkMetadataKeyAbsentFor("vAppTemplate", newMetadata, key);
   }

   @Test(testName = "PUT /vAppTemplate/{id}/guestCustomizationSection")
   public void testEditGuestCustomizationSection() {
      String computerName = name("n");
      GuestCustomizationSection newSection = GuestCustomizationSection.builder()
               .info("")
               .computerName(computerName)
               .build();
      
      final Task task = vAppTemplateClient.editVAppTemplateGuestCustomizationSection(vm.getHref(), newSection);
      assertTaskSucceeds(task);

      GuestCustomizationSection modified = vAppTemplateClient.getVAppTemplateGuestCustomizationSection(vm.getHref());
      
      checkGuestCustomizationSection(modified);
      assertEquals(modified.getComputerName(), computerName);
   }
   
   @Test(testName = "PUT /vAppTemplate/{id}/customizationSection")
   public void testEditCustomizationSection() {
      boolean oldVal = vAppTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI).isCustomizeOnInstantiate();
      boolean newVal = !oldVal;
      
      CustomizationSection customizationSection = CustomizationSection.builder()
               .info("")
               .customizeOnInstantiate(newVal)
               .build();
      
      final Task task = vAppTemplateClient.editVAppTemplateCustomizationSection(vAppTemplateURI, customizationSection);
      retryTaskSuccess.apply(task);

      CustomizationSection newCustomizationSection = vAppTemplateClient.getVAppTemplateCustomizationSection(vAppTemplateURI);
      assertEquals(newCustomizationSection.isCustomizeOnInstantiate(), newVal);
   }

   // FIXME deploymentLeaseInSeconds returned is null
   @Test(testName = "PUT /vAppTemplate/{id}/leaseSettingsSection")
   public void testEditLeaseSettingsSection() throws Exception {
      int deploymentLeaseInSeconds = random.nextInt(10000)+1;
      // NOTE use smallish number for storageLeaseInSeconds; it seems to be capped at 5184000?
      int storageLeaseInSeconds = random.nextInt(10000)+1;

      LeaseSettingsSection leaseSettingSection = LeaseSettingsSection.builder()
               .info("my info")
               .storageLeaseInSeconds(storageLeaseInSeconds)
               .deploymentLeaseInSeconds(deploymentLeaseInSeconds)
               .build();
      
      final Task task = vAppTemplateClient.editVappTemplateLeaseSettingsSection(vAppTemplateURI, leaseSettingSection);
      retryTaskSuccess.apply(task);
      
      LeaseSettingsSection newLeaseSettingsSection = vAppTemplateClient.getVappTemplateLeaseSettingsSection(vAppTemplateURI);
      assertEquals(newLeaseSettingsSection.getStorageLeaseInSeconds(), (Integer) storageLeaseInSeconds);
      assertEquals(newLeaseSettingsSection.getDeploymentLeaseInSeconds(), (Integer) deploymentLeaseInSeconds);
   }

   @Test(testName = "PUT /vAppTemplate/{id}/networkConfigSection")
   public void testEditNetworkConfigSection() {
      // TODO What to modify?
      
      NetworkConfigSection oldSection = vAppTemplateClient.getVAppTemplateNetworkConfigSection(vApp.getHref());
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
      
      final Task task = vAppTemplateClient.editVAppTemplateNetworkConfigSection(vApp.getHref(), newSection);
      assertTaskSucceeds(task);

      NetworkConfigSection modified = vAppTemplateClient.getVAppTemplateNetworkConfigSection(vAppTemplateURI);
      checkNetworkConfigSection(modified);

//      assertEquals(modified§.getNetworkConfigs().size(), 1);
//      
//      VAppNetworkConfiguration newVAppNetworkConfig = Iterables.get(modified§.getNetworkConfigs(), 0);
//      assertEquals(newVAppNetworkConfig.getNetworkName(), networkName);
   }

   @Test(testName = "PUT /vAppTemplate/{id}/networkConnectionSection")
   public void testEditNetworkConnectionSection() {
      // Look up a network in the Vdc
      Set<Reference> networks = vdc.getAvailableNetworks();
      Reference network = Iterables.getLast(networks);

      // Copy existing section and update fields
      NetworkConnectionSection oldSection = vAppClient.getNetworkConnectionSection(vm.getHref());
      NetworkConnectionSection newSection = oldSection.toBuilder()
            .networkConnection(NetworkConnection.builder()
                  .ipAddressAllocationMode(IpAddressAllocationMode.DHCP.toString())
                  .network(network.getName())
                  .build())
            .build();
      
      Task task = vAppTemplateClient.editVAppTemplateNetworkConnectionSection(vm.getHref(), newSection);
      assertTaskSucceeds(task);

      NetworkConnectionSection modified = vAppTemplateClient.getVAppTemplateNetworkConnectionSection(vm.getHref());
      checkNetworkConnectionSection(modified);
   }
   
   // FIXME cloneVAppTemplate is giving back 500 error
   @Test(testName = "DELETE /vAppTemplate/{id}", dependsOnMethods = { "testGetVAppTemplate" }) 
   public void testDeleteVAppTemplate() throws Exception {
      VAppTemplate clonedVappTemplate = cloneVAppTemplate(true);

      // Confirm that "get" works pre-delete
      VAppTemplate vAppTemplatePreDelete = vAppTemplateClient.getVAppTemplate(clonedVappTemplate.getHref());
      checkVAppTemplate(vAppTemplatePreDelete);
      
      // Delete the template
      final Task task = vAppTemplateClient.deleteVappTemplate(clonedVappTemplate.getHref());
      retryTaskSuccess.apply(task);

      // Confirm that can't access post-delete, i.e. template has been deleted
      try {
         vAppTemplateClient.getVAppTemplate(clonedVappTemplate.getHref());
      } catch (VCloudDirectorException e) {
         // success; should get a 403 because vAppTemplate no longer exists
      }
   }

   @Test(testName = "POST /vAppTemplate/{id}/action/disableDownload")
   public void testDisableVAppTemplateDownload() throws Exception {
      vAppTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      
      // TODO Check that it really is disabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      //
      // For example:
      //    VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      //    Set<Link> links = vAppTemplate.getLinks();
      //    assertFalse(hasLinkMatchingRel(links, "download.*"), "Should not offer download link after disabling download: "+vAppTemplate);
   }
   
   @Test(testName = "POST /vAppTemplate/{id}/action/enableDownload")
   public void testEnableVAppTemplateDownload() throws Exception {
      // First disable so that enable really has some work to do...
      vAppTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      final Task task = vAppTemplateClient.enableDownloadVappTemplate(vAppTemplateURI);
      retryTaskSuccess.apply(task);
      
      // TODO Check that it really is enabled. The only thing I can see for determining this 
      // is the undocumented "download" link in the VAppTemplate. But that is brittle and we
      // don't know what timing guarantees there are for adding/removing the link.
      //
      // For example:
      //    VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
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
   
   @Test(testName = "POST /vAppTemplate/{id}/action/consolidate")
   public void testConsolidateVAppTemplate() throws Exception {
      final Task task = vAppTemplateClient.consolidateVappTemplate(vm.getHref());
      assertTaskSucceedsLong(task);
      
      // TODO Need assertion that command had effect
   }
   
   @Test(testName = "POST /vAppTemplate/{id}/action/relocate") // FIXME Need a datastore reference
   public void testRelocateVAppTemplate() throws Exception {
      // TODO Need assertion that command had effect
      Reference dataStore = null; // FIXME
      RelocateParams relocateParams = RelocateParams.builder()
               .datastore(dataStore)
               .build();
      
      final Task task = vAppTemplateClient.relocateVappTemplate(vAppTemplateURI, relocateParams);
      assertTaskSucceedsLong(task);
   }
   
   @Test(testName = "GET /vAppTemplate/{id}/shadowVms")
   public void testGetShadowVms() {
      References references = vAppTemplateClient.getShadowVms(vAppTemplateURI);
      
      Checks.checkReferences(references);
   }
   
   // This failed previously, but is passing now. 
   // However, it's not part of the official API so not necessary to assert it.
   @Test(testName = "test completed task not included in vAppTemplate") 
   public void testCompletedTaskNotIncludedInVAppTemplate() throws Exception {
      // Kick off a task, and wait for it to complete
      vAppTemplateClient.disableDownloadVappTemplate(vAppTemplateURI);
      final Task task = vAppTemplateClient.enableDownloadVappTemplate(vAppTemplateURI);
      assertTaskDoneEventually(task);

      // Ask the VAppTemplate for its tasks, and the status of the matching task if it exists
      VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      Set<Task> tasks = vAppTemplate.getTasks();
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
