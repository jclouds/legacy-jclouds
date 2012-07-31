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

import java.net.URI;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "VirtualDCApiExpectTest", singleThreaded = true)
public class VirtualDCApiExpectTest extends BaseFGCPRestApiExpectTest {

    public void testListVirtualSystems() {
        HttpRequest request = buildGETWithQuery("Action=ListVSYS");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/ListVSYS-response.xml"))
                .build();

        VirtualDCApi client = requestSendsResponse(request, response).getVirtualDCApi();

        Set<? extends VSystem> vsysSet = client.listVirtualSystems();
        assertEquals(vsysSet.size(), 2);
    }

    public void testCreateVirtualSystem() {
        HttpRequest request = buildGETWithQuery("Action=CreateVSYS&vsysDescriptorId=myDescId&vsysName=myVSYS");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/CreateVSYS-response.xml"))
                .build();
        VirtualDCApi client = requestSendsResponse(request, response)
                .getVirtualDCApi();

        String vsysId = client.createVirtualSystem("myDescId", "myVSYS");
        assertEquals(vsysId, "CONTRACT-VSYS00001", "vsysId: " + vsysId);
    }


    public void testListServerTypes() {
        HttpRequest request = buildGETWithQuery("Action=ListServerType&diskImageId=dummy");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/ListServerType-response.xml"))
                .build();
        VirtualDCApi client = requestSendsResponse(request, response)
                .getVirtualDCApi();

        Set<ServerType> serverTypes = client.listServerTypes();
        assertNotNull(serverTypes, "serverTypes");
        assertEquals(serverTypes.size(), 4,
                "Unexpected number of server types: " + serverTypes.size());
//        System.out.println("return val: " + serverTypes);
    }

    public void testListPublicIPs() {
        HttpRequest request = buildGETWithQuery("Action=ListPublicIP");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/ListPublicIP-response.xml"))
                .build();
        VirtualDCApi client = requestSendsResponse(request, response)
                .getVirtualDCApi();

        Set<? extends PublicIP> ips = client.listPublicIPs();
        assertNotNull(ips, "ips");
        assertTrue(ips.size() > 1, "Unexpected number of ips: " + ips.size());
//        System.out.println("return val: " + ips);
    }

    public void testListDiskImages() {
        HttpRequest request = buildGETWithQuery("Action=ListDiskImage");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/ListDiskImages-response.xml"))
                .build();
        VirtualDCApi client = requestSendsResponse(request, response)
                .getVirtualDCApi();

        Set<? extends DiskImage> images = client.listDiskImages();
        assertNotNull(images, "images");
        assertTrue(images.size() > 5, "Unexpected number of images: " + images.size());
//        System.out.println("return val: " + images);
    }

    public void testListDiskImage() {
        HttpRequest request = buildGETWithQuery("Action=ListDiskImage&vsysDescriptorId=IMG_A1B2C3_1234567890ABCD");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/ListDiskImage-response.xml"))
                .build();
        VirtualDCApi client = requestSendsResponse(request, response)
                .getVirtualDCApi();

        Set<? extends DiskImage> images = client.listDiskImages(null, "IMG_A1B2C3_1234567890ABCD");
        assertNotNull(images, "images");
        assertTrue(images.size() == 1, "Unexpected number of images: " + images.size());
//        System.out.println("return val: " + images);
    }

/*    public void testGetDiskImageAttributes() {
        HttpRequest request = buildGETWithQuery("Action=GetDiskImageAttributes&diskImageId=IMG_A1B2C3_1234567890ABCD");
        HttpResponse response = HttpResponse.builder()
                .statusCode(200)
                .payload(payloadFromResource("/GetDiskImageAttributes-response.xml"))
                .build();
        client client = requestSendsResponse(request, response)
                .getVirtualDataCenter();

        DiskImage image = client.getDiskImageAttributes("IMG_A1B2C3_1234567890ABCD");

        assertNotNull(image, "image");
        System.out.println("return val: " + image);
    }
*/
    protected HttpRequest preparePOSTForAction(String action) {
        return HttpRequest
                .builder()
                .method("POST")
                .endpoint(
                        URI.create("https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint"))
                .payload(
                        payloadFromResourceWithContentType(
                                "/" + action.toLowerCase() + "-request.xml",
                                MediaType.TEXT_XML))
                .headers(
                        ImmutableMultimap.<String, String> builder()
                                .put("Accept", "text/xml")
                                .put("User-Agent", "OViSS-API-CLIENT").build())
                .build();
    }
}
