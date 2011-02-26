/**
 *
 * Copyright (C) 2010 Cloud Conscious) LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License) Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing) software
 * distributed under the License is distributed on an "AS IS" BASIS)
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND) either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.domain.JsonBall;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseAsyncJobTest {

   Injector i = Guice.createInjector(new GsonModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testWithNoResult() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0} }";

      AsyncJob<PublicIPAddress> expects = AsyncJob.<PublicIPAddress> builder().id(860).status(0).progress(0)
            .resultCode(0).build();

      ParseAsyncJob parser = i.getInstance(ParseAsyncJob.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(new HttpResponse(200, "ok",
            Payloads.newStringPayload(input)));

      assertEquals(response, expects);
   }

   public void testWithUnknownResultReturnsStringifiedJson() {
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0,\"jobresult\":{\"foo\":{\"bar\":1}}}}";

      AsyncJob<?> expects = AsyncJob.builder().id(860).status(0).progress(0).resultCode(0).result("{\"bar\":1}")
            .build();

      ParseAsyncJob parser = i.getInstance(ParseAsyncJob.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(new HttpResponse(200, "ok",
            Payloads.newStringPayload(input)));

      assertEquals(response, expects);
   }

   public void testWithBadResultReturnsMap() {
      // Not the best result object, but this is an unexpected error case. Cloud.com have verified
      // that this case will not happen. This code is only here to prevent exceptions from being
      // thrown in case they change their minds.
      String input = "{ \"queryasyncjobresultresponse\" : {\"jobid\":860,\"jobstatus\":0,\"jobprocstatus\":0,\"jobresultcode\":0,\"jobresult\":{\"foo\":{\"bar\":1},\"foo2\":{\"bar2\":2}}}}";

      AsyncJob<?> expects = AsyncJob.builder().id(860).status(0).progress(0).resultCode(0)
            .result(ImmutableMap.of("foo", new JsonBall("{\"bar\":1}"), "foo2", new JsonBall("{\"bar2\":2}"))).build();

      ParseAsyncJob parser = i.getInstance(ParseAsyncJob.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(new HttpResponse(200, "ok",
            Payloads.newStringPayload(input)));

      assertEquals(response, expects);
   }

   public void testPublicIPAddress() {
      InputStream is = getClass().getResourceAsStream("/queryasyncjobresultresponse-ipaddress.json");
      AsyncJob<PublicIPAddress> expects = AsyncJob
            .<PublicIPAddress> builder()
            .id(860)
            .status(1)
            .progress(0)
            .resultType("object")
            .resultCode(0)
            .result(
                  PublicIPAddress
                        .builder()
                        .id(6)
                        .IPAddress("72.52.126.35")
                        .allocated(
                              new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-23T20:15:01-0800"))
                        .zoneId(1).zoneName("San Jose 1").isSourceNAT(false).account("adrian").domainId(1)
                        .domain("ROOT").usesVirtualNetwork(true).isStaticNAT(false).associatedNetworkId(204)
                        .networkId(200).state(PublicIPAddress.State.ALLOCATING).build()

            ).build();

      ParseAsyncJob parser = i.getInstance(ParseAsyncJob.class);
      @SuppressWarnings("unchecked")
      AsyncJob<PublicIPAddress> response = (AsyncJob<PublicIPAddress>) parser.apply(new HttpResponse(200, "ok",
            Payloads.newInputStreamPayload(is)));

      assertEquals(response, expects);
   }
}
