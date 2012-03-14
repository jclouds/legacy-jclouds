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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.*;

import org.jclouds.vcloud.director.v1_5.domain.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VdcClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user", "vdc" }, testName = "VdcClientLiveTest")
public class VdcClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String VDC = "vdc";

   /*
    * Convenience reference to API client.
    */
   protected VdcClient vdcClient;
   
   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      vdcClient = context.getApi().getVdcClient();
   }
   
   @Test(testName = "GET /vdc/{id}")
   public void testGetVdc() {
      // required for testing
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
       
      Vdc vdc = vdcClient.getVdc(vdcURI);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
      assertTrue(!vdc.getDescription().equals("DO NOT USE"), "vDC isn't to be used for testing");
       
      // parent type
      Checks.checkEntityType(vdc);
      
      // required
      assertNotNull(vdc.getAllocationModel(), String.format(OBJ_FIELD_REQ, VDC, "allocationModel"));
      
      assertNotNull(vdc.getStorageCapacity(), String.format(OBJ_FIELD_REQ, VDC, "storageCapacity"));
      Checks.checkCapacityWithUsage(vdc.getStorageCapacity());
      
      assertNotNull(vdc.getComputeCapacity(), String.format(OBJ_FIELD_REQ, VDC, "computeCapacity"));
      Checks.checkComputeCapacity(vdc.getComputeCapacity());
      
      assertNotNull(vdc.getNicQuota(), String.format(OBJ_FIELD_REQ, VDC, "nicQuota"));
      assertTrue(vdc.getNicQuota() >= 0, String.format(OBJ_FIELD_GTE_0, VDC, "nicQuota", vdc.getNicQuota()));
      
      assertNotNull(vdc.getNetworkQuota(), String.format(OBJ_FIELD_REQ, VDC, "networkQuota"));
      assertTrue(vdc.getNetworkQuota() >= 0, String.format(OBJ_FIELD_GTE_0, VDC, "networkQuota", vdc.getNetworkQuota()));
      
       
      // optional
      // NOTE isEnabled cannot be checked
      if (vdc.getResourceEntities() != null) {
         Checks.checkResourceEntities(vdc.getResourceEntities());
      }
      if (vdc.getAvailableNetworks() != null) {
         Checks.checkAvailableNetworks(vdc.getAvailableNetworks());
      }
      if (vdc.getCapabilities() != null) {
         Checks.checkCapabilities(vdc.getCapabilities());
      }
      if(vdc.getVmQuota() != null) {
         assertTrue(vdc.getVmQuota() >= 0, String.format(OBJ_FIELD_GTE_0, VDC, "vmQuota", vdc.getVmQuota()));
      }
      if(vdc.getVmQuota() != null) {
         assertTrue(vdc.getVmQuota() >= 0, String.format(OBJ_FIELD_GTE_0, VDC, "vmQuota", vdc.getVmQuota()));
      }
   }
   
   @Test(testName = "POST /vdc/{id}/action/captureVApp", enabled = false)
   public void testCaptureVApp() {
      Reference templateSource = null; // TODO: vApp reference
      VAppTemplate template = vdcClient.captureVApp(vdcURI, CaptureVAppParams.builder()
            .source(templateSource)
                  // TODO: test optional params
                  //.name("")
                  //.description("")
                  //.sections(sections) // TODO: ovf sections
            .build());
      
      checkVAppTemplate(template);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "POST /vdc/{id}/action/cloneVApp", enabled = false)
   public void testCloneVApp() {
      Reference vAppSource = null; // TODO: vApp reference
      VApp vApp = vdcClient.cloneVApp(vdcURI, CloneVAppParams.builder()
            .source(vAppSource)
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
            .build());
      
      Checks.checkVApp(vApp);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "POST /vdc/{id}/action/cloneVAppTemplate", enabled = false)
   public void testCloneVAppTemplate() {
      Reference templateSource = null; // TODO: vAppTemplate reference
      VAppTemplate template = vdcClient.cloneVAppTemplate(vdcURI, CloneVAppTemplateParams.builder()
            .source(templateSource)
                  // TODO: test optional params
                  //.name("")
                  //.description("")
                  //.isSourceDelete(true)
                  //.sections(sections) // TODO: ovf sections
            .build());
      
      Checks.checkVAppTemplate(template);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "POST /vdc/{id}/action/composeVApp", enabled = false)
   public void testComposeVApp() {
      VApp vApp = vdcClient.composeVApp(vdcURI, ComposeVAppParams.builder()
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
            //.linkedClone()
            .build());
      
      Checks.checkVApp(vApp);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "POST /vdc/{id}/action/instantiateVAppTemplate", enabled = false)
   public void testInstantiateVAppTemplate() {
      Reference templateSource = null; // TODO: vApp or vAppTemplate reference
      VApp vApp = vdcClient.instantiateVApp(vdcURI, InstantiateVAppParams.builder()
            .source(templateSource)
                  // TODO: test optional params
                  //.name("")
                  //.description("")
                  //.isSourceDelete(true)
            .build());
      
      Checks.checkVApp(vApp);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "POST /vdc/{id}/action/uploadVAppTemplate", enabled = false)
   public void testUploadVAppTemplate() {
      VAppTemplate template = vdcClient.uploadVAppTemplate(vdcURI, UploadVAppTemplateParams.builder()
            // TODO: test optional params
            //.name("")
            //.description("")
            //.transferFormat("")
            //.manifestRequired(true)
            .build());
      
      Checks.checkVAppTemplate(template);
      
      // TODO: await task to complete
      // TODO: make assertions that the task was successful
   }
   
   @Test(testName = "GET /network/{id}/metadata", enabled = false)
   public void testGetMetadata() {
      Metadata metadata = vdcClient.getMetadataClient().getMetadata(vdcURI);
      // required for testing
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), 
            String.format(OBJ_FIELD_REQ_LIVE, VDC, "metadata.entries"));
      
      Checks.checkMetadataFor(VDC, metadata);
   }
   
   @Test(testName = "GET /network/{id}/metadata/{key}", enabled = false)
   public void testGetMetadataValue() {
      MetadataValue metadataValue = vdcClient.getMetadataClient().getMetadataValue(vdcURI, "key");
      
      Checks.checkMetadataValueFor(VDC, metadataValue);
   }
}
