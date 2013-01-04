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

import java.util.Set;

import org.jclouds.fujitsu.fgcp.domain.BuiltinServer;
import org.jclouds.fujitsu.fgcp.domain.PublicIP;
import org.jclouds.fujitsu.fgcp.domain.VDisk;
import org.jclouds.fujitsu.fgcp.domain.VServer;
import org.jclouds.fujitsu.fgcp.domain.VSystem;
import org.jclouds.fujitsu.fgcp.domain.VSystemStatus;
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
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      VSystem system = api.get("ABCDEFGH-A123B456CE");
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
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      VSystem system = api.getDetails("ABCDEFGH-A123B456CE");
      assertNotNull(system, "system");
   }

   public void testGetStatus() {
      HttpRequest request = buildGETWithQuery("Action=GetVSYSStatus"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/GetVSYSStatus-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      assertEquals(api.getStatus("ABCDEFGH-A123B456CE"), VSystemStatus.NORMAL);
   }

   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateVSYSAttribute"
            + "&vsysId=ABCDEFGH-A123B456CE"
            + "&attributeName=updateName" + "&attributeValue=new-name");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UpdateVDiskAttribute-response.xml"))
            .build();

      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      api.update("ABCDEFGH-A123B456CE", "updateName", "new-name");
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
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      Set<PublicIP> ips = api.listPublicIPs("ABCDEFGH-A123B456CE");
      assertNotNull(ips, "ips");
      assertEquals(2, ips.size(), "Unexpected number of ips: " + ips.size());
      assertEquals(ips.iterator().next().getVersion(), PublicIP.Version.IPv4);
   }

   public void testListServers() {
      HttpRequest request = buildGETWithQuery("Action=ListVServer"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/ListVServer-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      Set<VServer> servers = api.listServers("ABCDEFGH-A123B456CE");
      assertNotNull(servers, "servers");
      assertEquals(servers.size(), 2);
   }

   public void testDisks() {
      HttpRequest request = buildGETWithQuery("Action=ListVDisk"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/ListVDisk-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      Set<VDisk> disks = api.listDisks("ABCDEFGH-A123B456CE");
      assertNotNull(disks, "disks");
      assertEquals(disks.size(), 1);
   }

   public void testListBuiltinServers() {
      HttpRequest request = buildGETWithQuery("Action=ListEFM"
            + "&vsysId=ABCDEFGH-A123B456CE" + "&efmType=FW");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/ListEFM-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      Set<BuiltinServer> fws = api.listBuiltinServers("ABCDEFGH-A123B456CE", "FW");
      assertNotNull(fws, "fws");
      assertEquals(fws.size(), 1);
   }

   public void testAllocatePublicIP() {
      HttpRequest request = buildGETWithQuery("Action=AllocatePublicIP&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/AllocatePublicIP-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      api.allocatePublicIP("ABCDEFGH-A123B456CE");
   }

   public void testCreateBuiltinServer() {
      HttpRequest request = buildGETWithQuery("Action=CreateEFM"
            + "&efmType=SLB"
            + "&efmName=web%20load%20balancer"
            + "&networkId=ABCDEFGH-A123B456CE-N-DMZ"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/CreateEFM-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      String id = api.createBuiltinServer("web load balancer",
            "ABCDEFGH-A123B456CE-N-DMZ");
      assertEquals(id, "CONTRACT-EFM00001");
   }

   public void testCreateServer() {
      HttpRequest request = buildGETWithQuery("Action=CreateVServer"
            + "&vserverName=vm1"
            + "&vserverType=economy"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD"
            + "&networkId=ABCDEFGH-A123B456CE-N-DMZ"
            + "&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/CreateVServer-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      String id = api.createServer("vm1", "economy", "IMG_A1B2C3_1234567890ABCD",
            "ABCDEFGH-A123B456CE-N-DMZ");
      assertEquals(id, "ABCDEFGH-A123B456CE-S-0007");
   }

   public void testCreateDisk() {
      HttpRequest request = buildGETWithQuery("Action=CreateVDisk"
            + "&vsysId=ABCDEFGH-A123B456CE" + "&vdiskName=disk1"
            + "&size=10");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/CreateVDisk-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      String id = api.createDisk("ABCDEFGH-A123B456CE", "disk1", 10);
      assertEquals(id, "ABCDEFGH-A123B456CE-S-0006");
   }

/*
   public void testRegisterAsPrivateImage() {
      HttpRequest request = buildGETWithQuery("Action=AllocatePublicIP&vsysId=ABCDEFGH-A123B456CE");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/AllocatePublicIP-response.xml"))
            .build();
      VirtualSystemApi api = requestSendsResponse(request, response)
            .getVirtualSystemApi();

      api.registerAsPrivateVSYSDescriptor("ABCDEFGH-A123B456CE");
   }
*/
}
