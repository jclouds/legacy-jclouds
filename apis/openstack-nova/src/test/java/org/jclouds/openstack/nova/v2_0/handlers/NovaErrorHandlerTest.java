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
package org.jclouds.openstack.nova.v2_0.handlers;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.jclouds.date.DateCodec;
import org.jclouds.date.internal.DateServiceDateCodecFactory.DateServiceIso8601SecondsCodec;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.fallbacks.HeaderToRetryAfterException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.internal.GsonWrapper;
import org.jclouds.openstack.nova.v2_0.functions.OverLimitParser;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.RetryAfterException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Ticker;
import com.google.gson.Gson;

/**
 * 
 * @author Adrian Cole, Steve Loughran
 */
@Test(groups = "unit", testName = "NovaErrorHandlerTest", singleThreaded = true)
public class NovaErrorHandlerTest {
   
   private HttpCommand command;

   @BeforeTest
   void setupCommand(){
      command = command();
   }
   
   @Test
   public void test401MakesAuthorizationException() {
      fn.handleError(command, HttpResponse.builder().statusCode(401).message("Unauthorized").build());

      assertEquals(command.getException().getClass(), AuthorizationException.class);
      assertEquals(command.getException().getMessage(),
            "POST https://nova/v1.1/servers HTTP/1.1 -> HTTP/1.1 401 Unauthorized");
   }
   
   @Test
   public void test404MakesResourceNotFoundException() {
      fn.handleError(command, HttpResponse.builder().statusCode(404).message("Not Found").build());

      assertEquals(command.getException().getClass(), ResourceNotFoundException.class);
      assertEquals(command.getException().getMessage(),
            "POST https://nova/v1.1/servers HTTP/1.1 -> HTTP/1.1 404 Not Found");
   }

   // should wait until ips are associated w/the server
   HttpResponse noFixedIps = HttpResponse.builder().statusCode(400)
         .message("HTTP/1.1 400 Bad Request")
         .payload("{\"badRequest\": {\"message\": "+
                  "\"instance |71554| has no fixed_ips. unable to associate floating ip\", \"code\": 400}}")
         .build();
   
   @Test
   public void test400MakesIllegalStateExceptionOnQuotaExceededOnNoFixedIps() {
      fn.handleError(command, noFixedIps);

      assertEquals(command.getException().getClass(), IllegalStateException.class);
      assertEquals(command.getException().getMessage(), noFixedIps.getPayload().getRawContent());
   }
   
   HttpResponse alreadyExists = HttpResponse.builder().statusCode(400)
         .message("HTTP/1.1 400 Bad Request")
         .payload("{\"badRequest\": {\"message\": \"Server with the name 'test' already exists\", \"code\": 400}}")
         .build();
   
   @Test
   public void test400MakesIllegalStateExceptionOnAlreadyExists() {
      fn.handleError(command, alreadyExists);

      assertEquals(command.getException().getClass(), IllegalStateException.class);
      assertEquals(command.getException().getMessage(), alreadyExists.getPayload().getRawContent());
   }
   
   HttpResponse quotaExceeded = HttpResponse.builder().statusCode(400)
         .message("HTTP/1.1 400 Bad Request")
         .payload("{\"badRequest\": {\"message\": \"AddressLimitExceeded: Address quota exceeded. " +
                  "You cannot create any more addresses\", \"code\": 400}}")
         .build();
   
   @Test
   public void test400MakesInsufficientResourcesExceptionOnQuotaExceeded() {
      fn.handleError(command, quotaExceeded);

      assertEquals(command.getException().getClass(), InsufficientResourcesException.class);
      assertEquals(command.getException().getMessage(), quotaExceeded.getPayload().getRawContent());
   }
   
   HttpResponse tooLarge = HttpResponse.builder().statusCode(413)
         .message("HTTP/1.1 413 Request Entity Too Large")
         .payload("{\"badRequest\": {\"message\": \"Volume quota exceeded. You cannot create a volume of size 1G\", " +
                  "\"code\": 413, \"retryAfter\": 0}}")
         .build();
   
   @Test
   public void test413MakesInsufficientResourcesException() {
      fn.handleError(command, tooLarge);

      assertEquals(command.getException().getClass(), InsufficientResourcesException.class);
      assertEquals(command.getException().getMessage(), tooLarge.getPayload().getRawContent());
   }
   
   /**
    * Reponse received from Rackspace UK on November 14, 2012.
    */
   HttpResponse retryAt = HttpResponse.builder().statusCode(413)
         .message("HTTP/1.1 413 Request Entity Too Large")
         .payload("{ 'overLimit' : { 'code' : 413,"
                 + " 'message' : 'OverLimit Retry...', " 
                 + " 'details' : 'Error Details...',"
                 + " 'retryAt' : '2012-11-14T21:51:28UTC' }}")
         .build();
   
   @Test
   public void test413WithRetryAtExceptionParsesDelta() {
      fn.handleError(command, retryAt);

      assertEquals(command.getException().getClass(), RetryAfterException.class);
      assertEquals(command.getException().getMessage(), "retry in 3600 seconds");
   }
   
   /**
    * Folsom response. This contains a delta in seconds to retry after, not a
    * fixed time.
    * 
    */
   HttpResponse retryAfter = HttpResponse.builder().statusCode(413)
         .message("HTTP/1.1 413 Request Entity Too Large")
         .payload("{ 'overLimit': { 'message': 'This request was rate-limited.', "
                 + " 'retryAfter': '54', "
                 + " 'details': 'Only 1 POST request(s) can be made to \\'*\\' every minute.'" + " }}")
         .build();
   
   @Test
   public void test413WithRetryAfterExceptionFolsom() {
      fn.handleError(command, retryAfter);

      assertEquals(command.getException().getClass(), RetryAfterException.class);
      assertEquals(command.getException().getMessage(), "retry in 54 seconds");
   }
   
   /**
    * Folsom response with a retryAt field inserted -at a different date. This
    * can be used to verify that the retryAfter field is picked up first
    */
   HttpResponse retryAfterTrumps = HttpResponse.builder().statusCode(413)
         .message("HTTP/1.1 413 Request Entity Too Large")
         .payload("{ 'overLimit': {"
                 + " 'message': 'This request was rate-limited.', " 
                 + " 'retryAfter': '54', "
                 + " 'retryAt' : '2012-11-14T21:51:28UTC',"
                 + " 'details': 'Only 1 POST request(s) can be made to \\'*\\' every minute.' }}")
         .build();
   
   @Test
   public void test413WithRetryAfterTrumpsRetryAt() {
      fn.handleError(command, retryAfterTrumps);

      assertEquals(command.getException().getClass(), RetryAfterException.class);
      assertEquals(command.getException().getMessage(), "retry in 54 seconds");
   }
   
   HttpResponse badRetryAt = HttpResponse.builder().statusCode(413)
         .message("HTTP/1.1 413 Request Entity Too Large")
         .payload("{ 'overLimit' : { 'code' : 413,"
                 + " 'message' : 'OverLimit Retry...', " 
                 + " 'details' : 'Error Details...',"
                 + " 'retryAt' : '2012-11-~~~:51:28UTC' }}")
         .build();
   
   @Test
   public void test413WithBadRetryAtFormatFallsBack() {
      fn.handleError(command, badRetryAt);

      assertEquals(command.getException().getClass(), InsufficientResourcesException.class);
      assertEquals(command.getException().getMessage(), badRetryAt.getPayload().getRawContent());
   }
   
   
   DateCodec iso8601Seconds = new DateServiceIso8601SecondsCodec(new SimpleDateFormatDateService());
   
   Ticker y2k = new Ticker(){

      @Override
      public long read() {
         return TimeUnit.MILLISECONDS.toNanos(iso8601Seconds.toDate("2012-11-14T20:51:28UTC").getTime());
      }
      
   };
   
   NovaErrorHandler fn = new NovaErrorHandler(HeaderToRetryAfterException.create(y2k, iso8601Seconds),
         new OverLimitParser(new GsonWrapper(new Gson())));

   private HttpCommand command() {
      return new HttpCommand(HttpRequest.builder().method("POST").endpoint("https://nova/v1.1/servers").build());
   }

}
