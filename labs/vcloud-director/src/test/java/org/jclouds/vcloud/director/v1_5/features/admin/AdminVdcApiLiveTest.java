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
package org.jclouds.vcloud.director.v1_5.features.admin;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.features.VdcApi;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link VdcApi}
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin" }, singleThreaded = true, testName = "AdminVdcApiLiveTest")
public class AdminVdcApiLiveTest extends BaseVCloudDirectorApiLiveTest {
   
   public static final String VDC = "admin vdc";
 
   /*
    * Convenience reference to API api.
    */
   protected AdminVdcApi vdcApi;
   protected MetadataApi.Writeable metadataApi;
   
   protected URI adminVdcUri;

   private String metadataKey;
   private String metadataValue;
   
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      vdcApi = adminContext.getApi().getVdcApi();
      metadataApi = vdcApi.getMetadataApi();
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
      adminVdcUri = toAdminUri(vdcURI);
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (metadataKey != null) {
         try {
            Task task = metadataApi.deleteMetadataEntry(adminVdcUri, metadataKey);
            taskDoneEventually(task);
         } catch (VCloudDirectorException e) {
            logger.warn(e, "Error deleting metadata-value (perhaps it doesn't exist?); continuing...");
         }
      }
   }

   @Test(description = "GET /admin/vdc/{id}")
   public void testGetVdc() {
      AdminVdc vdc = vdcApi.getVdc(adminVdcUri);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
       
      // parent type
      Checks.checkAdminVdc(vdc);
   }
   
   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}", enabled=false)
   public void testEditVdc() throws Exception {
      String origName = vdcApi.getVdc(adminVdcUri).getName();
      String newName = name("a");
      Exception exception = null;
      
      AdminVdc vdc = AdminVdc.builder()
               .name(newName)
               .build();
      
      try {
         Task task = vdcApi.editVdc(adminVdcUri, vdc);
         assertTaskSucceeds(task);
         
         AdminVdc modified = vdcApi.getVdc(adminVdcUri);
         assertEquals(modified.getName(), newName);
          
         // parent type
         Checks.checkAdminVdc(vdc);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            AdminVdc restorableVdc = AdminVdc.builder().name(origName).build();
            Task task = vdcApi.editVdc(adminVdcUri, restorableVdc);
            assertTaskSucceeds(task);
         } catch (Exception e) {
            if (exception != null) {
               logger.warn(e, "Error resetting adminVdc.name; rethrowing original test exception...");
               throw exception;
            } else {
               throw e;
            }
         }
      }
   }
   
   // TODO insufficient permissions to test
   @Test(description = "DELETE /admin/vdc/{id}", enabled=false)
   public void testDeleteVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to delete!
      Task task = vdcApi.deleteVdc(adminVdcUri);
      assertTaskSucceeds(task);
         
      try {
         vdcApi.getVdc(adminVdcUri);
      } catch (VCloudDirectorException e) {
         // success; unreachable because it has been deleted
      }
   }
   
   // TODO insufficient permissions to test
   @Test(description = "DISABLE/ENABLE /admin/vdc/{id}", enabled=false)
   public void testDisableAndEnableVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to delete!
      Exception exception = null;
      
      try {
         vdcApi.disableVdc(adminVdcUri);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            vdcApi.enableVdc(adminVdcUri);
         } catch (Exception e) {
            if (exception != null) {
               logger.warn(e, "Error resetting adminVdc.name; rethrowing original test exception...");
               throw exception;
            } else {
               throw e;
            }
         }
      }
   }
   
   @Test(description = "GET /admin/vdc/{id}/metadata")
   public void testGetMetadata() throws Exception {
      Metadata metadata = metadataApi.getMetadata(adminVdcUri);

      Checks.checkMetadata(metadata);
   }
   
   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}/metadata", enabled=false)
   public void testSetMetadata() throws Exception {
      metadataKey = name("key-");
      metadataValue = name("value-");
      Metadata metadata = Metadata.builder()
               .entry(MetadataEntry.builder().entry(metadataKey, metadataValue).build())
               .build();
      
      Task task = metadataApi.mergeMetadata(adminVdcUri, metadata);
      assertTaskSucceeds(task);
      
      MetadataValue modified = metadataApi.getMetadataValue(adminVdcUri, metadataKey);
      Checks.checkMetadataValueFor("AdminVdc", modified, metadataValue);
      Checks.checkMetadata(metadata);
   }
   
   // TODO insufficient permissions to test
   @Test(description = "GET /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadata" }, enabled=false)
   public void testGetMetadataValue() throws Exception {
      MetadataValue retrievedMetadataValue = metadataApi.getMetadataValue(adminVdcUri, metadataKey);
         
      Checks.checkMetadataValueFor("AdminVdc", retrievedMetadataValue, metadataValue);
   }
   
   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadataValue" }, enabled=false )
   public void testSetMetadataValue() throws Exception {
      metadataValue = name("value-");
      MetadataValue newV = MetadataValue.builder().value(metadataValue).build();
      
      Task task = metadataApi.setMetadata(adminVdcUri, metadataKey, newV);
      assertTaskSucceeds(task);
      
      MetadataValue retrievedMetadataValue = metadataApi.getMetadataValue(adminVdcUri, metadataKey);
      Checks.checkMetadataValueFor("AdminVdc", retrievedMetadataValue, metadataValue);
   }
   
   // TODO insufficient permissions to test
   @Test(description = "DELETE /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" }, enabled=false )
   public void testDeleteMetadataValue() throws Exception {
      // TODO Remove dependency on other tests; make cleanUp delete a list of metadata entries?
      
      Task task = metadataApi.deleteMetadataEntry(adminVdcUri, metadataKey);
      assertTaskSucceeds(task);

      try {
         metadataApi.getMetadataValue(adminVdcUri, metadataKey);
         fail("Retrieval of metadata value "+metadataKey+" should have fail after deletion");
      } catch (VCloudDirectorException e) {
         // success; should not be accessible
      }
   }
}
