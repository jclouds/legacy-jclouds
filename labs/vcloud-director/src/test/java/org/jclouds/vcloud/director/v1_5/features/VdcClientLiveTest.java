/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.domain.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link VdcClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user", "vdc" }, singleThreaded = true, testName = "VdcClientLiveTest")
public class VdcClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String VDC = "vdc";

   /*
    * Convenience reference to API client.
    */
   protected VdcClient vdcClient;
   protected VAppTemplateClient vappTemplateClient;
   protected VAppClient vappClient;
   
   private VApp instantiatedVApp;
   private VApp clonedVApp;
   private VApp composedVApp;
   private VAppTemplate clonedVAppTemplate;
   private VAppTemplate capturedVAppTemplate;
   private VAppTemplate uploadedVAppTemplate;
   private boolean metadataSet = false;
   
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredClients() {
      vdcClient = context.getApi().getVdcClient();
      vappTemplateClient = context.getApi().getVAppTemplateClient();
      vappClient = context.getApi().getVAppClient();
      
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
   }
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (clonedVAppTemplate != null) {
         cleanUpVAppTemplate(clonedVAppTemplate);
      }
      if (capturedVAppTemplate != null) {
         cleanUpVAppTemplate(capturedVAppTemplate);
      }
      if (uploadedVAppTemplate != null) {
         cleanUpVAppTemplate(uploadedVAppTemplate);
      }
      if (instantiatedVApp != null) {
         cleanUpVApp(instantiatedVApp);
      }
      if (clonedVApp != null) {
         cleanUpVApp(clonedVApp);
      }
      if (composedVApp != null) {
         cleanUpVApp(composedVApp);
      }
      
      if (metadataSet) {
         try {
	         Task delete = adminContext.getApi().getVdcClient().getMetadataClient().deleteMetadataEntry(toAdminUri(vdcURI), "key");
	         taskDoneEventually(delete);
         } catch (Exception e) {
            logger.warn(e, "Error deleting metadata entry");
         }
      }
   }
   
   @Test(description = "GET /vdc/{id}")
   public void testGetVdc() {
      Vdc vdc = vdcClient.getVdc(vdcURI);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
      assertTrue(!vdc.getDescription().equals("DO NOT USE"), "vDC isn't to be used for testing");
       
      Checks.checkVdc(vdc);
   }
   
   @Test(description = "POST /vdc/{id}/action/captureVApp", dependsOnMethods = { "testInstantiateVAppTemplate" } )
   public void testCaptureVApp() {
      String name = name("captured-");
      
      CaptureVAppParams captureVappParams = CaptureVAppParams.builder()
               .name(name)
               .source(instantiatedVApp.getHref())
               // TODO: test optional params
               //.description("")
               //.sections(sections) // TODO: ovf sections
               .build();
      
      capturedVAppTemplate = vdcClient.captureVApp(vdcURI, captureVappParams);

      Task task = Iterables.getFirst(capturedVAppTemplate.getTasks(), null);
      assertTaskSucceedsLong(task);

      Checks.checkVAppTemplate(capturedVAppTemplate);
      
      assertEquals(capturedVAppTemplate.getName(), name, 
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "name", name, capturedVAppTemplate.getName()));
   }
   
   @Test(description = "POST /vdc/{id}/action/cloneVApp", dependsOnMethods = { "testInstantiateVAppTemplate" } )
   public void testCloneVApp() {
      CloneVAppParams cloneVappParams = CloneVAppParams.builder()
               .source(instantiatedVApp.getHref())
               // TODO: test optional params
               //.name("") 
               //.description("")
               //.deploy(true)
               //.isSourceDelete(true)
               //.powerOn(true)
               //.instantiationParams(InstantiationParams.builder()
               //      .sections(sections) // TODO: ovf sections? various tests?
               //      .build())
   
               // Reserved. Unimplemented params; may test eventually when implemented
               //.vAppParent(vAppParentRef)
               //.linkedClone(true)
               .build();
      
      clonedVApp = vdcClient.cloneVApp(vdcURI, cloneVappParams);
      
      Task task = Iterables.getFirst(clonedVApp.getTasks(), null);
      assertNotNull(task, "vdcClient.cloneVApp returned VApp that did not contain any tasks");
      assertTaskSucceedsLong(task);

      Checks.checkVApp(clonedVApp);
   }
   
   @Test(description = "POST /vdc/{id}/action/cloneVAppTemplate")
   public void testCloneVAppTemplate() {
      clonedVAppTemplate = vdcClient.cloneVAppTemplate(vdcURI, CloneVAppTemplateParams.builder()
               .source(vAppTemplateURI)
               .build());
      
      Task task = Iterables.getFirst(clonedVAppTemplate.getTasks(), null);
      assertNotNull(task, "vdcClient.cloneVAppTemplate returned VAppTemplate that did not contain any tasks");
      assertTaskSucceedsLong(task);
      
      Checks.checkVAppTemplate(clonedVAppTemplate);
   }
   
   @Test(description = "POST /vdc/{id}/action/composeVApp")
   public void testComposeVApp() {
      String name = name("composed-");
      
      composedVApp = vdcClient.composeVApp(vdcURI, ComposeVAppParams.builder()
            .name(name)
            // TODO: test optional params
            //.sourcedItem(SourcedCompositionItemParam.builder()
                        //.sourcedItem(vAppTemplateURI)
                        //.build())
            //.description("")
            //.deploy(true)
            //.isSourceDelete(false)
            //.powerOn(true)
            //.instantiationParams(InstantiationParams.builder()
            //      .sections(sections) // TODO: ovf sections? various tests?
            //      .build())

            // Reserved. Unimplemented params; may test eventually when implemented
            //.linkedClone()
            .build());

      Task task = Iterables.getFirst(composedVApp.getTasks(), null);
      assertNotNull(task, "vdcClient.composeVApp returned VApp that did not contain any tasks");
      assertTaskSucceedsLong(task);

      Checks.checkVApp(composedVApp);
      assertEquals(composedVApp.getName(), name, 
               String.format(OBJ_FIELD_EQ, "VApp", "name", name, composedVApp.getName()));
   }
   
   // TODO Duplicates code in VAppClientLiveTest
   @Test(description = "POST /vdc/{id}/action/instantiateVAppTemplate")
   public void testInstantiateVAppTemplate() {
      Vdc vdc = vdcClient.getVdc(vdcURI);

      Set<Reference> networks = vdc.getAvailableNetworks();
      Optional<Reference> parentNetwork = Iterables.tryFind(
            networks, new Predicate<Reference>() {
                  @Override
                  public boolean apply(Reference reference) {
                     return reference.getHref().equals(networkURI);
                  }
            });

      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", networkURI.toASCIIString()));
      }

      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder()
            .parentNetwork(parentNetwork.get())
            .fenceMode(FenceMode.BRIDGED)
            .build();
      
      NetworkConfigSection networkConfigSection = NetworkConfigSection.builder()
               .info("Configuration parameters for logical networks")
               .networkConfigs(
                     ImmutableSet.of(VAppNetworkConfiguration.builder()
                           .networkName("vAppNetwork")
                           .configuration(networkConfiguration)
                           .build()))
               .build();

      InstantiationParams instantiationParams = InstantiationParams.builder()
                              .sections(ImmutableSet.of(networkConfigSection))
                              .build();
                     
      InstantiateVAppTemplateParams instantiate = InstantiateVAppTemplateParams.builder()
            .name(name("test-vapp-"))
            .notDeploy()
            .notPowerOn()
            .description("Test VApp")
            .instantiationParams(instantiationParams)
            .source(vAppTemplateURI)
            .build();

      instantiatedVApp = vdcClient.instantiateVApp(vdcURI, instantiate);
      Task instantiationTask = Iterables.getFirst(instantiatedVApp.getTasks(), null);
      assertTaskSucceedsLong(instantiationTask);
      
      Checks.checkVApp(instantiatedVApp);
   }
   
   @Test(description = "POST /vdc/{id}/action/uploadVAppTemplate")
   public void testUploadVAppTemplate() {
      // TODO Should test all 4 stages of upload; currently doing only stage 1 here.
      //  1. creating empty vApp template entity 
      //  2. uploading an OVF of vApp template 
      //  3. uploading disks described from the OVF 
      //  4. finishing task for uploading
      
      String name = name("uploaded-");
      
      UploadVAppTemplateParams uploadVAppTemplateParams = UploadVAppTemplateParams.builder()
               .name(name)
               // TODO: test optional params
               //.description("")
               //.transferFormat("")
               //.manifestRequired(true)
               .build();
      
      uploadedVAppTemplate = vdcClient.uploadVAppTemplate(vdcURI, uploadVAppTemplateParams);
      
      Checks.checkVAppTemplateWhenNotReady(uploadedVAppTemplate);
      
      assertEquals(uploadedVAppTemplate.getName(), name, 
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "name", name, uploadedVAppTemplate.getName()));
      
      ResourceEntityType.Status expectedStatus = ResourceEntityType.Status.UNRESOLVED;
      ResourceEntityType.Status actualStatus = uploadedVAppTemplate.getStatus();
      assertEquals(actualStatus, expectedStatus,
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "status", expectedStatus, actualStatus));
      
   }
   
   private void setupMetadata() {
      adminContext.getApi().getVdcClient().getMetadataClient().setMetadata(toAdminUri(vdcURI), 
            "key", MetadataValue.builder().value("value").build());
      metadataSet = true;
   }
   
   @Test(description = "GET /vdc/{id}/metadata", dependsOnMethods = { "testGetVdc" } )
   public void testGetMetadata() {
      if(adminContext != null) {
         setupMetadata();
      }
      
      Metadata metadata = vdcClient.getMetadataClient().getMetadata(vdcURI);
      
      // required for testing
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), 
            String.format(OBJ_FIELD_REQ_LIVE, VDC, "metadata.entries"));
      
      Checks.checkMetadataFor(VDC, metadata);
   }
   
   @Test(description = "GET /vdc/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" } )
   public void testGetMetadataValue() {
      // First find a key
      Metadata metadata = vdcClient.getMetadataClient().getMetadata(vdcURI);
      Map<String, String> metadataMap = Checks.metadataToMap(metadata);
      String key = Iterables.getFirst(metadataMap.keySet(), "MadeUpKey!");
      String value = metadataMap.get(key);
      
      MetadataValue metadataValue = vdcClient.getMetadataClient().getMetadataValue(vdcURI, key);
      
      Checks.checkMetadataValueFor(VDC, metadataValue, value);
   }
}
