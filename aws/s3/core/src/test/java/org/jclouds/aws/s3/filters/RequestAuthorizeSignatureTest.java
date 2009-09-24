/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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
 * ====================================================================
 */
package org.jclouds.aws.s3.filters;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.util.Jsr330;

@Test(groups = "unit", testName = "s3.RequestAuthorizeSignatureTest")
public class RequestAuthorizeSignatureTest {

   private Injector injector;
   private RequestAuthorizeSignature filter;

   @DataProvider(parallel = true)
   public Object[][] dataProvider() {
      return new Object[][] {
               { new HttpRequest(HttpMethod.GET, URI.create("http://s3.amazonaws.com:80")) },
               { new HttpRequest(
                        HttpMethod.GET,
                        URI
                                 .create("http://adriancole.s3int5.s3-external-3.amazonaws.com:80/testObject")) },
               { new HttpRequest(HttpMethod.GET, URI.create("http://s3.amazonaws.com:80/?acl"))

               } };
   }

   /**
    * NOTE this test is dependent on how frequently the timestamp updates. At the time of writing,
    * this was once per second. If this timestamp update interval is increased, it could make this
    * test appear to hang for a long time.
    */
   @Test(threadPoolSize = 3, dataProvider = "dataProvider", timeOut = 3000)
   void testIdempotent(HttpRequest request) {
      filter.filter(request);
      String signature = request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION);
      String date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
      int iterations = 1;
      while (filter.filter(request).getFirstHeaderOrNull(HttpHeaders.DATE).equals(date)) {
         iterations++;
         assertEquals(signature, request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION));
      }
      System.out.printf("%s: %d iterations before the timestamp updated %n", Thread.currentThread()
               .getName(), iterations);
   }

   @Test
   void testAppendBucketNameHostHeader() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      request.getHeaders().put(HttpHeaders.HOST, "adriancole.s3int5.s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "/adriancole.s3int5");
   }

   @Test
   void testAclQueryString() {
      URI host = URI.create("http://s3.amazonaws.com:80/?acl");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?acl");
   }

   // "?acl", "?location", "?logging", or "?torrent"

   @Test
   void testAppendBucketNameHostHeaderService() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      request.getHeaders().put(HttpHeaders.HOST, "s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "");
   }

   
   @Test
   void testHeadersGoLowercase() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      request.getHeaders().put("x-amz-adrian", "s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "");
   }
   @Test
   void testAppendBucketNameURIHost() {
      URI host = URI.create("http://adriancole.s3int5.s3-external-3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "/adriancole.s3int5");
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      // filter.createNewStamp();
      String timeStamp = filter.timestampAsHeaderString();
      // replay(filter);
      for (int i = 0; i < 10; i++)
         filter.updateIfTimeOut();
      assert timeStamp.equals(filter.timestampAsHeaderString());
      Thread.sleep(1000);
      assert !timeStamp.equals(filter.timestampAsHeaderString());
      // verify(filter);
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    */
   @BeforeClass
   protected void createFilter() {
      injector = Guice.createInjector(new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "foo");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to(
                     "bar");
            bind(DateService.class);

         }
      });
      filter = injector.getInstance(RequestAuthorizeSignature.class);
   }

}