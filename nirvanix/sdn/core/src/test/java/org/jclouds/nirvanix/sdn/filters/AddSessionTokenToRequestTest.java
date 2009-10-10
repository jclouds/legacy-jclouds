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
package org.jclouds.nirvanix.sdn.filters;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jclouds.http.HttpRequest;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.rest.internal.RuntimeDelegateImpl;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

@Test(groups = "unit", testName = "sdn.AddSessionTokenToRequestTest")
public class AddSessionTokenToRequestTest {

   private Injector injector;
   private AddSessionTokenToRequest filter;

   @DataProvider
   public Object[][] dataProvider() {
      return new Object[][] { { new HttpRequest(HttpMethod.GET, URI.create("https://host:443")) },
               { new HttpRequest(HttpMethod.GET, URI.create("https://host/path")) },
               { new HttpRequest(HttpMethod.GET, URI.create("https://host/?query"))

               } };
   }

   @Test(dataProvider = "dataProvider")
   public void testRequests(HttpRequest request) {
      String token = filter.getSessionToken();

      String query = request.getEndpoint().getQuery();
      request = filter.filter(request);
      assertEquals(request.getEndpoint().getQuery(), query == null ? "sessionToken=" + token
               : query + "&sessionToken=" + token);
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      String token = filter.getSessionToken();
      for (int i = 0; i < 10; i++)
         filter.updateIfTimeOut();
      assert token.equals(filter.getSessionToken());
   }

   /**
    * before class, as we need to ensure that the filter is threadsafe.
    * 
    */
   @BeforeClass
   protected void createFilter() {
      injector = Guice.createInjector(new AbstractModule() {

         protected void configure() {
            RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
            bind(DateService.class);
         }

         @SuppressWarnings("unused")
         @SessionToken
         @Provides
         String authTokenProvider() {
            return System.currentTimeMillis() + "";
         }
      });
      filter = injector.getInstance(AddSessionTokenToRequest.class);
   }

}