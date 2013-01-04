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
package org.jclouds.fujitsu.fgcp.services;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
import org.jclouds.fujitsu.fgcp.domain.VServerWithDetails;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "VirtualServerApiExpectTest", singleThreaded = true)
public class VirtualServerApiExpectTest extends BaseFGCPRestApiExpectTest {

   public void testStart() {
      HttpRequest request = buildGETWithQuery("Action=StartVServer"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/StartVServer-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.start("CONTRACT-VSYS00001-S-0005");
   }

   public void testStop() {
      HttpRequest request = buildGETWithQuery("Action=StopVServer"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/StopVServer-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.stop("CONTRACT-VSYS00001-S-0005");
   }

   public void testStopForcefully() {
      HttpRequest request = buildGETWithQuery("Action=StopVServer"
            + "&force=true"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/StopVServer-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.stopForcefully("CONTRACT-VSYS00001-S-0005");
   }

   public void testDestroy() {
      HttpRequest request = buildGETWithQuery("Action=DestroyVServer"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DestroyVServer-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.destroy("CONTRACT-VSYS00001-S-0005");
   }

   public void testGet() {
      HttpRequest request = buildGETWithQuery("Action=GetVServerAttributes"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVServerAttributes-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      assertNotNull(api.get("CONTRACT-VSYS00001-S-0005"));
   }

   public void testGetDetails() {
      HttpRequest request = buildGETWithQuery("Action=GetVServerConfiguration"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVServerConfiguration-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      VServerWithDetails server = api.getDetails("CONTRACT-VSYS00001-S-0005");
      assertNotNull(server);
      assertEquals(server.getId(), "CONTRACT-VSYS00001-S-0005");
      assertEquals(server.getVnics().iterator().next().getNicNo(), 0);
      assertEquals(server.getVnics().iterator().next().getPrivateIp(), "192.168.4.13");
      assertEquals(server.getVnics().iterator().next().getNetworkId(), "CONTRACT-VSYS00001-N-DMZ");
      assertEquals(server.getImage().getId(), "IMG_A1B2C3_1234567890ABCD");
      assertEquals(server.getImage().getSysvolSize(), 10.0f);
   }

   public void testGetStatus() {
      HttpRequest request = buildGETWithQuery("Action=GetVServerStatus"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVServerStatus-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      assertEquals(api.getStatus("CONTRACT-VSYS00001-S-0005"), VServerStatus.STOPPED);
   }

   public void testInitialPassword() {
      HttpRequest request = buildGETWithQuery("Action=GetVServerInitialPassword"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVServerInitialPassword-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      assertEquals(api.getInitialPassword("CONTRACT-VSYS00001-S-0005"), "mySecretpwd1");
   }

   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateVServerAttribute"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&attributeName=vserverName"
            + "&attributeValue=new%20name"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UpdateVServerAttribute-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.update("CONTRACT-VSYS00001-S-0005", "vserverName", "new name");
   }

   public void testAttachDisk() {
      HttpRequest request = buildGETWithQuery("Action=AttachVDisk"
            + "&vserverId=CONTRACT-VSYS00001-S-0005"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/AttachVDisk-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      api.attachDisk("CONTRACT-VSYS00001-S-0005", "CONTRACT-VSYS00001-D-0001");
   }

/*   public void testGetPerformanceInformation() {
      HttpRequest request = buildGETWithQuery("Action=GetPerformanceInformation"
            + "&serverId=CONTRACT-VSYS00001-S-0005"
            + "&interval=10minute"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetPerformanceInformation-response.xml"))
            .build();

      VirtualServerApi api = requestSendsResponse(request, response)
            .getVirtualServerApi();

      assertNotNull(api.getPerformanceInformation(
            "CONTRACT-VSYS00001-S-0005", "10minute"));
   }
*/
}
