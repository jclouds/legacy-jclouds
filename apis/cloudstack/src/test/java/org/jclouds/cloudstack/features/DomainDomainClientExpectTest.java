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
package org.jclouds.cloudstack.features;

import static org.jclouds.cloudstack.options.ListDomainChildrenOptions.Builder.parentDomainId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack DomainDomainClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "DomainDomainClientExpectTest")
public class DomainDomainClientExpectTest extends BaseCloudStackExpectTest<DomainDomainClient> {

   public void testListDomainsWhenResponseIs2xx() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                          "command=listDomains&listAll=true&apiKey=identity&signature=sVFaGTu0DNSTVtWy3wtRt7KTx0w%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listdomainsresponse.json"))
            .build());

      assertEquals(client.listDomains(),
         ImmutableSet.of(
            Domain.builder().id("1").name("ROOT").level(0).hasChild(true).build(),
            Domain.builder().id("2").name("jclouds1").level(1).parentDomainId("1")
               .parentDomainName("ROOT").hasChild(false).build()
         ));
   }

   public void testListDomainsWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                          "command=listDomains&listAll=true&apiKey=identity&signature=sVFaGTu0DNSTVtWy3wtRt7KTx0w%3D"))
            .addHeader("Accept", "application/json")
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
                          "command=listDomains&listAll=true&id=1&apiKey=identity&signature=M16YxHWKST/cIRUHvWhfWovJugU%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getdomainresponse.json"))
            .build());

      assertEquals(client.getDomainById("1"),
         Domain.builder().id("1").name("ROOT").level(0).hasChild(true).build());
   }

   public void testGetDomainWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                          "command=listDomains&listAll=true&id=1&apiKey=identity&signature=M16YxHWKST/cIRUHvWhfWovJugU%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.getDomainById("1"));
   }

   public void testListDomainChildrenWhenResponseIs2xx() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                          "command=listDomainChildren&listAll=true&id=1&isrecursive=true&apiKey=identity&signature=Jn6kFkloRvfaaivlJiHd0F5J3Jk%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listdomainchildrenresponse.json"))
            .build());

      assertEquals(client.listDomainChildren(parentDomainId("1").isRecursive(true)),
         ImmutableSet.of(
            Domain.builder().id("2").name("jclouds1").level(1).parentDomainId("1")
               .parentDomainName("ROOT").hasChild(false).build(),
            Domain.builder().id("3").name("jclouds2").level(1).parentDomainId("1")
               .parentDomainName("ROOT").hasChild(false).build()
         ));
   }

   public void testListDomainChildrenWhenResponseIs404() {
      DomainDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listDomainChildren&listAll=true&id=1&isrecursive=true&apiKey=identity&" +
                          "signature=Jn6kFkloRvfaaivlJiHd0F5J3Jk%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listDomainChildren(parentDomainId("1").isRecursive(true)), ImmutableSet.of());
   }

   @Override
   protected DomainDomainClient clientFrom(CloudStackContext context) {
      return context.getDomainApi().getDomainClient();
   }
}
