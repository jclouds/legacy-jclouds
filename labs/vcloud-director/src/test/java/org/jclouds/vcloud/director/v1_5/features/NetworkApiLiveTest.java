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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.URN_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link NetworkApi}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "NetworkApiLiveTest")
public class NetworkApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   public static final String NETWORK = "network";

   /*
    * Convenience reference to API api.
    */
   protected NetworkApi networkApi;

   private boolean metadataSet = false;

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      networkApi = context.getApi().getNetworkApi();
   }

   @AfterClass(alwaysRun = true)
   public void cleanUp() {
      if (metadataSet) {
         try {
            Task remove = adminContext.getApi().getMetadataApi(networkUrn).remove("key");
            taskDoneEventually(remove);
         } catch (Exception e) {
            logger.warn(e, "Error when deleting metadata");
         }
      }
   }

   @Test(description = "GET /network/{id}")
   public void testGetNetwork() {
      // required for testing
      assertNotNull(networkUrn, String.format(URN_REQ_LIVE, NETWORK));

      Network abstractNetwork = networkApi.get(networkUrn);
      assertTrue(abstractNetwork instanceof OrgNetwork,
               String.format(REQUIRED_VALUE_OBJECT_FMT, ".class", NETWORK, abstractNetwork.getClass(), "OrgNetwork"));
      OrgNetwork network = Network.toSubType(abstractNetwork);
      assertNotNull(network, String.format(OBJ_REQ_LIVE, NETWORK));
      assertNotEquals("DO NOT USE", network.getDescription(), "Network isn't to be used for testing");

      Checks.checkOrgNetwork(network);
   }

   private void setupMetadata() {
      //TODO: block until complete
      adminContext.getApi().getMetadataApi(networkUrn).put("key", "value");
      metadataSet = true;
   }

   @Test(description = "GET /network/{id}/metadata", dependsOnMethods = { "testGetNetwork" })
   public void testGetMetadata() {
      if (adminContext != null) {
         setupMetadata();
      }

      Metadata metadata = context.getApi().getMetadataApi(networkUrn).get();
      // required for testing
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()),
               String.format(OBJ_FIELD_REQ_LIVE, NETWORK, "metadata.entries"));

      // parent type
      checkResource(metadata);

      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // required elements and attributes
         assertNotNull(entry.getKey(),
                  String.format(OBJ_FIELD_ATTRB_REQ, networkApi, "MetadataEntry", entry.getKey(), "key"));
         assertNotNull(entry.getValue(),
                  String.format(OBJ_FIELD_ATTRB_REQ, networkApi, "MetadataEntry", entry.getValue(), "value"));

         // parent type
         checkResource(entry);
      }
   }

   @Test(description = "GET /network/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetMetadataValue() {
      String metadataValue = context.getApi().getMetadataApi(networkUrn).get("key");

      assertEquals(metadataValue, "value", String.format(OBJ_FIELD_EQ, NETWORK, "metadataEntry.value", "value", metadataValue));
   }
}
