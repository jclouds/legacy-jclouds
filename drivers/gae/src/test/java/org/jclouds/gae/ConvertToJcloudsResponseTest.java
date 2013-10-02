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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.crypto.Crypto;
import org.jclouds.date.internal.DateServiceDateCodecFactory;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ConvertToJcloudsResponseTest {
   ConvertToJcloudsResponse req;
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
      req = new ConvertToJcloudsResponse(new DefaultContentMetadataCodec(new DateServiceDateCodecFactory(
            new SimpleDateFormatDateService())));
   }

   @Test
   void testConvertHostHeaderToEndPoint() {
      // TODO
   }

   @Test
   void testConvertWithHeaders() throws IOException {
      HTTPResponse gaeResponse = createMock(HTTPResponse.class);
      expect(gaeResponse.getResponseCode()).andReturn(200);
      List<HTTPHeader> headers = Lists.newArrayList();
      headers.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
      expect(gaeResponse.getHeaders()).andReturn(headers);
      expect(gaeResponse.getContent()).andReturn(null).atLeastOnce();
      replay(gaeResponse);
      HttpResponse response = req.apply(gaeResponse);
      assertEquals(response.getStatusCode(), 200);
      assertEquals(response.getPayload(), null);
      assertEquals(response.getHeaders().size(), 0);
   }

   @Test
   void testConvertWithContent() throws IOException {
      HTTPResponse gaeResponse = createMock(HTTPResponse.class);
      expect(gaeResponse.getResponseCode()).andReturn(200);
      List<HTTPHeader> headers = Lists.newArrayList();
      headers.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
      expect(gaeResponse.getHeaders()).andReturn(headers);
      expect(gaeResponse.getContent()).andReturn("hello".getBytes()).atLeastOnce();
      replay(gaeResponse);
      HttpResponse response = req.apply(gaeResponse);
      assertEquals(response.getStatusCode(), 200);
      assertEquals(Strings2.toString(response.getPayload()), "hello");
      assertEquals(response.getHeaders().size(), 0);
      assertEquals(response.getPayload().getContentMetadata().getContentType(), "text/xml");
   }

}
