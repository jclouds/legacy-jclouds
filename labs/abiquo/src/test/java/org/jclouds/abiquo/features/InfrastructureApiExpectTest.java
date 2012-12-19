/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.abiquo.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypesDto;

/**
 * xpect tests for the {@link InfrastructureApi} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "InfrastructureApiExpectTest")
public class InfrastructureApiExpectTest extends BaseAbiquoRestApiExpectTest<InfrastructureApi> {

   public void testListNetworkServiceTypesReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes")) //
                  .addHeader("Authorization", basicAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypesDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-list.xml",
                              normalize(NetworkServiceTypesDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypesDto nsts = api.listNetworkServiceTypes(datacenter);
      assertEquals(nsts.getCollection().size(), 2);
      assertEquals(nsts.getCollection().get(0).getName(), "Service Network");
      assertEquals(nsts.getCollection().get(1).getName(), "Storage Network");
   }

   public void testGetNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
                  .addHeader("Authorization", basicAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypeDto created = api.getNetworkServiceType(datacenter, 1);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
      assertEquals(created.isDefaultNST(), true);
   }

   public void testGetNetworkServiceTypeReturns4xx() {
      InfrastructureApi api = requestSendsResponse(HttpRequest.builder() //
            .method("GET") //
            .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
            .addHeader("Authorization", basicAuth) //
            .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE)) //
            .build(), //
            HttpResponse.builder().statusCode(404).build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      assertNull(api.getNetworkServiceType(datacenter, 1));
   }

   public void testCreateNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("POST") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes")) //
                  .addHeader("Authorization", basicAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE))
                  //
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-create.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(201)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      DatacenterDto datacenter = new DatacenterDto();
      datacenter.addLink(new RESTLink("networkservicetypes",
            "http://localhost/api/admin/datacenters/1/networkservicetypes"));

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      nst.setName("Service Network");
      nst.setDefaultNST(true);

      NetworkServiceTypeDto created = api.createNetworkServiceType(datacenter, nst);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
      assertEquals(created.isDefaultNST(), true);
   }

   public void testUpdateNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("PUT") //
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
                  .addHeader("Authorization", basicAuth) //
                  .addHeader("Accept", normalize(NetworkServiceTypeDto.MEDIA_TYPE))
                  //
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/nst-edit.xml",
                              normalize(NetworkServiceTypeDto.MEDIA_TYPE))) //
                  .build());

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      RESTLink editLink = new RESTLink("edit", "http://localhost/api/admin/datacenters/1/networkservicetypes/1");
      editLink.setType(NetworkServiceTypeDto.BASE_MEDIA_TYPE);
      nst.addLink(editLink);
      nst.setId(1);
      nst.setDefaultNST(true);
      nst.setName("Service Network");

      NetworkServiceTypeDto created = api.updateNetworkServiceType(nst);
      assertNotNull(created.getId());
      assertEquals(created.getName(), "Service Network");
   }

   public void testDeleteNetworkServiceTypeReturns2xx() {
      InfrastructureApi api = requestSendsResponse(HttpRequest.builder() //
            .method("DELETE") //
            .endpoint(URI.create("http://localhost/api/admin/datacenters/1/networkservicetypes/1")) //
            .addHeader("Authorization", basicAuth) //
            .build(), //
            HttpResponse.builder().statusCode(204).build());

      NetworkServiceTypeDto nst = new NetworkServiceTypeDto();
      RESTLink editLink = new RESTLink("edit", "http://localhost/api/admin/datacenters/1/networkservicetypes/1");
      editLink.setType(NetworkServiceTypeDto.BASE_MEDIA_TYPE);
      nst.addLink(editLink);

      api.deleteNetworkServiceType(nst);
   }

   @Override
   protected InfrastructureApi clientFrom(AbiquoApi api) {
      return api.getInfrastructureApi();
   }

}
