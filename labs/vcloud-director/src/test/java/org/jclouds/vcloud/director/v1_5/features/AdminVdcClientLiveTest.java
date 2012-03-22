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
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link VdcClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "vdc" }, singleThreaded = true, testName = "AdminVdcClientLiveTest")
public class AdminVdcClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String VDC = "admin vdc";
 
   /*
    * Convenience reference to API client.
    */
   protected AdminVdcClient vdcClient;
   protected MetadataClient.Writeable metadataClient;
   
   protected URI adminVdcUri;

   private String metadataKey;
   private String metadataValue;
   
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredClients() {
      vdcClient = context.getApi().getAdminVdcClient();
      metadataClient = vdcClient.getMetadataClient();
      assertNotNull(vdcURI, String.format(REF_REQ_LIVE, VDC));
      adminVdcUri = toAdminUri(vdcURI);
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (metadataKey != null) {
         try {
            Task task = metadataClient.deleteMetadataEntry(adminVdcUri, metadataKey);
            assertTaskSucceeds(task);
         } catch (VCloudDirectorException e) {
            logger.warn(e, "Error deleting metadata-value (perhaps it doesn't exist?); continuing...");
         }
      }
   }

   @Test(testName = "GET /admin/vdc/{id}")
   public void testGetVdc() {
      AdminVdc vdc = vdcClient.getVdc(adminVdcUri);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));
       
      // parent type
      Checks.checkAdminVdc(vdc);
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "PUT /admin/vdc/{id}", enabled=false)
   public void testEditVdc() throws Exception {
      String origName = vdcClient.getVdc(adminVdcUri).getName();
      String newName = name("a");
      Exception exception = null;
      
      AdminVdc vdc = AdminVdc.builder()
               .name(newName)
               .build();
      
      try {
         Task task = vdcClient.editVdc(adminVdcUri, vdc);
         assertTaskSucceeds(task);
         
         AdminVdc modified = vdcClient.getVdc(adminVdcUri);
         assertEquals(modified.getName(), newName);
          
         // parent type
         Checks.checkAdminVdc(vdc);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            AdminVdc restorableVdc = AdminVdc.builder().name(origName).build();
            Task task = vdcClient.editVdc(adminVdcUri, restorableVdc);
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
   @Test(testName = "DELETE /admin/vdc/{id}", enabled=false)
   public void testDeleteVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to delete!
      Task task = vdcClient.deleteVdc(adminVdcUri);
      assertTaskSucceeds(task);
         
      try {
         vdcClient.getVdc(adminVdcUri);
      } catch (VCloudDirectorException e) {
         // success; unreachable because it has been deleted
      }
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "DISABLE/ENABLE /admin/vdc/{id}", enabled=false)
   public void testDisableAndEnableVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to delete!
      Exception exception = null;
      
      try {
         vdcClient.disableVdc(adminVdcUri);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            vdcClient.enableVdc(adminVdcUri);
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
   
   @Test(testName = "GET /admin/vdc/{id}/metadata")
   public void testGetMetadata() throws Exception {
      Metadata metadata = metadataClient.getMetadata(adminVdcUri);

      Checks.checkMetadata(metadata);
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "PUT /admin/vdc/{id}/metadata", enabled=false)
   public void testSetMetadata() throws Exception {
      metadataKey = name("key-");
      metadataValue = name("value-");
      Metadata metadata = Metadata.builder()
               .entry(MetadataEntry.builder().entry(metadataKey, metadataValue).build())
               .build();
      
      Task task = metadataClient.mergeMetadata(adminVdcUri, metadata);
      assertTaskSucceeds(task);
      
      MetadataValue modified = metadataClient.getMetadataValue(adminVdcUri, metadataKey);
      Checks.checkMetadataValueFor("AdminVdc", modified, metadataValue);
      Checks.checkMetadata(metadata);
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "GET /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadata" }, enabled=false)
   public void testGetMetadataValue() throws Exception {
      MetadataValue retrievedMetadataValue = metadataClient.getMetadataValue(adminVdcUri, metadataKey);
         
      Checks.checkMetadataValueFor("AdminVdc", retrievedMetadataValue, metadataValue);
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "PUT /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadataValue" }, enabled=false )
   public void testSetMetadataValue() throws Exception {
      metadataValue = name("value-");
      MetadataValue newV = MetadataValue.builder().value(metadataValue).build();
      
      Task task = metadataClient.setMetadata(adminVdcUri, metadataKey, newV);
      assertTaskSucceeds(task);
      
      MetadataValue retrievedMetadataValue = metadataClient.getMetadataValue(adminVdcUri, metadataKey);
      Checks.checkMetadataValueFor("AdminVdc", retrievedMetadataValue, metadataValue);
   }
   
   // TODO insufficient permissions to test
   @Test(testName = "DELETE /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" }, enabled=false )
   public void testDeleteMetadataValue() throws Exception {
      // TODO Remove dependency on other tests; make cleanUp delete a list of metadata entries?
      
      Task task = metadataClient.deleteMetadataEntry(adminVdcUri, metadataKey);
      assertTaskSucceeds(task);

      try {
         metadataClient.getMetadataValue(adminVdcUri, metadataKey);
         fail("Retrieval of metadata value "+metadataKey+" should have fail after deletion");
      } catch (VCloudDirectorException e) {
         // success; should not be accessible
      }
   }
}
