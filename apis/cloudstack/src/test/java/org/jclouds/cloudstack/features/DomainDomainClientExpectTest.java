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
package org.jclouds.cloudstack.features;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.options.ListDomainChildrenOptions;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static org.jclouds.cloudstack.options.ListDomainChildrenOptions.Builder.parentDomainId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Test the CloudStack DomainDomainClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "DomainDomainClientExpectTest")
public class DomainDomainClientExpectTest extends BaseCloudStackRestClientExpectTest<DomainDomainClient> {

   public void testListDomainsWhenResponseIs2xx() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomains&apiKey=identity&signature=MmzRB%2FpKlYyWy7kE3IMXrg4BUtk%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listdomainsresponse.json"))
            .build());

      assertEquals(client.listDomains(),
         ImmutableSet.of(
            Domain.builder().id(1L).name("ROOT").level(0).hasChild(true).build(),
            Domain.builder().id(2L).name("jclouds1").level(1).parentDomainId(1)
               .parentDomainName("ROOT").hasChild(false).build()
         ));
   }

   public void testListDomainsWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomains&apiKey=identity&signature=MmzRB%2FpKlYyWy7kE3IMXrg4BUtk%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listDomains(), ImmutableSet.of());
   }

   public void testGetDomainWhenResponseIs2xx() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomains&id=1&apiKey=identity&signature=emQKWkVhospRkaUzjKljME2rW0k%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getdomainresponse.json"))
            .build());

      assertEquals(client.getDomainById(1),
         Domain.builder().id(1L).name("ROOT").level(0).hasChild(true).build());
   }

   public void testGetDomainWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomains&id=1&apiKey=identity&signature=emQKWkVhospRkaUzjKljME2rW0k%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.getDomainById(1));
   }

   public void testListDomainChildrenWhenResponseIs2xx() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomainChildren&id=1&isrecursive=true&apiKey=identity&signature=bDMSkjme8k0ANUPm4YiTYKe2N88%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listdomainchildrenresponse.json"))
            .build());

      assertEquals(client.listDomainChildren(parentDomainId(1).isRecursive(true)),
         ImmutableSet.of(
            Domain.builder().id(2L).name("jclouds1").level(1).parentDomainId(1)
               .parentDomainName("ROOT").hasChild(false).build(),
            Domain.builder().id(3L).name("jclouds2").level(1).parentDomainId(1)
               .parentDomainName("ROOT").hasChild(false).build()
         ));
   }

   public void testListDomainChildrenWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomainChildren&id=1&isrecursive=true&apiKey=identity&" +
                  "signature=bDMSkjme8k0ANUPm4YiTYKe2N88%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listDomainChildren(parentDomainId(1).isRecursive(true)), ImmutableSet.of());
   }

   @Override
   protected DomainDomainClient clientFrom(CloudStackContext context) {
      return context.getDomainContext().getApi().getDomainClient();
   }
}