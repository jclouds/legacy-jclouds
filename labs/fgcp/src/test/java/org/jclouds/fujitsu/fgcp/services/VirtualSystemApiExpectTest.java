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
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "VirtualSystemApiExpectTest", singleThreaded = true)
public class VirtualSystemApiExpectTest extends BaseFGCPRestApiExpectTest {

    public void testGet() {
        HttpRequest request = buildGETWithQuery("Action=GetVSYSAttributes&vsysId=ABCDEFGH-A123B456CE");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVSYSAttributes-response.xml"))
                .build();
        VirtualSystemApi client = requestSendsResponse(request, response)
                .getVirtualSystemApi();

        VSystem system = client.get("ABCDEFGH-A123B456CE");
        assertNotNull(system, "system");
    }

    public void testGetDetails() {
        HttpRequest request = buildGETWithQuery("Action=GetVSYSConfiguration&vsysId=ABCDEFGH-A123B456CE");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/GetVSYSConfiguration-response.xml"))
                .build();
        VirtualSystemApi client = requestSendsResponse(request, response)
                .getVirtualSystemApi();

        VSystem system = client.getDetails("ABCDEFGH-A123B456CE");
        assertNotNull(system, "system");
    }

    public void testListPublicIPs() {
        HttpRequest request = buildGETWithQuery("Action=ListPublicIP"
                + "&vsysId=ABCDEFGH-A123B456CE");
        HttpResponse response = HttpResponse
                .builder()
                .statusCode(200)
                .payload(
                        payloadFromResource("/ListPublicIP_one_vsys-response.xml"))
                .build();
        VirtualSystemApi client = requestSendsResponse(request, response)
                .getVirtualSystemApi();

        Set<PublicIP> ips = client.listPublicIPs("ABCDEFGH-A123B456CE");
        assertNotNull(ips, "ips");
        assertTrue(ips.size() == 2, "Unexpected number of ips: " + ips.size());
        assertEquals(ips.iterator().next().getVersion(), PublicIP.Version.IPv4);
    }

}
