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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.features.VdcApi;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

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
   protected MetadataApi metadataApi;

   private String metadataKey;
   private String metadataValue;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      vdcApi = adminContext.getApi().getVdcApi();
      metadataApi = context.getApi().getMetadataApi(vdcUrn);
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (metadataKey != null) {
         try {
            Task task = metadataApi.remove(metadataKey);
            taskDoneEventually(task);
         } catch (VCloudDirectorException e) {
            logger.warn(e, "Error deleting metadata-value (perhaps it doesn't exist?); continuing...");
         }
      }
   }

   @Test(description = "GET /admin/vdc/{id}")
   public void testGetVdc() {
      AdminVdc vdc = vdcApi.get(vdcUrn);
      assertNotNull(vdc, String.format(OBJ_REQ_LIVE, VDC));

      // parent type
      Checks.checkAdminVdc(vdc);
   }

   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}", enabled = false)
   public void testEditVdc() throws Exception {
      String origName = lazyGetVdc().getName();
      String newName = name("a");
      Exception exception = null;

      AdminVdc vdc = AdminVdc.builder().name(newName).build();

      try {
         Task task = vdcApi.edit(vdcUrn, vdc);
         assertTaskSucceeds(task);

         AdminVdc modified = vdcApi.get(vdcUrn);
         assertEquals(modified.getName(), newName);

         // parent type
         Checks.checkAdminVdc(vdc);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            AdminVdc restorableVdc = AdminVdc.builder().name(origName).build();
            Task task = vdcApi.edit(vdcUrn, restorableVdc);
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
   @Test(description = "DELETE /admin/vdc/{id}", enabled = false)
   public void testRemoveVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to remove!
      Task task = vdcApi.remove(vdcUrn);
      assertTaskSucceeds(task);

      try {
         vdcApi.get(vdcUrn);
      } catch (VCloudDirectorException e) {
         // success; unreachable because it has been removed
         // TODO: ^^ wrong. this should return null
      }
   }

   // TODO insufficient permissions to test
   @Test(description = "DISABLE/ENABLE /admin/vdc/{id}", enabled = false)
   public void testDisableAndEnableVdc() throws Exception {
      // TODO Need to have a VDC that we're happy to remove!
      Exception exception = null;

      try {
         vdcApi.disable(vdcUrn);
      } catch (Exception e) {
         exception = e;
      } finally {
         try {
            vdcApi.enable(vdcUrn);
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
      Metadata metadata = metadataApi.get();

      Checks.checkMetadata(metadata);
   }

   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}/metadata", enabled = false)
   public void testSetMetadata() throws Exception {
      metadataKey = name("key-");
      metadataValue = name("value-");

      Task task = metadataApi.putAll(ImmutableMap.of(metadataKey, metadataValue));
      
      assertTaskSucceeds(task);

      String modified = metadataApi.get(metadataKey);
      assertEquals(modified, metadataValue);
   }

   // TODO insufficient permissions to test
   @Test(description = "GET /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadata" }, enabled = false)
   public void testGetMetadataValue() throws Exception {
      String retrievedMetadataValue = metadataApi.get(metadataKey);

      assertEquals(retrievedMetadataValue, metadataValue);
   }

   // TODO insufficient permissions to test
   @Test(description = "PUT /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadataValue" }, enabled = false)
   public void testSetMetadataValue() throws Exception {
      metadataValue = name("value-");

      Task task = metadataApi.put(metadataKey, metadataValue);
      assertTaskSucceeds(task);

      String retrievedMetadataValue = metadataApi.get(metadataKey);
      assertEquals(retrievedMetadataValue, metadataValue);
   }

   // TODO insufficient permissions to test
   @Test(description = "DELETE /admin/vdc/{id}/metadata/{key}", dependsOnMethods = { "testSetMetadataValue" }, enabled = false)
   public void testRemoveMetadataValue() throws Exception {
      // TODO Remove dependency on other tests; make cleanUp remove a list of metadata entries?

      Task task = metadataApi.remove(metadataKey);
      assertTaskSucceeds(task);

      assertNull(metadataApi.get(metadataKey));
   }
}
