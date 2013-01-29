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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.route53.features;

import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.route53.Route53Api;
import org.jclouds.route53.domain.ResourceRecordSet.Type;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;
import org.jclouds.route53.internal.BaseRoute53ApiExpectTest;
import org.jclouds.route53.parse.ListResourceRecordSetsResponseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ResourceRecordSetApiExpectTest")
public class ResourceRecordSetApiExpectTest extends BaseRoute53ApiExpectTest {

   HttpRequest list = HttpRequest.builder().method("GET")
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset")
         .addHeader("Host", "route53.amazonaws.com")
         .addHeader("Date", "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization",
               "AWS3-HTTPS AWSAccessKeyId=identity,Algorithm=HmacSHA256,Signature=pylxNiLcrsjNRZOsxyT161JCwytVPHyc2rFfmNCuZKI=")
         .build();

   HttpResponse listResponse = HttpResponse.builder().statusCode(200)
         .payload(payloadFromResourceWithContentType("/rrsets.xml", "text/xml")).build();
   
   public void testListWhenResponseIs2xx() {
      Route53Api success = requestSendsResponse(list, listResponse);
      assertEquals(success.getResourceRecordSetApiForZone("Z1PA6795UKMFR9").list().get(0).toString(), new ListResourceRecordSetsResponseTest().expected()
            .toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() {
      Route53Api fail = requestSendsResponse(list, notFound);
      assertEquals(fail.getResourceRecordSetApiForZone("Z1PA6795UKMFR9").list().get(0).toImmutableSet(), ImmutableSet.of());
   }
   
   HttpRequest listAt = HttpRequest.builder().method("GET")
         .endpoint("https://route53.amazonaws.com/2012-02-29/hostedzone/Z1PA6795UKMFR9/rrset?name=testdoc2.example.com&type=NS")
         .addHeader("Host", "route53.amazonaws.com")
         .addHeader("Date", "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization",
               "AWS3-HTTPS AWSAccessKeyId=identity,Algorithm=HmacSHA256,Signature=pylxNiLcrsjNRZOsxyT161JCwytVPHyc2rFfmNCuZKI=")
         .build();
   
   public void testListAtWhenResponseIs2xx() {
      Route53Api apiWhenAtExist = requestSendsResponse(listAt, listResponse);
      NextRecord next = NextRecord.nameAndType("testdoc2.example.com", Type.NS);
      assertEquals(apiWhenAtExist.getResourceRecordSetApiForZone("Z1PA6795UKMFR9").listAt(next).toString(),
            new ListResourceRecordSetsResponseTest().expected().toString());
   }
   
   public void testList2PagesWhenResponseIs2xx() {
      HttpResponse noMore = HttpResponse.builder().statusCode(200)
            .payload(payloadFromStringWithContentType("<ListResourceRecordSetsResponse />", "text/xml")).build();

      Route53Api success = requestsSendResponses(list, listResponse, listAt, noMore);
      assertEquals(success.getResourceRecordSetApiForZone("Z1PA6795UKMFR9").list().concat().toImmutableSet(), new ListResourceRecordSetsResponseTest().expected()
            .toImmutableSet());
   }
}
