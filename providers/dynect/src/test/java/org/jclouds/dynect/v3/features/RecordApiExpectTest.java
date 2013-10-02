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
package org.jclouds.dynect.v3.features;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;
import static org.jclouds.dynect.v3.domain.rdata.AData.a;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.domain.CreateRecord;
import org.jclouds.dynect.v3.domain.Job;
import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.domain.rdata.AData;
import org.jclouds.dynect.v3.internal.BaseDynECTApiExpectTest;
import org.jclouds.dynect.v3.parse.GetAAAARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetCNAMERecordResponseTest;
import org.jclouds.dynect.v3.parse.GetMXRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetNSRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetPTRRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSOARecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSPFRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSRVRecordResponseTest;
import org.jclouds.dynect.v3.parse.GetSSHFPRecordResponseTest;
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
   HttpRequest getSOA = HttpRequest.builder().method(GET)
                                   .endpoint("https://api2.dynect.net/REST/SOARecord/jclouds.org/jclouds.org/50976579")
                                   .addHeader("API-Version", "3.3.8")
                                   .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                   .addHeader("Auth-Token", authToken).build();   

   HttpResponse soaResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_soa.json", APPLICATION_JSON)).build();

   RecordId soaId = recordIdBuilder()
                            .zone("jclouds.org")
                            .fqdn("jclouds.org")
                            .type("SOA")
                            .id(50976579l).build();

   public void testGetWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSOA, soaResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").get(soaId).toString(),
                   new GetRecordResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getSOA, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").get(soaId));
   }

   HttpRequest getAAAA = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/AAAARecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();
   
   HttpResponse aaaaResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_aaaa.json", APPLICATION_JSON)).build();

   RecordId aaaaId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("AAAA")
        .id(50976579l).build();

   public void testGetAAAAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getAAAA, aaaaResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getAAAA(aaaaId.getFQDN(), aaaaId.getId()).toString(),
                   new GetAAAARecordResponseTest().expected().toString());
   }

   public void testGetAAAAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getAAAA, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getAAAA(aaaaId.getFQDN(), aaaaId.getId()));
   }

   HttpRequest getA = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/ARecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse aResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_a.json", APPLICATION_JSON)).build();

   RecordId aId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("A")
        .id(50976579l).build();

   public void testGetAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getA, aResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getA(aId.getFQDN(), aId.getId()).toString(),
                   new GetARecordResponseTest().expected().toString());
   }

   public void testGetAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getA, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getA(aId.getFQDN(), aId.getId()));
   }

   HttpRequest getCNAME = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/CNAMERecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse cnameResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_cname.json", APPLICATION_JSON)).build();

   RecordId cnameId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("CNAME")
        .id(50976579l).build();

   public void testGetCNAMEWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getCNAME, cnameResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getCNAME(cnameId.getFQDN(), cnameId.getId()).toString(),
                   new GetCNAMERecordResponseTest().expected().toString());
   }

   public void testGetCNAMEWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getCNAME, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getCNAME(cnameId.getFQDN(), cnameId.getId()));
   }

   HttpRequest getMX = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/MXRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse mxResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_mx.json", APPLICATION_JSON)).build();

   RecordId mxId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("MX")
        .id(50976579l).build();

   public void testGetMXWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getMX, mxResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getMX(mxId.getFQDN(), mxId.getId()).toString(),
                   new GetMXRecordResponseTest().expected().toString());
   }

   public void testGetMXWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getMX, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getMX(mxId.getFQDN(), mxId.getId()));
   }

   HttpRequest getNS = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/NSRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse nsResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_ns.json", APPLICATION_JSON)).build();

   RecordId nsId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("NS")
        .id(50976579l).build();

   public void testGetNSWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getNS, nsResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getNS(nsId.getFQDN(), nsId.getId()).toString(),
                   new GetNSRecordResponseTest().expected().toString());
   }

   public void testGetNSWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getNS, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getNS(nsId.getFQDN(), nsId.getId()));
   }

   HttpRequest getPTR = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/PTRRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse ptrResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_ptr.json", APPLICATION_JSON)).build();

   RecordId ptrId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("PTR")
        .id(50976579l).build();

   public void testGetPTRWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getPTR, ptrResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getPTR(ptrId.getFQDN(), ptrId.getId()).toString(),
                   new GetPTRRecordResponseTest().expected().toString());
   }

   public void testGetPTRWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getPTR, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getPTR(ptrId.getFQDN(), ptrId.getId()));
   }

   public void testGetSOAWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSOA, soaResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getSOA(soaId.getFQDN(), soaId.getId()).toString(),
                   new GetSOARecordResponseTest().expected().toString());
   }

   public void testGetSOAWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getSOA, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getSOA(soaId.getFQDN(), soaId.getId()));
   }

   HttpRequest getSPF = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/SPFRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse spfResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_spf.json", APPLICATION_JSON)).build();

   RecordId spfId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("SPF")
        .id(50976579l).build();

   public void testGetSPFWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSPF, spfResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getSPF(spfId.getFQDN(), spfId.getId()).toString(),
                   new GetSPFRecordResponseTest().expected().toString());
   }

   HttpRequest getSRV = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/SRVRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse srvResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_srv.json", APPLICATION_JSON)).build();

   RecordId srvId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("SRV")
        .id(50976579l).build();

   public void testGetSRVWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSRV, srvResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getSRV(srvId.getFQDN(), srvId.getId()).toString(),
                   new GetSRVRecordResponseTest().expected().toString());
   }

   HttpRequest getSSHFP = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/SSHFPRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse sshfpResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_sshfp.json", APPLICATION_JSON)).build();

   RecordId sshfpId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("SSHFP")
        .id(50976579l).build();

   public void testGetSSHFPWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getSSHFP, sshfpResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getSSHFP(sshfpId.getFQDN(), sshfpId.getId()).toString(),
                   new GetSSHFPRecordResponseTest().expected().toString());
   }

   HttpRequest getTXT = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/TXTRecord/jclouds.org/jclouds.org/50976579")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   
   
   HttpResponse txtResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/get_record_txt.json", APPLICATION_JSON)).build();

   RecordId txtId = recordIdBuilder()
        .zone("jclouds.org")
        .fqdn("jclouds.org")
        .type("TXT")
        .id(50976579l).build();

   public void testGetTXTWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, getTXT, txtResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").getTXT(txtId.getFQDN(), txtId.getId()).toString(),
                   new GetTXTRecordResponseTest().expected().toString());
   }

   public void testGetTXTWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, getTXT, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").getTXT(txtId.getFQDN(), txtId.getId()));
   }

   HttpRequest list = HttpRequest.builder().method(GET)
                                 .endpoint("https://api2.dynect.net/REST/AllRecord/jclouds.org")
                                 .addHeader("API-Version", "3.3.8")
                                 .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                 .addHeader("Auth-Token", authToken).build();   

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/list_records.json", APPLICATION_JSON)).build();

   public void testListWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, list, listResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").list().toString(),
                   new ListRecordsResponseTest().expected().toString());
   }

   HttpRequest listByFQDN = HttpRequest.builder().method(GET)
         .endpoint("https://api2.dynect.net/REST/AllRecord/jclouds.org/www.foo.com")
         .addHeader("API-Version", "3.3.8")
         .addHeader(CONTENT_TYPE, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken).build();   

   public void testListByFQDNWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, listByFQDN, listResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").listByFQDN("www.foo.com").toString(),
      new ListRecordsResponseTest().expected().toString());
   }
   
   public void testListByFQDNWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, listByFQDN, notFound);
      assertTrue(fail.getRecordApiForZone("jclouds.org").listByFQDN("www.foo.com").isEmpty());
   }

   HttpRequest listByFQDNAndType = HttpRequest.builder().method(GET)
                                              .endpoint("https://api2.dynect.net/REST/ARecord/jclouds.org/www.foo.com")
                                              .addHeader("API-Version", "3.3.8")
                                              .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                              .addHeader("Auth-Token", authToken).build();   

   public void testListByFQDNAndTypeWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, listByFQDNAndType, listResponse);
      assertEquals(success.getRecordApiForZone("jclouds.org").listByFQDNAndType("www.foo.com", "A").toString(),
            new ListRecordsResponseTest().expected().toString());
   }
   
   public void testListByFQDNAndTypeWhenResponseIs404() {
       DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, listByFQDNAndType, notFound);
       assertTrue(fail.getRecordApiForZone("jclouds.org").listByFQDNAndType("www.foo.com", "A").isEmpty());
    }

   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://api2.dynect.net/REST/ARecord/jclouds.org/www.jclouds.org")
         .addHeader("API-Version", "3.3.8")
         .addHeader(ACCEPT, APPLICATION_JSON)
         .addHeader("Auth-Token", authToken)
         .payload(stringPayload("{\"rdata\":{\"address\":\"1.1.1.1\"},\"ttl\":86400}"))
         .build();   

   HttpResponse createResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/new_record.json", APPLICATION_JSON)).build();

   public void testCreateWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, create, createResponse);
      CreateRecord<AData> record = CreateRecord.<AData> builder()
                                               .fqdn("www.jclouds.org")
                                               .type("A")
                                               .ttl(86400)
                                               .rdata(a("1.1.1.1"))
                                               .build();
      assertEquals(success.getRecordApiForZone("jclouds.org").scheduleCreate(record), Job.success(285372440l));
   }

   HttpRequest delete = HttpRequest.builder().method(DELETE)
                                             .endpoint("https://api2.dynect.net/REST/ARecord/jclouds.org/www.jclouds.org/285372440")
                                             .addHeader("API-Version", "3.3.8")
                                             .addHeader(ACCEPT, APPLICATION_JSON)
                                             .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                                             .addHeader("Auth-Token", authToken).build();  

   HttpResponse deleteResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/delete_record.json", APPLICATION_JSON)).build();

   RecordId id = recordIdBuilder()
                     .zone("jclouds.org")
                     .fqdn("www.jclouds.org")
                     .type("A")
                     .id(285372440l)
                     .build();

   public void testDeleteWhenResponseIs2xx() {
      DynECTApi success = requestsSendResponses(createSession, createSessionResponse, delete, deleteResponse);

      assertEquals(success.getRecordApiForZone("jclouds.org").scheduleDelete(id), Job.success(285372457l));
   }

   public void testDeleteWhenResponseIs404() {
      DynECTApi fail = requestsSendResponses(createSession, createSessionResponse, delete, notFound);
      assertNull(fail.getRecordApiForZone("jclouds.org").scheduleDelete(id));
   }
}
