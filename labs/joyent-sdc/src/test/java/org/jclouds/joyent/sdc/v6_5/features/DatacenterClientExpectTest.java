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
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DatacenterClientExpectTest")
public class DatacenterClientExpectTest extends BaseSDCClientExpectTest {
   HttpRequest getDatacenters = HttpRequest.builder()
                                           .method("GET")
                                           .endpoint(URI.create("https://api.joyentcloud.com/my/datacenters"))
                                           .headers(ImmutableMultimap.<String, String> builder()
                                                    .put("X-Api-Version", "~6.5")
                                                    .put("Accept", "application/json")
                                                    .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                                           .build();

   public void testGetDatacentersWhenResponseIs2xx() {
      HttpResponse getDatacentersResponse = HttpResponse.builder()
                                                        .statusCode(200)
                                                        .payload(payloadFromResource("/datacenters.json")).build();
      
      SDCClient clientWhenDatacentersExists = requestSendsResponse(getDatacenters, getDatacentersResponse);
      
      assertEquals(clientWhenDatacentersExists.getDatacenterClient().getDatacenters(), 
               ImmutableMap.<String, URI> builder()
                  .put("us-east-1", URI.create("https://us-east-1.api.joyentcloud.com"))
                  .put("us-west-1", URI.create("https://us-west-1.api.joyentcloud.com"))
                  .put("us-sw-1", URI.create("https://us-sw-1.api.joyentcloud.com"))
                  .put("eu-ams-1", URI.create("https://eu-ams-1.api.joyentcloud.com")).build());
   }

   public void testGetDatacentersWhenResponseIs404() {
      HttpResponse getDatacentersResponse = HttpResponse.builder().statusCode(404).build();
      
      SDCClient getDatacentersWhenNone = requestSendsResponse(getDatacenters, getDatacentersResponse);
      
      assertEquals(getDatacentersWhenNone.getDatacenterClient().getDatacenters(), ImmutableMap.of());
   }
}
