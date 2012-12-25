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

}
