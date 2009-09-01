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
package org.jclouds.azure.storage.filters;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

@Test(groups = "unit", testName = "azurestorage.SharedKeyAuthenticationTest")
public class SharedKeyAuthenticationTest {

   private static final String KEY = HttpUtils.toBase64String("bar".getBytes());
   private static final String ACCOUNT = "foo";
   private Injector injector;
   private SharedKeyAuthentication filter;

   @DataProvider(parallel = true)
   public Object[][] dataProvider() {
      return new Object[][] {
               { new HttpRequest(
                        HttpMethod.PUT,
                        URI
                                 .create("http://"
                                          + ACCOUNT
                                          + ".blob.core.windows.net/movies/MOV1.avi?comp=block&blockid=BlockId1&timeout=60")) },
               { new HttpRequest(HttpMethod.PUT, URI.create("http://" + ACCOUNT
                        + ".blob.core.windows.net/movies/MOV1.avi?comp=blocklist&timeout=120")) },
               { new HttpRequest(HttpMethod.GET, URI.create("http://" + ACCOUNT
                        + ".blob.core.windows.net/movies/MOV1.avi")) } };
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
   void testAclQueryStringRoot() {
      URI host = URI.create("http://" + ACCOUNT + ".blob.core.windows.net/?comp=list");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?comp=list");
   }

   @Test
   void testAclQueryStringRelative() {
      URI host = URI.create("http://" + ACCOUNT
               + ".blob.core.windows.net/mycontainer?restype=container");
      HttpRequest request = new HttpRequest(HttpMethod.GET, host);
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/mycontainer?restype=container");
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
            bindConstant().annotatedWith(
                     Names.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(ACCOUNT);
            bindConstant().annotatedWith(
                     Names.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(KEY);
            bind(DateService.class);

         }
      });
      filter = injector.getInstance(SharedKeyAuthentication.class);
   }

}