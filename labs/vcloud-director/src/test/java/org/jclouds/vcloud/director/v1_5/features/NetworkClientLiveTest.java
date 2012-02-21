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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REF_REQ_LIVE;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkResourceType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code NetworkClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "apitests" }, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String NETWORK = "network";
 
   /*
    * Convenience reference to API client.
    */
   protected NetworkClient networkClient;
 
   private Reference networkRef;
   
   @BeforeGroups(groups = { "live" }, dependsOnMethods = { "setupClient" })
   public void before() {
      String networkId = "a604f3c2-0343-453e-ae1f-cddac5b7bd94"; // TODO: inject
      networkRef = Reference.builder()
            .type("application/vnd.vmware.vcloud.orgNetwork+xml")
            .name("")
            .href(URI.create(endpoint+"/network/"+networkId)) 
            .id(networkId)
            .build();
      networkClient = context.getApi().getNetworkClient();
   }

   @Test(testName = "GET /network/{id}")
   public void testGetNetwork() {
      // required for testing
      assertNotNull(networkRef, String.format(REF_REQ_LIVE, NETWORK));
       
      OrgNetwork network = networkClient.getNetwork(networkRef);
      assertNotNull(network, String.format(OBJ_REQ_LIVE, NETWORK));
      assertTrue(!network.getDescription().equals("DO NOT USE"), "Network isn't to be used for testing");
       
      // parent type
      Checks.checkNetworkType(network);
       
      // optional
      ReferenceType<?> networkPoolRef = network.getNetworkPool();
      if (networkPoolRef != null) {
         Checks.checkReferenceType(networkPoolRef);
      }
      
      IpAddresses allowedExternalIpAddresses = network.getAllowedExternalIpAddresses();
      if (allowedExternalIpAddresses != null) {
         Checks.checkIpAddresses(allowedExternalIpAddresses);
      }
   }
   
   @Test(testName = "GET /network/{id}/metadata")
   public void testGetMetadata() {
      Metadata metadata = networkClient.getMetadata(networkRef);
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
   
   @Test(testName = "GET /network/{id}/metadata/{key}")
   public void testGetMetadataValue() {
      MetadataValue metadataValue = networkClient.getMetadataValue(networkRef, "key");
       
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
