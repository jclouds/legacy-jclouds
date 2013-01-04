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

import org.jclouds.fujitsu.fgcp.domain.DiskImage;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "DiskImageApiExpectTest", singleThreaded = true)
public class DiskImageApiExpectTest extends BaseFGCPRestApiExpectTest {

   public void testGet() {
      HttpRequest request = buildGETWithQuery("Action=GetDiskImageAttributes"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/GetDiskImageAttributes-response.xml"))
            .build();

      DiskImageApi api = requestSendsResponse(request, response)
            .getDiskImageApi();

      DiskImage image = api.get("IMG_A1B2C3_1234567890ABCD");

      assertEquals(image.getId(), "IMG_A1B2C3_1234567890ABCD");
      assertEquals(image.getCreatorName(), "ABCDEFGH");
   }

   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateDiskImageAttribute"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD"
            + "&updateLcId=en"
            + "&attributeName=updateName"
            + "&attributeValue=new-name");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/UpdateDiskImageAttribute-response.xml"))
            .build();

      DiskImageApi api = requestSendsResponse(request, response)
            .getDiskImageApi();

      api.update("IMG_A1B2C3_1234567890ABCD", "en", "updateName", "new-name");
   }

   public void testDeregister() {
      HttpRequest request = buildGETWithQuery("Action=UnregisterDiskImage"
            + "&diskImageId=IMG_A1B2C3_1234567890ABCD");
      HttpResponse response = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/UnregisterDiskImage-response.xml"))
            .build();

      DiskImageApi api = requestSendsResponse(request, response)
            .getDiskImageApi();

      api.deregister("IMG_A1B2C3_1234567890ABCD");
   }

}
