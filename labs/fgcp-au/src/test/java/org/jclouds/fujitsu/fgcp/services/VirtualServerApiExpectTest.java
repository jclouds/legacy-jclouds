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
}
