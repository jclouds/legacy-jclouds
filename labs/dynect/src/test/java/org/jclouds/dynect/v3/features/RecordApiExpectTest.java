/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.dynect.v3.parse.GetAAAARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetCNAMERecordResponseTest;
import org.jclouds.dynect.v3.parse.GetMXRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetNSRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetPTRRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSOARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSRVRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetTXTRecordResponseTest;
import org.jclouds.dynect.v3.parse.ListRecordsResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RecordApiExpectTest")
public class RecordApiExpectTest extends BaseDynECTApiExpectTest {
   HttpRequest getSOA = HttpRequest.builder().method("GET")
                                .endpoint("https://api2.dynect.net/REST/SOARecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
                                .addHeader("API-Version", "3.3.8")
                                .addHeader("Auth-Token", authToken)
                                .payload(emptyJsonPayload()).build();   

   HttpResponse soaResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_soa.json", APPLICATION_JSON)).build();

   RecordId soaId = recordIdBuilder()
                            .zone("adrianc.zone.dynecttest.jclouds.org")
                            .fqdn("adrianc.zone.dynecttest.jclouds.org")
                            .type("SOA")
                            .id(50976579l).build();

   public void testGetWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSOA, soaResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").get(soaId).toString(),
                   new GetRecordResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getSOA, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").get(soaId));
   }

   HttpRequest getAAAA = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/AAAARecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse aaaaResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_aaaa.json", APPLICATION_JSON)).build();

   RecordId aaaaId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("AAAA")
        .id(50976579l).build();

   public void testGetAAAAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getAAAA, aaaaResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getAAAA(aaaaId.getFQDN(), aaaaId.getId()).toString(),
                   new GetAAAARecordResponseTest().expected().toString());
   }

   public void testGetAAAAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getAAAA, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getAAAA(aaaaId.getFQDN(), aaaaId.getId()));
   }

   HttpRequest getA = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/ARecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse aResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_a.json", APPLICATION_JSON)).build();

   RecordId aId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("A")
        .id(50976579l).build();

   public void testGetAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getA, aResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getA(aId.getFQDN(), aId.getId()).toString(),
                   new GetARecordResponseTest().expected().toString());
   }

   public void testGetAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getA, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getA(aId.getFQDN(), aId.getId()));
   }

   HttpRequest getCNAME = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/CNAMERecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse cnameResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_cname.json", APPLICATION_JSON)).build();

   RecordId cnameId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("CNAME")
        .id(50976579l).build();

   public void testGetCNAMEWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getCNAME, cnameResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getCNAME(cnameId.getFQDN(), cnameId.getId()).toString(),
                   new GetCNAMERecordResponseTest().expected().toString());
   }

   public void testGetCNAMEWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getCNAME, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getCNAME(cnameId.getFQDN(), cnameId.getId()));
   }

   HttpRequest getMX = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/MXRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse mxResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_mx.json", APPLICATION_JSON)).build();

   RecordId mxId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("MX")
        .id(50976579l).build();

   public void testGetMXWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getMX, mxResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getMX(mxId.getFQDN(), mxId.getId()).toString(),
                   new GetMXRecordResponseTest().expected().toString());
   }

   public void testGetMXWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getMX, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getMX(mxId.getFQDN(), mxId.getId()));
   }

   HttpRequest getNS = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/NSRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse nsResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_ns.json", APPLICATION_JSON)).build();

   RecordId nsId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("NS")
        .id(50976579l).build();

   public void testGetNSWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getNS, nsResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getNS(nsId.getFQDN(), nsId.getId()).toString(),
                   new GetNSRecordResponseTest().expected().toString());
   }

   public void testGetNSWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getNS, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getNS(nsId.getFQDN(), nsId.getId()));
   }

   HttpRequest getPTR = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/PTRRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse ptrResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_ptr.json", APPLICATION_JSON)).build();

   RecordId ptrId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("PTR")
        .id(50976579l).build();

   public void testGetPTRWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getPTR, ptrResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getPTR(ptrId.getFQDN(), ptrId.getId()).toString(),
                   new GetPTRRecordResponseTest().expected().toString());
   }

   public void testGetPTRWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getPTR, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getPTR(ptrId.getFQDN(), ptrId.getId()));
   }

   public void testGetSOAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSOA, soaResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getSOA(soaId.getFQDN(), soaId.getId()).toString(),
                   new GetSOARecordResponseTest().expected().toString());
   }

   public void testGetSOAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getSOA, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getSOA(soaId.getFQDN(), soaId.getId()));
   }

   HttpRequest getSRV = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/SRVRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse srvResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_srv.json", APPLICATION_JSON)).build();

   RecordId srvId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("SRV")
        .id(50976579l).build();

   public void testGetSRVWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSRV, srvResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getSRV(srvId.getFQDN(), srvId.getId()).toString(),
                   new GetSRVRecordResponseTest().expected().toString());
   }

   HttpRequest getTXT = HttpRequest.builder().method("GET")
         .endpoint("https://api2.dynect.net/REST/TXTRecord/adrianc.zone.dynecttest.jclouds.org/adrianc.zone.dynecttest.jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader("Auth-Token", authToken)
         .payload(emptyJsonPayload()).build();   
   
   HttpResponse txtResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/get_record_txt.json", APPLICATION_JSON)).build();

   RecordId txtId = recordIdBuilder()
        .zone("adrianc.zone.dynecttest.jclouds.org")
        .fqdn("adrianc.zone.dynecttest.jclouds.org")
        .type("TXT")
        .id(50976579l).build();

   public void testGetTXTWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getTXT, txtResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getTXT(txtId.getFQDN(), txtId.getId()).toString(),
                   new GetTXTRecordResponseTest().expected().toString());
   }

   public void testGetTXTWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getTXT, notFound);
      assertNull(fail.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").getTXT(txtId.getFQDN(), txtId.getId()));
   }

   HttpRequest list = HttpRequest.builder().method("GET")
                                 .endpoint("https://api2.dynect.net/REST/AllRecord/adrianc.zone.dynecttest.jclouds.org")
                                 .addHeader("API-Version", "3.3.8")
                                 .addHeader("Auth-Token", authToken)
                                 .payload(emptyJsonPayload()).build();   

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/list_records.json", APPLICATION_JSON)).build();

   public void testListWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, list, listResponse);
      assertEquals(success.getRecordApiForZone("adrianc.zone.dynecttest.jclouds.org").list().toString(),
                   new ListRecordsResponseTest().expected().toString());
   }
}
