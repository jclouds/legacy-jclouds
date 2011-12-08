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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.tmrk.enterprisecloud.binders;

import com.sun.jersey.api.uri.UriBuilderImpl;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * Tests {@code BindDateToQueryParam}
 * @author Jason King
 */
@Test(groups = "unit", testName = "BindDateToQueryParamTest")
public class BindDateToQueryParamTest {

   private DateService dateService;
   private BindDateToQueryParam binder;
   
   @BeforeMethod
   public void setUp() {
      dateService = new SimpleDateFormatDateService();
      Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {
         @Override
         public UriBuilder get() {
            return new UriBuilderImpl();
         }
      };
      binder = new BindMyDateToQueryParam(uriBuilderProvider);
   }
   
   public void testNullDate() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://localhost:9999"));
      HttpRequest result = binder.bindToRequest(request, null);
      assertEquals(result.getRequestLine(),"GET https://localhost:9999 HTTP/1.1");
   }
   
   public void testDatePresent() {
      HttpRequest request = new HttpRequest("GET", URI.create("https://localhost:9999"));
      Date date = dateService.iso8601SecondsDateParse("2011-12-08T15:09:33Z");
      HttpRequest result = binder.bindToRequest(request, date);
      assertEquals(result.getRequestLine(),"GET https://localhost:9999?mydate=2011-12-08T15%3A09%3A33Z HTTP/1.1");
   }
   
   
   private static class BindMyDateToQueryParam extends BindDateToQueryParam {
      public BindMyDateToQueryParam(Provider<UriBuilder> uriBuilderProvider) {
         super(uriBuilderProvider, "mydate");
      }
   }
}
