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

import static org.testng.Assert.*;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link NetworkClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "api", "user" }, singleThreaded = true, testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private NetworkClient networkClient;

   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      networkClient = context.getApi().getNetworkClient();
   }
   
   @Test(testName = "GET /network/{id}")
   public void testWhenResponseIs2xxLoginReturnsValidNetwork() {
      Reference networkRef = Reference.builder()
            .href(URI.create(endpoint + "/network/" + networkId)).build();
      
      OrgNetwork network = networkClient.getNetwork(networkRef);
      
      // TODO change to checkOrgNetwork(...)
      checkEntityType(network);
   }
   
   @Test(testName = "GET /network/{id} - invalid")
   public void testWhenResponseIs400ForInvalidNetworkId() {
      Reference networkRef = Reference.builder()
            .href(URI.create(endpoint + "/network/NOTAUUID")).build();
      
      Error expected = Error.builder()
            .message("validation error on field 'id': String value has invalid format or length")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();
      
      try {
         networkClient.getNetwork(networkRef);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }

   @Test(testName = "GET /network/{id} - fake")
   public void testWhenResponseIs403ForFakeNetworkId() {
      Reference networkRef = Reference.builder()
            .href(URI.create(endpoint + "/network/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")).build();
      
      Error expected = Error.builder()
            .message("This operation is denied.")
            .majorErrorCode(403)
            .minorErrorCode("ACCESS_TO_RESOURCE_IS_FORBIDDEN")
            .build();

      try {
         networkClient.getNetwork(networkRef);
         fail("Should give HTTP 403 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      } catch (Exception e) {
         fail("Should have thrown a VCloudDirectorException");
      }
   }
   
   @Test(testName = "GET /network/{id}/metadata")
   public void testWhenResponseIs2xxLoginReturnsValidMetadataList() {
      Reference networkRef = Reference.builder()
            .href(URI.create(endpoint + "/network/"+networkId)).build();
      
      Metadata metadata = context.getApi().getNetworkClient().getMetadata(networkRef);

      checkMetadata(metadata);

      // TODO assert has metadata in order to support subsequent test
      // TODO assign metadata key (todo- ordering)
   }
   
   String metadataKey = "key";
   
   //TODO depends on previous
   @Test(testName = "GET /network/{id}/metadata", enabled=false)
   public void testWhenResponseIs2xxLoginReturnsValidMetadataEntry() {
      Reference networkRef = Reference.builder()
            .href(URI.create(endpoint + "/network/"+networkId)).build();
      
      MetadataEntry metadataEntry = networkClient.getMetadataEntry(networkRef, metadataKey);
 
      checkMetadataEntry(metadataEntry);
   }
}
