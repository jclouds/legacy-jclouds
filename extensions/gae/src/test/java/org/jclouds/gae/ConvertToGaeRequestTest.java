/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gae;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.Payloads;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.repackaged.com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ConvertToGaeRequestTest {
   ConvertToGaeRequest req;
   URI endPoint;

   @BeforeTest
   void setupClient() throws MalformedURLException {
      endPoint = URI.create("http://localhost:80/foo");
      req = new ConvertToGaeRequest(new JCEEncryptionService());
   }

   @Test
   void testConvertRequestGetsTargetAndUri() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      HTTPRequest gaeRequest = req.apply(request);
      assertEquals(gaeRequest.getURL().getPath(), "/foo");
   }

   @Test
   void testConvertRequestSetsFetchOptions() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      HTTPRequest gaeRequest = req.apply(request);
      assert gaeRequest.getFetchOptions() != null;
   }

   @Test
   void testConvertRequestSetsHeaders() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      request.getHeaders().put("foo", "bar");
      HTTPRequest gaeRequest = req.apply(request);
      assertEquals(gaeRequest.getHeaders().get(0).getName(), "foo");
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "bar");
   }

   @Test
   void testConvertRequestNoContent() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      HTTPRequest gaeRequest = req.apply(request);
      assert gaeRequest.getPayload() == null;
      assertEquals(gaeRequest.getHeaders().size(), 2);// content length, user agent
      assertEquals(gaeRequest.getHeaders().get(0).getName(), HttpHeaders.USER_AGENT);
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "jclouds/1.0 urlfetch/1.3.2");
   }

   @Test
   void testConvertRequestStringContent() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      request.setPayload("hoot!");
      testHoot(request);
   }

   @Test
   void testConvertRequestInputStreamContent() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      request.setPayload(Utils.toInputStream("hoot!"));
      testHoot(request);
   }

   @Test
   void testConvertRequestBytesContent() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      request.setPayload("hoot!".getBytes());
      testHoot(request);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   void testConvertRequestBadContent() throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      request.setPayload(Payloads.newPayload(new Date()));
      req.apply(request);
   }

   @Test
   @Parameters("basedir")
   void testConvertRequestFileContent(String basedir) throws IOException {
      HttpRequest request = new HttpRequest(HttpMethod.GET, endPoint);
      File file = new File(basedir, "target/testfiles/hoot");
      file.getParentFile().mkdirs();
      Files.write("hoot!".getBytes(Charsets.UTF_8), file);
      request.setPayload(file);
      testHoot(request);
   }

   private void testHoot(HttpRequest request) throws IOException {
      request.getHeaders().put(HttpHeaders.CONTENT_TYPE, "text/plain");
      HTTPRequest gaeRequest = req.apply(request);
      try {
         assertEquals(gaeRequest.getHeaders().get(0).getName(), HttpHeaders.CONTENT_TYPE);
         assertEquals(gaeRequest.getHeaders().get(0).getValue(), "text/plain");
      } catch (AssertionError e) {
         assertEquals(gaeRequest.getHeaders().get(1).getName(), HttpHeaders.CONTENT_TYPE);
         assertEquals(gaeRequest.getHeaders().get(1).getValue(), "text/plain");
      }
      assertEquals(new String(gaeRequest.getPayload()), "hoot!");
   }
}
