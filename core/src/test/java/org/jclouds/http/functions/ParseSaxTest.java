/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Tests behavior of {@code ParseSax}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseSaxTest")
public class ParseSaxTest extends BaseHandlerTest {
   public static class TestHandler extends ParseSax.HandlerWithResult<String> {
      @Override
      public String getResult() {
         return "";
      }
   }

   ParseSax<String> createParser() {
      return factory.create(injector.getInstance(TestHandler.class));
   }

   @Test
   public void testAddDetailsAndPropagateOkWhenRequestWithNoDataAndRuntimeExceptionThrowsOriginalException()
         throws ExecutionException, InterruptedException, TimeoutException, IOException {

      ParseSax<String> parser = createParser();
      Exception input = new RuntimeException("foo");

      try {
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e, input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWhenRequestWithNoDataAndExceptionPropagates() throws ExecutionException,
         InterruptedException, TimeoutException, IOException {

      ParseSax<String> parser = createParser();
      Exception input = new Exception("foo");

      try {
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWhenRequestIsNotNullAndResponseIsNull() throws ExecutionException,
         InterruptedException, TimeoutException, IOException {

      ParseSax<String> parser = createParser();
      HttpRequest request = new HttpRequest("GET", URI.create("http://foohost"));
      Exception input = new Exception("foo");

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(null, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "request: GET http://foohost HTTP/1.1; cause: java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWithValidRequestResponse() throws ExecutionException, InterruptedException,
         TimeoutException, IOException {

      ParseSax<String> parser = createParser();
      HttpRequest request = new HttpRequest("GET", URI.create("http://foohost"));
      HttpResponse response = new HttpResponse(304, "Not Modified", null);
      Exception input = new Exception("foo");

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(response, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(), "request: GET http://foohost HTTP/1.1; response: HTTP/1.1 304 Not Modified; cause: java.lang.Exception: foo");
         assertEquals(e.getCause(), input);
      }
   }

   @Test
   public void testAddDetailsAndPropagateOkWithValidRequestResponseWithSAXParseException() throws ExecutionException,
         InterruptedException, TimeoutException, IOException {

      ParseSax<String> parser = createParser();
      HttpRequest request = new HttpRequest("GET", URI.create("http://foohost"));
      HttpResponse response = new HttpResponse(304, "Not Modified", null);
      Locator locator = createMock(Locator.class);
      expect(locator.getColumnNumber()).andReturn(1);
      expect(locator.getLineNumber()).andReturn(1);
      expect(locator.getPublicId()).andReturn("publicId");
      expect(locator.getSystemId()).andReturn("systemId");
      replay(locator);
      Exception input = new SAXParseException("foo", locator);
      verify(locator);

      try {
         parser.setContext(request);
         parser.addDetailsAndPropagate(response, input);
      } catch (RuntimeException e) {
         assertEquals(e.getMessage(),
               "request: GET http://foohost HTTP/1.1; response: HTTP/1.1 304 Not Modified; error at 1:1 in document systemId; cause: org.xml.sax.SAXParseException: foo");
         assertEquals(e.getCause(), input);
      }
   }

}