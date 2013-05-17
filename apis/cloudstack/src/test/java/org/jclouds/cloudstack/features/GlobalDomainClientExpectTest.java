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

import static org.jclouds.cloudstack.options.UpdateDomainOptions.Builder.name;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * Test the CloudStack GlobalDomainClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "GlobalDomainClientExpectTest")
public class GlobalDomainClientExpectTest extends BaseCloudStackExpectTest<GlobalDomainClient> {

   public void testCreateDomainWhenResponseIs2xx() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createDomain&" +
                  "name=test&apiKey=identity&signature=6cxzEo7h63G0hgTTMLm4lGsSDK8%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createdomainresponse.json"))
            .build());

      assertEquals(client.createDomain("test"),
         Domain.builder().id("10").name("test").level(1).parentDomainId("1")
            .parentDomainName("ROOT").hasChild(false).build());
   }

   public void testCreateDomainWhenResponseIs404() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createDomain&" +
                  "name=test&apiKey=identity&signature=6cxzEo7h63G0hgTTMLm4lGsSDK8%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.createDomain("test"));
   }

   public void testUpdateDomainWhenResponseIs2xx() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=updateDomain&id=10&name=test-2&apiKey=identity&signature=5t1eUf2Eyf/aB6qt%2BqIj%2BmcwFIo%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/updatedomainresponse.json"))
            .build());

      assertEquals(client.updateDomain("10", name("test-2")),
         Domain.builder().id("10").name("test-2").level(1).parentDomainId("1")
            .parentDomainName("ROOT").hasChild(false).build());
   }

   public void testUpdateDomainWhenResponseIs404() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=updateDomain&id=10&name=test-2&apiKey=identity&signature=5t1eUf2Eyf/aB6qt%2BqIj%2BmcwFIo%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.updateDomain("10", name("test-2")));
   }

   public void testDeleteOnlyDomain() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deleteDomain&cleanup=false&id=1&apiKey=identity&signature=/5aLbigg612t9IrZi0JZO7CyiOU%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deletedomainresponse.json"))
            .build());

      client.deleteOnlyDomain("1");
   }

   public void testDeleteDomainAndAttachedResources() {
      GlobalDomainClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=deleteDomain&cleanup=true&id=1&apiKey=identity&signature=grL7JStvtYUT89Jr0D8FgwMyJpU%3D"))
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/deletedomainresponse.json"))
            .build());

      client.deleteDomainAndAttachedResources("1");
   }

   @Override
   protected GlobalDomainClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getDomainClient();
   }
}
