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

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack AccountClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "AccountClientExpectTest")
public class AccountClientExpectTest extends BaseCloudStackExpectTest<AccountClient> {


   public void testListAccountsWhenResponseIs2xx() {

      AccountClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost:8080/client/api?response=json&command=listAccounts&listAll=true&apiKey=identity&signature=yMZYMZxzFlaUsbfxtuppMwNhpXI%3D")
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listaccountsresponse.json"))
            .build());

      Set<User> users = ImmutableSet.of(
         User.builder()
            .id("505")
            .name("jclouds")
            .firstName("Adrian")
            .lastName("Cole")
            .email("adrian@jclouds.org")
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-04-19T01:57:24+0000"))
            .state(User.State.ENABLED)
            .account("jclouds")
            .accountType(Account.Type.USER)
            .domainId("457")
            .domain("AA000062-jclouds-dev")
            .apiKey("APIKEY")
            .secretKey("SECRETKEY").build());

      assertEquals(client.listAccounts(),
         ImmutableSet.of(Account.builder()
            .id("505")
            .name("jclouds")
            .type(Account.Type.USER)
            .domainId("457")
            .domain("AA000062-jclouds-dev")
            .receivedBytes(318900216)
            .sentBytes(23189677)
            .VMLimit(15l)
            .VMs(1)
            .IPsAvailable(14l)
            .IPLimit(15l)
            .IPs(0)
            .IPsAvailable(15l)
            .volumeLimit(90l)
            .volumes(2)
            .volumesAvailable(88l)
            .snapshotLimit(250l)
            .snapshots(0)
            .snapshotsAvailable(250l)
            .templateLimit(15l)
            .templates(0)
            .templatesAvailable(15l)
            .VMsAvailable(14l)
            .VMsStopped(0)
            .VMsRunning(1)
            .state(Account.State.ENABLED)
            .users(users).build()));
   }

   @Override
   protected AccountClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getAccountClient();
   }
}
