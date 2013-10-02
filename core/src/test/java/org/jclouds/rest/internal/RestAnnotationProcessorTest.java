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
package org.jclouds.rest.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.calculateMD5;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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

import org.eclipse.jetty.http.HttpHeaders;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.filters.StripExpectHeader;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.PayloadParams;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindAsHostPrefix;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;
import org.jclouds.util.Strings2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
/**
 * Tests behavior of {@code RestAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "RestAnnotationProcessorTest")
public class RestAnnotationProcessorTest extends BaseRestApiTest {

   @ConfiguresRestClient
   protected static class CallerModule extends RestClientModule<Caller, AsyncCaller> {
      CallerModule() {
         super(ImmutableMap.<Class<?>, Class<?>> of(Callee.class, AsyncCallee.class, Callee2.class, AsyncCallee2.class));
      }

      @Override
      protected void configure() {
         super.configure();
         bind(new TypeLiteral<Supplier<URI>>() {
         }).annotatedWith(Localhost2.class).toInstance(Suppliers.ofInstance(URI.create("http://localhost:1111")));
         bind(IOExceptionRetryHandler.class).toInstance(IOExceptionRetryHandler.NEVER_RETRY);
      }
   }

   @Path("/client/{jclouds.api-version}")
   public static interface AsyncCallee extends Closeable {
      @GET
      @Path("/{path}")
      ListenableFuture<Void> onePath(@PathParam("path") String path);
   }

   @Path("/client/{jclouds.api-version}")
   public static interface AsyncCallee2 {
      @GET
      @Path("/{path}/2")
      ListenableFuture<Void> onePath(@PathParam("path") String path);
   }

   @Endpoint(Localhost2.class)
   public static interface Caller extends Closeable {

      // tests that we can pull from suppliers
      @Provides
      @Localhost2
      URI getURI();

      @Delegate
      public Callee getCallee();

      @Delegate
      public Callee2 getCallee2();

      @Delegate
      public Callee getCallee(@EndpointParam URI endpoint);

      @Delegate
      public Optional<Callee> getOptionalCallee(@EndpointParam URI endpoint);

      @Delegate
      @Path("/testing/testing/{wibble}")
      public Callee getCalleeWithPath(@EndpointParam URI endpoint, @PathParam("wibble") String wibble);
   }

   public static interface Callee extends Closeable {
      void onePath(String path);
   }

   public static interface Callee2 {
      void onePath(String path);
   }

   public static interface AsyncCaller extends Closeable {
      @Provides
      @Localhost2
      URI getURI();

      @Delegate
      public AsyncCallee getCallee();

      @Delegate
      public AsyncCallee2 getCallee2();

      @Delegate
      public AsyncCallee getCallee(@EndpointParam URI endpoint);

      @Delegate
      public Optional<AsyncCallee> getOptionalCallee(@EndpointParam URI endpoint);

      @Delegate
      @Path("/testing/testing/{wibble}")
      public AsyncCallee getCalleeWithPath(@EndpointParam URI endpoint, @PathParam("wibble") String wibble);
   }

   public void testAsyncDelegateIsLazyLoadedAndRequestIncludesVersionAndPath() throws InterruptedException,
         ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(),
                  "GET http://localhost:9999/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(AsyncCaller.class).getCallee().onePath("foo").get();

   }

   public void testDelegateIsLazyLoadedAndRequestIncludesVersionAndPath() throws InterruptedException,
         ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {
         int callCounter = 0;

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            if (callCounter == 1)
               assertEquals(command.getCurrentRequest().getRequestLine(),
                     "GET http://localhost:1111/client/1/bar/2 HTTP/1.1");
            else
               assertEquals(command.getCurrentRequest().getRequestLine(),
                     "GET http://localhost:1111/client/1/foo HTTP/1.1");
            callCounter++;
            return HttpResponse.builder().build();
         }
      });

      try {
         child.getInstance(Callee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(Caller.class).getCallee().onePath("foo");
      child.getInstance(Caller.class).getCallee2().onePath("bar");
      // Note if wrong method is picked up, we'll see "http://localhost:1111/client/1/foo/2"!
      child.getInstance(Caller.class).getCallee().onePath("foo");
   }

   public void testAsyncDelegateIsLazyLoadedAndRequestIncludesEndpointVersionAndPath() throws InterruptedException,
         ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(AsyncCaller.class).getCallee(URI.create("http://howdyboys")).onePath("foo").get();

      assertEquals(child.getInstance(AsyncCaller.class).getURI(), URI.create("http://localhost:1111"));

   }

   public void testAsyncDelegateWithPathParamIsLazyLoadedAndRequestIncludesEndpointVersionAndPath()
         throws InterruptedException, ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(),
                  "GET http://howdyboys/testing/testing/thepathparam/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(AsyncCaller.class).getCalleeWithPath(URI.create("http://howdyboys"), "thepathparam")
            .onePath("foo").get();

      assertEquals(child.getInstance(AsyncCaller.class).getURI(), URI.create("http://localhost:1111"));
   }

   public void testAsyncDelegateIsLazyLoadedAndRequestIncludesEndpointVersionAndPathOptionalPresent()
         throws InterruptedException, ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(AsyncCaller.class).getOptionalCallee(URI.create("http://howdyboys")).get().onePath("foo").get();

      assertEquals(child.getInstance(AsyncCaller.class).getURI(), URI.create("http://localhost:1111"));

   }

   public void testAsyncDelegateIsLazyLoadedAndRequestIncludesEndpointVersionAndPathCanOverrideOptionalBehaviour()
         throws InterruptedException, ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      }, new AbstractModule() {

         @Override
         protected void configure() {
            bind(ImplicitOptionalConverter.class).toInstance(new ImplicitOptionalConverter() {

               @Override
               public Optional<Object> apply(InvocationSuccess input) {
                  return Optional.absent();
               }

            });
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      assert !child.getInstance(AsyncCaller.class).getOptionalCallee(URI.create("http://howdyboys")).isPresent();

      assertEquals(child.getInstance(AsyncCaller.class).getURI(), URI.create("http://localhost:1111"));

   }

   public void testDelegateIsLazyLoadedAndRequestIncludesEndpointVersionAndPath() throws InterruptedException,
         ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public ListenableFuture<HttpResponse> submit(HttpCommand command) {
            return Futures.immediateFuture(invoke(command));
         }

         @Override
         public HttpResponse invoke(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return HttpResponse.builder().build();
         }

      });

      try {
         child.getInstance(Callee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      assertEquals(child.getInstance(Caller.class).getURI(), URI.create("http://localhost:1111"));

   }

   private Injector injectorForCaller(HttpCommandExecutorService service, Module... modules) {
      return ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(Caller.class, AsyncCaller.class,
                        "http://localhost:9999"))
            .modules(
                  ImmutableSet.<Module> builder().add(new MockModule(service)).add(new NullLoggingModule())
                        .add(new CallerModule()).addAll(ImmutableSet.<Module> copyOf(modules)).build()).buildInjector();

   }

   @Target({ ElementType.METHOD })
   @Retention(RetentionPolicy.RUNTIME)
   @javax.ws.rs.HttpMethod("FOO")
   public @interface FOO {
   }

   @Retention(value = RetentionPolicy.RUNTIME)
   @Target(value = { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
   @Qualifier
   public @interface Localhost2 {
   }

   @QueryParams(keys = "x-ms-version", values = "2009-07-17")
   public class TestQuery {
      @FOO
      @Path("/")
      @QueryParams(keys = "x-ms-rubbish", values = "bin")
      public void foo() {
      }

      @FOO
      @Path("/")
      @QueryParams(keys = { "foo", "fooble" }, values = { "bar", "baz" })
      public void foo2() {
      }

      @FOO
      @Path("/")
      @QueryParams(keys = { "foo", "fooble" }, values = { "bar", "baz" })
      public void foo3(@QueryParam("robbie") String robbie) {
      }

      @FOO
      @Path("/")
      @QueryParams(keys = { "foo", "fooble" }, values = { "bar", "baz" })
      public void foo3Nullable(@Nullable @QueryParam("robbie") String robbie) {
      }

      @FOO
      @Path("/")
      public void queryParamIterable(@Nullable @QueryParam("foo") Iterable<String> bars) {
      }
   }

   public void testQuery() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = processor.apply(Invocation.create(method(TestQuery.class, "foo")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&x-ms-rubbish=bin");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQuery2() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = processor.apply(Invocation.create(method(TestQuery.class, "foo2")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=bar&fooble=baz");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQuery3() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = processor.apply(Invocation.create(method(TestQuery.class, "foo3", String.class),
            ImmutableList.<Object> of("wonder")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=bar&fooble=baz&robbie=wonder");
      assertEquals(request.getMethod(), "FOO");
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "param\\{robbie\\} for invocation TestQuery.foo3")
   public void testNiceNPEQueryParam() throws SecurityException, NoSuchMethodException, IOException {
      processor.apply(Invocation.create(method(TestQuery.class, "foo3", String.class),
            Lists.<Object> newArrayList((String) null)));
   }

   public void testNoNPEOnQueryParamWithNullable() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = processor.apply(Invocation.create(
            method(TestQuery.class, "foo3Nullable", String.class), Lists.<Object> newArrayList((String) null)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=bar&fooble=baz");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQueryParamIterableOneString() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = processor.apply(Invocation.create(
            method(TestQuery.class, "queryParamIterable", Iterable.class),
            ImmutableList.<Object> of(ImmutableSet.of("1"))));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=1");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQueryParamIterableString() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQuery.class, "queryParamIterable", Iterable.class);
      Set<String> bars = ImmutableSortedSet.<String> of("1", "2", "3");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(bars)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=1&foo=2&foo=3");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQueryParamIterableInteger() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQuery.class, "queryParamIterable", Iterable.class);
      Set<Integer> bars = ImmutableSortedSet.<Integer> of(1, 2, 3);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(bars)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=1&foo=2&foo=3");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQueryParamIterableEmpty() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQuery.class, "queryParamIterable", Iterable.class);
      Set<String> bars = Collections.emptySet();
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(bars)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQueryParamIterableNull() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQuery.class, "queryParamIterable", Iterable.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            Lists.<Object> newArrayList((String) null)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17");
      assertEquals(request.getMethod(), "FOO");
   }

   public interface TestPayloadParamVarargs {
      @POST
      public void varargs(HttpRequestOptions... options);

      @POST
      public void varargsWithReq(String required, HttpRequestOptions... options);

      @POST
      public void post(HttpRequestOptions options);

      @POST
      @Produces(MediaType.APPLICATION_OCTET_STREAM)
      public void post();

      @POST
      @Produces(MediaType.APPLICATION_OCTET_STREAM)
      public void post(Payload payload);
   }

   public void testHttpRequestOptionsNoPayloadParam() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "post");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "", "application/octet-stream", false);
   }

   private class TestHttpRequestOptions extends BaseHttpRequestOptions {
      TestHttpRequestOptions payload(String payload) {
         this.payload = payload;
         return this;
      }

      TestHttpRequestOptions headerParams(Multimap<String, String> headers) {
         this.headers.putAll(headers);
         return this;
      }

      TestHttpRequestOptions queryParams(Multimap<String, String> params) {
         this.queryParameters.putAll(params);
         return this;
      }
   }

   public void testHttpRequestOptionsPayloadParam() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "post", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(Payloads.newStringPayload("foo"))));
      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "foo", "application/octet-stream", false);
   }

   public void testHttpRequestWithOnlyContentType() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "post", HttpRequestOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(new TestHttpRequestOptions().payload("fooya"))));
      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testHeaderAndQueryVarargs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "varargs",
            HttpRequestOptions[].class);
      GeneratedHttpRequest request = processor.apply(
            Invocation.create(method,
            ImmutableList.<Object> of(new TestHttpRequestOptions().payload("fooya"),
                  new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
                  new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value")))));
      assertRequestLineEquals(request, "POST http://localhost:9999?key=value HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-header-1: fooya\n");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testHeaderAndQueryVarargsPlusReq() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "varargsWithReq", String.class,
            HttpRequestOptions[].class);
      GeneratedHttpRequest request = processor.apply(
            Invocation.create(method,
            ImmutableList.<Object> of("required param", new TestHttpRequestOptions().payload("fooya"),
                  new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
                  new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value")))));
      assertRequestLineEquals(request, "POST http://localhost:9999?key=value HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-header-1: fooya\n");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testDuplicateHeaderAndQueryVarargs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayloadParamVarargs.class, "varargs",
            HttpRequestOptions[].class);
      GeneratedHttpRequest request = processor.apply(
            Invocation.create(method,
            ImmutableList.<Object> of(new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value")),
                  new TestHttpRequestOptions().payload("fooya"),
                  new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
                  new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "anothervalue")),
                  new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya again!")),
                  new TestHttpRequestOptions().payload("last_payload_wins!"))));
      assertRequestLineEquals(request, "POST http://localhost:9999?key=value&key=anothervalue HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-header-1: fooya\nX-header-1: fooya again!\n");
      assertPayloadEquals(request, "last_payload_wins!", "application/unknown", false);
   }

   public class TestCustomMethod {
      @FOO
      public void foo() {
      }
   }

   public void testCustomMethod() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestCustomMethod.class, "foo");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "");
      assertEquals(request.getMethod(), "FOO");
   }

   public interface Parent {
      public void foo();
   }

   public class TestOverridden implements Parent {
      @POST
      public void foo() {
      }
   }

   public void testOverriddenMethod() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestOverridden.class, "foo");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "");
      assertEquals(request.getMethod(), "POST");
   }

   public class TestOverriddenEndpoint implements Parent {

      @POST
      @Endpoint(Localhost2.class)
      public void foo() {
      }

      @POST
      public void foo(@EndpointParam URI endpoint) {
      }
   }

   public void testOverriddenEndpointMethod() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestOverriddenEndpoint.class, "foo");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPort(), 1111);
      assertEquals(request.getEndpoint().getPath(), "");
      assertEquals(request.getMethod(), "POST");
   }

   public void testOverriddenEndpointParameter() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestOverriddenEndpoint.class, "foo", URI.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(URI.create("http://wowsa:8001"))));
      assertEquals(request.getEndpoint().getHost(), "wowsa");
      assertEquals(request.getEndpoint().getPort(), 8001);
      assertEquals(request.getEndpoint().getPath(), "");
      assertEquals(request.getMethod(), "POST");
   }

   public interface TestPost {
      @POST
      void post(@Nullable @BinderParam(BindToStringPayload.class) String content);

      @POST
      void postNonnull(@BinderParam(BindToStringPayload.class) String content);

      @POST
      public void postAsJson(@BinderParam(BindToJsonPayload.class) String content);

      @POST
      @Path("/{foo}")
      public void postWithPath(@PathParam("foo") @PayloadParam("fooble") String path, MapBinder content);

      @POST
      @Path("/{foo}")
      @MapBinder(BindToJsonPayload.class)
      public void postWithMethodBinder(@PathParam("foo") @PayloadParam("fooble") String path);

      @POST
      @Path("/{foo}")
      @PayloadParams(keys = "rat", values = "atat")
      @MapBinder(BindToJsonPayload.class)
      public void postWithMethodBinderAndDefaults(@PathParam("foo") @PayloadParam("fooble") String path);

      @POST
      @Path("/{foo}")
      @PayloadParams(keys = "rat", values = "atat")
      @org.jclouds.rest.annotations.Payload("name {fooble}")
      @Produces(MediaType.TEXT_PLAIN)
      public void testPayload(@PathParam("foo") @PayloadParam("fooble") String path);
   }

   public void testCreatePostRequest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "post", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePostRequestNullOk1() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "post", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            Lists.<Object> newArrayList((String) null)));

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, "application/unknown", false);
   }

   public void testCreatePostRequestNullOk2() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "post", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            Lists.<Object> newArrayList((String) null)));

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, "application/unknown", false);
   }

   public void testCreatePostRequestNullNotOk1() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postNonnull", String.class);
      try {
         GeneratedHttpRequest request = processor.apply(Invocation.create(method,
               Lists.<Object> newArrayList((String) null)));
         Assert.fail("call should have failed with illegal null parameter, not permitted " + request + " to be created");
      } catch (NullPointerException e) {
         assertTrue(e.toString().indexOf("postNonnull parameter 1") >= 0,
            "Error message should have referred to 'parameter 1': " + e);
      }
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "postNonnull parameter 1")
   public void testCreatePostRequestNullNotOk2() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postNonnull", String.class);
      processor.apply(Invocation.create(method, Lists.<Object> newArrayList((String) null)));
   }

   public void testCreatePostJsonRequest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postAsJson", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "\"data\"", "application/json", false);
   }

   public void testCreatePostWithPathRequest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postWithPath", String.class, MapBinder.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("data", new org.jclouds.rest.MapBinder() {
               @Override
               public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
                  request.setPayload((String) postParams.get("fooble"));
                  return request;
               }

               @Override
               public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
                  throw new RuntimeException("this shouldn't be used in POST");
               }
            })));
      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePostWithMethodBinder() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postWithMethodBinder", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);
   }

   public void testCreatePostWithMethodBinderAndDefaults() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "postWithMethodBinderAndDefaults", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\",\"rat\":\"atat\"}", "application/json", false);
   }

   public void testCreatePostWithPayload() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPost.class, "testPayload", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "name data", "text/plain", false);
   }

   static interface TestMultipartForm {
      @POST
      void withStringPart(@PartParam(name = "fooble") String path);

      @POST
      void withParamStringPart(@FormParam("name") String name, @PartParam(name = "file") String path);

      @POST
      void withParamFilePart(@FormParam("name") String name, @PartParam(name = "file") File path);

      @POST
      void withParamFileBinaryPart(@FormParam("name") String name,
            @PartParam(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM) File path);

      @POST
      void withParamByteArrayBinaryPart(
            @FormParam("name") String name,
            @PartParam(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM, filename = "{name}.tar.gz") byte[] content);
   }

   public void testMultipartWithStringPart() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withStringPart", String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("foobledata")));
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"fooble\"\r\n" + //
                  "\r\n" + //
                  "foobledata\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "fooble")
   public void testMultipartWithStringPartNullNotOkay() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withStringPart", String.class);
      processor.apply(Invocation.create(method, Lists.<Object> newArrayList((String) null)));
   }

   public void testMultipartWithParamStringPart() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withParamStringPart", String.class,
            String.class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", "foobledata")));
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"name\"\r\n" + //
                  "\r\n" + //
                  "name\r\n" + // /
                  "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"file\"\r\n" + //
                  "\r\n" + //
                  "foobledata\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "param\\{name\\} for invocation TestMultipartForm.withParamStringPart")
   public void testMultipartWithParamStringPartNullNotOk() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withParamStringPart", String.class,
            String.class);
      processor.apply(Invocation.create(method, Lists.<Object> newArrayList(null, "foobledata")));
   }

   public void testMultipartWithParamFilePart() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withParamFilePart", String.class,
            File.class);
      File file = File.createTempFile("foo", "bar");
      Files.append("foobledata", file, UTF_8);
      file.deleteOnExit();

      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", file)));
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"name\"\r\n" + //
                  "\r\n" + //
                  "name\r\n" + // /
                  "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" + //
                  "\r\n" + //
                  "foobledata\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   }

   public void testMultipartWithParamByteArrayPart() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withParamByteArrayBinaryPart",
            String.class, byte[].class);
      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", "goo".getBytes())));
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"name\"\r\n" + //
                  "\r\n" + //
                  "name\r\n" + // /
                  "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"file\"; filename=\"name.tar.gz\"\r\n" + //
                  "Content-Type: application/octet-stream\r\n" + //
                  "\r\n" + //
                  "goo\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   };

   public void testMultipartWithParamFileBinaryPart() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestMultipartForm.class, "withParamFileBinaryPart",
            String.class, File.class);
      File file = File.createTempFile("foo", "bar");
      Files.write(new byte[] { 17, 26, 39, 40, 50 }, file);
      file.deleteOnExit();

      GeneratedHttpRequest httpRequest = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("name", file)));
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"name\"\r\n" + //
                  "\r\n" + //
                  "name\r\n" + // /
                  "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" + //
                  "Content-Type: application/octet-stream\r\n" + //
                  "\r\n" + //
                  "'(2\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   }

   public interface TestPut {
      @PUT
      @Path("/{foo}")
      @MapBinder(BindToJsonPayload.class)
      void putWithMethodBinder(@PathParam("foo") @PayloadParam("fooble") String path);

      @PUT
      @Path("/{foo}")
      @Produces(MediaType.TEXT_PLAIN)
      void putWithMethodBinderProduces(@PathParam("foo") @BinderParam(BindToStringPayload.class) String path);

      @PUT
      @Path("/{foo}")
      @MapBinder(BindToJsonPayload.class)
      @Consumes("application/json")
      View putWithMethodBinderConsumes(@PathParam("foo") @PayloadParam("fooble") String path);

      @GET
      @Path("/")
      @Consumes("application/json")
      Map<String, String> testGeneric();

      @GET
      @Path("/")
      @Consumes("application/json")
      ListenableFuture<Map<String, String>> testGeneric2();

      @GET
      @Path("/")
      @Consumes("application/json")
      ListenableFuture<? extends Map<String, String>> testGeneric3();

      @GET
      @Path("/")
      @Unwrap
      @Consumes("application/json")
      String testUnwrap();

      @GET
      @Path("/")
      @SelectJson("foo")
      @Consumes("application/json")
      String testUnwrapValueNamed();

      @POST
      @Path("/")
      String testWrapWith(@WrapWith("foo") String param);

      @GET
      @Path("/")
      @Unwrap
      @Consumes("application/json")
      ListenableFuture<String> testUnwrap2();

      @GET
      @Path("/")
      @Unwrap
      @Consumes("application/json")
      ListenableFuture<Set<String>> testUnwrap3();

      @GET
      @Path("/")
      @Unwrap
      @Consumes("application/json")
      ListenableFuture<? extends Set<String>> testUnwrap4();

      @GET
      @Path("/")
      @SelectJson("jobid")
      ListenableFuture<Long> selectLong();

      @GET
      @Path("/")
      @SelectJson("jobid")
      @Transform(AddOne.class)
      ListenableFuture<Long> selectLongAddOne();

      static class AddOne implements Function<Long, Long> {

         @Override
         public Long apply(Long o) {
            return o + 1;
         }
      }

      @GET
      @Path("/")
      @SelectJson("runit")
      @OnlyElement
      @Consumes("application/json")
      ListenableFuture<String> selectOnlyElement();

      @Target({ ElementType.METHOD })
      @Retention(RetentionPolicy.RUNTIME)
      @HttpMethod("ROWDY")
      public @interface ROWDY {
      }

      @ROWDY
      @Path("/strings/{id}")
      ListenableFuture<Boolean> rowdy(@PathParam("id") String path);

      @ROWDY
      @Path("/ints/{id}")
      ListenableFuture<Boolean> rowdy(@PathParam("id") int path);
   }

   static class View {
      String foo;
   }

   public void testAlternateHttpMethod() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "rowdy", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "ROWDY http://localhost:9999/strings/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   public void testAlternateHttpMethodSameArity() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "rowdy", int.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "ROWDY http://localhost:9999/ints/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCreatePutWithMethodBinder() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "putWithMethodBinder", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);
   }

   public void testCreatePutWithMethodProduces() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "putWithMethodBinderProduces", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "text/plain", false);
   }

   public void testCreatePutWithMethodConsumes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "putWithMethodBinderConsumes", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("data")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            View.class.cast(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build())).foo,
            "bar");

   }

   public void testGeneric1() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testGeneric");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, ParseJson.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   public void testGeneric2() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testGeneric2");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, ParseJson.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   public void testGeneric3() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testGeneric3");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, ParseJson.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   public void testUnwrap1() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testUnwrap");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   public void testUnwrapValueNamed() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testUnwrapValueNamed");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, ParseFirstJsonValueNamed.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   public void testWrapWith() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testWrapWith", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("bar")));
      assertPayloadEquals(request, "{\"foo\":\"bar\"}", "application/json", false);
   }

   public void testUnwrap2() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testUnwrap2");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   public void testUnwrap3() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testUnwrap3");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok")
                  .payload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}").build()), ImmutableSet.of("0.7.0", "0.7.1"));
   }

   public void testUnwrap4() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "testUnwrap4");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok")
                  .payload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}").build()), ImmutableSet.of("0.7.0", "0.7.1"));
   }

   public void selectLong() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "selectLong");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));

      assertResponseParserClassEquals(method, request, ParseFirstJsonValueNamed.class);
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok")
                  .payload("{ \"destroyvirtualmachineresponse\" : {\"jobid\":4} }").build()), Long.valueOf(4));
   }

   public void selectLongAddOne() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPut.class, "selectLongAddOne");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      Function<HttpResponse, ?> parser = transformer.apply(request);

      assertEquals(
            parser.apply(HttpResponse.builder().statusCode(200).message("ok")
                  .payload("{ \"destroyvirtualmachineresponse\" : {\"jobid\":4} }").build()), Long.valueOf(5));
   }

   static class TestRequestFilter1 implements HttpRequestFilter {
      public HttpRequest filter(HttpRequest request) throws HttpException {
         return request;
      }
   }

   static class TestRequestFilter2 implements HttpRequestFilter {
      public HttpRequest filter(HttpRequest request) throws HttpException {
         return request;
      }
   }

   @RequestFilters(TestRequestFilter1.class)
   static interface TestRequestFilter {
      @GET
      @RequestFilters(TestRequestFilter2.class)
      public void get();

      @GET
      @OverrideRequestFilters
      @RequestFilters(TestRequestFilter2.class)
      public void getOverride();

      @OverrideRequestFilters
      @RequestFilters(TestRequestFilter2.class)
      public void getOverride(HttpRequest request);

      @POST
      public void post();
   }

   @Test
   public void testRequestFilter() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequestFilter.class, "get");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter1.class);
      assertEquals(request.getFilters().get(1).getClass(), TestRequestFilter2.class);
   }

   public void testRequestFilterOverride() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequestFilter.class, "getOverride");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method));
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter2.class);
   }

   public void testRequestFilterOverrideOnRequest() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequestFilter.class, "getOverride", HttpRequest.class);
      GeneratedHttpRequest request = processor.apply(
            Invocation.create(method,
            ImmutableList.<Object> of(HttpRequest.builder().method("GET").endpoint("http://localhost")
                  .addHeader("foo", "bar").build())));
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter2.class);
   }

   @Test
   public void testRequestFilterStripExpect() {
      // First, verify that by default, the StripExpectHeader filter is not applied
      Invokable<?, ?> method = method(TestRequestFilter.class, "post");
      Invocation invocation = Invocation.create(method,
         ImmutableList.<Object>of(HttpRequest.builder().method("POST").endpoint("http://localhost")
            .addHeader(HttpHeaders.EXPECT, "100-Continue").build()));
      GeneratedHttpRequest request = processor.apply(invocation);
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter1.class);

      // Now let's create a new injector with the property set. Use that to create the annotation processor.
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_STRIP_EXPECT_HEADER, "true");
      Injector injector = ContextBuilder.newBuilder(
            AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(Callee.class, AsyncCallee.class,
               "http://localhost:9999"))
         .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {
            protected void configure() {
               bind(new TypeLiteral<Supplier<URI>>() {
               }).annotatedWith(Localhost2.class).toInstance(
                  Suppliers.ofInstance(URI.create("http://localhost:1111")));
            }}))
         .overrides(overrides).buildInjector();
      RestAnnotationProcessor newProcessor = injector.getInstance(RestAnnotationProcessor.class);
      // Verify that this time the filter is indeed applied as expected.
      request = newProcessor.apply(invocation);
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(1).getClass(), StripExpectHeader.class);
   }

   public class TestEncoding {
      @GET
      @Path("/{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }
   }

   @Test
   public void testSkipEncoding() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestEncoding.class, "twoPaths", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "localhost")));
      assertEquals(request.getEndpoint().getPath(), "/1/localhost");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @Test
   public void testEncodingPath() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestEncoding.class, "twoPaths", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("/", "localhost")));
      assertEquals(request.getEndpoint().getPath(), "///localhost");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @Path("/v1/{identity}")
   public interface TestConstantPathParam {
      @Named("testidentity")
      @PathParam("identity")
      void setUsername();

      @GET
      @Path("/{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2);
   }

   @Test(enabled = false)
   public void testConstantPathParam() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestConstantPathParam.class, "twoPaths", String.class,
            String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "localhost")));
      assertRequestLineEquals(request, "GET http://localhost:9999/v1/ralphie/1/localhost HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   public class TestPath {
      @GET
      @Path("/{path}")
      public void onePath(@PathParam("path") String path) {
      }

      @GET
      @Path("/{path}")
      public void onePathNullable(@Nullable @PathParam("path") String path) {
      }

      @GET
      @Path("/{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }

      @GET
      @Path("/{path2}/{path1}")
      public void twoPathsOutOfOrder(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }

      @GET
      @Path("/{path}")
      public void onePathParamExtractor(@PathParam("path") @ParamParser(FirstCharacter.class) String path) {
      }

      @GET
      @Path("/")
      public void oneQueryParamExtractor(@QueryParam("one") @ParamParser(FirstCharacter.class) String one) {
      }

      @POST
      @Path("/")
      public void oneFormParamExtractor(@FormParam("one") @ParamParser(FirstCharacter.class) String one) {
      }
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "param\\{path\\} for invocation TestPath.onePath")
   public void testNiceNPEPathParam() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPath.class, "onePath", String.class);
      processor.apply(Invocation.create(method, Lists.<Object> newArrayList((String) null)));
   }

   @Test
   public void testPathParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPath.class, "onePathParamExtractor", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("localhost")));
      assertRequestLineEquals(request, "GET http://localhost:9999/l HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   @Test
   public void testQueryParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPath.class, "oneQueryParamExtractor", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("localhost")));
      assertRequestLineEquals(request, "GET http://localhost:9999/?one=l HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   @Test
   public void testFormParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPath.class, "oneFormParamExtractor", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("localhost")));
      assertRequestLineEquals(request, "POST http://localhost:9999/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "one=l", "application/x-www-form-urlencoded", false);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "param\\{one\\} for invocation TestPath.oneFormParamExtractor")
   public void testNiceNPEFormParam() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPath.class, "oneFormParamExtractor", String.class);
      processor.apply(Invocation.create(method, Lists.<Object> newArrayList((String) null)));
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

   public class TestHeader {
      @GET
      @Path("/")
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneHeader(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @Headers(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoHeader(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoHeaders(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @Path("/")
      @Headers(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoHeadersOutOfOrder(@PathParam("key") String path, @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoHeader() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestHeader.class, "twoHeader", String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot"))).getHeaders();
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("slash"), ImmutableList.of("/robot"));
      assertEquals(headers.get("hyphen"), ImmutableList.of("-robot"));
   }

   @Headers(keys = "x-amz-copy-source", values = "/{bucket}")
   public class TestClassHeader {
      @GET
      @Path("/")
      public void oneHeader(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassHeader() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestClassHeader.class, "oneHeader", String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot"))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot"));
   }

   @Test
   public void testBuildOneHeader() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestHeader.class, "oneHeader", String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot"))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot"));
   }

   @Test
   public void testBuildTwoHeaders() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestHeader.class, "twoHeaders", String.class, String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot", "eggs"))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot/eggs"));
   }

   @Test
   public void testBuildTwoHeadersOutOfOrder() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestHeader.class, "twoHeadersOutOfOrder", String.class,
            String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot", "eggs"))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/eggs/robot"));
   }

   public class TestReplaceQueryOptions extends BaseHttpRequestOptions {
      public TestReplaceQueryOptions() {
         this.queryParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @Test
   public void testQueryInOptions() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQueryReplace.class, "queryInOptions", String.class,
            TestReplaceQueryOptions.class);
      String query = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("robot", new TestReplaceQueryOptions()))).getEndpoint()
            .getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   public class TestQueryReplace {

      @GET
      @Path("/")
      public void queryInOptions(@PathParam("bucket") String path, TestReplaceQueryOptions options) {
      }

      @GET
      @Path("/")
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneQuery(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @QueryParams(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoQuery(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoQuerys(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @Path("/")
      @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoQuerysOutOfOrder(@PathParam("key") String path, @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoQuery() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQueryReplace.class, "twoQuery", String.class);
      String query = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getEndpoint().getQuery();
      assertEquals(query, "slash=/robot&hyphen=-robot");
   }

   @QueryParams(keys = "x-amz-copy-source", values = "/{bucket}")
   public class TestClassQuery {
      @GET
      @Path("/")
      public void oneQuery(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassQuery() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestClassQuery.class, "oneQuery", String.class);
      String query = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneQuery() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQueryReplace.class, "oneQuery", String.class);
      String query = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoQuerys() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQueryReplace.class, "twoQuerys", String.class, String.class);
      String query = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("robot", "eggs"))).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoQuerysOutOfOrder() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestQueryReplace.class, "twoQuerysOutOfOrder", String.class,
            String.class);
      String query = processor
            .apply(Invocation.create(method, ImmutableList.<Object> of("robot", "eggs"))).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/eggs/robot");
   }

   public interface TestTransformers {
      @GET
      ListenableFuture<Integer> noTransformer();

      @GET
      @ResponseParser(ReturnStringIf2xx.class)
      ListenableFuture<Void> oneTransformer();

      @GET
      @ResponseParser(ReturnStringIf200Context.class)
      ListenableFuture<Void> oneTransformerWithContext();

      @GET
      ListenableFuture<InputStream> futureInputStream();

      @GET
      ListenableFuture<URI> futureUri();

      @PUT
      ListenableFuture<Void> put(Payload payload);

      @PUT
      @Headers(keys = "Transfer-Encoding", values = "chunked")
      ListenableFuture<Void> putXfer(Payload payload);

      @PUT
      ListenableFuture<Void> put(PayloadEnclosing payload);
   }

   public void testPutPayloadEnclosing() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", PayloadEnclosing.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(new PayloadEnclosingImpl(newStringPayload("whoops")))));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayloadEnclosingGenerateMD5() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", PayloadEnclosing.class);
      PayloadEnclosing payloadEnclosing = new PayloadEnclosingImpl(newStringPayload("whoops"));
      calculateMD5(payloadEnclosing);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payloadEnclosing)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");

      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutInputStreamPayloadEnclosingGenerateMD5() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", PayloadEnclosing.class);
      PayloadEnclosing payloadEnclosing = new PayloadEnclosingImpl(
            newInputStreamPayload(Strings2.toInputStream("whoops")));

      calculateMD5(payloadEnclosing);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payloadEnclosing)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");

      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutPayloadChunkedNoContentLength() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "putXfer", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(newStringPayload("whoops"))));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Transfer-Encoding: chunked\n");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayload() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(newStringPayload("whoops"))));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayloadContentDisposition() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentDisposition("attachment; filename=photo.jpg");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", "attachment; filename=photo.jpg", null, null, false);
   }

   public void testPutPayloadContentEncoding() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentEncoding("gzip");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", null, "gzip", null, false);
   }

   public void testPutPayloadContentLanguage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentLanguage("en");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", null, null, "en", false);
   }

   public void testPutPayloadWithGeneratedMD5AndNoContentType() throws SecurityException, NoSuchMethodException,
         IOException {
      Payload payload = newStringPayload("whoops");
      calculateMD5(payload);
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutInputStreamPayload() throws SecurityException, NoSuchMethodException, IOException {
      Payload payload = newInputStreamPayload(Strings2.toInputStream("whoops"));
      payload.getContentMetadata().setContentLength((long) "whoops".length());
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutInputStreamPayloadWithMD5() throws NoSuchAlgorithmException, IOException, SecurityException,
         NoSuchMethodException {
      Payload payload = newStringPayload("whoops");
      calculateMD5(payload);
      Invokable<?, ?> method = method(TestTransformers.class, "put", Payload.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(payload)));
      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testInputStreamListenableFuture() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestTransformers.class, "futureInputStream");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(TestTransformers.class, method);
      assertEquals(transformer, ReturnInputStream.class);
   }

   @SuppressWarnings("unchecked")
   public <T> Class<? extends Function<HttpResponse, ?>> unwrap(Class<T> type, Invokable<?, ?> method) {
      return (Class<? extends Function<HttpResponse, ?>>) transformer
            .getParserOrThrowException(Invocation.create(method, ImmutableList.of())).getTypeLiteral().getRawType();
   }

   public void testURIListenableFuture() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestTransformers.class, "futureUri");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(TestTransformers.class, method);
      assertEquals(transformer, ParseURIFromListOrLocationHeaderIf20x.class);
   }

   public static class ReturnStringIf200Context extends ReturnStringIf2xx implements
         InvocationContext<ReturnStringIf200Context> {

      public HttpRequest request;

      public ReturnStringIf200Context setContext(HttpRequest request) {
         this.request = request;
         return this;
      }

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testNoTransformer() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestTransformers.class, "noTransformer");
      unwrap(TestTransformers.class, method);
   }

   public void oneTransformerWithContext() throws SecurityException, NoSuchMethodException {
      GeneratedHttpRequest request = GeneratedHttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost")
            .invocation(
                  Invocation.create(method(TestTransformers.class, "oneTransformerWithContext"),
                        ImmutableList.of())).build();
      Function<HttpResponse, ?> transformer = this.transformer.apply(request);
      assertEquals(transformer.getClass(), ReturnStringIf200Context.class);
      assertEquals(((ReturnStringIf200Context) transformer).request, request);
   }

   public void testOneTransformer() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestTransformers.class, "oneTransformer");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(TestTransformers.class, method);
      assertEquals(transformer, ReturnStringIf2xx.class);
   }

   public interface TestRequest {
      @GET
      @VirtualHost
      @Path("/{id}")
      ListenableFuture<String> get(@PathParam("id") String id, HttpRequestOptions options);

      @GET
      @VirtualHost
      @Path("/{id}")
      ListenableFuture<String> get(@PathParam("id") String id, HttpRequestOptions... options);

      @GET
      @Path("/{id}")
      @ResponseParser(ReturnStringIf2xx.class)
      ListenableFuture<String> get(@PathParam("id") String id, @HeaderParam(HttpHeaders.HOST) String host);

      @GET
      @Path("/{id}")
      @QueryParams(keys = "max-keys", values = "0")
      ListenableFuture<String> getQuery(@PathParam("id") String id);

      @GET
      @Path("/{id}")
      @QueryParams(keys = "acl")
      ListenableFuture<String> getQueryNull(@PathParam("id") String id);

      @GET
      @Path("/{id}")
      @QueryParams(keys = "acl", values = "")
      ListenableFuture<String> getQueryEmpty(@PathParam("id") String id);

      @PUT
      @Path("/{id}")
      ListenableFuture<String> put(@PathParam("id") @ParamParser(FirstCharacter.class) String id,
            @BinderParam(BindToStringPayload.class) String payload);

      @PUT
      @Path("/{id}")
      @VirtualHost
      ListenableFuture<String> putOptions(@PathParam("id") String id, HttpRequestOptions options);

      @PUT
      @Path("/{id}")
      @Headers(keys = "foo", values = "--{id}--")
      @ResponseParser(ReturnTrueIf2xx.class)
      ListenableFuture<String> putHeader(@PathParam("id") String id,
            @BinderParam(BindToStringPayload.class) String payload);
   }

   public void testCreateGetVarArgOptionsThatProducesHeaders() throws SecurityException, NoSuchMethodException {
      Date date = new Date();
      GetOptions options = GetOptions.Builder.ifModifiedSince(date);
      Invokable<?, ?> method = method(TestRequest.class, "get", String.class,
            HttpRequestOptions[].class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", options)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost:9999"));
      assertEquals(request.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE),
            ImmutableList.of(dateService.rfc822DateFormat(date)));
   }

   public void testCreateGetOptionsThatProducesHeaders() throws SecurityException, NoSuchMethodException {
      Date date = new Date();
      GetOptions options = GetOptions.Builder.ifModifiedSince(date);
      Invokable<?, ?> method = method(TestRequest.class, "get", String.class, HttpRequestOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", options)));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost:9999"));
      assertEquals(request.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE),
            ImmutableList.of(dateService.rfc822DateFormat(date)));
   }

   public class PrefixOptions extends BaseHttpRequestOptions {
      public PrefixOptions withPrefix(String prefix) {
         queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
         return this;
      }
   }

   public void testCreateGetOptionsThatProducesQuery() throws SecurityException, NoSuchMethodException, IOException {
      PrefixOptions options = new PrefixOptions().withPrefix("1");
      Invokable<?, ?> method = method(TestRequest.class, "get", String.class, HttpRequestOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", options)));
      assertRequestLineEquals(request, "GET http://localhost:9999/1?prefix=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: localhost:9999\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCreateGetQuery() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequest.class, "getQuery", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("1")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getEndpoint().getQuery(), "max-keys=0");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   public void testCreateGetQueryNull() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequest.class, "getQueryNull", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("1")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getEndpoint().getQuery(), "acl");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   public void testCreateGetQueryEmpty() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestRequest.class, "getQueryEmpty", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of("1")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getEndpoint().getQuery(), "acl=");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   public class PayloadOptions extends BaseHttpRequestOptions {
      @Override
      public String buildStringPayload() {
         return "foo";
      }
   }

   public void testCreateGetOptionsThatProducesPayload() throws SecurityException, NoSuchMethodException, IOException {
      PayloadOptions options = new PayloadOptions();
      Invokable<?, ?> method = method(TestRequest.class, "putOptions", String.class,
            HttpRequestOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", options)));

      assertRequestLineEquals(request, "PUT http://localhost:9999/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: localhost:9999\n");
      assertPayloadEquals(request, "foo", "application/unknown", false);
   }

   @DataProvider(name = "strings")
   public Object[][] createData() {
      return new Object[][] { { "apples" }, { "sp ace" }, { "unic₪de" }, { "qu?stion" } };
   }

   @Test(dataProvider = "strings")
   public void testCreateGetRequest(String key) throws SecurityException, NoSuchMethodException,
         UnsupportedEncodingException {
      Invokable<?, ?> method = method(TestRequest.class, "get", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(key, "localhost")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      String expectedPath = "/" + URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");
      assertEquals(request.getEndpoint().getRawPath(), expectedPath);
      assertEquals(request.getEndpoint().getPath(), "/" + key);
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost"));
   }

   public void testCreatePutRequest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestRequest.class, "put", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("111", "data")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePutHeader() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestRequest.class, "putHeader", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "data")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "foo: --1--\n");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public class TestVirtualHostMethod {
      @GET
      @Path("/{id}")
      @VirtualHost
      public ListenableFuture<String> get(@PathParam("id") String id, String foo) {
         return null;
      }
   }

   @Test
   public void testVirtualHostMethod() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestVirtualHostMethod.class, "get", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "localhost")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost:9999"));
   }

   public interface TestVirtualHost {
      @GET
      @Path("/{id}")
      @VirtualHost
      ListenableFuture<String> get(@PathParam("id") String id, String foo);

      @GET
      @Path("/{id}")
      ListenableFuture<String> getPrefix(@PathParam("id") String id, @BinderParam(BindAsHostPrefix.class) String foo);

   }

   @Test
   public void testVirtualHost() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestVirtualHost.class, "get", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "localhost")));
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost:9999"));
   }

   @Test
   public void testHostPrefix() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestVirtualHost.class, "getPrefix", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("1", "holy")));
      assertEquals(request.getEndpoint().getHost(), "holy.localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testHostPrefixEmpty() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestVirtualHost.class, "getPrefix", String.class, String.class);
      processor.apply(Invocation.create(method, ImmutableList.<Object> of("1", "")));
   }

   public interface TestHeaders {
      @GET
      void oneHeader(@HeaderParam("header") String header);

      @GET
      void oneIntHeader(@HeaderParam("header") int header);

      @GET
      void twoDifferentHeaders(@HeaderParam("header1") String header1, @HeaderParam("header2") String header2);

      @GET
      void twoSameHeaders(@HeaderParam("header") String header1, @HeaderParam("header") String header2);
   }

   @Test
   public void testOneHeader() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestHeaders.class, "oneHeader", String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot"))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), ImmutableList.of("robot"));
   }

   @Test
   public void testOneIntHeader() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestHeaders.class, "oneIntHeader", int.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(1))).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), ImmutableList.of("1"));
   }

   @Test
   public void testTwoDifferentHeaders() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestHeaders.class, "twoDifferentHeaders", String.class,
            String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot", "egg"))).getHeaders();
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("header1"), ImmutableList.of("robot"));
      assertEquals(headers.get("header2"), ImmutableList.of("egg"));
   }

   @Test
   public void testTwoSameHeaders() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestHeaders.class, "twoSameHeaders", String.class, String.class);
      Multimap<String, String> headers = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("robot", "egg"))).getHeaders();
      assertEquals(headers.size(), 2);
      Collection<String> values = headers.get("header");
      assert values.contains("robot");
      assert values.contains("egg");
   }

   public interface TestEndpointParams {
      @GET
      void oneEndpointParam(@EndpointParam(parser = ConvertToURI.class) String EndpointParam);

      @Singleton
      public static class ConvertToURI implements Function<Object, URI> {

         @Override
         public URI apply(Object from) {
            return URI.create(from.toString());
         }

      }

      @GET
      void twoEndpointParams(@EndpointParam(parser = ConvertTwoToURI.class) String EndpointParam1,
            @EndpointParam(parser = ConvertTwoToURI.class) String EndpointParam2);

      @Singleton
      public static class ConvertTwoToURI implements Function<Object, URI> {

         @SuppressWarnings("unchecked")
         @Override
         public URI apply(Object from) {
            return URI.create(Joiner.on('/').join((Iterable<Object>) from));
         }

      }

   }

   @Test
   public void testOneEndpointParam() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestEndpointParams.class, "oneEndpointParam", String.class);
      URI uri = RestAnnotationProcessor.getEndpointInParametersOrNull(
            Invocation.create(method, ImmutableList.<Object> of("robot")), injector);
      assertEquals(uri, URI.create("robot"));

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testTwoDifferentEndpointParams() throws SecurityException, NoSuchMethodException, ExecutionException {
      Invokable<?, ?> method = method(TestEndpointParams.class, "twoEndpointParams", String.class,
            String.class);
      RestAnnotationProcessor.getEndpointInParametersOrNull(
            Invocation.create(method, ImmutableList.<Object> of("robot", "egg")), injector);
   }

   public interface TestPayload {
      @PUT
      public void put(@BinderParam(BindToStringPayload.class) String content);

      @PUT
      @Path("/{foo}")
      public ListenableFuture<Void> putWithPath(@PathParam("foo") String path,
            @BinderParam(BindToStringPayload.class) String content);

      @PUT
      public void twoEntities(@BinderParam(BindToStringPayload.class) String payload1,
            @BinderParam(BindToStringPayload.class) String payload2);
   }

   @Test
   public void testPut() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayload.class, "put", String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("test")));

      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "test", "application/unknown", false);
   }

   @Test
   public void putWithPath() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TestPayload.class, "putWithPath", String.class, String.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of("rabble", "test")));

      assertRequestLineEquals(request, "PUT http://localhost:9999/rabble HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "test", "application/unknown", false);
   }

   public class TestReplaceFormOptions extends BaseHttpRequestOptions {
      public TestReplaceFormOptions() {
         this.formParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   public class TestFormReplace {

      @POST
      @Path("/")
      public void formInOptions(@PathParam("bucket") String path, TestReplaceFormOptions options) {
      }

      @POST
      @Path("/")
      @FormParams(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneForm(@PathParam("bucket") String path) {
      }

      @POST
      @Path("/")
      @FormParams(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoForm(@PathParam("bucket") String path) {
      }

      @POST
      @Path("/")
      @FormParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoForms(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @POST
      @Path("/")
      @FormParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoFormsOutOfOrder(@PathParam("key") String path, @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoForm() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestFormReplace.class, "twoForm", String.class);
      Object form = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getPayload().getRawContent();
      assertEquals(form, "slash=/robot&hyphen=-robot");
   }

   @FormParams(keys = "x-amz-copy-source", values = "/{bucket}")
   public interface TestClassForm {
      @POST
      @Path("/")
      void oneForm(@PathParam("bucket") String path);
   }

   @Test
   public void testBuildOneClassForm() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestClassForm.class, "oneForm", String.class);
      Object form = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneForm() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestFormReplace.class, "oneForm", String.class);
      Object form = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot")))
            .getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoForms() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestFormReplace.class, "twoForms", String.class, String.class);
      Object form = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot", "eggs")))
            .getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoFormsOutOfOrder() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(TestFormReplace.class, "twoFormsOutOfOrder", String.class,
            String.class);
      Object form = processor.apply(Invocation.create(method, ImmutableList.<Object> of("robot", "eggs")))
            .getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/eggs/robot");
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testAddHostNullWithHost() throws Exception {
      assertNull(RestAnnotationProcessor.addHostIfMissing(null, null));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAddHostWithHostHasNoHost() throws Exception {
      assertNull(RestAnnotationProcessor.addHostIfMissing(null, new URI("/no/host")));
   }

   @Test
   public void testAddHostNullOriginal() throws Exception {
      assertNull(RestAnnotationProcessor.addHostIfMissing(null, new URI("http://foo")));
   }

   @Test
   public void testAddHostOriginalHasHost() throws Exception {

      URI original = new URI("http://hashost/foo");
      URI result = RestAnnotationProcessor.addHostIfMissing(original, new URI("http://foo"));
      assertEquals(original, result);
   }

   @Test
   public void testAddHostIfMissing() throws Exception {
      URI result = RestAnnotationProcessor.addHostIfMissing(new URI("/bar"), new URI("http://foo"));
      assertEquals(new URI("http://foo/bar"), result);
   }

   DateService dateService = new SimpleDateFormatDateService();
   RestAnnotationProcessor processor;
   TransformerForRequest transformer;

   @BeforeClass
   void setupFactory() {
      injector = ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(Callee.class, AsyncCallee.class,
                        "http://localhost:9999"))
            .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {
               protected void configure() {
                  bind(new TypeLiteral<Supplier<URI>>() {
                  }).annotatedWith(Localhost2.class).toInstance(
                        Suppliers.ofInstance(URI.create("http://localhost:1111")));
               }
            })).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      processor = injector.getInstance(RestAnnotationProcessor.class);
      transformer = injector.getInstance(TransformerForRequest.class);
   }
}
