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
import org.jclouds.joyent.cloudapi.v6_5.parse.ParseDatasetListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Gerald Pereira
 */
@Test(groups = "unit", testName = "DatasetApiExpectTest")
public class DatasetApiExpectTest extends BaseJoyentCloudApiExpectTest {
   public HttpRequest list = HttpRequest.builder().method("GET")
                                        .endpoint("https://us-sw-1.api.joyentcloud.com/my/datasets")
                                        .addHeader("X-Api-Version", "~6.5")
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build();
   
   public HttpResponse listResponse = HttpResponse.builder().statusCode(200)
                                                 .payload(payloadFromResource("/dataset_list.json")).build();
   
   public void testListDatasetsWhenResponseIs2xx() {

      JoyentCloudApi apiWhenDatasetsExists = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(apiWhenDatasetsExists.getDatasetApiForDatacenter("us-sw-1").list().toString(), new ParseDatasetListTest()
               .expected().toString());
   }

   public void testListDatasetsWhenResponseIs404() {
      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      JoyentCloudApi listWhenNone = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(listWhenNone.getDatasetApiForDatacenter("us-sw-1").list(), ImmutableSet.of());
   }
}
