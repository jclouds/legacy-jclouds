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
package org.jclouds.joyent.cloudapi.v6_5.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.internal.BaseJoyentCloudApiExpectTest;
import org.jclouds.joyent.cloudapi.v6_5.parse.ParsePackageListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "PackageApiExpectTest")
public class PackageApiExpectTest extends BaseJoyentCloudApiExpectTest {
   public HttpRequest list = HttpRequest.builder().method("GET")
                                        .endpoint("https://us-sw-1.api.joyentcloud.com/my/packages")
                                        .addHeader("X-Api-Version", "~6.5")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();
   
   public HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/package_list.json")).build();
   
   public void testListPackagesWhenResponseIs2xx() {

      JoyentCloudApi apiWhenPackagesExists = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(apiWhenPackagesExists.getPackageApiForDatacenter("us-sw-1").list(), new ParsePackageListTest().expected());
   }

   public void testListPackagesWhenResponseIs404() {
      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      JoyentCloudApi listWhenNone = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(listWhenNone.getPackageApiForDatacenter("us-sw-1").list(), ImmutableSet.of());
   }
}
