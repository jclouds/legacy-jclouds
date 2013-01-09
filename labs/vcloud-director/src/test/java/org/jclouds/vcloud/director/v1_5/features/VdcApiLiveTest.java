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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.URN_REQ_LIVE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntity;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.Network.FenceMode;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.params.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiationParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link VdcApi}
 * 
 * @author danikov, Adrian Cole
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "VdcApiLiveTest")
public class VdcApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   public static final String VDC = "vdc";

   /*
    * Convenience reference to API api.
    */
   protected VdcApi vdcApi;
   protected VAppTemplateApi vappTemplateApi;
   protected VAppApi vappApi;

   private VApp instantiatedVApp;
   private VApp clonedVApp;
   private VApp composedVApp;
   private VAppTemplate clonedVAppTemplate;
   private VAppTemplate capturedVAppTemplate;
   private VAppTemplate uploadedVAppTemplate;
   private boolean metadataSet = false;
   private Network network;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      vdcApi = context.getApi().getVdcApi();
      vappTemplateApi = context.getApi().getVAppTemplateApi();
      vappApi = context.getApi().getVAppApi();

      assertNotNull(vdcUrn, String.format(URN_REQ_LIVE, VDC));
      network = lazyGetNetwork();
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
            Task remove = adminContext.getApi().getMetadataApi(vdcUrn).remove("key");
            taskDoneEventually(remove);
         } catch (Exception e) {
            logger.warn(e, "Error deleting metadata entry");
         }
      }
   }

   @Test(description = "GET /vdc/{id}")
   public void testGetVdc() {
      Vdc vdc = lazyGetVdc();
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
      assertNotEquals("DO NOT USE", vdc.getDescription(), "vDC isn't to be used for testing");
      Checks.checkVdc(vdc);
   }

   @Test(description = "POST /vdc/{id}/action/captureVApp", dependsOnMethods = { "testInstantiateVAppTemplate" })
   public void testCaptureVApp() {
	  VAppTemplate vAppTemplate = vappTemplateApi.get(vAppTemplateUrn);
      String name = name("captured-");

      CaptureVAppParams captureVappParams = CaptureVAppParams.builder()
    		  .name(name)
    		  .source(instantiatedVApp.getHref())
      // TODO: test optional params
      // .description("")
      // .sections(sections) // TODO: ovf sections
               .build();

      capturedVAppTemplate = vdcApi.captureVApp(vdcUrn, captureVappParams);

      Task task = Iterables.getFirst(capturedVAppTemplate.getTasks(), null);
      assertTaskSucceedsLong(task);

      Checks.checkVAppTemplate(capturedVAppTemplate);

      assertEquals(capturedVAppTemplate.getName(), name,
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "name", name, capturedVAppTemplate.getName()));
   }

   @Test(description = "POST /vdc/{id}/action/cloneVApp", dependsOnMethods = { "testInstantiateVAppTemplate" })
   public void testCloneVApp() {
      CloneVAppParams cloneVappParams = CloneVAppParams.builder().source(instantiatedVApp.getHref())
    		  .name(name("vappClone-"))
    		  .deploy(true)
		      // TODO: test optional params
		      // .description("")
		      // .isSourceDelete(true)
		      // .powerOn(true)
		      // .instantiationParams(InstantiationParams.builder()
		      // .sections(sections) 
    		  // TODO: ovf sections? various tests?
		      // .build())

               // Reserved. Unimplemented params; may test eventually when implemented
               // .vAppParent(vAppParentRef)
               // .linkedClone(true)
               .build();

      clonedVApp = vdcApi.cloneVApp(vdcUrn, cloneVappParams);

      Task task = Iterables.getFirst(clonedVApp.getTasks(), null);
      assertNotNull(task, "vdcApi.cloneVApp returned VApp that did not contain any tasks");
      assertTaskSucceedsLong(task);

      Checks.checkVApp(clonedVApp);
   }

   @Test(description = "POST /vdc/{id}/action/cloneVAppTemplate")
   public void testCloneVAppTemplate() {
      clonedVAppTemplate = vdcApi.cloneVAppTemplate(vdcUrn,
               CloneVAppTemplateParams.builder()
               	  .name(name("vappTemplateClone-"))
                  .source(lazyGetVAppTemplate().getHref())
                  .build());

      Task task = Iterables.getFirst(clonedVAppTemplate.getTasks(), null);
      assertNotNull(task, "vdcApi.cloneVAppTemplate returned VAppTemplate that did not contain any tasks");
      assertTaskSucceedsLong(task);

      Checks.checkVAppTemplate(clonedVAppTemplate);
   }

   @Test(description = "POST /vdc/{id}/action/composeVApp")
   public void testComposeVApp() {
      String name = name("composed-");

      composedVApp = vdcApi.composeVApp(vdcUrn, ComposeVAppParams.builder().name(name)
      // TODO: test optional params
      // .sourcedItem(SourcedCompositionItemParam.builder()
      // .sourcedItem(vAppTemplateURI)
      // .build())
      // .description("")
      // .deploy(true)
      // .isSourceDelete(false)
      // .powerOn(true)
      // .instantiationParams(InstantiationParams.builder()
      // .sections(sections) // TODO: ovf sections? various tests?
      // .build())

               // Reserved. Unimplemented params; may test eventually when implemented
               // .linkedClone()
               .build());

      Task task = Iterables.getFirst(composedVApp.getTasks(), null);
      assertNotNull(task, "vdcApi.composeVApp returned VApp that did not contain any tasks");
      assertTaskSucceedsLong(task);

      Checks.checkVApp(composedVApp);
      assertEquals(composedVApp.getName(), name,
               String.format(OBJ_FIELD_EQ, "VApp", "name", name, composedVApp.getName()));
   }

   // TODO Duplicates code in VAppApiLiveTest
   @Test(description = "POST /vdc/{id}/action/instantiateVAppTemplate")
   public void testInstantiateVAppTemplate() {
      Vdc vdc = vdcApi.get(vdcUrn);

      Set<Reference> networks = vdc.getAvailableNetworks();
      Optional<Reference> parentNetwork = Iterables.tryFind(networks, new Predicate<Reference>() {
         @Override
         public boolean apply(Reference reference) {
            return reference.getHref().equals(network.getHref());
         }
      });

      if (!parentNetwork.isPresent()) {
         fail(String.format("Could not find network %s in vdc", network.getHref().toASCIIString()));
      }

      NetworkConfiguration networkConfiguration = NetworkConfiguration.builder().parentNetwork(parentNetwork.get())
               .fenceMode(FenceMode.BRIDGED).build();

      NetworkConfigSection networkConfigSection = NetworkConfigSection
               .builder()
               .info("Configuration parameters for logical networks")
               .networkConfigs(
                        ImmutableSet.of(VAppNetworkConfiguration.builder().networkName("vAppNetwork")
                                 .configuration(networkConfiguration).build())).build();

      InstantiationParams instantiationParams = InstantiationParams.builder()
               .sections(ImmutableSet.of(networkConfigSection)).build();

      InstantiateVAppTemplateParams instantiate = InstantiateVAppTemplateParams.builder().name(name("test-vapp-"))
               .notDeploy().notPowerOn().description("Test VApp").instantiationParams(instantiationParams)
               .source(lazyGetVAppTemplate().getHref()).build();

      instantiatedVApp = vdcApi.instantiateVApp(vdcUrn, instantiate);
      Task instantiationTask = Iterables.getFirst(instantiatedVApp.getTasks(), null);
      assertTaskSucceedsLong(instantiationTask);

      Checks.checkVApp(instantiatedVApp);
   }

   @Test(description = "POST /vdc/{id}/action/uploadVAppTemplate")
   public void testUploadVAppTemplate() {
      // TODO Should test all 4 stages of upload; currently doing only stage 1 here.
      // 1. creating empty vApp template entity
      // 2. uploading an OVF of vApp template
      // 3. uploading disks described from the OVF
      // 4. finishing task for uploading

      String name = name("uploaded-");

      UploadVAppTemplateParams uploadVAppTemplateParams = UploadVAppTemplateParams.builder().name(name)
      // TODO: test optional params
      // .description("")
      // .transferFormat("")
      // .manifestRequired(true)
               .build();

      uploadedVAppTemplate = vdcApi.uploadVAppTemplate(vdcUrn, uploadVAppTemplateParams);

      Checks.checkVAppTemplateWhenNotReady(uploadedVAppTemplate);

      assertEquals(uploadedVAppTemplate.getName(), name,
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "name", name, uploadedVAppTemplate.getName()));

      ResourceEntity.Status expectedStatus = ResourceEntity.Status.UNRESOLVED;
      ResourceEntity.Status actualStatus = uploadedVAppTemplate.getStatus();
      assertEquals(actualStatus, expectedStatus,
               String.format(OBJ_FIELD_EQ, "VAppTemplate", "status", expectedStatus, actualStatus));

   }

   @Test(description = "GET /vdc/{id}/metadata", dependsOnMethods = { "testGetVdc" })
   public void testGetMetadata() {
      Metadata metadata = context.getApi().getMetadataApi(vdcUrn).get();

      // required for testing
      assertTrue(Iterables.isEmpty(metadata.getMetadataEntries()),
               String.format(OBJ_FIELD_REQ_LIVE, VDC, "metadata.entries"));

      Checks.checkMetadataFor(VDC, metadata);
   }

   @Test(description = "GET /vdc/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetMetadataValue() {
      // setupMetadata();
      // First find a key
      Metadata metadata = context.getApi().getMetadataApi(vdcUrn).get();
      Map<String, String> metadataMap = Checks.metadataToMap(metadata);
      String key = Iterables.getFirst(metadataMap.keySet(), "MadeUpKey!");
      String value = metadataMap.get(key);

      String metadataValue = context.getApi().getMetadataApi(vdcUrn).get(key);

      assertEquals(metadataValue, value);
   }
   
}
