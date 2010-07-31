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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.common.base.Throwables;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ConvertToJcloudsResponseTest {
   ConvertToJcloudsResponse req;
   URI endPoint;

   protected volatile static EncryptionService encryptionService;
   static {
      try {
         encryptionService = new JCEEncryptionService();
      } catch (NoSuchAlgorithmException e) {
         Throwables.propagate(e);
      } catch (CertificateException e) {
         Throwables.propagate(e);
      }
   }

   @BeforeTest
   void setupClient() throws MalformedURLException {
      endPoint = URI.create("http://localhost:80/foo");
      req = new ConvertToJcloudsResponse(new HttpUtils(encryptionService, 0, 0, 0, 0));
   }

   @Test
   void testConvertHostHeaderToEndPoint() {
      // TODO
   }

   @Test
   void testConvertWithHeaders() throws IOException {
      HTTPResponse gaeResponse = createMock(HTTPResponse.class);
      expect(gaeResponse.getResponseCode()).andReturn(200);
      List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
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
      List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
      headers.add(new HTTPHeader(HttpHeaders.CONTENT_TYPE, "text/xml"));
      expect(gaeResponse.getHeaders()).andReturn(headers);
      expect(gaeResponse.getContent()).andReturn("hello".getBytes()).atLeastOnce();
      replay(gaeResponse);
      HttpResponse response = req.apply(gaeResponse);
      assertEquals(response.getStatusCode(), 200);
      assertEquals(Utils.toStringAndClose(response.getPayload().getInput()), "hello");
      assertEquals(response.getHeaders().size(), 0);
      assertEquals(response.getPayload().getContentType(), "text/xml");
   }

}
