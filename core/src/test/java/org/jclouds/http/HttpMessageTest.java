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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "HttpMessageTest")
public class HttpMessageTest {

   public void testEndpoint() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foo").build();

      assertEquals(request.toBuilder()
                          .endpoint("http://bar").build(), 
              HttpRequest.builder()
                         .method("GET")
                         .endpoint("http://bar").build());
   }

   public void testReplaceHeader() {
      HttpRequest request = HttpRequest.builder()
                                       .method("GET")
                                       .endpoint("http://foo")
                                       .addHeader("foo", "bar").build();

      assertEquals(request.toBuilder().replaceHeader("foo", "baz").build(), 
               HttpRequest.builder()
                          .method("GET")
                          .endpoint("http://foo")
                          .addHeader("foo", "baz").build());
   }

   public void testRemoveHeader() {
      HttpRequest request = HttpRequest.builder()
                                       .method("GET")
                                       .endpoint("http://foo")
                                       .addHeader("foo", "bar").build();

      assertEquals(request.toBuilder().removeHeader("foo").build(),
               HttpRequest.builder().method("GET").endpoint("http://foo").build());
   }

   public void testReplaceHeaders() {
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://foo")
                                       .addHeader("foo", "bar")
                                       .addHeader("rabbit", "tree").build();

      assertEquals(
               request.toBuilder()
                      .replaceHeaders(ImmutableMultimap.of("foo", "bar", "rabbit", "robot", "robert", "baz")).build().getHeaders(),
               ImmutableMultimap.of("foo", "bar", "rabbit", "robot", "robert", "baz"));
   }

   public void testPutHeadersAddsAnotherValue() {
      HttpRequest request = HttpRequest.builder()
                                       .method("GET").endpoint("http://foo")
                                       .addHeader("foo", "bar").build();

      assertEquals(request.toBuilder()
                          .addHeader("foo", "baz").build().getHeaders(), 
                   ImmutableMultimap.<String, String> builder()
                                    .putAll("foo", "bar", "baz").build());
   }

}
