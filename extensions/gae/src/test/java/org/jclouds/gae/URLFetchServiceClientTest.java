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
package org.jclouds.gae;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class URLFetchServiceClientTest {
   URLFetchServiceClient client;
   URI endPoint;

   @BeforeTest
   void setupClient() throws MalformedURLException {
      endPoint = URI.create("http://localhost:80");
      client = new URLFetchServiceClient(createNiceMock(URLFetchService.class),
               createNiceMock(DelegatingRetryHandler.class),
               createNiceMock(DelegatingErrorHandler.class));
   }

   @Test
   void testConvertHostHeaderToEndPoint() {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.getHeaders().put(HttpHeaders.HOST, "weird");
      client.convertHostHeaderToEndPoint(request);
      assertEquals(request.getEndPoint().getHost(), "weird");
      assert request.getFirstHeaderOrNull(HttpHeaders.HOST) == null;
   }

   @Test
   void testConvertWithHeaders() {
      HTTPResponse gaeResponse = createMock(HTTPResponse.class);
      expect(gaeResponse.getResponseCode()).andReturn(200);
      List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
      headers.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
      expect(gaeResponse.getHeaders()).andReturn(headers);
      expect(gaeResponse.getContent()).andReturn(null).atLeastOnce();
      replay(gaeResponse);
      HttpResponse response = client.convert(gaeResponse);
      assertEquals(response.getStatusCode(), 200);
      assertEquals(response.getContent(), null);
      assertEquals(response.getHeaders().size(), 1);
      assertEquals(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "text/xml");
   }

   @Test
   void testConvertWithContent() throws IOException {
      HTTPResponse gaeResponse = createMock(HTTPResponse.class);
      expect(gaeResponse.getResponseCode()).andReturn(200);
      List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
      headers.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
      expect(gaeResponse.getHeaders()).andReturn(headers);
      expect(gaeResponse.getContent()).andReturn("hello".getBytes()).atLeastOnce();
      replay(gaeResponse);
      HttpResponse response = client.convert(gaeResponse);
      assertEquals(response.getStatusCode(), 200);
      assertEquals(IOUtils.toString(response.getContent()), "hello");
      assertEquals(response.getHeaders().size(), 1);
      assertEquals(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE), "text/xml");
   }

   @Test
   void testConvertRequestGetsTargetAndUri() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      HTTPRequest gaeRequest = client.convert(request);
      assertEquals(gaeRequest.getURL().getPath(), "/foo");
   }

   @Test
   void testConvertRequestSetsFetchOptions() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      HTTPRequest gaeRequest = client.convert(request);
      assert gaeRequest.getFetchOptions() != null;
   }

   @Test
   void testConvertRequestSetsHeaders() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.getHeaders().put("foo", "bar");
      HTTPRequest gaeRequest = client.convert(request);
      assertEquals(gaeRequest.getHeaders().get(0).getName(), "foo");
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "bar");
   }

   @Test
   void testConvertRequestNoContent() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      HTTPRequest gaeRequest = client.convert(request);
      assert gaeRequest.getPayload() == null;
      assertEquals(gaeRequest.getHeaders().size(), 1);// content length
   }

   @Test
   void testConvertRequestStringContent() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.setPayload("hoot!");
      testHoot(request);
   }

   @Test
   void testConvertRequestInputStreamContent() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.setPayload(IOUtils.toInputStream("hoot!"));
      testHoot(request);
   }

   @Test
   void testConvertRequestBytesContent() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.setPayload("hoot!".getBytes());
      testHoot(request);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   void testConvertRequestBadContent() throws IOException {
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.setPayload(new Date());
      client.convert(request);

   }

   @Test
   @Parameters("basedir")
   void testConvertRequestFileContent(String basedir) throws IOException {
      File file = new File(basedir, "target/testfiles/hoot");
      file.getParentFile().mkdirs();
      IOUtils.write("hoot!", new FileOutputStream(file));
      HttpRequest request = new HttpRequest(endPoint, HttpMethod.GET, "foo");
      request.setPayload(file);
      testHoot(request);
   }

   private void testHoot(HttpRequest request) throws IOException {
      request.getHeaders().put(HttpHeaders.CONTENT_TYPE, "text/plain");
      HTTPRequest gaeRequest = client.convert(request);
      assertEquals(gaeRequest.getHeaders().get(0).getName(), HttpHeaders.CONTENT_TYPE);
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "text/plain");
      assertEquals(new String(gaeRequest.getPayload()), "hoot!");
   }
}
