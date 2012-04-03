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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResourceType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Network;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link NetworkClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "user", "network" }, singleThreaded = true, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String NETWORK = "network";
 
   /*
    * Convenience reference to API client.
    */
   protected NetworkClient networkClient;
   
   private boolean metadataSet = false;
    
   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredClients() {
      networkClient = context.getApi().getNetworkClient();
   }
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (metadataSet) {
         adminContext.getApi().getNetworkClient().getMetadataClient()
            .deleteMetadataEntry(toAdminUri(networkURI), "key");
      }
   }
   
   @Test(description = "GET /network/{id}")
   public void testGetNetwork() {
      // required for testing
      assertNotNull(networkURI, String.format(REF_REQ_LIVE, NETWORK));
       
      Network abstractNetwork = networkClient.getNetwork(networkURI);
      assertTrue(abstractNetwork instanceof OrgNetwork, String.format(REQUIRED_VALUE_OBJECT_FMT, 
            ".class", NETWORK, abstractNetwork.getClass(),"OrgNetwork"));
      OrgNetwork network = Network.toSubType(abstractNetwork);
      assertNotNull(network, String.format(OBJ_REQ_LIVE, NETWORK));
      assertTrue(!network.getDescription().equals("DO NOT USE"), "Network isn't to be used for testing");
       
      Checks.checkOrgNetwork(network);
   }
   
   private void setupMetadata() {
      adminContext.getApi().getNetworkClient().getMetadataClient().setMetadata(toAdminUri(networkURI), 
            "key", MetadataValue.builder().value("value").build());
      metadataSet = true;
   }
   
   @Test(description = "GET /network/{id}/metadata", dependsOnMethods = { "testGetNetwork" })
   public void testGetMetadata() {
      if (adminContext != null) {
         setupMetadata();
      }
      
      Metadata metadata = networkClient.getMetadataClient().getMetadata(networkURI);
      // required for testing
      assertFalse(Iterables.isEmpty(metadata.getMetadataEntries()), 
            String.format(OBJ_FIELD_REQ_LIVE, NETWORK, "metadata.entries"));
       
      // parent type
      checkResourceType(metadata);
       
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // required elements and attributes
         assertNotNull(entry.getKey(), 
               String.format(OBJ_FIELD_ATTRB_REQ, networkClient, "MetadataEntry", entry.getKey(), "key"));
         assertNotNull(entry.getValue(), 
               String.format(OBJ_FIELD_ATTRB_REQ, networkClient, "MetadataEntry", entry.getValue(), "value"));
          
         // parent type
         checkResourceType(entry);
      }
   }
   
   @Test(description = "GET /network/{id}/metadata/{key}", dependsOnMethods = { "testGetMetadata" })
   public void testGetMetadataValue() {
      MetadataValue metadataValue = networkClient.getMetadataClient().getMetadataValue(networkURI, "key");
       
      // Check parent type
      checkResourceType(metadataValue);
       
      // Check required elements and attributes
      String value = metadataValue.getValue();
      assertNotNull(value, 
            String.format(OBJ_FIELD_ATTRB_REQ, NETWORK, "MetadataEntry", 
                  metadataValue.toString(), "value"));
      assertEquals(value, "value", 
            String.format(OBJ_FIELD_EQ, NETWORK, "metadataEntry.value", "value", value));
   }
}
