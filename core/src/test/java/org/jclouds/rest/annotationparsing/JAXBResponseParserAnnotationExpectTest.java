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
package org.jclouds.rest.annotationparsing;

import static org.jclouds.providers.AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests the use of the {@link JAXBResponseParser} annotation.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "JAXBResponseParserAnnotationExpectTest")
public class JAXBResponseParserAnnotationExpectTest extends
      BaseRestClientExpectTest<JAXBResponseParserAnnotationExpectTest.TestJAXBApi> {

   @XmlRootElement(name = "test")
   public static class TestJAXBDomain {
      private String elem;

      public String getElem() {
         return elem;
      }

      public void setElem(String elem) {
         this.elem = elem;
      }

      @Override
      public String toString() {
         return "TestJAXBDomain [elem=" + elem + "]";
      }

   }

   public interface TestJAXBApi extends Closeable {
      public TestJAXBDomain jaxbGetWithAnnotation();

      public Object jaxbGetWithAnnotationAndCustomClass();

      public TestJAXBDomain jaxbGetWithAcceptHeader();

      public String jaxbGetWithTransformer();
   }

   public interface TestJAXBAsyncApi extends Closeable {
      @GET
      @Path("/jaxb/annotation")
      @JAXBResponseParser
      public ListenableFuture<TestJAXBDomain> jaxbGetWithAnnotation();

      @GET
      @Path("/jaxb/custom")
      @JAXBResponseParser(TestJAXBDomain.class)
      public ListenableFuture<Object> jaxbGetWithAnnotationAndCustomClass();

      @GET
      @Path("/jaxb/header")
      @Consumes(MediaType.APPLICATION_XML)
      public ListenableFuture<TestJAXBDomain> jaxbGetWithAcceptHeader();

      @GET
      @Path("/jaxb/transformer")
      @JAXBResponseParser(TestJAXBDomain.class)
      @Transform(ToString.class)
      public ListenableFuture<String> jaxbGetWithTransformer();
   }

   private static class ToString implements Function<Object, String> {
      @Override
      public String apply(Object input) {
         return Functions.toStringFunction().apply(input);
      }
   }

   @Test
   public void testJAXBResponseParserAnnotationWithoutValue() throws SecurityException, NoSuchMethodException {
      TestJAXBApi api = requestSendsResponse( //
            HttpRequest.builder().method("GET").endpoint("http://mock/jaxb/annotation").build(), //
            HttpResponse.builder().statusCode(200).payload("<test><elem>Hello World</elem></test>").build());

      TestJAXBDomain result = api.jaxbGetWithAnnotation();
      assertEquals(result.getElem(), "Hello World");
   }

   @Test
   public void testJAXBResponseParserAnnotationWithCustomValue() throws SecurityException, NoSuchMethodException {
      TestJAXBApi api = requestSendsResponse( //
            HttpRequest.builder().method("GET").endpoint("http://mock/jaxb/custom").build(), //
            HttpResponse.builder().statusCode(200).payload("<test><elem>Hello World</elem></test>").build());

      Object result = api.jaxbGetWithAnnotationAndCustomClass();
      assertTrue(result instanceof TestJAXBDomain);
      assertEquals(TestJAXBDomain.class.cast(result).getElem(), "Hello World");
   }

   @Test
   public void testJAXBResponseParserAnnotationWithAcceptHeader() throws SecurityException, NoSuchMethodException {
      TestJAXBApi api = requestSendsResponse( //
            HttpRequest.builder().method("GET").endpoint("http://mock/jaxb/header")
                  .addHeader("Accept", MediaType.APPLICATION_XML).build(), //
            HttpResponse.builder().statusCode(200).payload("<test><elem>Hello World</elem></test>").build());

      TestJAXBDomain result = api.jaxbGetWithAcceptHeader();
      assertEquals(result.getElem(), "Hello World");
   }

   @Test
   public void testJAXBResponseParserAnnotationWithTransformer() throws SecurityException, NoSuchMethodException {
      TestJAXBApi api = requestSendsResponse( //
            HttpRequest.builder().method("GET").endpoint("http://mock/jaxb/transformer").build(), //
            HttpResponse.builder().statusCode(200).payload("<test><elem>Hello World</elem></test>").build());

      String result = api.jaxbGetWithTransformer();
      assertEquals(result, "TestJAXBDomain [elem=Hello World]");
   }

   @Override
   public ProviderMetadata createProviderMetadata() {
      return forClientMappedToAsyncClientOnEndpoint(TestJAXBApi.class, TestJAXBAsyncApi.class, "http://mock");
   }

}
