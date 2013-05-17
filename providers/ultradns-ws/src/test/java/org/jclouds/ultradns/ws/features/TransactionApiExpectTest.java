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
package org.jclouds.ultradns.ws.features;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.TooManyTransactionsException;
import org.jclouds.ultradns.ws.internal.BaseUltraDNSWSApiExpectTest;
import org.testng.annotations.Test;
/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "TransactionApiExpectTest")
public class TransactionApiExpectTest extends BaseUltraDNSWSApiExpectTest {
   HttpRequest start = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/start_tx.xml", "application/xml")).build();

   HttpResponse startResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tx_started.xml", "application/xml")).build();

   public void testStartWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(start, startResponse);

      assertEquals(success.getTransactionApi().start().toString(), "jclouds-37562");
   }

   HttpResponse tooManyResponse = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/tx_toomany.xml")).build();
   
   @Test(expectedExceptions = TooManyTransactionsException.class, expectedExceptionsMessageRegExp = "Ultra API only allows 3 concurrent transactions per user")
   public void testStartWhenResponseError9010() {
      UltraDNSWSApi tooMany = requestSendsResponse(start, tooManyResponse);
      tooMany.getTransactionApi().start();
   }

   HttpRequest commit = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/commit_tx.xml", "application/xml")).build();

   HttpResponse commitResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tx_committed.xml", "application/xml")).build();

   public void testCommitWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(commit, commitResponse);
      success.getTransactionApi().commit("jclouds-37562");
   }
   
   HttpResponse txDoesntExist = HttpResponse.builder().message("Server Error").statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
         .payload(payloadFromResource("/tx_doesnt_exist.xml")).build();

   @Test(expectedExceptions = ResourceNotFoundException.class, expectedExceptionsMessageRegExp = "No transaction with Id AAAAAAAAAAAAAAAA found for the user .*")
   public void testCommitWhenResponseError1602() {
      UltraDNSWSApi notFound = requestSendsResponse(commit, txDoesntExist);
      notFound.getTransactionApi().commit("jclouds-37562");
   }

   HttpRequest rollback = HttpRequest.builder().method(POST)
         .endpoint("https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01")
         .addHeader(HOST, "ultra-api.ultradns.com:8443")
         .payload(payloadFromResourceWithContentType("/rollback_tx.xml", "application/xml")).build();

   HttpResponse rollbackResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/tx_rolledback.xml", "application/xml")).build();

   public void testRollbackWhenResponseIs2xx() {
      UltraDNSWSApi success = requestSendsResponse(rollback, rollbackResponse);
      success.getTransactionApi().rollback("jclouds-37562");
   }

   public void testRollbackWhenResponseError1602IsOK() {
      UltraDNSWSApi notFound = requestSendsResponse(rollback, txDoesntExist);
      notFound.getTransactionApi().rollback("jclouds-37562");
   }
}
