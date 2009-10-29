/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http.httpnio.util;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.apache.http.HttpEntityEnclosingRequest;
import org.jclouds.http.HttpRequest;
import org.mortbay.jetty.HttpMethods;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests parsing of nio
 * 
 * @author Adrian Cole
 */
@Test(testName = "httpnio.NioHttpUtilsTest")
public class NioHttpUtilsTest {
   @DataProvider(name = "gets")
   public Object[][] createData() {
      return new Object[][] { { "object" }, { "/path" }, { "sp%20ace" }, { "unic¿de" },
               { "qu?stion" } };
   }

   @Test(dataProvider = "gets")
   public void testConvert(String uri) {
      HttpEntityEnclosingRequest apacheRequest = NioHttpUtils
               .convertToApacheRequest(new HttpRequest(HttpMethods.GET, URI
                        .create("https://s3.amazonaws.com:443/" + uri), ImmutableMultimap.of(
                        "Host", "s3.amazonaws.com")));
      assertEquals(apacheRequest.getHeaders("Host")[0].getValue(), "s3.amazonaws.com");
      assertEquals(apacheRequest.getRequestLine().getMethod(), "GET");
      assertEquals(apacheRequest.getRequestLine().getUri(), "/" + uri);
   }

   public void testConvertWithQuery() {
      HttpEntityEnclosingRequest apacheRequest = NioHttpUtils
               .convertToApacheRequest(new HttpRequest(HttpMethods.GET, URI
                        .create("https://s3.amazonaws.com:443/?max-keys=0"), ImmutableMultimap.of(
                        "Host", "s3.amazonaws.com")));
      assertEquals(apacheRequest.getHeaders("Host")[0].getValue(), "s3.amazonaws.com");
      assertEquals(apacheRequest.getRequestLine().getMethod(), "GET");
      assertEquals(apacheRequest.getRequestLine().getUri(), "/?max-keys=0");
   }
}
