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
package org.jclouds.route53;

import static com.google.common.net.HttpHeaders.DATE;
import static com.google.common.net.HttpHeaders.HOST;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.route53.internal.BaseRoute53ApiExpectTest;
import org.jclouds.route53.parse.GetChangeResponseTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "Route53ApiExpectTest")
public class Route53ApiExpectTest extends BaseRoute53ApiExpectTest {

   HttpRequest getChange = HttpRequest.builder().method(GET)
         .endpoint("https://route53.amazonaws.com/2012-02-29/change/C2682N5HXP0BZ4")
         .addHeader(HOST, "route53.amazonaws.com")
         .addHeader(DATE, "Mon, 21 Jan 02013 19:29:03 -0800")
         .addHeader("X-Amzn-Authorization", authForDate)
         .build();

   HttpResponse getChangeResponse = HttpResponse.builder().statusCode(OK.getStatusCode())
         .payload(payloadFromResourceWithContentType("/change.xml", "text/xml")).build();

   public void testGetChangeWhenResponseIs2xx() {
      Route53Api api = requestSendsResponse(getChange, getChangeResponse);
      assertEquals(api.getChange("C2682N5HXP0BZ4").toString(), new GetChangeResponseTest().expected().toString());
   }

   public void testGetChangeNullWhenResponseIs404() {
      Route53Api api = requestSendsResponse(getChange, notFound);
      assertNull(api.getChange("C2682N5HXP0BZ4"));
   }
}
