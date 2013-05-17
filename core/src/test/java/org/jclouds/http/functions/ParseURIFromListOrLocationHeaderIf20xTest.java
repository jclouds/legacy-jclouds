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
package org.jclouds.http.functions;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.LOCATION;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.util.Strings2.toInputStream;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.testng.annotations.Test;

import com.google.common.base.Function;

@Test(groups = { "unit" })
public class ParseURIFromListOrLocationHeaderIf20xTest {

   @Test
   public void testExceptionWhenNoContentOn200() {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/uri-list");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andReturn(null);
      payload.release();

      replay(payload);
      replay(response);
      try {
         function.apply(response);
      } catch (Exception e) {
         assert e.getMessage().equals("no content");
      }
      verify(payload);
      verify(response);
   }

   @Test
   public void testExceptionWhenIOExceptionOn200() {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/uri-list");
      RuntimeException exception = new RuntimeException("bad");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andThrow(exception);
      payload.release();

      replay(payload);
      replay(response);
      try {
         function.apply(response);
      } catch (Exception e) {
         assert e.equals(exception);
      }
      verify(payload);
      verify(response);
   }

   @Test
   public void testResponseOk() {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/uri-list");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andReturn(toInputStream("http://locahost")).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://locahost"));

      verify(payload);
      verify(response);
   }

   @Test
   public void testResponseLocationOk() {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(LOCATION)).andReturn("http://locahost");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://locahost"));
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponseLowercaseLocationOk() {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(LOCATION)).andReturn(null);
      expect(response.getFirstHeaderOrNull("location")).andReturn("http://locahost");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://locahost"));
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponsePathLocationOk() {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://new/fd").build();
      
      function.setContext(request);
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://new/path"));
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponsePathPortLocationOk() {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://new:8080/fd").build();

      function.setContext(request);
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://new:8080/path"));
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponsePathSchemeLocationOk() {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("https://new/fd").build();

      function.setContext(request);
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("https://new/path"));
      verify(response);
      verify(payload);

   }
}
