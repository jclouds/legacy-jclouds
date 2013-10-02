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
package org.jclouds.rest.binders;

import static org.testng.Assert.assertEquals;

import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.http.HttpRequest;
import org.jclouds.xml.XMLParser;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Tests behavior of {@code BindToXMLPayload}.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindToXMLPayloadTest")
public class BindToXMLPayloadTest {
   XMLParser xml = new JAXBParser("true");

   @Test
   public void testBindJAXBObject() throws SecurityException, NoSuchMethodException {
      BindToXMLPayload binder = new BindToXMLPayload(xml);

      // Build the object to bind
      TestJAXBDomain obj = new TestJAXBDomain();
      obj.setElem("Hello World");

      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      request = binder.bindToRequest(request, obj);
      assertEquals(request.getPayload().getRawContent(), XMLParser.DEFAULT_XML_HEADER
            + "\n<test>\n    <elem>Hello World</elem>\n</test>\n");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/xml");
   }

   @Test
   public void testHeaderIsChangedIfNeeded() throws SecurityException, NoSuchMethodException {
      BindToXMLPayload binder = new BindToXMLPayload(xml);

      // Build the object to bind
      TestJAXBDomain obj = new TestJAXBDomain();
      obj.setElem("Hello World");

      // Add the unknown content-type header to verify it is changed by the
      // binder
      Multimap<String, String> headers = ImmutableMultimap.<String, String> of("Content-type", "application/unknown");
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").headers(headers).build();

      request = binder.bindToRequest(request, obj);
      assertEquals(request.getPayload().getRawContent(), XMLParser.DEFAULT_XML_HEADER
            + "\n<test>\n    <elem>Hello World</elem>\n</test>\n");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/xml");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      BindToXMLPayload binder = new BindToXMLPayload(xml);
      binder.bindToRequest(HttpRequest.builder().method("GET").endpoint("http://momma").build(), null);
   }

   @Test(expectedExceptions = BindException.class)
   public void testInvalidObjectBinding() {
      BindToXMLPayload binder = new BindToXMLPayload(xml);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      request = binder.bindToRequest(request, new Object());
   }

   @XmlRootElement(name = "test")
   public static class TestJAXBDomain {
      private String elem;

      public String getElem() {
         return elem;
      }

      public void setElem(String elem) {
         this.elem = elem;
      }

   }
}
