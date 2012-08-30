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
import static org.testng.Assert.assertNotNull;

import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.PublicIPStatus;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "PublicIPAddressApiExpectTest", singleThreaded = true)
public class PublicIPAddressApiExpectTest extends BaseFGCPRestApiExpectTest {

    public void testAttach() {
        HttpRequest request = buildGETWithQuery("Action=AttachPublicIP"
                + "&vsysId=CONTRACT-VSYS00001"
                + "&publicIp=123.45.67.89");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/AttachPublicIP-response.xml"))
                .build();

        PublicIPAddressApi api = requestSendsResponse(request, response)
                .getPublicIPAddressApi();

        api.attach("CONTRACT-VSYS00001", "123.45.67.89");
    }

    public void testDetach() {
        HttpRequest request = buildGETWithQuery("Action=DetachPublicIP"
                + "&vsysId=CONTRACT-VSYS00001"
                + "&publicIp=123.45.67.89");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/DetachPublicIP-response.xml"))
                .build();

        PublicIPAddressApi api = requestSendsResponse(request, response)
                .getPublicIPAddressApi();

        api.detach("CONTRACT-VSYS00001", "123.45.67.89");
    }

    public void testFree() {
        HttpRequest request = buildGETWithQuery("Action=FreePublicIP"
                + "&vsysId=CONTRACT-VSYS00001"
                + "&publicIp=123.45.67.89");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/FreePublicIP-response.xml"))
                .build();

        PublicIPAddressApi api = requestSendsResponse(request, response)
                .getPublicIPAddressApi();

        api.free("CONTRACT-VSYS00001", "123.45.67.89");
    }

    public void testGetStatus() {
        HttpRequest request = buildGETWithQuery("Action=GetPublicIPStatus"
                + "&publicIp=123.45.67.89");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/GetPublicIPStatus-response.xml"))
                .build();

        PublicIPAddressApi api = requestSendsResponse(request, response)
                .getPublicIPAddressApi();

        PublicIPStatus status = api.getStatus("123.45.67.89");
        assertEquals(status, PublicIPStatus.ATTACHED);
    }

    public void testGet() {
        HttpRequest request = buildGETWithQuery("Action=GetPublicIPAttributes"
                + "&publicIp=123.45.67.89");
        HttpResponse response = HttpResponse.builder().statusCode(200)
                .payload(payloadFromResource("/GetPublicIPAttributes-response.xml"))
                .build();

        PublicIPAddressApi api = requestSendsResponse(request, response)
                .getPublicIPAddressApi();

        PublicIP ip = api.get("123.45.67.89");

        assertNotNull(ip, "ip");
        assertEquals(ip.getAddress(), "123.45.67.89");
        assertEquals(ip.getVersion(), PublicIP.Version.IPv4);
    }

}
