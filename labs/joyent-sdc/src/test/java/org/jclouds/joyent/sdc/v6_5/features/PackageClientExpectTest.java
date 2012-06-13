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
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.sdc.v6_5.SDCClient;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientExpectTest;
import org.jclouds.joyent.sdc.v6_5.parse.ParsePackageListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "PackageClientExpectTest")
public class PackageClientExpectTest extends BaseSDCClientExpectTest {
   HttpRequest listPackages = HttpRequest
         .builder()
         .method("GET")
         .endpoint(URI.create("https://us-sw-1.api.joyentcloud.com/my/packages"))
         .headers(
               ImmutableMultimap.<String, String> builder().put("X-Api-Version", "~6.5")
                     .put("Accept", "application/json").put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                     .build()).build();

   public void testListPackagesWhenResponseIs2xx() {
      HttpResponse listPackagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/package_list.json")).build();

      SDCClient clientWhenPackagesExists = requestsSendResponses(getDatacenters, getDatacentersResponse, listPackages, listPackagesResponse);

      assertEquals(clientWhenPackagesExists.getPackageClientForDatacenter("us-sw-1").listPackages(), new ParsePackageListTest().expected());
   }

   public void testListPackagesWhenResponseIs404() {
      HttpResponse listPackagesResponse = HttpResponse.builder().statusCode(404).build();

      SDCClient listPackagesWhenNone = requestsSendResponses(getDatacenters, getDatacentersResponse, listPackages, listPackagesResponse);

      assertEquals(listPackagesWhenNone.getPackageClientForDatacenter("us-sw-1").listPackages(), ImmutableSet.of());
   }
}
