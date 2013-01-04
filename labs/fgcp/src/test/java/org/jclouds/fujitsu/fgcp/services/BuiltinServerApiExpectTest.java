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
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerBackup;
import org.jclouds.fujitsu.fgcp.domain.BuiltinServerStatus;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "BuiltinServerApiExpectTest", singleThreaded = true)
public class BuiltinServerApiExpectTest extends BaseFGCPRestApiExpectTest {

   public void testStart() {
      HttpRequest request = buildGETWithQuery("Action=StartEFM"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/StartEFM-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.start("CONTRACT-VSYS00001-S-0001");
   }

   public void testStop() {
      HttpRequest request = buildGETWithQuery("Action=StopEFM"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/StopEFM-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.stop("CONTRACT-VSYS00001-S-0001");
   }

   public void testDestroy() {
      HttpRequest request = buildGETWithQuery("Action=DestroyEFM"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DestroyEFM-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.destroy("CONTRACT-VSYS00001-S-0001");
   }

   public void testGet() {
      HttpRequest request = buildGETWithQuery("Action=GetEFMAttributes"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetEFMAttributes-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      assertEquals(api.get("CONTRACT-VSYS00001-S-0001").getType(), BuiltinServer.BuiltinServerType.FW);
   }

/*
   public void testGetDetails() {
      HttpRequest request = buildGETWithQuery("Action=GetEFMConfiguration"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetEFMConfiguration-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

//      assertNotNull(api.getDetails("CONTRACT-VSYS00001-S-0001"));
   }
*/

   public void testGetStatus() {
      HttpRequest request = buildGETWithQuery("Action=GetEFMStatus"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetEFMStatus-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      assertEquals(api.getStatus("CONTRACT-VSYS00001-S-0001"), BuiltinServerStatus.RUNNING);
   }

   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateEFMAttribute"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&attributeName=vserverName"
            + "&attributeValue=new%20name"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UpdateEFMAttribute-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.update("CONTRACT-VSYS00001-S-0001", "vserverName", "new name");
   }

   public void testBackup() {
      HttpRequest request = buildGETWithQuery("Action=BackupEFM"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/BackupEFM-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.backup("CONTRACT-VSYS00001-S-0001");
   }

   public void testRestore() {
      HttpRequest request = buildGETWithQuery("Action=RestoreEFM"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&backupId=003"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/RestoreEFM-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.restore("CONTRACT-VSYS00001-S-0001", "003");
   }

   public void testListBackups() {
      HttpRequest request = buildGETWithQuery("Action=ListEFMBackup"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/ListEFMBackup-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      Set<BuiltinServerBackup> backups = api.listBackups("CONTRACT-VSYS00001-S-0001");
      assertNotNull(backups, "backups");
      assertEquals(backups.size(), 2);
      assertEquals(backups.iterator().next().getId(), "001");
      assertEquals(backups.iterator().next().getTime(), "20121008201127");
   }

   public void testDestroyBackup() {
      HttpRequest request = buildGETWithQuery("Action=DestroyEFMBackup"
            + "&efmId=CONTRACT-VSYS00001-S-0001"
            + "&backupId=003"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DestroyEFMBackup-response.xml"))
            .build();

      BuiltinServerApi api = requestSendsResponse(request, response)
            .getFirewallApi();

      api.destroyBackup("CONTRACT-VSYS00001-S-0001", "003");
   }

}
