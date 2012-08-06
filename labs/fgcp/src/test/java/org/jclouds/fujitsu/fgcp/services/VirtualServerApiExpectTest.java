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

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.Assert.assertEquals;

import org.jclouds.fujitsu.fgcp.domain.VServerStatus;
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
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/StartVServer-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        serverApi.start("CONTRACT-VSYS00001-S-0001");
    }

    public void testStop() {
        HttpRequest request = buildGETWithQuery("Action=StopVServer"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/StopVServer-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        serverApi.stop("CONTRACT-VSYS00001-S-0001");
    }

    public void testDestroy() {
        HttpRequest request = buildGETWithQuery("Action=DestroyVServer"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/DestroyVServer-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        serverApi.destroy("CONTRACT-VSYS00001-S-0001");
    }

    public void testGet() {
        HttpRequest request = buildGETWithQuery("Action=GetVServerAttributes"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVServerAttributes-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        assertNotNull(serverApi.get("CONTRACT-VSYS00001-S-0001"));
    }

    public void testGetDetails() {
        HttpRequest request = buildGETWithQuery("Action=GetVServerConfiguration"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVServerConfiguration-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        assertNotNull(serverApi.getDetails("CONTRACT-VSYS00001-S-0001"));
    }

    public void testGetStatus() {
        HttpRequest request = buildGETWithQuery("Action=GetVServerStatus"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVServerStatus-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        assertEquals(serverApi.getStatus("CONTRACT-VSYS00001-S-0001"), VServerStatus.STOPPED);
    }

    public void testInitialPassword() {
        HttpRequest request = buildGETWithQuery("Action=GetVServerInitialPassword"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVServerInitialPassword-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        assertEquals(serverApi.getInitialPassword("CONTRACT-VSYS00001-S-0001"), "mySecretpwd1");
    }

    public void testUpdate() {
        HttpRequest request = buildGETWithQuery("Action=UpdateVServerAttribute"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&attributeValue=new%20name"
                + "&attributeName=vserverName"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/UpdateVServerAttribute-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        serverApi.update("CONTRACT-VSYS00001-S-0001", "vserverName", "new name");
    }

    public void testAttachDisk() {
        HttpRequest request = buildGETWithQuery("Action=AttachVDisk"
                + "&vserverId=CONTRACT-VSYS00001-S-0001"
                + "&vdiskId=CONTRACT-VSYS00001-D-0001"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/AttachVDisk-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        serverApi.attachDisk("CONTRACT-VSYS00001-S-0001", "CONTRACT-VSYS00001-D-0001");
    }

/*    public void testGetPerformanceInformation() {
        HttpRequest request = buildGETWithQuery("Action=GetPerformanceInformation"
                + "&serverId=CONTRACT-VSYS00001-S-0001"
                + "&interval=10minute"
                + "&vsysId=CONTRACT-VSYS00001");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetPerformanceInformation-response.xml"))
                .build();

        VirtualServerApi serverApi = requestSendsResponse(request, response)
                .getVirtualServerApi();

        assertNotNull(serverApi.getPerformanceInformation(
                "CONTRACT-VSYS00001-S-0001", "10minute"));
    }
*/
}
