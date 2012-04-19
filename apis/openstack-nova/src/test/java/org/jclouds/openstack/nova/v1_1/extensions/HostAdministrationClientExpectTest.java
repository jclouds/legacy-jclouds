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
package org.jclouds.openstack.nova.v1_1.extensions;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests HostAdministrationClient guice wiring and parsing
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "HostAdministrationClientExpectTest")
public class HostAdministrationClientExpectTest extends BaseNovaClientExpectTest {
   
   
   public void testList() throws Exception {
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/os-hosts");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();
      
      client.listHosts();
   }

   public void testGet() throws Exception {
      URI endpoint = URI.create("https://compute.north.host/v1.1/3456/os-hosts/xyz");
      HostAdministrationClient client = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().method("GET").headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
                  .endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).build()).getHostAdministrationExtensionForZone("az-1.region-a.geo-1").get();

      client.getHostResourceUsage("xyz");
   }

}
