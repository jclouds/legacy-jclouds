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
package org.jclouds.http.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.http.HttpHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.sun.jersey.api.uri.UriBuilderImpl;

@Test(groups = { "unit" })
public class ParseURIFromListOrLocationHeaderIf20xTest {
   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   @Test
   public void testExceptionWhenNoContentOn200() throws ExecutionException, InterruptedException,
            TimeoutException, IOException {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/uri-list");
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
   public void testExceptionWhenIOExceptionOn200() throws ExecutionException, InterruptedException,
            TimeoutException, IOException {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/uri-list");
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
   public void testResponseOk() throws Exception {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/uri-list");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andReturn(Strings2.toInputStream("http://locahost")).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://locahost"));

      verify(payload);
      verify(response);
   }

   @Test
   public void testResponseLocationOk() throws Exception {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(HttpHeaders.LOCATION)).andReturn("http://locahost");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://locahost"));
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponseLowercaseLocationOk() throws Exception {
      Function<HttpResponse, URI> function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(HttpHeaders.LOCATION)).andReturn(null);
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
   public void testResponsePathLocationOk() throws Exception {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      Payload payload = createMock(Payload.class);

      function.setContext(request);
      expect(request.getEndpoint()).andReturn(URI.create("http://new/fd")).atLeastOnce();
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(HttpHeaders.LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(request);
      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://new/path"));
      verify(request);
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponsePathPortLocationOk() throws Exception {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      Payload payload = createMock(Payload.class);

      function.setContext(request);
      expect(request.getEndpoint()).andReturn(URI.create("http://new:8080/fd")).atLeastOnce();
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(HttpHeaders.LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(request);
      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("http://new:8080/path"));
      verify(request);
      verify(response);
      verify(payload);

   }

   @Test
   public void testResponsePathSchemeLocationOk() throws Exception {
      ParseURIFromListOrLocationHeaderIf20x function = new ParseURIFromListOrLocationHeaderIf20x(
               uriBuilderProvider);
      HttpResponse response = createMock(HttpResponse.class);
      GeneratedHttpRequest request = createMock(GeneratedHttpRequest.class);
      Payload payload = createMock(Payload.class);

      function.setContext(request);
      expect(request.getEndpoint()).andReturn(URI.create("https://new/fd")).atLeastOnce();
      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE)).andReturn("text/plain");
      expect(response.getFirstHeaderOrNull(HttpHeaders.LOCATION)).andReturn("path");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      payload.release();

      replay(request);
      replay(payload);
      replay(response);
      assertEquals(function.apply(response), URI.create("https://new/path"));
      verify(request);
      verify(response);
      verify(payload);

   }
}
