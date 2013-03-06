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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.openstack.swift.v1.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.v1.SwiftApi;
import org.jclouds.openstack.swift.v1.domain.Account;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiExpectTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AccountApiExpectTest")
public class AccountApiExpectTest extends BaseSwiftApiExpectTest {
   
   public void testGetAccountMetadataWhenResponseIs2xx() throws Exception {

      HttpRequest get = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(204)
            .addHeader("X-Account-Container-Count", "3")
            .addHeader("X-Account-Bytes-Used", "323479").build();

      SwiftApi apiWhenExists = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, get, listResponse);

      assertEquals(
            apiWhenExists.getAccountApiForRegion("region-a.geo-1").get(),
            Account.builder().containerCount(3).bytesUsed(323479).build());
   }
   

}
