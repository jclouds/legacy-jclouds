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
package org.jclouds.http;

import static com.google.common.net.MediaType.FORM_DATA;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "http.HttpRequestTest")
public class HttpRequestTest {
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testConstructorHostNull() throws Exception {
      URI uri = URI.create("http://adriancole.compute1138eu.s3-external-3.amazonaws.com:-1");
      assert uri.getHost() == null : "test requires something to produce a uri with a null hostname";
      HttpRequest.builder().method("GET").endpoint(uri).build();
   }

   public void testReplaceQueryParams() throws Exception {
      URI uri = URI.create("http://goo.com:443?header=value1");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(uri).build();

      assertEquals(request.toBuilder().replaceQueryParam("header", "foo").build(), HttpRequest.builder().method("GET")
               .endpoint("http://goo.com:443?header=foo").build());
   }

   // it is easy to accidentally encode twice. make sure this always works!
   public void testEncodesOnlyOnce() throws Exception {
      URI uri = URI.create("http://goo.com:443?header=value1");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(uri).build();

      assertEquals(request.toBuilder().replaceQueryParam("header", "hello?").build(),
            HttpRequest.builder().method("GET").endpoint("http://goo.com:443?header=hello%3F").build());
   }

   public void testAddFormParamAddsAnotherValue() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foo")
               .payload("foo=bar").build();
      Payload payload = Payloads.newStringPayload("foo=bar&foo=baz");
      payload.getContentMetadata().setContentType(FORM_DATA.toString());
      assertEquals(request.toBuilder().addFormParams(ImmutableMultimap.of("foo", "baz")).build(), HttpRequest
               .builder().method("GET").endpoint("http://foo").payload(payload).build());
   }

   // the following caused issues for the fgcp provider
   // (see RequestAuthenticator#addQueryParamsToRequest)
   // base64 symbols should be url encoded in query param
   // note that + ends up encoded as %20 (space), not %2B (plus)
   public void testAddingBase64EncodedQueryParamCausingPlusToUrlEncodedSpaceConversion() {
      String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
      URI uri = URI
            .create("http://goo.com:443?header1=valueWithUrlEncoded%2BPlus");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(uri)
      // addQueryParam invocation causes %2B's in prev. params to
      // convert to %20.
            .addQueryParam("header2", base64Chars).build();

      assertEquals(
            request.getRequestLine(),
            "GET http://goo.com:443?header1=valueWithUrlEncoded%20Plus&header2=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%20/%3D HTTP/1.1");
   }

   // note that + ends up encoded as %20 (space) in the first param, %2B (plus)
   // in the last param and %2F converts back into slash
   public void testAddBase64AndUrlEncodedQueryParams() {
      String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%2B%2F%3D";
      URI uri = URI.create("http://goo.com:443?header1=" + base64Chars);
      HttpRequest request = HttpRequest.builder()
            .method("GET")
            .endpoint(uri)
            // the addition of another param causes %2B's in prev. params to
            // convert to %20.
            .addQueryParam("header2", base64Chars)
            .build();

      assertEquals(
            request.getRequestLine(),
            "GET http://goo.com:443?header1=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%20/%3D&header2=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789%2B/%3D HTTP/1.1");
   }

   // base64 symbols with newline separator should be url encoded in query param
   public void testAddBase64EncodedQueryParamWithNewlines() {
      String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n0123456789%2B/=";
      URI uri = URI.create("http://goo.com:443?header1=value1");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint(uri)
            .addQueryParam("header2", base64Chars).build();

      assertEquals(
            request.getRequestLine(),
            "GET http://goo.com:443?header1=value1&header2=ABCDEFGHIJKLMNOPQRSTUVWXYZ%0Aabcdefghijklmnopqrstuvwxyz%0A0123456789%2B/%3D HTTP/1.1");
   }
}
