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

import static org.testng.Assert.assertNotNull;

import org.jclouds.fujitsu.fgcp.domain.VSystemDescriptor;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Dies Koper
 */
@Test(groups = "unit", testName = "SystemTemplateApiExpectTest", singleThreaded = true)
public class SystemTemplateApiExpectTest extends BaseFGCPRestApiExpectTest {

   public void testGet() {
      HttpRequest request = buildGETWithQuery("Action=GetVSYSDescriptorConfiguration"
            + "&vsysDescriptorId=3-tier%20Skeleton");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/GetVSYSDescriptorConfiguration-response.xml"))
            .build();
      SystemTemplateApi client = requestSendsResponse(request, response)
            .getSystemTemplateApi();

      VSystemDescriptor desc = client.get("3-tier Skeleton");
      assertNotNull(desc, "desc");
//      assertEquals(desc.)
   }


   public void testUpdate() {
      HttpRequest request = buildGETWithQuery("Action=UpdateVSYSDescriptorAttribute"
            + "&vsysDescriptorId=3-tier%20Skeleton"
            + "&updateLcId=en" + "&attributeName=updateName"
            + "&attributeValue=new-name");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UpdateVSYSDescriptorAttribute-response.xml"))
            .build();

      SystemTemplateApi api = requestSendsResponse(request, response)
            .getSystemTemplateApi();

      api.update("3-tier Skeleton", "en", "updateName", "new-name");
   }

   public void testDeregister() {
      HttpRequest request = buildGETWithQuery("Action=UnregisterVSYSDescriptor"
            + "&vsysDescriptorId=3-tier%20Skeleton");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UnregisterVSYSDescriptor-response.xml"))
            .build();

      SystemTemplateApi api = requestSendsResponse(request, response)
            .getSystemTemplateApi();

      api.deregister("3-tier Skeleton");
   }

   public void testDeregisterPrivateTemplate() {
      HttpRequest request = buildGETWithQuery("Action=UnregisterPrivateVSYSDescriptor"
            + "&vsysDescriptorId=3-tier%20Skeleton");
      HttpResponse response = HttpResponse
            .builder()
            .statusCode(200)
            .payload(
                  payloadFromResource("/UnregisterPrivateVSYSDescriptor-response.xml"))
            .build();

      SystemTemplateApi api = requestSendsResponse(request, response)
            .getSystemTemplateApi();

      api.deregisterPrivateTemplate("3-tier Skeleton");
   }
}
