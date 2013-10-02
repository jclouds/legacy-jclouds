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
package org.jclouds.cloudstack.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJob.ResultCode;
import org.jclouds.cloudstack.domain.AsyncJob.Status;
import org.jclouds.cloudstack.domain.AsyncJobError;
import org.jclouds.cloudstack.domain.AsyncJobError.ErrorCode;
import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateExtraction;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseAsyncJobFromHttpResponseTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testWithNoResult() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0} }";

      AsyncJob<PublicIPAddress> expects = AsyncJob.<PublicIPAddress>builder()
         .id("860")
         .status(Status.IN_PROGRESS)
         .progress(0)
         .resultCode(ResultCode.SUCCESS).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(input).build());

      assertEquals(response, expects);
   }

   public void testWithSuccessTrueResultSetsNullResult() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":1138,\"jobstatus\":1,\"jobprocstatus\":0,\"jobresultcode\":0,\"jobresulttype\":\"object\",\"jobresult\":{\"success\":true}} }";

      AsyncJob<PublicIPAddress> expects = AsyncJob.<PublicIPAddress>builder()
         .id("1138")
         .status(Status.SUCCEEDED)
         .progress(0)
         .resultType("object")
         .resultCode(ResultCode.SUCCESS).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(input).build());
      assertEquals(response, expects);
   }

   public void testWithErrorSetsResultNullSoToAvoidClassCastExceptions() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":1103,\"jobstatus\":2,\"jobprocstatus\":0,\"jobresultcode\":530,\"jobresulttype\":\"object\",\"jobresult\":{\"errorcode\":530,\"errortext\":\"Internal error executing command, please contact your system administrator\"}} }";

      AsyncJob<PublicIPAddress> expects = AsyncJob
         .<PublicIPAddress>builder()
         .id("1103")
         .status(Status.FAILED)
         .progress(0)
         .resultType("object")
         .error(AsyncJobError.builder().errorCode(ErrorCode.INTERNAL_ERROR).errorText("Internal error executing " +
            "command, please contact your system administrator").build())
         .resultCode(ResultCode.FAIL).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(input).build());
      assertEquals(response, expects);
   }

   public void testWithUnknownResultReturnsStringifiedJson() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0,\"jobresult\":{\"foo\":{\"bar\":1}}}}";

      AsyncJob<?> expects = AsyncJob.builder()
         .id("860")
         .status(Status.IN_PROGRESS)
         .progress(0)
         .resultCode(ResultCode.SUCCESS)
         .result("{\"bar\":1}")
         .build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(input).build());
      assertEquals(response, expects);
   }

   public void testWithBadResultReturnsMap() {
      // Not the best result object, but this is an unexpected error case.
      // Cloud.com have verified
      // that this case will not happen. This code is only here to prevent
      // exceptions from being
      // thrown in case they change their minds.
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0,\"jobresult\":{\"foo\":{\"bar\":1},\"foo2\":{\"bar2\":2}}}}";

      AsyncJob<?> expects = AsyncJob.builder()
         .id("860")
         .status(Status.IN_PROGRESS)
         .progress(0)
         .resultCode(ResultCode.SUCCESS)
         .result(ImmutableMap.of("foo", new JsonBall("{\"bar\":1}"), "foo2", new JsonBall("{\"bar2\":2}"))).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(input).build());
      assertEquals(response, expects);
   }

   public void testPublicIPAddress() {
      InputStream is = getClass().getResourceAsStream("/queryasyncjobresultresponse-ipaddress.json");
      AsyncJob<PublicIPAddress> expects = AsyncJob
         .<PublicIPAddress>builder()
         .id("860")
         .status(Status.SUCCEEDED)
         .progress(0)
         .resultType("object")
         .resultCode(ResultCode.SUCCESS)
         .result(
            PublicIPAddress
               .builder()
               .id("6")
               .IPAddress("72.52.126.35")
               .allocated(
                  new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-23T20:15:01-0800"))
               .zoneId("1").zoneName("San Jose 1").isSourceNAT(false).account("adrian").domainId("1")
               .domain("ROOT").usesVirtualNetwork(true).isStaticNAT(false).associatedNetworkId("204")
               .networkId("200").state(PublicIPAddress.State.ALLOCATING).build()

         ).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(is).build());
      assertEquals(response, expects);
   }

   public void testIPForwardingRule() {
      InputStream is = getClass().getResourceAsStream("/queryasyncjobresultresponse-ipforwardingrule.json");
      AsyncJob<IPForwardingRule> expects = AsyncJob
         .<IPForwardingRule>builder()
         .id("1133")
         .status(Status.SUCCEEDED)
         .progress(0)
         .resultType("object")
         .resultCode(ResultCode.SUCCESS)
         .result(
            IPForwardingRule.builder().id("109").protocol("tcp").virtualMachineId("226")
               .virtualMachineName("i-3-226-VM").IPAddressId("36").IPAddress("72.52.126.65").startPort(22)
               .endPort(22).state("Active").build()).build();

      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      @SuppressWarnings("unchecked")
      AsyncJob<IPForwardingRule> response = (AsyncJob<IPForwardingRule>) parser.apply(HttpResponse.builder()
                                                                                                .statusCode(200).message("ok")
                                                                                                .payload(is).build());
      assertEquals(response, expects);
   }

   public void testOverloadedKeyName() {
      InputStream is = getClass().getResourceAsStream("/queryasyncjobresultresponse-createtemplate.json");
      ParseAsyncJobFromHttpResponse parser = i.getInstance(ParseAsyncJobFromHttpResponse.class);
      AsyncJob<?> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertTrue(response.getResult() instanceof Template, "response expected to be Template, actually is " + response.getResult().getClass());

      is = getClass().getResourceAsStream("/queryasyncjobresultresponse-extracttemplate.json");
      response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertTrue(response.getResult() instanceof TemplateExtraction, "response expected to be TemplateExtraction, actually is " + response.getResult().getClass());
   }
}
