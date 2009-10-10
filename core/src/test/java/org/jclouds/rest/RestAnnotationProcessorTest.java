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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseURIList;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.DecoratorParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.HostPrefixParam;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapEntityParam;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.decorators.AddAsJsonEntity;
import org.jclouds.rest.decorators.AddAsStringEntity;
import org.jclouds.rest.decorators.MapRequestDecorator;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.DateService;
import org.jclouds.util.Jsr330;
import org.joda.time.DateTime;
import org.mortbay.jetty.HttpHeaders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jaxrs.JaxrsUtilTest")
public class RestAnnotationProcessorTest {
   @Target( { ElementType.METHOD })
   @Retention(RetentionPolicy.RUNTIME)
   @javax.ws.rs.HttpMethod("FOO")
   public @interface FOO {
   }

   @Retention(value = RetentionPolicy.RUNTIME)
   @Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
   @Qualifier
   public @interface Localhost {
   }

   @Retention(value = RetentionPolicy.RUNTIME)
   @Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
   @Qualifier
   public @interface Localhost2 {
   }

   @QueryParams(keys = "x-ms-version", values = "2009-07-17")
   @Endpoint(Localhost.class)
   public class TestQuery {
      @FOO
      @QueryParams(keys = "x-ms-rubbish", values = "bin")
      public void foo() {
      }

      @FOO
      @QueryParams(keys = { "foo", "fooble" }, values = { "bar", "baz" })
      public void foo2() {
      }

      @FOO
      @QueryParams(keys = { "foo", "fooble" }, values = { "bar", "baz" })
      public void foo3(@QueryParam("robbie") String robbie) {
      }
   }

   public void testQuery() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo");
      HttpRequest httpMethod = factory(TestQuery.class).createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getEndpoint().getQuery(), "x-ms-version=2009-07-17&x-ms-rubbish=bin");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   public void testQuery2() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo2");
      HttpRequest httpMethod = factory(TestQuery.class).createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "x-ms-version=2009-07-17&foo=bar&fooble=baz");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   public void testQuery3() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo3", String.class);
      HttpRequest httpMethod = factory(TestQuery.class).createRequest(method,
               new Object[] { "wonder" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "x-ms-version=2009-07-17&foo=bar&fooble=baz&robbie=wonder");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   @Endpoint(Localhost.class)
   public class TestEntityParamVarargs {
      @POST
      public void varargs(HttpRequestOptions... options) {
      }

      @POST
      public void post(HttpRequestOptions options) {
      }
   }

   public void testHttpRequestOptionsEntityParam() throws SecurityException, NoSuchMethodException {
      Method method = TestEntityParamVarargs.class.getMethod("post", HttpRequestOptions.class);
      verifyTestPostOptions(method);
   }

   public void testEntityParamVarargs() throws SecurityException, NoSuchMethodException {
      Method method = TestEntityParamVarargs.class.getMethod("varargs", Array.newInstance(
               HttpRequestOptions.class, 0).getClass());
      verifyTestPostOptions(method);
   }

   private void verifyTestPostOptions(Method method) {
      HttpRequest httpMethod = factory(TestEntityParamVarargs.class).createRequest(method,
               new Object[] { new HttpRequestOptions() {

                  public Multimap<String, String> buildMatrixParameters() {
                     return HashMultimap.create();
                  }

                  public String buildPathSuffix() {
                     return null;
                  }

                  public Multimap<String, String> buildQueryParameters() {
                     return HashMultimap.create();
                  }

                  public Multimap<String, String> buildRequestHeaders() {
                     return HashMultimap.create();
                  }

                  public String buildStringEntity() {
                     return "fooya";
                  }

               } });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("fooya".getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), "fooya");
   }

   @Endpoint(Localhost.class)
   public class TestCustomMethod {
      @FOO
      public void foo() {
      }
   }

   public void testCustomMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestCustomMethod.class.getMethod("foo");
      HttpRequest httpMethod = factory(TestCustomMethod.class).createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   public interface Parent {
      public void foo();
   }

   @Endpoint(Localhost.class)
   public class TestOverridden implements Parent {
      @POST
      public void foo() {
      }
   }

   public void testOverriddenMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestOverridden.class.getMethod("foo");
      HttpRequest httpMethod = factory(TestOverridden.class).createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), "POST");
   }

   @Endpoint(Localhost.class)
   public class TestOverriddenEndpoint implements Parent {

      @POST
      @Endpoint(Localhost2.class)
      public void foo() {
      }

      @POST
      public void foo(@Endpoint URI endpoint) {
      }
   }

   public void testOverriddenEndpointMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestOverriddenEndpoint.class.getMethod("foo");
      HttpRequest httpMethod = factory(TestOverriddenEndpoint.class).createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPort(), 8081);
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), "POST");
   }

   public void testOverriddenEndpointParameter() throws SecurityException, NoSuchMethodException {
      Method method = TestOverriddenEndpoint.class.getMethod("foo", URI.class);
      HttpRequest httpMethod = factory(TestOverriddenEndpoint.class).createRequest(method,
               new Object[] { URI.create("http://wowsa:8001") });
      assertEquals(httpMethod.getEndpoint().getHost(), "wowsa");
      assertEquals(httpMethod.getEndpoint().getPort(), 8001);
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), "POST");
   }

   @Endpoint(Localhost.class)
   public class TestPost {
      @POST
      public void post(@DecoratorParam(AddAsStringEntity.class) String content) {
      }

      @POST
      public void postAsJson(@DecoratorParam(AddAsJsonEntity.class) String content) {
      }

      @POST
      @Path("{foo}")
      public void postWithPath(@PathParam("foo") @MapEntityParam("fooble") String path,
               MapRequestDecorator content) {
      }

      @POST
      @Path("{foo}")
      @MapBinder(AddAsJsonEntity.class)
      public void postWithMethodBinder(@PathParam("foo") @MapEntityParam("fooble") String path) {
      }
   }

   public void testCreatePostRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest httpMethod = factory(TestPost.class).createRequest(method,
               new Object[] { "data" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("data".getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), "data");
   }

   public void testCreatePostJsonRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestPost.class.getMethod("postAsJson", String.class);
      HttpRequest httpMethod = factory(TestPost.class).createRequest(method,
               new Object[] { "data" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/json"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("\"data\"".getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), "\"data\"");
   }

   public void testCreatePostWithPathRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestPost.class.getMethod("postWithPath", String.class,
               MapRequestDecorator.class);
      HttpRequest httpMethod = factory(TestPost.class).createRequest(method,
               new Object[] { "data", new MapRequestDecorator() {
                  public HttpRequest decorateRequest(HttpRequest request,
                           Map<String, String> postParams) {
                     request.setEntity(postParams.get("fooble"));
                     return request;
                  }

                  public HttpRequest decorateRequest(HttpRequest request, Object toBind) {
                     throw new RuntimeException("this shouldn't be used in POST");
                  }
               } });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/data");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(httpMethod.getEntity(), "data");
   }

   public void testCreatePostWithMethodBinder() throws SecurityException, NoSuchMethodException {
      Method method = TestPost.class.getMethod("postWithMethodBinder", String.class);
      HttpRequest httpMethod = factory(TestPost.class).createRequest(method,
               new Object[] { "data", });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/data");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/json"));
      String expected = "{\"fooble\":\"data\"}";
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(expected.getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), expected);
   }

   @Endpoint(Localhost.class)
   public class TestPut {
      @PUT
      @Path("{foo}")
      @MapBinder(AddAsJsonEntity.class)
      public void putWithMethodBinder(@PathParam("foo") @MapEntityParam("fooble") String path) {
      }

      @PUT
      @Path("{foo}")
      @Produces(MediaType.TEXT_PLAIN)
      public void putWithMethodBinderProduces(
               @PathParam("foo") @DecoratorParam(AddAsStringEntity.class) String path) {
      }

      @PUT
      @Path("{foo}")
      @MapBinder(AddAsJsonEntity.class)
      @Consumes(MediaType.APPLICATION_JSON)
      public void putWithMethodBinderConsumes(
               @PathParam("foo") @MapEntityParam("fooble") String path) {
      }
   }

   public void testCreatePutWithMethodBinder() throws SecurityException, NoSuchMethodException {
      Method method = TestPut.class.getMethod("putWithMethodBinder", String.class);
      HttpRequest httpMethod = factory(TestPut.class).createRequest(method,
               new Object[] { "data", });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/data");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/json"));
      String expected = "{\"fooble\":\"data\"}";
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(expected.getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), expected);
   }
   
   public void testCreatePutWithMethodProduces() throws SecurityException, NoSuchMethodException {
      Method method = TestPut.class.getMethod("putWithMethodBinderProduces", String.class);
      HttpRequest httpMethod = factory(TestPut.class).createRequest(method,
               new Object[] { "data", });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/data");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("text/plain"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("data".getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), "data");
   }
   
   public void testCreatePutWithMethodConsumes() throws SecurityException, NoSuchMethodException {
      Method method = TestPut.class.getMethod("putWithMethodBinderConsumes", String.class);
      HttpRequest httpMethod = factory(TestPut.class).createRequest(method,
               new Object[] { "data", });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/data");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/json"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.ACCEPT), Collections
               .singletonList("application/json"));
      String expected = "{\"fooble\":\"data\"}";
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(expected.getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), expected);
   }


   static class TestRequestFilter1 implements HttpRequestFilter {
      public HttpRequest filter(HttpRequest request) throws HttpException {
         return null;
      }
   }

   static class TestRequestFilter2 implements HttpRequestFilter {
      public HttpRequest filter(HttpRequest request) throws HttpException {
         return null;
      }
   }

   @RequestFilters(TestRequestFilter1.class)
   @Endpoint(Localhost.class)
   static class TestRequestFilter {
      @GET
      @RequestFilters(TestRequestFilter2.class)
      public void get() {
      }
   }

   @Test
   public void testRequestFilter() throws SecurityException, NoSuchMethodException {
      Method method = TestRequestFilter.class.getMethod("get");
      HttpRequest httpMethod = factory(TestRequestFilter.class).createRequest(method,
               new Object[] {});
      assertEquals(httpMethod.getFilters().size(), 2);
      assertEquals(httpMethod.getFilters().get(0).getClass(), TestRequestFilter1.class);
      assertEquals(httpMethod.getFilters().get(1).getClass(), TestRequestFilter2.class);
   }

   @SkipEncoding('/')
   @Endpoint(Localhost.class)
   public class TestEncoding {
      @GET
      @Path("{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }
   }

   @Test
   public void testSkipEncoding() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest httpMethod = factory(TestEncoding.class).createRequest(method,
               new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/1/localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Test
   public void testEncodingPath() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest httpMethod = factory(TestEncoding.class).createRequest(method,
               new Object[] { "/", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "///localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @SkipEncoding('/')
   @Path("/v1/{account}")
   @Endpoint(Localhost.class)
   public interface TestConstantPathParam {
      @Named("testaccount")
      @PathParam("account")
      void setUsername();

      @GET
      @Path("{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2);
   }

   @Test
   public void testConstantPathParam() throws SecurityException, NoSuchMethodException {
      Method method = TestConstantPathParam.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest httpMethod = factory(TestConstantPathParam.class).createRequest(method,
               new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/v1/ralphie/1/localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Endpoint(Localhost.class)
   public class TestPath {
      @GET
      @Path("{path}")
      public void onePath(@PathParam("path") String path) {
      }

      @GET
      @Path("{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }

      @GET
      @Path("{path2}/{path1}")
      public void twoPathsOutOfOrder(@PathParam("path1") String path,
               @PathParam("path2") String path2) {
      }

      @GET
      @Path("{path}")
      public void onePathParamExtractor(
               @PathParam("path") @ParamParser(FirstCharacter.class) String path) {
      }

      @GET
      @Path("{path}")
      @PathParam("path")
      @ParamParser(FirstCharacterFirstElement.class)
      public void onePathParamExtractorMethod(String path) {
      }
   }

   @Test
   public void testParamExtractor() throws SecurityException, NoSuchMethodException {
      Method method = TestPath.class.getMethod("onePathParamExtractor", String.class);
      HttpRequest httpMethod = factory(TestPath.class).createRequest(method,
               new Object[] { "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/l");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Test
   public void testParamExtractorMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestPath.class.getMethod("onePathParamExtractorMethod", String.class);
      HttpRequest httpMethod = factory(TestPath.class).createRequest(method,
               new Object[] { "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/l");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   static class FirstCharacter implements Function<Object, String> {
      public String apply(Object from) {
         return from.toString().substring(0, 1);
      }
   }

   static class FirstCharacterFirstElement implements Function<Object, String> {
      public String apply(Object from) {
         return ((String) ((Object[]) from)[0]).substring(0, 1);
      }
   }

   @Endpoint(Localhost.class)
   public class TestHeader {
      @GET
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneHeader(@PathParam("bucket") String path) {
      }

      @GET
      @Headers(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoHeader(@PathParam("bucket") String path) {
      }

      @GET
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoHeaders(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoHeadersOutOfOrder(@PathParam("key") String path,
               @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoHeader() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneHeader = TestHeader.class.getMethod("twoHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader,
               new Object[] { "robot" }).getHeaders();
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("slash"), Collections.singletonList("/robot"));
      assertEquals(headers.get("hyphen"), Collections.singletonList("-robot"));
   }

   @Headers(keys = "x-amz-copy-source", values = "/{bucket}")
   @Endpoint(Localhost.class)
   public class TestClassHeader {
      @GET
      public void oneHeader(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassHeader() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneHeader = TestClassHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestClassHeader.class).createRequest(oneHeader,
               new Object[] { "robot" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot"));
   }

   @Test
   public void testBuildOneHeader() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneHeader = TestHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader,
               new Object[] { "robot" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot"));
   }

   @Test
   public void testBuildTwoHeaders() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoHeaders = TestHeader.class.getMethod("twoHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(twoHeaders,
               new Object[] { "robot", "eggs" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot/eggs"));
   }

   @Test
   public void testBuildTwoHeadersOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoHeadersOutOfOrder = TestHeader.class.getMethod("twoHeadersOutOfOrder",
               String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(
               twoHeadersOutOfOrder, new Object[] { "robot", "eggs" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/eggs/robot"));
   }

   public class TestReplaceQueryOptions extends BaseHttpRequestOptions {
      public TestReplaceQueryOptions() {
         this.queryParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @Test
   public void testQueryInOptions() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneQuery = TestQueryReplace.class.getMethod("queryInOptions", String.class,
               TestReplaceQueryOptions.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery,
               new Object[] { "robot", new TestReplaceQueryOptions() }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Endpoint(Localhost.class)
   public class TestQueryReplace {

      @GET
      public void queryInOptions(@PathParam("bucket") String path, TestReplaceQueryOptions options) {
      }

      @GET
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneQuery(@PathParam("bucket") String path) {
      }

      @GET
      @QueryParams(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoQuery(@PathParam("bucket") String path) {
      }

      @GET
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoQuerys(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoQuerysOutOfOrder(@PathParam("key") String path,
               @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoQuery() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneQuery = TestQueryReplace.class.getMethod("twoQuery", String.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery,
               new Object[] { "robot" }).getEndpoint().getQuery();
      assertEquals(query, "slash=/robot&hyphen=-robot");
   }

   @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}")
   @Endpoint(Localhost.class)
   public class TestClassQuery {
      @GET
      public void oneQuery(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassQuery() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneQuery = TestClassQuery.class.getMethod("oneQuery", String.class);
      String query = factory(TestClassQuery.class)
               .createRequest(oneQuery, new Object[] { "robot" }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneQuery() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneQuery = TestQueryReplace.class.getMethod("oneQuery", String.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery,
               new Object[] { "robot" }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoQuerys() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoQuerys = TestQueryReplace.class.getMethod("twoQuerys", String.class, String.class);
      String query = factory(TestQueryReplace.class).createRequest(twoQuerys,
               new Object[] { "robot", "eggs" }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoQuerysOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoQuerysOutOfOrder = TestQueryReplace.class.getMethod("twoQuerysOutOfOrder",
               String.class, String.class);
      String query = factory(TestQueryReplace.class).createRequest(twoQuerysOutOfOrder,
               new Object[] { "robot", "eggs" }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/eggs/robot");
   }

   public class TestReplaceMatrixOptions extends BaseHttpRequestOptions {
      public TestReplaceMatrixOptions() {
         this.matrixParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @Test
   public void testMatrixInOptions() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("matrixInOptions", String.class,
               TestReplaceMatrixOptions.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix,
               new Object[] { "robot", new TestReplaceMatrixOptions() }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Endpoint(Localhost.class)
   @Path("/")
   public class TestMatrixReplace {

      @GET
      public void matrixInOptions(@PathParam("bucket") String path, TestReplaceMatrixOptions options) {
      }

      @GET
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneMatrix(@PathParam("bucket") String path) {
      }

      @GET
      @MatrixParams(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoMatrix(@PathParam("bucket") String path) {
      }

      @GET
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoMatrixs(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoMatrixsOutOfOrder(@PathParam("key") String path,
               @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoMatrix() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("twoMatrix", String.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix,
               new Object[] { "robot" }).getEndpoint().getPath();
      assertEquals(path, "/;slash=/robot;hyphen=-robot");
   }

   @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}")
   @Endpoint(Localhost.class)
   @Path("/")
   public class TestClassMatrix {
      @GET
      public void oneMatrix(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassMatrix() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneMatrix = TestClassMatrix.class.getMethod("oneMatrix", String.class);
      String path = factory(TestClassMatrix.class).createRequest(oneMatrix,
               new Object[] { "robot" }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneMatrix() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("oneMatrix", String.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix,
               new Object[] { "robot" }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoMatrixs() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoMatrixs = TestMatrixReplace.class.getMethod("twoMatrixs", String.class,
               String.class);
      String path = factory(TestMatrixReplace.class).createRequest(twoMatrixs,
               new Object[] { "robot", "eggs" }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoMatrixsOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoMatrixsOutOfOrder = TestMatrixReplace.class.getMethod("twoMatrixsOutOfOrder",
               String.class, String.class);
      String path = factory(TestMatrixReplace.class).createRequest(twoMatrixsOutOfOrder,
               new Object[] { "robot", "eggs" }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/eggs/robot");
   }

   @Endpoint(Localhost.class)
   public interface TestTransformers {
      @GET
      public int noTransformer();

      @GET
      @ResponseParser(ReturnStringIf200.class)
      public void oneTransformer();

      @GET
      @ResponseParser(ReturnStringIf200Context.class)
      public void oneTransformerWithContext();

      @GET
      public InputStream inputStream();

      @GET
      public Future<InputStream> futureInputStream();

      @GET
      public URI uri();

      @GET
      public Future<URI> futureUri();
   }

   @SuppressWarnings("static-access")
   public void testInputStream() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("inputStream");
      Class<? extends Function<HttpResponse, ?>> transformer = factory(TestTransformers.class)
               .getParserOrThrowException(method);
      assertEquals(transformer, ReturnInputStream.class);
   }

   @SuppressWarnings("static-access")
   public void testInputStreamFuture() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("futureInputStream");
      Class<? extends Function<HttpResponse, ?>> transformer = factory(TestTransformers.class)
               .getParserOrThrowException(method);
      assertEquals(transformer, ReturnInputStream.class);
   }

   @SuppressWarnings("static-access")
   public void testURI() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("uri");
      Class<? extends Function<HttpResponse, ?>> transformer = factory(TestTransformers.class)
               .getParserOrThrowException(method);
      assertEquals(transformer, ParseURIList.class);
   }

   @SuppressWarnings("static-access")
   public void testURIFuture() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("futureUri");
      Class<? extends Function<HttpResponse, ?>> transformer = factory(TestTransformers.class)
               .getParserOrThrowException(method);
      assertEquals(transformer, ParseURIList.class);
   }

   public static class ReturnStringIf200Context extends ReturnStringIf200 implements InvocationContext {
      private Object[] args;
      private HttpRequest request;

      public Object[] getArgs() {
         return args;
      }

      public HttpRequest getRequest() {
         return request;
      }

      public void setContext(HttpRequest request, Object[] args) {
         this.request = request;
         this.args = args;
      }

   }

   @SuppressWarnings("static-access")
   @Test(expectedExceptions = { RuntimeException.class })
   public void testNoTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("noTransformer");
      factory(TestTransformers.class).getParserOrThrowException(method);
   }

   public void oneTransformerWithContext() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("oneTransformerWithContext");
      HttpRequest request = new HttpRequest("GET", URI.create("http://localhost"));
      Object[] args = new Object[] {};
      Function<HttpResponse, ?> transformer = factory(TestTransformers.class).createResponseParser(
               method, request, args);
      assertEquals(transformer.getClass(), ReturnStringIf200Context.class);
      assertEquals(((ReturnStringIf200Context) transformer).getArgs(), args);
      assertEquals(((ReturnStringIf200Context) transformer).getRequest(), request);
   }

   @SuppressWarnings("static-access")
   public void testOneTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("oneTransformer");
      Class<? extends Function<HttpResponse, ?>> transformer = factory(TestTransformers.class)
               .getParserOrThrowException(method);
      assertEquals(transformer, ReturnStringIf200.class);
   }

   @Endpoint(Localhost.class)
   public class TestRequest {
      @GET
      @VirtualHost
      @Path("/{id}")
      public Future<String> get(@PathParam("id") String id, HttpRequestOptions options) {
         return null;
      }

      @GET
      @VirtualHost
      @Path("/{id}")
      public Future<String> get(@PathParam("id") String id, HttpRequestOptions... options) {
         return null;
      }

      @GET
      @Path("/{id}")
      @ResponseParser(ReturnStringIf200.class)
      public Future<String> get(@PathParam("id") String id,
               @HeaderParam(HttpHeaders.HOST) String host) {
         return null;
      }

      @GET
      @Path("/{id}")
      @QueryParams(keys = "max-keys", values = "0")
      public Future<String> getQuery(@PathParam("id") String id) {
         return null;
      }

      @GET
      @Path("/{id}")
      @QueryParams(keys = "acl")
      public Future<String> getQueryNull(@PathParam("id") String id) {
         return null;
      }

      @PUT
      @Path("/{id}")
      public Future<String> put(@PathParam("id") @ParamParser(FirstCharacter.class) String id,
               @DecoratorParam(AddAsStringEntity.class) String payload) {
         return null;
      }

      @PUT
      @Path("/{id}")
      @VirtualHost
      public Future<String> putOptions(@PathParam("id") String id, HttpRequestOptions options) {
         return null;
      }

      @PUT
      @Path("/{id}")
      @Headers(keys = "foo", values = "--{id}--")
      @ResponseParser(ReturnTrueIf2xx.class)
      public Future<String> putHeader(@PathParam("id") String id,
               @DecoratorParam(AddAsStringEntity.class) String payload) {
         return null;
      }
   }

   public void testCreateGetVarArgOptionsThatProducesHeaders() throws SecurityException,
            NoSuchMethodException {
      DateTime date = new DateTime();
      GetOptions options = GetOptions.Builder.ifModifiedSince(date);
      HttpRequestOptions[] optionsHolder = new HttpRequestOptions[] {};
      Method method = TestRequest.class.getMethod("get", String.class, optionsHolder.getClass());
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1", options });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE), Collections
               .singletonList(dateService.rfc822DateFormat(date)));
   }

   public void testCreateGetOptionsThatProducesHeaders() throws SecurityException,
            NoSuchMethodException {
      DateTime date = new DateTime();
      GetOptions options = GetOptions.Builder.ifModifiedSince(date);
      Method method = TestRequest.class.getMethod("get", String.class, HttpRequestOptions.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1", options });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE), Collections
               .singletonList(dateService.rfc822DateFormat(date)));
   }

   @Endpoint(Localhost.class)
   public class PrefixOptions extends BaseHttpRequestOptions {
      public PrefixOptions withPrefix(String prefix) {
         queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
         return this;
      }
   }

   public void testCreateGetOptionsThatProducesQuery() throws SecurityException,
            NoSuchMethodException {
      PrefixOptions options = new PrefixOptions().withPrefix("1");
      Method method = TestRequest.class.getMethod("get", String.class, HttpRequestOptions.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1", options });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getEndpoint().getQuery(), "prefix=1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
   }

   public void testCreateGetQuery() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQuery", String.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getEndpoint().getQuery(), "max-keys=0");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   public void testCreateGetQueryNull() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQueryNull", String.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getEndpoint().getQuery(), "acl");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Endpoint(Localhost.class)
   public class EntityOptions extends BaseHttpRequestOptions {
      @Override
      public String buildStringEntity() {
         return "foo";
      }
   }

   public void testCreateGetOptionsThatProducesEntity() throws SecurityException,
            NoSuchMethodException {
      EntityOptions options = new EntityOptions();
      Method method = TestRequest.class.getMethod("putOptions", String.class,
               HttpRequestOptions.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1", options });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
      assertEquals(httpMethod.getEntity(), "foo");
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("foo".getBytes().length + ""));
   }

   @DataProvider(name = "strings")
   public Object[][] createData() {
      return new Object[][] { { "apples" }, { "sp ace" }, { "unic¿de" }, { "qu?stion" } };
   }

   @Test(dataProvider = "strings")
   public void testCreateGetRequest(String key) throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method method = TestRequest.class.getMethod("get", String.class, String.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { key, "localhost" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      String expectedPath = "/" + URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");
      assertEquals(httpMethod.getEndpoint().getRawPath(), expectedPath);
      assertEquals(httpMethod.getEndpoint().getPath(), "/" + key);
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
   }

   public void testCreatePutRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("put", String.class, String.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "111", "data" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("data".getBytes().length + ""));
      assertEquals(httpMethod.getEntity(), "data");
   }

   public void testCreatePutHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("putHeader", String.class, String.class);
      HttpRequest httpMethod = factory(TestRequest.class).createRequest(method,
               new Object[] { "1", "data" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("data".getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get("foo"), Collections.singletonList("--1--"));
      assertEquals(httpMethod.getEntity(), "data");
   }

   @Endpoint(Localhost.class)
   public class TestVirtualHostMethod {
      @GET
      @Path("/{id}")
      @VirtualHost
      public Future<String> get(@PathParam("id") String id, String foo) {
         return null;
      }
   }

   @Test
   public void testVirtualHostMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHostMethod.class.getMethod("get", String.class, String.class);
      HttpRequest httpMethod = factory(TestVirtualHostMethod.class).createRequest(method,
               new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
   }

   @Endpoint(Localhost.class)
   public class TestVirtualHost {
      @GET
      @Path("/{id}")
      @VirtualHost
      public Future<String> get(@PathParam("id") String id, String foo) {
         return null;
      }

      @GET
      @Path("/{id}")
      public Future<String> getPrefix(@PathParam("id") String id, @HostPrefixParam("") String foo) {
         return null;
      }

      @GET
      @Path("/{id}")
      public Future<String> getPrefixDot(@PathParam("id") String id, @HostPrefixParam String foo) {
         return null;
      }
   }

   @Test
   public void testVirtualHost() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("get", String.class, String.class);
      HttpRequest httpMethod = factory(TestVirtualHost.class).createRequest(method,
               new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));
   }

   @Test
   public void testHostPrefix() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefix", String.class, String.class);
      HttpRequest httpMethod = factory(TestVirtualHost.class).createRequest(method,
               new Object[] { "1", "holy" });
      assertEquals(httpMethod.getEndpoint().getHost(), "holylocalhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Test
   public void testHostPrefixDot() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefixDot", String.class, String.class);
      HttpRequest httpMethod = factory(TestVirtualHost.class).createRequest(method,
               new Object[] { "1", "holy" });
      assertEquals(httpMethod.getEndpoint().getHost(), "holy.localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testHostPrefixDotEmpty() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefixDot", String.class, String.class);
      factory(TestVirtualHost.class).createRequest(method, new Object[] { "1", "" });
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testHostPrefixDotNull() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefixDot", String.class, String.class);
      factory(TestVirtualHost.class).createRequest(method, new Object[] { "1", null });
   }

   @Endpoint(Localhost.class)
   public class TestHeaders {
      @GET
      public void oneHeader(@HeaderParam("header") String header) {
      }

      @GET
      public void oneIntHeader(@HeaderParam("header") int header) {
      }

      @GET
      public void twoDifferentHeaders(@HeaderParam("header1") String header1,
               @HeaderParam("header2") String header2) {
      }

      @GET
      public void twoSameHeaders(@HeaderParam("header") String header1,
               @HeaderParam("header") String header2) {
      }
   }

   @Test
   public void testOneHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot" }, ImmutableMultimap.<String, String> of().entries());
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("robot"));
   }

   @Test
   public void testOneIntHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("oneIntHeader", int.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(method,
               new Object[] { 1 }, ImmutableMultimap.<String, String> of().entries());
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("1"));
   }

   @Test
   public void testTwoDifferentHeaders() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class
               .getMethod("twoDifferentHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot", "egg" }, ImmutableMultimap.<String, String> of().entries());
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("header1"), Collections.singletonList("robot"));
      assertEquals(headers.get("header2"), Collections.singletonList("egg"));
   }

   @Test
   public void testTwoSameHeaders() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("twoSameHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot", "egg" }, ImmutableMultimap.<String, String> of().entries());
      assertEquals(headers.size(), 2);
      Collection<String> values = headers.get("header");
      assert values.contains("robot");
      assert values.contains("egg");
   }

   @Endpoint(Localhost.class)
   public interface TestEntity {
      @PUT
      public void put(@DecoratorParam(AddAsStringEntity.class) String content);

      @PUT
      @Path("{foo}")
      public Future<Void> putWithPath(@PathParam("foo") String path,
               @DecoratorParam(AddAsStringEntity.class) String content);

      @PUT
      public void twoEntities(@DecoratorParam(AddAsStringEntity.class) String entity1,
               @DecoratorParam(AddAsStringEntity.class) String entity2);
   }

   @Test
   public void testPut() throws SecurityException, NoSuchMethodException {
      Method method = TestEntity.class.getMethod("put", String.class);
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost:8080"));
      factory(TestEntity.class).decorateRequest(method, new Object[] { "test" }, request);
      assertEquals(request.getEntity(), "test");
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("test".getBytes().length + ""));
      assertEquals(
               factory(TestEntity.class).createResponseParser(method, request, null).getClass(),
               ReturnVoidIf2xx.class);
   }

   @Test
   public void putWithPath() throws SecurityException, NoSuchMethodException {
      Method method = TestEntity.class.getMethod("putWithPath", String.class, String.class);
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost:8080"));
      factory(TestEntity.class).decorateRequest(method, new Object[] { "rabble", "test" }, request);
      assertEquals(request.getEntity(), "test");
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("test".getBytes().length + ""));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPutTwoEntities() throws SecurityException, NoSuchMethodException {
      Method method = TestEntity.class.getMethod("twoEntities", String.class, String.class);
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost:8080"));
      factory(TestEntity.class)
               .decorateRequest(method, new Object[] { "test", "ralphie" }, request);
   }

   @SuppressWarnings("unchecked")
   private <T> RestAnnotationProcessor<T> factory(Class<T> clazz) {
      return ((RestAnnotationProcessor<T>) injector.getInstance(Key.get(TypeLiteral.get(Types
               .newParameterizedType(RestAnnotationProcessor.class, clazz)))));
   }

   Injector injector;
   DateService dateService = new DateService();

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named("testaccount")).to("ralphie");
            bind(URI.class).annotatedWith(Localhost.class).toInstance(
                     URI.create("http://localhost:8080"));
            bind(URI.class).annotatedWith(Localhost2.class).toInstance(
                     URI.create("http://localhost:8081"));
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());

   }
}
