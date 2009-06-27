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

import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.util.DateService;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "s3.RequestAuthorizeSignatureTest")
public class RequestAuthorizeSignatureTest {

   @Test
   void testAppendBucketNameHostHeader() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(host, HttpMethod.GET, "/");
      request.getHeaders().put(HttpHeaders.HOST, "adriancole.s3int5.s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      RequestAuthorizeSignature.appendBucketName(request, builder);
      assertEquals(builder.toString(), "/adriancole.s3int5");
   }

   @Test
   void testAppendBucketNameHostHeaderService() {
      URI host = URI.create("http://s3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(host, HttpMethod.GET, "/");
      request.getHeaders().put(HttpHeaders.HOST, "s3.amazonaws.com");
      StringBuilder builder = new StringBuilder();
      RequestAuthorizeSignature.appendBucketName(request, builder);
      assertEquals(builder.toString(), "");
   }

   @Test
   void testAppendBucketNameURIHost() {
      URI host = URI.create("http://adriancole.s3int5.s3-external-3.amazonaws.com:80");
      HttpRequest request = new HttpRequest(host, HttpMethod.GET, "/");
      StringBuilder builder = new StringBuilder();
      RequestAuthorizeSignature.appendBucketName(request, builder);
      assertEquals(builder.toString(), "/adriancole.s3int5");
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      RequestAuthorizeSignature filter = createFilter();
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

   private RequestAuthorizeSignature createFilter() {
      return Guice.createInjector(new AbstractModule() {

         protected void configure() {
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "foo");
            bindConstant().annotatedWith(Names.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to(
                     "bar");
            bind(DateService.class);

         }
      }).getInstance(RequestAuthorizeSignature.class);
   }

}