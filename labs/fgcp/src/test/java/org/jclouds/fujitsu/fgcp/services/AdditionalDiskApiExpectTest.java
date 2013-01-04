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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "AdditionalDiskApiExpectTest", singleThreaded = true)
public class AdditionalDiskApiExpectTest extends BaseFGCPRestApiExpectTest {

   public void testGet() {
      HttpRequest request = buildGETWithQuery("Action=GetVDiskAttributes"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVDiskAttributes-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      assertEquals(api.get("CONTRACT-VSYS00001-D-0001").getSize(), 10.0);
   }

   public void testGetStatus() {
      HttpRequest request = buildGETWithQuery("Action=GetVDiskStatus"
            + "&vdiskId=CONTRACT-VSYS00001-S-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVDiskStatus-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      // api is returning STOPPED which is not a documented status. Documentation error?
//      assertEquals(api.getStatus("CONTRACT-VSYS00001-S-0001"), VDiskStatus.STOPPED);
   }

   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateVDiskAttribute"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&attributeName=updateName" + "&attributeValue=new-name"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UpdateVDiskAttribute-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.update("CONTRACT-VSYS00001-D-0001", "updateName", "new-name");
   }

   public void testDestroy() {
      HttpRequest request = buildGETWithQuery("Action=DestroyVDisk"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DestroyVDisk-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.destroy("CONTRACT-VSYS00001-D-0001");
   }

   public void testBackup() {
      HttpRequest request = buildGETWithQuery("Action=BackupVDisk"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/BackupVDisk-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.backup("CONTRACT-VSYS00001-D-0001");
   }

   public void testRestore() {
      HttpRequest request = buildGETWithQuery("Action=RestoreVDisk"
            + "&vsysId=CONTRACT-VSYS00001"
            + "&backupId=003");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/RestoreVDisk-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.restore("CONTRACT-VSYS00001", "003");
   }

   public void testDetach() {
      HttpRequest request = buildGETWithQuery("Action=DetachVDisk"
            + "&vdiskId=CONTRACT-VSYS00001-D-0001"
            + "&vserverId=CONTRACT-VSYS00001-S-0006"
            + "&vsysId=CONTRACT-VSYS00001");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DetachVDisk-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.detach("CONTRACT-VSYS00001-D-0001", "CONTRACT-VSYS00001-S-0006");
   }

   public void testDestroyBackup() {
      HttpRequest request = buildGETWithQuery("Action=DestroyVDiskBackup"
            + "&vsysId=CONTRACT-VSYS00001"
            + "&backupId=003");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/DestroyVDiskBackup-response.xml"))
            .build();

      AdditionalDiskApi api = requestSendsResponse(request, response)
            .getAdditionalDiskApi();

      api.destroyBackup("CONTRACT-VSYS00001", "003");
   }

}
