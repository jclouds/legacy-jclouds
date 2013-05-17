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

import static org.jclouds.reflect.Reflection2.method;

import java.util.Calendar;
import java.util.Date;

import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.GenerateUsageRecordsOptions;
import org.jclouds.cloudstack.options.ListUsageRecordsOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code GlobalUsageAsyncClient}
 *
 * @author Richard Downer
 */
@Test(groups = "unit", testName = "GlobalUsageAsyncClientTest")
public class GlobalUsageAsyncClientTest extends BaseCloudStackAsyncClientTest<GlobalUsageAsyncClient> {

   public void testGenerateUsageRecords() throws Exception {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2012);
      c.set(Calendar.MONTH, Calendar.JANUARY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      Date start = c.getTime();
      c.set(Calendar.DAY_OF_MONTH, 31);
      Date end = c.getTime();

      Invokable<?, ?> method = method(GlobalUsageAsyncClient.class, "generateUsageRecords",
         Date.class, Date.class, GenerateUsageRecordsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(start, end));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=generateUsageRecords&startdate=2012-01-01&enddate=2012-01-31 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testGenerateUsageRecordsOptions() throws Exception {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2012);
      c.set(Calendar.MONTH, Calendar.JANUARY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      Date start = c.getTime();
      c.set(Calendar.DAY_OF_MONTH, 31);
      Date end = c.getTime();

      Invokable<?, ?> method = method(GlobalUsageAsyncClient.class, "generateUsageRecords",
         Date.class, Date.class, GenerateUsageRecordsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(start, end, GenerateUsageRecordsOptions.Builder.domainId("42")));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=generateUsageRecords&startdate=2012-01-01&enddate=2012-01-31&domainid=42 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListUsageRecords() throws Exception {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2012);
      c.set(Calendar.MONTH, Calendar.JANUARY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      Date start = c.getTime();
      c.set(Calendar.DAY_OF_MONTH, 31);
      Date end = c.getTime();

      Invokable<?, ?> method = method(GlobalUsageAsyncClient.class, "listUsageRecords",
         Date.class, Date.class, ListUsageRecordsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(start, end));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listUsageRecords&listAll=true&startdate=2012-01-01&enddate=2012-01-31 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListUsageRecordsOptions() throws Exception {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2012);
      c.set(Calendar.MONTH, Calendar.JANUARY);
      c.set(Calendar.DAY_OF_MONTH, 1);
      Date start = c.getTime();
      c.set(Calendar.DAY_OF_MONTH, 31);
      Date end = c.getTime();

      Invokable<?, ?> method = method(GlobalUsageAsyncClient.class, "listUsageRecords",
         Date.class, Date.class, ListUsageRecordsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(start, end, ListUsageRecordsOptions.Builder.accountInDomain("fred", "42").accountId("41").keyword("bob")));

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=listUsageRecords&listAll=true&startdate=2012-01-01&enddate=2012-01-31&account=fred&domainid=42&accountid=41&keyword=bob HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }
}
