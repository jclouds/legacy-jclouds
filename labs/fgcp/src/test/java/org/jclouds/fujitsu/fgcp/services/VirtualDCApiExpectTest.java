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

import java.util.Map;
import java.util.Set;

import org.jclouds.fujitsu.fgcp.domain.AddressRange;
import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.fujitsu.fgcp.domain.EventLog;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.ServerType;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

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

      VirtualDCApi api = requestSendsResponse(request, response).getVirtualDCApi();

      Set<VSystem> vsysSet = api.listVirtualSystems();
      assertEquals(vsysSet.size(), 2);
   }

   public void testCreateVirtualSystem() {
      HttpRequest request = buildGETWithQuery("Action=CreateVSYS&vsysDescriptorId=myDescId&vsysName=myVSYS");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/CreateVSYS-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      String vsysId = api.createVirtualSystem("myDescId", "myVSYS");
      assertEquals(vsysId, "CONTRACT-VSYS00001", "vsysId: " + vsysId);
   }


   public void testListServerTypes() {
      HttpRequest request = buildGETWithQuery("Action=ListServerType&diskImageId=dummy");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/ListServerType-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Set<ServerType> serverTypes = api.listServerTypes();
      assertNotNull(serverTypes, "serverTypes");
      assertEquals(serverTypes.size(), 4,
            "Unexpected number of server types: " + serverTypes.size());
   }

   public void testListPublicIPs() {
      HttpRequest request = buildGETWithQuery("Action=ListPublicIP");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/ListPublicIP-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Map<PublicIP, String> ips = api.listPublicIPs();

      assertNotNull(ips, "ips");
      assertEquals(ips.size(), 2, "Unexpected number of ips: " + ips.size());
      assertEquals(ips.keySet().size(), 2, "Unexpected number of ips: " + ips.size());
      assertTrue(ips.containsValue("ABCDEFGH-A123B456CE"), "missing system id");
      assertEquals(ips.keySet().iterator().next().getVersion(), PublicIP.Version.IPv4);
   }

   public void testListDiskImages() {
      HttpRequest request = buildGETWithQuery("Action=ListDiskImage");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/ListDiskImages-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Set<DiskImage> images = api.listDiskImages();

      assertNotNull(images, "images");
      assertTrue(images.size() > 5, "Unexpected number of images: " + images.size());
   }

   public void testListDiskImage() {
      HttpRequest request = buildGETWithQuery("Action=ListDiskImage&vsysDescriptorId=IMG_A1B2C3_1234567890ABCD");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/ListDiskImage-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Set<DiskImage> images = api.listDiskImages(null, "IMG_A1B2C3_1234567890ABCD");

      assertNotNull(images, "images");
      assertEquals(1, images.size(), "Unexpected number of images: " + images.size());
   }

   public void testGetAddressRange() {
      HttpRequest request = buildGETWithQuery("Action=GetAddressRange");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/GetAddressRange-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Set<AddressRange> range = api.getAddressRange();
      assertNotNull(range);
      assertEquals(range.size(), 1);
   }

   public void testAddAddressRange() {
      HttpRequest request = buildGETWithQuery("Action=AddAddressRange"
            + "&pipFrom=192.168.0.0"
            + "&pipTo=192.168.30.0");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/AddAddressRange-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      api.addAddressRange("192.168.0.0", "192.168.30.0");
   }

   public void testDeleteAddressRange() {
      HttpRequest request = buildGETWithQuery("Action=DeleteAddressRange"
            + "&pipFrom=192.168.0.0"
            + "&pipTo=192.168.30.0");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/DeleteAddressRange-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      api.deleteAddressRange("192.168.0.0", "192.168.30.0");
   }

   public void testCreateAddressPool() {
      HttpRequest request = buildGETWithQuery("Action=CreateAddressPool"
            + "&pipFrom=192.168.0.0"
            + "&pipTo=192.168.30.0");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/CreateAddressPool-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      api.createAddressPool("192.168.0.0", "192.168.30.0");
   }

   public void testGetEventLog() {
      HttpRequest request = buildGETWithQuery("Action=GetEventLog");
      HttpResponse response = HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/GetEventLog-response.xml"))
            .build();
      VirtualDCApi api = requestSendsResponse(request, response)
            .getVirtualDCApi();

      Set<EventLog> logs = api.getEventLogs();
      assertNotNull(logs);
      //TODO: get one with several
//      assertEquals(logs.size(), 1);
   }



}
