/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.keystone.v2_0;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.jclouds.openstack.keystone.v2_0.parse.ParseRackspaceApiMetadataTest;
import org.testng.annotations.Test;

/**
 *
 * @author Adrian Cole
 */
@Test(testName = "KeystoneApiExpectTest")
public class KeystoneApiExpectTest extends BaseKeystoneRestApiExpectTest<KeystoneApi> {

   public void testGetApiMetaData() {
      KeystoneApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            HttpRequest.builder().method("GET").endpoint(endpoint + "/v2.0/").
            addHeader("Accept", APPLICATION_JSON).build(),
            HttpResponse.builder().statusCode(200).
                  payload(payloadFromResourceWithContentType("/raxVersion.json", APPLICATION_JSON)).build());
      ApiMetadata metadata = api.getApiMetadata();

      assertEquals(metadata, new ParseRackspaceApiMetadataTest().expected());
   }

   public void testGetApiMetaDataFailNotFound() {
      KeystoneApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
            HttpRequest.builder().method("GET").endpoint(endpoint + "/v2.0/").addHeader("Accept", APPLICATION_JSON).build(),
            HttpResponse.builder().statusCode(404).build());
      assertNull(api.getApiMetadata());
   }

}

