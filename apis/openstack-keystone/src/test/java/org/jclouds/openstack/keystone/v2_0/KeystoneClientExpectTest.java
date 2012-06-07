/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.openstack.keystone.v2_0.parse.ParseRackspaceApiMetadataTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 *
 * @author Adrian Cole
 */
@Test(testName = "KeystoneClientExpectTest")
public class KeystoneClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {

   public void testGetApiMetaData() {
      KeystoneClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            HttpRequest.builder().method("GET").endpoint(URI.create(endpoint + "/v2.0/")).
            headers(ImmutableMultimap.of("Accept", APPLICATION_JSON)).build(),
            HttpResponse.builder().statusCode(200).
                  payload(payloadFromResourceWithContentType("/raxVersion.json", APPLICATION_JSON)).build());
      ApiMetadata metadata = client.getApiMetadata();

      assertEquals(metadata, new ParseRackspaceApiMetadataTest().expected());
   }

   public void testGetApiMetaDataFailNotFound() {
      KeystoneClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/").headers(ImmutableMultimap.of("Accept", APPLICATION_JSON)).build(),
            standardResponseBuilder(404).build());
      assertNull(client.getApiMetadata());
   }

}

