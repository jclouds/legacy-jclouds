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

import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.binders.JsonBinder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.mortbay.jetty.HttpHeaders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jaxrs.JaxrsUtilTest")
public class JaxrsAnnotationProcessorTest {

   @Target( { ElementType.METHOD })
   @Retention(RetentionPolicy.RUNTIME)
   @javax.ws.rs.HttpMethod("FOO")
   public @interface FOO {
   }

   @Query(key = "x-ms-version", value = "2009-07-17")
   public class TestQuery {
      @FOO
      @Query(key = "x-ms-rubbish", value = "bin")
      public void foo() {
      }
   }

   public void testQuery() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestQuery.class).createRequest(endpoint, method,
               new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getEndpoint().getQuery(), "x-ms-version=2009-07-17&x-ms-rubbish=bin");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   public class TestCustomMethod {
      @FOO
      public void foo() {
      }
   }

   public void testCustomMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestCustomMethod.class.getMethod("foo");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestCustomMethod.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "");
      assertEquals(httpMethod.getMethod(), "FOO");
   }

   public class TestPost {
      @POST
      public void post(@EntityParam String content) {
      }

      @POST
      public void postAsJson(@EntityParam(JsonBinder.class) String content) {
      }

      @POST
      @Path("{foo}")
      public void postWithPath(@PathParam("foo") @MapEntityParam("fooble") String path,
               MapEntityBinder content) {
      }

      @POST
      @Path("{foo}")
      @MapBinder(JsonBinder.class)
      public void postWithMethodBinder(@PathParam("foo") @MapEntityParam("fooble") String path) {
      }

   }

   public void testCreatePostRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestPost.class.getMethod("post", String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestPost.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestPost.class).createRequest(endpoint, method,
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
      Method method = TestPost.class.getMethod("postWithPath", String.class, MapEntityBinder.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestPost.class).createRequest(endpoint, method,
               new Object[] { "data", new MapEntityBinder() {

                  public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
                     request.setEntity(postParams.get("fooble"));
                  }

                  public void addEntityToRequest(Object toBind, HttpRequest request) {
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestPost.class).createRequest(endpoint, method,
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

   public class TestPut {

      @PUT
      @Path("{foo}")
      @MapBinder(JsonBinder.class)
      public void putWithMethodBinder(@PathParam("foo") @MapEntityParam("fooble") String path) {
      }

   }

   public void testCreatePutWithMethodBinder() throws SecurityException, NoSuchMethodException {
      Method method = TestPut.class.getMethod("putWithMethodBinder", String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestPut.class).createRequest(endpoint, method,
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
   static class TestRequestFilter {

      @GET
      @RequestFilters(TestRequestFilter2.class)
      public void get() {
      }

   }

   @Test
   public void testRequestFilter() throws SecurityException, NoSuchMethodException {
      Method method = TestRequestFilter.class.getMethod("get");
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequestFilter.class).createRequest(endpoint,
               method, new Object[] {});
      assertEquals(httpMethod.getFilters().size(), 2);
      assertEquals(httpMethod.getFilters().get(0).getClass(), TestRequestFilter1.class);
      assertEquals(httpMethod.getFilters().get(1).getClass(), TestRequestFilter2.class);
   }

   @SkipEncoding('/')
   public class TestEncoding {

      @GET
      @Path("{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }

   }

   @Test
   public void testSkipEncoding() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestEncoding.class).createRequest(endpoint, method,
               new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/1/localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @Test
   public void testEncodingPath() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestEncoding.class).createRequest(endpoint, method,
               new Object[] { "/", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "///localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   @SkipEncoding('/')
   @Path("/v1/{account}")
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestConstantPathParam.class).createRequest(endpoint,
               method, new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getPath(), "/v1/ralphie/1/localhost");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

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
   }

   static class FirstCharacter implements Function<Object, String> {

      public String apply(Object from) {
         return from.toString().substring(0, 1);
      }

   }

   public class TestHeader {

      @GET
      @Header(key = "x-amz-copy-source", value = "/{bucket}")
      public void oneHeader(@PathParam("bucket") String path) {
      }

      @GET
      @Header(key = "x-amz-copy-source", value = "/{bucket}/{key}")
      public void twoHeaders(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @Header(key = "x-amz-copy-source", value = "/{bucket}/{key}")
      public void twoHeadersOutOfOrder(@PathParam("key") String path,
               @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildOneHeader() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method oneHeader = TestHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = HashMultimap.create();
      factory.create(TestHeader.class).addHeaderIfAnnotationPresentOnMethod(headers, oneHeader,
               new Object[] { "robot" });
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot"));
   }

   @Test
   public void testBuildTwoHeaders() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoHeaders = TestHeader.class.getMethod("twoHeaders", String.class, String.class);
      Multimap<String, String> headers = HashMultimap.create();
      factory.create(TestHeader.class).addHeaderIfAnnotationPresentOnMethod(headers, twoHeaders,
               new Object[] { "robot", "eggs" });
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot/eggs"));
   }

   @Test
   public void testBuildTwoHeadersOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoHeadersOutOfOrder = TestHeader.class.getMethod("twoHeadersOutOfOrder",
               String.class, String.class);
      Multimap<String, String> headers = HashMultimap.create();
      factory.create(TestHeader.class).addHeaderIfAnnotationPresentOnMethod(headers,
               twoHeadersOutOfOrder, new Object[] { "robot", "eggs" });
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/eggs/robot"));
   }

   public class TestTransformers {

      @GET
      public void noTransformer() {
      }

      @GET
      @ResponseParser(ReturnStringIf200.class)
      public void oneTransformer() {
      }

   }

   @SuppressWarnings("static-access")
   @Test(expectedExceptions = { RuntimeException.class })
   public void testNoTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("noTransformer");
      Class<? extends Function<HttpResponse, ?>> transformer = factory.create(
               TestTransformers.class).getParserOrThrowException(method);
      assertEquals(transformer, ReturnStringIf200.class);
   }

   @SuppressWarnings("static-access")
   public void testOneTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("oneTransformer");
      Class<? extends Function<HttpResponse, ?>> transformer = factory.create(
               TestTransformers.class).getParserOrThrowException(method);
      assertEquals(transformer, ReturnStringIf200.class);
   }

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
      @Query(key = "max-keys", value = "0")
      public Future<String> getQuery(@PathParam("id") String id) {
         return null;
      }

      @GET
      @Path("/{id}")
      @Query(key = "acl")
      public Future<String> getQueryNull(@PathParam("id") String id) {
         return null;
      }

      @PUT
      @Path("/{id}")
      public Future<String> put(@PathParam("id") @ParamParser(FirstCharacter.class) String id,
               @EntityParam String payload) {
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
      @Header(key = "foo", value = "--{id}--")
      @ResponseParser(ReturnTrueIf2xx.class)
      public Future<String> putHeader(@PathParam("id") String id, @EntityParam String payload) {
         return null;
      }
   }

   public void testCreateGetVarArgOptionsThatProducesHeaders() throws SecurityException,
            NoSuchMethodException {
      DateTime date = new DateTime();
      GetOptions options = GetOptions.Builder.ifModifiedSince(date);
      HttpRequestOptions[] optionsHolder = new HttpRequestOptions[] {};
      Method method = TestRequest.class.getMethod("get", String.class, optionsHolder.getClass());
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
               new Object[] { "1" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getEndpoint().getQuery(), "max-keys=0");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

   public void testCreateGetQueryNull() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQueryNull", String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
               new Object[] { "1" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getEndpoint().getQuery(), "acl");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestRequest.class).createRequest(endpoint, method,
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestVirtualHostMethod.class).createRequest(endpoint,
               method, new Object[] { "1", "localhost" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.HOST), Collections
               .singletonList("localhost"));

   }

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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestVirtualHost.class).createRequest(endpoint,
               method, new Object[] { "1", "localhost" });
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
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestVirtualHost.class).createRequest(endpoint,
               method, new Object[] { "1", "holy" });
      assertEquals(httpMethod.getEndpoint().getHost(), "holylocalhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);

   }

   @Test
   public void testHostPrefixDot() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefixDot", String.class, String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = factory.create(TestVirtualHost.class).createRequest(endpoint,
               method, new Object[] { "1", "holy" });
      assertEquals(httpMethod.getEndpoint().getHost(), "holy.localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/1");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
   }

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
      Multimap<String, String> headers = factory.create(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot" });
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("robot"));
   }

   @Test
   public void testOneIntHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("oneIntHeader", int.class);
      Multimap<String, String> headers = factory.create(TestHeaders.class).buildHeaders(method,
               new Object[] { 1 });
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("1"));
   }

   @Test
   public void testTwoDifferentHeaders() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class
               .getMethod("twoDifferentHeaders", String.class, String.class);
      Multimap<String, String> headers = factory.create(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot", "egg" });
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("header1"), Collections.singletonList("robot"));
      assertEquals(headers.get("header2"), Collections.singletonList("egg"));
   }

   @Test
   public void testTwoSameHeaders() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("twoSameHeaders", String.class, String.class);
      Multimap<String, String> headers = factory.create(TestHeaders.class).buildHeaders(method,
               new Object[] { "robot", "egg" });
      assertEquals(headers.size(), 2);
      Collection<String> values = headers.get("header");
      assert values.contains("robot");
      assert values.contains("egg");
   }

   public class TestEntity {
      @PUT
      public void put(@EntityParam String content) {
      }

      @PUT
      @Path("{foo}")
      public void putWithPath(@PathParam("foo") String path, @EntityParam String content) {
      }

      @PUT
      public void twoEntities(@EntityParam String entity1, @EntityParam String entity2) {
      }

   }

   @Test
   public void testPut() throws SecurityException, NoSuchMethodException {
      Method method = TestEntity.class.getMethod("put", String.class);
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost:8080"));
      factory.create(TestEntity.class).buildEntityIfPostOrPutRequest(method,
               new Object[] { "test" }, request);
      assertEquals(request.getEntity(), "test");
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList("application/unknown"));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("test".getBytes().length + ""));
   }

   @Test
   public void putWithPath() throws SecurityException, NoSuchMethodException {
      Method method = TestEntity.class.getMethod("putWithPath", String.class, String.class);
      HttpRequest request = new HttpRequest(HttpMethod.PUT, URI.create("http://localhost:8080"));

      factory.create(TestEntity.class).buildEntityIfPostOrPutRequest(method,
               new Object[] { "rabble", "test" }, request);
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
      factory.create(TestEntity.class).buildEntityIfPostOrPutRequest(method,
               new Object[] { "test", "ralphie" }, request);
   }

   JaxrsAnnotationProcessor.Factory factory;
   DateService dateService = new DateService();

   @BeforeClass
   void setupFactory() {
      factory = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named("testaccount")).to("ralphie");
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule()).getInstance(
               JaxrsAnnotationProcessor.Factory.class);
   }
}
