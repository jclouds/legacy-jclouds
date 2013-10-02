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
package org.jclouds.gae;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.internal.DateServiceDateCodecFactory;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.io.Payloads;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.repackaged.com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ConvertToGaeRequestTest {
   ConvertToGaeRequest req;
   URI endPoint;

   protected volatile static Crypto crypto;
   static {
      try {
         crypto = new JCECrypto();
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (CertificateException e) {
         Throwables.propagate(e);
      }
   }

   @BeforeTest
   void setupClient() {
      endPoint = URI.create("http://localhost:80/foo");
      req = new ConvertToGaeRequest(new HttpUtils(0, 0, 0, 0), new DefaultContentMetadataCodec(
            new DateServiceDateCodecFactory(new SimpleDateFormatDateService())));
   }

   @Test
   void testConvertRequestGetsTargetAndUri() throws IOException {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(endPoint).build();
      HTTPRequest gaeRequest = req.apply(request);
      assertEquals(gaeRequest.getURL().getPath(), "/foo");
   }

   @Test
   void testConvertRequestSetsFetchOptions() throws IOException {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(endPoint).build();
      HTTPRequest gaeRequest = req.apply(request);
      assert gaeRequest.getFetchOptions() != null;
   }

   @Test
   void testConvertRequestSetsHeaders() throws IOException {
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .addHeader("foo", "bar").build();
      HTTPRequest gaeRequest = req.apply(request);
      assertEquals(gaeRequest.getHeaders().get(0).getName(), "foo");
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "bar");
   }

   @Test
   void testConvertRequestNoContent() throws IOException {
      HttpRequest request = HttpRequest.builder().method(HttpMethod.GET).endpoint(endPoint).build();
      HTTPRequest gaeRequest = req.apply(request);
      assert gaeRequest.getPayload() == null;
      assertEquals(gaeRequest.getHeaders().size(), 1);// user agent
      assertEquals(gaeRequest.getHeaders().get(0).getName(), HttpHeaders.USER_AGENT);
      assertEquals(gaeRequest.getHeaders().get(0).getValue(), "jclouds/1.0 urlfetch/1.4.3");
   }

   @Test
   void testConvertRequestStringContent() throws IOException {
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .payload("hoot!").build();
      testHoot(request);
   }

   @Test
   void testConvertRequestInputStreamContent() throws IOException {
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .payload(Strings2.toInputStream("hoot!")).build();
      request.getPayload().getContentMetadata().setContentLength(5l);
      testHoot(request);
   }

   @Test
   void testConvertRequestBytesContent() throws IOException {
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .payload("hoot!".getBytes()).build();
      testHoot(request);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   void testConvertRequestBadContent() throws IOException {
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .payload(Payloads.newPayload(new Date())).build();
      req.apply(request);
   }

   @Test
   @Parameters("basedir")
   void testConvertRequestFileContent(String basedir) throws IOException {
      File file = new File(basedir, "target/testfiles/hoot");
      file.getParentFile().mkdirs();
      Files.write("hoot!".getBytes(Charsets.UTF_8), file);
      HttpRequest request = HttpRequest.builder()
                                       .method(HttpMethod.GET)
                                       .endpoint(endPoint)
                                       .payload(file).build();
      testHoot(request);
   }

   private void testHoot(HttpRequest request) throws IOException {
      request.getPayload().getContentMetadata().setContentType("text/plain");
      request.getPayload().getContentMetadata().setContentMD5(new byte[] { 1, 2, 3, 4 });
      HTTPRequest gaeRequest = req.apply(request);

      StringBuilder builder = new StringBuilder();
      for (HTTPHeader header : gaeRequest.getHeaders()) {
         builder.append(header.getName()).append(": ").append(header.getValue()).append("\n");
      }
      assertEquals(builder.toString(),
      // note content-length is prohibited in gae
            "User-Agent: jclouds/1.0 urlfetch/1.4.3\nExpect: 100-continue\nContent-Type: text/plain\nContent-MD5: AQIDBA==\n");
      assertEquals(new String(gaeRequest.getPayload()), "hoot!");
   }
}
