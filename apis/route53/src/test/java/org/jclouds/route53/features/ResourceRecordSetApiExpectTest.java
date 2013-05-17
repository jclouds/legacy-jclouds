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
package org.jclouds.route53.features;
import static com.google.common.net.HttpHeaders.DATE;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.route53.InvalidChangeBatchException;
import org.jclouds.route53.Route53Api;
import org.jclouds.route53.domain.ChangeBatch;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;
import org.jclouds.route53.internal.BaseRoute53ApiExpectTest;
import org.jclouds.route53.parse.GetChangeResponseTest;
import org.jclouds.route53.parse.ListResourceRecordSetsResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ResourceResourceRecordSetApiExpectTest")
public class ResourceRecordSetApiExpectTest extends BaseRoute53ApiExpectTest {

   HttpRequest create = HttpRequest.builder().method(POST)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate)
         .payload(payloadFromResourceWithContentType("/create_rrs_request.xml", "application/xml")).build();
   
   HttpResponse jobResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/change.xml", "text/xml")).build();

   public void testCreateWhenResponseIs2xx() {
      Route53Api success = requestSendsResponse(create, jobResponse);
      assertEquals(success.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").create(ResourceRecordSet.builder().name("jclouds.org.").type("TXT").add("my texts").build()).toString(),
            new GetChangeResponseTest().expected().toString());
   }

   HttpRequest apply = HttpRequest.builder().method(POST)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate)
         .payload(payloadFromResourceWithContentType("/batch_rrs_request.xml", "application/xml")).build();

   public void testApplyWhenResponseIs2xx() {
      Route53Api success = requestSendsResponse(apply, jobResponse);
      assertEquals(success.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").apply(
            ChangeBatch.builder()
                       .delete(ResourceRecordSet.builder().name("jclouds.org.").type("TXT").add("my texts").build())
                       .create(ResourceRecordSet.builder().name("jclouds.org.").type("TXT").add("my better texts").build())
                       .build()).toString(),
            new GetChangeResponseTest().expected().toString());
   }

   @Test(expectedExceptions = InvalidChangeBatchException.class, expectedExceptionsMessageRegExp = "\\[Tried to create resource record set duplicate.example.com. type A, but it already exists, Tried to delete resource record set noexist.example.com. type A, but it was not found\\]")
   public void testApplyWhenResponseIs4xx() {
      HttpResponse batchErrorFound = HttpResponse.builder().statusCode(BAD_REQUEST.getStatusCode())
            .payload(payloadFromResourceWithContentType("/invalid_change_batch.xml", "application/xml")).build();

      Route53Api fails = requestSendsResponse(apply, batchErrorFound);
      fails.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").apply(
            ChangeBatch.builder()
                       .delete(ResourceRecordSet.builder().name("jclouds.org.").type("TXT").add("my texts").build())
                       .create(ResourceRecordSet.builder().name("jclouds.org.").type("TXT").add("my better texts").build())
                       .build());
   }

   HttpRequest list = HttpRequest.builder().method(GET)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate).build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/rrsets.xml", "text/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      Route53Api success = requestSendsResponse(list, listResponse);
      assertEquals(success.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").list().get(0).toString(),
            new ListResourceRecordSetsResponseTest().expected().toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() {
      Route53Api fail = requestSendsResponse(list, notFound);
      assertEquals(fail.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").list().get(0).toSet(), ImmutableSet.of());
   }

   HttpRequest listAt = HttpRequest.builder().method(GET)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset?name=testdoc2.example.com")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate).build();

   public void testListAtWhenResponseIs2xx() {
      Route53Api apiWhenAtExist = requestSendsResponse(listAt, listResponse);
      NextRecord next = NextRecord.name("testdoc2.example.com");
      assertEquals(apiWhenAtExist.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").listAt(next).toString(),
            new ListResourceRecordSetsResponseTest().expected().toString());
   }

   HttpRequest listAtNameAndType = HttpRequest.builder().method(GET)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset?name=testdoc2.example.com&type=NS")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate).build();
   
   public void testListAtNameAndTypeWhenResponseIs2xx() {
      Route53Api apiWhenAtExist = requestSendsResponse(listAtNameAndType, listResponse);
      NextRecord next = NextRecord.nameAndType("testdoc2.example.com", "NS");
      assertEquals(apiWhenAtExist.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").listAt(next).toString(),
            new ListResourceRecordSetsResponseTest().expected().toString());
   }
   
   public void testList2PagesWhenResponseIs2xx() {
      HttpResponse noMore = HttpResponse.builder().statusCode(OK.getStatusCode())
            .payload(payloadFromStringWithContentType("<ListResourceRecordSetsResponse />", "text/xml")).build();

      Route53Api success = requestsSendResponses(list, listResponse, listAtNameAndType, noMore);
      assertEquals(success.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").list().concat().toSet(),
            new ListResourceRecordSetsResponseTest().expected().toSet());
   }

   HttpRequest delete = HttpRequest.builder().method(POST)
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate)
         .payload(payloadFromResourceWithContentType("/delete_rrs_request.xml", "application/xml")).build();

   public void testDeleteWhenResponseIs2xx() {
      Route53Api success = requestSendsResponse(delete, jobResponse);
      assertEquals(success.getResourceRecordSetApiForHostedZone("Z1PA6795UKMFR9").create(ResourceRecordSet.builder()
                                                                                                          .name("jclouds.org.")
                                                                                                          .type("TXT")
                                                                                                          .ttl(0)
                                                                                                          .add("my texts").build()).toString(),
            new GetChangeResponseTest().expected().toString());
   }
}
