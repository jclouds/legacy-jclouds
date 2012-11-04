/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.rest.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.io.Payloads.calculateMD5;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
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
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.jetty.http.HttpHeaders;
import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.Timeout;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MatrixParams;
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
import org.jclouds.rest.annotations.SkipEncoding;
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
import org.jclouds.xml.XMLParser;
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
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.sun.jersey.api.uri.UriBuilderImpl;

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
   public static interface AsyncCallee {
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
   @Timeout(duration = 10, timeUnit = TimeUnit.NANOSECONDS)
   public static interface Caller {

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

   @Timeout(duration = 10, timeUnit = TimeUnit.NANOSECONDS)
   public static interface Callee {

      void onePath(String path);
   }
   
   @Timeout(duration = 10, timeUnit = TimeUnit.NANOSECONDS)
   public static interface Callee2 {
      
      void onePath(String path);
   }

   public static interface AsyncCaller {
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
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(),
                  "GET http://localhost:9999/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
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
         int callCounter=0;
         @Override
         public Future<HttpResponse> submit(HttpCommand command) {
            if (callCounter == 1) assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://localhost:1111/client/1/bar/2 HTTP/1.1");
            else assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://localhost:1111/client/1/foo HTTP/1.1");
            callCounter++;
            return Futures.immediateFuture(HttpResponse.builder().build());
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
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
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

   public void testAsyncDelegateWithPathParamIsLazyLoadedAndRequestIncludesEndpointVersionAndPath() throws InterruptedException,
         ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/testing/testing/thepathparam/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
         }

      });

      try {
         child.getInstance(AsyncCallee.class);
         fail("Callee shouldn't be bound yet");
      } catch (ConfigurationException e) {

      }

      child.getInstance(AsyncCaller.class).getCalleeWithPath(URI.create("http://howdyboys"), "thepathparam").onePath("foo").get();

      assertEquals(child.getInstance(AsyncCaller.class).getURI(), URI.create("http://localhost:1111"));
   }

   public void testAsyncDelegateIsLazyLoadedAndRequestIncludesEndpointVersionAndPathOptionalPresent()
         throws InterruptedException, ExecutionException {
      Injector child = injectorForCaller(new HttpCommandExecutorService() {

         @Override
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
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
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
         }

      }, new AbstractModule() {

         @Override
         protected void configure() {
            bind(ImplicitOptionalConverter.class).toInstance(new ImplicitOptionalConverter() {

               @Override
               public Optional<Object> apply(ClassMethodArgsAndReturnVal input) {
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
         public Future<HttpResponse> submit(HttpCommand command) {
            assertEquals(command.getCurrentRequest().getRequestLine(), "GET http://howdyboys/client/1/foo HTTP/1.1");
            return Futures.immediateFuture(HttpResponse.builder().build());
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

   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

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
   }

   public void testUnEncodeQuery() {
      URI expects = URI
            .create("http://services.nirvanix.com/ws/Metadata/SetMetadata.ashx?output=json&path=adriancole-compute.testObjectOperations&metadata=chef:sushi&metadata=foo:bar&sessionToken=775ef26e-0740-4707-ad92-afe9814bc436");

      URI start = URI
            .create("http://services.nirvanix.com/ws/Metadata/SetMetadata.ashx?output=json&path=adriancole-compute.testObjectOperations&metadata=chef%3Asushi&metadata=foo%3Abar&sessionToken=775ef26e-0740-4707-ad92-afe9814bc436");
      URI value = RestAnnotationProcessor.replaceQuery(uriBuilderProvider, start, start.getQuery(), null, '/', ':');
      assertEquals(value, expects);
   }

   public void testQuery() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo");
      HttpRequest request = factory(TestQuery.class).createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&x-ms-rubbish=bin");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQuery2() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo2");
      HttpRequest request = factory(TestQuery.class).createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=bar&fooble=baz");
      assertEquals(request.getMethod(), "FOO");
   }

   public void testQuery3() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo3", String.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, new Object[] { "wonder" });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "x-ms-version=2009-07-17&foo=bar&fooble=baz&robbie=wonder");
      assertEquals(request.getMethod(), "FOO");
   }
   
   @Test
   public void testNiceNPEQueryParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestQuery.class.getMethod("foo3", String.class);
      try {
         factory(TestPath.class).createRequest(method, (String) null);
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "param{robbie} for method TestQuery.foo3");
      }
   }

   public void testNoNPEOnQueryParamWithNullable() throws SecurityException, NoSuchMethodException {
      Method method = TestQuery.class.getMethod("foo3Nullable", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, (String) null);
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "foo=bar&fooble=baz");
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
      Method method = TestPayloadParamVarargs.class.getMethod("post");
      HttpRequest request = factory(TestQuery.class).createRequest(method);
      assertRequestLineEquals(request, "POST http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "", "application/octet-stream", false);
   }
   
   private class TestHttpRequestOptions extends BaseHttpRequestOptions {
      TestHttpRequestOptions payload(String payload) { this.payload = payload; return this; }
      TestHttpRequestOptions headerParams(Multimap<String, String> headers) { this.headers.putAll(headers); return this; }
      TestHttpRequestOptions queryParams(Multimap<String, String> params) { this.queryParameters.putAll(params); return this; }
   }

   public void testHttpRequestOptionsPayloadParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("post", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, Payloads.newStringPayload("foo"));
      assertRequestLineEquals(request, "POST http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "foo", "application/octet-stream", false);
   }
   
   public void testHttpRequestWithOnlyContentType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("post", HttpRequestOptions.class);
      HttpRequest request = factory(TestPayloadParamVarargs.class).createRequest(method, new TestHttpRequestOptions().payload("fooya"));
      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testHeaderAndQueryVarargs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("varargs", Array.newInstance(HttpRequestOptions.class, 0)
            .getClass());
      HttpRequest request = factory(TestPayloadParamVarargs.class).createRequest(method,
            new TestHttpRequestOptions().payload("fooya"),
            new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
            new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value")));
      assertRequestLineEquals(request, "POST http://localhost:9999?key=value HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-header-1: fooya\n");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testHeaderAndQueryVarargsPlusReq() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("varargsWithReq", String.class, Array.newInstance(HttpRequestOptions.class, 0)
            .getClass());
      HttpRequest request = factory(TestPayloadParamVarargs.class).createRequest(method, "required param",
            new Object[]{ new TestHttpRequestOptions().payload("fooya"),
            new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
            new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value"))});
      assertRequestLineEquals(request, "POST http://localhost:9999?key=value HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "X-header-1: fooya\n");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
   }

   public void testDuplicateHeaderAndQueryVarargs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("varargs", Array.newInstance(HttpRequestOptions.class, 0)
            .getClass());
      HttpRequest request = factory(TestPayloadParamVarargs.class).createRequest(method,
            new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "value")),
            new TestHttpRequestOptions().payload("fooya"),
            new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya")),
            new TestHttpRequestOptions().queryParams(ImmutableMultimap.of("key", "anothervalue")),
            new TestHttpRequestOptions().headerParams(ImmutableMultimap.of("X-header-1", "fooya again!")),
            new TestHttpRequestOptions().payload("last_payload_wins!"));
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
      Method method = TestCustomMethod.class.getMethod("foo");
      HttpRequest request = factory(TestCustomMethod.class).createRequest(method, new Object[] {});
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
      Method method = TestOverridden.class.getMethod("foo");
      HttpRequest request = factory(TestOverridden.class).createRequest(method, new Object[] {});
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
      Method method = TestOverriddenEndpoint.class.getMethod("foo");
      HttpRequest request = factory(TestOverriddenEndpoint.class).createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPort(), 1111);
      assertEquals(request.getEndpoint().getPath(), "");
      assertEquals(request.getMethod(), "POST");
   }

   public void testOverriddenEndpointParameter() throws SecurityException, NoSuchMethodException {
      Method method = TestOverriddenEndpoint.class.getMethod("foo", URI.class);
      HttpRequest request = factory(TestOverriddenEndpoint.class).createRequest(method,
            new Object[] { URI.create("http://wowsa:8001") });
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
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePostRequestNullOk1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method);

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, "application/unknown", false);
   }

   public void testCreatePostRequestNullOk2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, "application/unknown", false);
   }

   public void testCreatePostRequestNullNotOk1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postNonnull", String.class);
      try {
         HttpRequest request = factory(TestPost.class).createRequest(method);
         Assert.fail("call should have failed with illegal null parameter, not permitted " + request + " to be created");
      } catch (NullPointerException e) {
         Assert.assertTrue(e.toString().indexOf("postNonnull parameter 1") >= 0,
               "Error message should have referred to 'parameter 1': " + e);
      }
   }

   public void testCreatePostRequestNullNotOk2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postNonnull", String.class);
      try {
         HttpRequest request = factory(TestPost.class).createRequest(method, (String) null);
         Assert.fail("call should have failed with illegal null parameter, not permitted " + request + " to be created");
      } catch (NullPointerException e) {
         Assert.assertTrue(e.toString().indexOf("postNonnull parameter 1") >= 0,
               "Error message should have referred to parameter 'parameter 1': " + e);
      }
   }

   public void testCreatePostJsonRequest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postAsJson", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "\"data\"", "application/json", false);
   }

   public void testCreatePostWithPathRequest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postWithPath", String.class, MapBinder.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data", new org.jclouds.rest.MapBinder() {
         @Override
         public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
            request.setPayload((String) postParams.get("fooble"));
            return request;
         }

         @Override
         public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
            throw new RuntimeException("this shouldn't be used in POST");
         }
      });
      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePostWithMethodBinder() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postWithMethodBinder", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);
   }

   public void testCreatePostWithMethodBinderAndDefaults() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("postWithMethodBinderAndDefaults", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

      assertRequestLineEquals(request, "POST http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\",\"rat\":\"atat\"}", "application/json", false);
   }

   public void testCreatePostWithPayload() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("testPayload", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

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
      Method method = TestMultipartForm.class.getMethod("withStringPart", String.class);
      GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
            "foobledata");
      assertRequestLineEquals(httpRequest, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest,//
            "----JCLOUDS--\r\n" + //
                  "Content-Disposition: form-data; name=\"fooble\"\r\n" + //
                  "\r\n" + //
                  "foobledata\r\n" + //
                  "----JCLOUDS----\r\n", "multipart/form-data; boundary=--JCLOUDS--", false);
   }

   public void testMultipartWithStringPartNullNotOkay() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withStringPart", String.class);
      try {
         GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
               (String) null);
         Assert.fail("call should have failed with illegal null parameter, not permitted " + httpRequest
               + " to be created");
      } catch (NullPointerException e) {
         Assert.assertTrue(e.toString().indexOf("fooble") >= 0,
               "Error message should have referred to parameter 'fooble': " + e);
      }
   }

   public void testMultipartWithParamStringPart() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withParamStringPart", String.class, String.class);
      GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
            "name", "foobledata");
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

   public void testMultipartWithParamStringPartNullNotOk() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withParamStringPart", String.class, String.class);
      try {
         GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
               null, "foobledata");
         Assert.fail("call should have failed with illegal null parameter, not permitted " + httpRequest
               + " to be created");
      } catch (NullPointerException e) {
         Assert.assertTrue(e.toString().indexOf("name") >= 0,
               "Error message should have referred to parameter 'name': " + e);
      }
   }

   public void testMultipartWithParamFilePart() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withParamFilePart", String.class, File.class);
      File file = File.createTempFile("foo", "bar");
      Files.append("foobledata", file, UTF_8);
      file.deleteOnExit();

      GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
            "name", file);
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
      Method method = TestMultipartForm.class.getMethod("withParamByteArrayBinaryPart", String.class, byte[].class);
      GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
            "name", "goo".getBytes());
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
      Method method = TestMultipartForm.class.getMethod("withParamFileBinaryPart", String.class, File.class);
      File file = File.createTempFile("foo", "bar");
      Files.write(new byte[] { 17, 26, 39, 40, 50 }, file);
      file.deleteOnExit();

      GeneratedHttpRequest httpRequest = factory(TestMultipartForm.class).createRequest(method,
            "name", file);
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
      @Consumes(MediaType.APPLICATION_JSON)
      View putWithMethodBinderConsumes(@PathParam("foo") @PayloadParam("fooble") String path);

      @GET
      @Path("/")
      @Consumes(MediaType.APPLICATION_JSON)
      Map<String, String> testGeneric();

      @GET
      @Path("/")
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<Map<String, String>> testGeneric2();

      @GET
      @Path("/")
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<? extends Map<String, String>> testGeneric3();

      @GET
      @Path("/")
      @Unwrap
      @Consumes(MediaType.APPLICATION_JSON)
      String testUnwrap();

      @GET
      @Path("/")
      @SelectJson("foo")
      @Consumes(MediaType.APPLICATION_JSON)
      String testUnwrapValueNamed();

      @POST
      @Path("/")
      String testWrapWith(@WrapWith("foo") String param);

      @GET
      @Path("/")
      @Unwrap
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<String> testUnwrap2();

      @GET
      @Path("/")
      @Unwrap
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<Set<String>> testUnwrap3();

      @GET
      @Path("/")
      @Unwrap
      @Consumes(MediaType.APPLICATION_JSON)
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
      @Consumes(MediaType.APPLICATION_JSON)
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
      Method method = TestPut.class.getMethod("rowdy", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "ROWDY http://localhost:9999/strings/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   public void testAlternateHttpMethodSameArity() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("rowdy", int.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "ROWDY http://localhost:9999/ints/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCreatePutWithMethodBinder() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("putWithMethodBinder", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);
   }

   public void testCreatePutWithMethodProduces() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("putWithMethodBinderProduces", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "text/plain", false);
   }

   @SuppressWarnings("unchecked")
   public void testCreatePutWithMethodConsumes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("putWithMethodBinderConsumes", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "PUT http://localhost:9999/data HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, "{\"fooble\":\"data\"}", "application/json", false);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, View> parser = (Function<HttpResponse, View>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()).foo, "bar");

   }

   @SuppressWarnings("unchecked")
   public void testGeneric1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testGeneric2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric2");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testGeneric3() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()),
            ImmutableMap.of("foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testUnwrap1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   @SuppressWarnings("unchecked")
   public void testUnwrapValueNamed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrapValueNamed");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseFirstJsonValueNamed.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   public void testWrapWith() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testWrapWith", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "bar");
      assertPayloadEquals(request, "{\"foo\":\"bar\"}", "application/json", false);
   }

   @SuppressWarnings("unchecked")
   public void testUnwrap2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap2");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{ foo:\"bar\"}").build()), "bar");

   }

   @SuppressWarnings("unchecked")
   public void testUnwrap3() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}").build()),
            ImmutableSet.of("0.7.0", "0.7.1"));
   }

   @SuppressWarnings("unchecked")
   public void testUnwrap4() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap4");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}").build()),
            ImmutableSet.of("0.7.0", "0.7.1"));
   }

   @SuppressWarnings("unchecked")
   public void selectLong() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("selectLong");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseFirstJsonValueNamed.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok")
            .payload("{ \"destroyvirtualmachineresponse\" : {\"jobid\":4} }").build()), Long.valueOf(4));
   }

   @SuppressWarnings("unchecked")
   public void selectLongAddOne() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("selectLongAddOne");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok")
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
   }

   @Test
   public void testRequestFilter() throws SecurityException, NoSuchMethodException {
      Method method = TestRequestFilter.class.getMethod("get");
      HttpRequest request = factory(TestRequestFilter.class).createRequest(method, new Object[] {});
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter1.class);
      assertEquals(request.getFilters().get(1).getClass(), TestRequestFilter2.class);
   }

   public void testRequestFilterOverride() throws SecurityException, NoSuchMethodException {
      Method method = TestRequestFilter.class.getMethod("getOverride");
      HttpRequest request = factory(TestRequestFilter.class).createRequest(method, new Object[] {});
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter2.class);
   }

   public void testRequestFilterOverrideOnRequest() throws SecurityException, NoSuchMethodException {
      Method method = TestRequestFilter.class.getMethod("getOverride", HttpRequest.class);
      HttpRequest request = factory(TestRequestFilter.class).createRequest(
            method,
            HttpRequest.builder().method("GET").endpoint("http://localhost")
                  .addHeader("foo", "bar").build());
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), TestRequestFilter2.class);
   }

   @SkipEncoding('/')
   public class TestEncoding {
      @GET
      @Path("/{path1}/{path2}")
      public void twoPaths(@PathParam("path1") String path, @PathParam("path2") String path2) {
      }
   }

   @Test
   public void testSkipEncoding() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest request = factory(TestEncoding.class).createRequest(method, new Object[] { "1", "localhost" });
      assertEquals(request.getEndpoint().getPath(), "/1/localhost");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @Test
   public void testEncodingPath() throws SecurityException, NoSuchMethodException {
      Method method = TestEncoding.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest request = factory(TestEncoding.class).createRequest(method, new Object[] { "/", "localhost" });
      assertEquals(request.getEndpoint().getPath(), "///localhost");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @SkipEncoding('/')
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
      Method method = TestConstantPathParam.class.getMethod("twoPaths", String.class, String.class);
      HttpRequest request = factory(TestConstantPathParam.class).createRequest(method,
            new Object[] { "1", "localhost" });
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

      @GET
      @Path("/")
      public void oneMatrixParamExtractor(@MatrixParam("one") @ParamParser(FirstCharacter.class) String one) {
      }

      @GET
      @Path("/{path}")
      @PathParam("path")
      @ParamParser(FirstCharacterFirstElement.class)
      public void onePathParamExtractorMethod(String path) {
      }
   }
   
   @Test
   public void testNiceNPEPathParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("onePath", String.class);
      try {
         factory(TestPath.class).createRequest(method, (String) null);
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "param{path} for method TestPath.onePath");
      }
   }
   
   @Test
   public void testPathParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("onePathParamExtractor", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, new Object[] { "localhost" });
      assertRequestLineEquals(request, "GET http://localhost:9999/l HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   @Test
   public void testQueryParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneQueryParamExtractor", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, "localhost");
      assertRequestLineEquals(request, "GET http://localhost:9999/?one=l HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }

   @Test
   public void testMatrixParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneMatrixParamExtractor", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, new Object[] { "localhost" });
      assertRequestLineEquals(request, "GET http://localhost:9999/;one=l HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);
   }
   
   @Test
   public void testNiceNPEMatrixParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneMatrixParamExtractor", String.class);
      try {
         factory(TestPath.class).createRequest(method, (String) null);
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "param{one} for method TestPath.oneMatrixParamExtractor");
      }
   }
   
   @Test
   public void testFormParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneFormParamExtractor", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, new Object[] { "localhost" });
      assertRequestLineEquals(request, "POST http://localhost:9999/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "one=l", "application/x-www-form-urlencoded", false);
   }
   
   @Test
   public void testNiceNPEFormParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneFormParamExtractor", String.class);
      try {
         factory(TestPath.class).createRequest(method, (String) null);
      } catch (NullPointerException e) {
         assertEquals(e.getMessage(), "param{one} for method TestPath.oneFormParamExtractor");
      }
   }
   
   @Test
   public void testParamExtractorMethod() throws SecurityException, NoSuchMethodException {
      Method method = TestPath.class.getMethod("onePathParamExtractorMethod", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, new Object[] { "localhost" });
      assertEquals(request.getEndpoint().getPath(), "/l");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
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
      Method oneHeader = TestHeader.class.getMethod("twoHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader, new Object[] { "robot" })
            .getHeaders();
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
      Method oneHeader = TestClassHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestClassHeader.class).createRequest(oneHeader,
            new Object[] { "robot" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot"));
   }

   @Test
   public void testBuildOneHeader() throws SecurityException, NoSuchMethodException {
      Method oneHeader = TestHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader, new Object[] { "robot" })
            .getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot"));
   }

   @Test
   public void testBuildTwoHeaders() throws SecurityException, NoSuchMethodException {
      Method twoHeaders = TestHeader.class.getMethod("twoHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(twoHeaders,
            new Object[] { "robot", "eggs" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), ImmutableList.of("/robot/eggs"));
   }

   @Test
   public void testBuildTwoHeadersOutOfOrder() throws SecurityException, NoSuchMethodException {
      Method twoHeadersOutOfOrder = TestHeader.class.getMethod("twoHeadersOutOfOrder", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(twoHeadersOutOfOrder,
            new Object[] { "robot", "eggs" }).getHeaders();
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
      Method oneQuery = TestQueryReplace.class.getMethod("queryInOptions", String.class, TestReplaceQueryOptions.class);
      String query = factory(TestQueryReplace.class)
            .createRequest(oneQuery, new Object[] { "robot", new TestReplaceQueryOptions() }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @SkipEncoding('/')
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
      Method oneQuery = TestQueryReplace.class.getMethod("twoQuery", String.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery, new Object[] { "robot" }).getEndpoint()
            .getQuery();
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
      Method oneQuery = TestClassQuery.class.getMethod("oneQuery", String.class);
      String query = factory(TestClassQuery.class).createRequest(oneQuery, new Object[] { "robot" }).getEndpoint()
            .getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneQuery() throws SecurityException, NoSuchMethodException {
      Method oneQuery = TestQueryReplace.class.getMethod("oneQuery", String.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery, new Object[] { "robot" }).getEndpoint()
            .getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoQuerys() throws SecurityException, NoSuchMethodException {
      Method twoQuerys = TestQueryReplace.class.getMethod("twoQuerys", String.class, String.class);
      String query = factory(TestQueryReplace.class).createRequest(twoQuerys, new Object[] { "robot", "eggs" })
            .getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoQuerysOutOfOrder() throws SecurityException, NoSuchMethodException {
      Method twoQuerysOutOfOrder = TestQueryReplace.class.getMethod("twoQuerysOutOfOrder", String.class, String.class);
      String query = factory(TestQueryReplace.class)
            .createRequest(twoQuerysOutOfOrder, new Object[] { "robot", "eggs" }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/eggs/robot");
   }

   public class TestReplaceMatrixOptions extends BaseHttpRequestOptions {
      public TestReplaceMatrixOptions() {
         this.matrixParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @Test
   public void testMatrixInOptions() throws SecurityException, NoSuchMethodException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("matrixInOptions", String.class,
            TestReplaceMatrixOptions.class);
      String path = factory(TestMatrixReplace.class)
            .createRequest(oneMatrix, new Object[] { "robot", new TestReplaceMatrixOptions() }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Path("/")
   public class TestMatrixReplace {

      @GET
      @Path("/")
      public void matrixInOptions(@PathParam("bucket") String path, TestReplaceMatrixOptions options) {
      }

      @GET
      @Path("/")
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}")
      public void oneMatrix(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @MatrixParams(keys = { "slash", "hyphen" }, values = { "/{bucket}", "-{bucket}" })
      public void twoMatrix(@PathParam("bucket") String path) {
      }

      @GET
      @Path("/")
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoMatrixs(@PathParam("bucket") String path, @PathParam("key") String path2) {
      }

      @GET
      @Path("/")
      @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}/{key}")
      public void twoMatrixsOutOfOrder(@PathParam("key") String path, @PathParam("bucket") String path2) {
      }
   }

   @Test
   public void testBuildTwoMatrix() throws SecurityException, NoSuchMethodException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("twoMatrix", String.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix, new Object[] { "robot" }).getEndpoint()
            .getPath();
      assertEquals(path, "/;slash=/robot;hyphen=-robot");
   }

   @MatrixParams(keys = "x-amz-copy-source", values = "/{bucket}")
   @Path("/")
   public class TestClassMatrix {
      @GET
      @Path("/")
      public void oneMatrix(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassMatrix() throws SecurityException, NoSuchMethodException {
      Method oneMatrix = TestClassMatrix.class.getMethod("oneMatrix", String.class);
      String path = factory(TestClassMatrix.class).createRequest(oneMatrix, new Object[] { "robot" }).getEndpoint()
            .getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneMatrix() throws SecurityException, NoSuchMethodException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("oneMatrix", String.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix, new Object[] { "robot" }).getEndpoint()
            .getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoMatrixs() throws SecurityException, NoSuchMethodException {
      Method twoMatrixs = TestMatrixReplace.class.getMethod("twoMatrixs", String.class, String.class);
      String path = factory(TestMatrixReplace.class).createRequest(twoMatrixs, new Object[] { "robot", "eggs" })
            .getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoMatrixsOutOfOrder() throws SecurityException, NoSuchMethodException {
      Method twoMatrixsOutOfOrder = TestMatrixReplace.class.getMethod("twoMatrixsOutOfOrder", String.class,
            String.class);
      String path = factory(TestMatrixReplace.class)
            .createRequest(twoMatrixsOutOfOrder, new Object[] { "robot", "eggs" }).getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/eggs/robot");
   }

   public interface TestTransformers {
      @GET
      int noTransformer();

      @GET
      @ResponseParser(ReturnStringIf2xx.class)
      void oneTransformer();

      @GET
      @ResponseParser(ReturnStringIf200Context.class)
      void oneTransformerWithContext();

      @GET
      InputStream inputStream();

      @GET
      ListenableFuture<InputStream> futureInputStream();

      @GET
      URI uri();

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
      Method method = TestTransformers.class.getMethod("put", PayloadEnclosing.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method,
            new PayloadEnclosingImpl(newStringPayload("whoops")));
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayloadEnclosingGenerateMD5() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("put", PayloadEnclosing.class);
      PayloadEnclosing payloadEnclosing = new PayloadEnclosingImpl(newStringPayload("whoops"));
      calculateMD5(payloadEnclosing, crypto.md5());
      HttpRequest request = factory(TestQuery.class).createRequest(method, payloadEnclosing);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");

      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutInputStreamPayloadEnclosingGenerateMD5() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = TestTransformers.class.getMethod("put", PayloadEnclosing.class);
      PayloadEnclosing payloadEnclosing = new PayloadEnclosingImpl(
            newInputStreamPayload(Strings2.toInputStream("whoops")));

      calculateMD5(payloadEnclosing, crypto.md5());
      HttpRequest request = factory(TestQuery.class).createRequest(method, payloadEnclosing);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");

      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutPayloadChunkedNoContentLength() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("putXfer", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, newStringPayload("whoops"));
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Transfer-Encoding: chunked\n");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayload() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, newStringPayload("whoops"));
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutPayloadContentDisposition() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentDisposition("attachment; filename=photo.jpg");
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", "attachment; filename=photo.jpg", null, null, false);
   }

   public void testPutPayloadContentEncoding() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentEncoding("gzip");
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", null, "gzip", null, false);
   }

   public void testPutPayloadContentLanguage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      Payload payload = newStringPayload("whoops");
      payload.getContentMetadata().setContentLanguage("en");
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", null, null, "en", false);
   }

   public void testPutPayloadWithGeneratedMD5AndNoContentType() throws SecurityException, NoSuchMethodException,
         IOException {
      Payload payload = newStringPayload("whoops");
      calculateMD5(payload, crypto.md5());
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testPutInputStreamPayload() throws SecurityException, NoSuchMethodException, IOException {
      Payload payload = newInputStreamPayload(Strings2.toInputStream("whoops"));
      payload.getContentMetadata().setContentLength((long) "whoops".length());
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", false);
   }

   public void testPutInputStreamPayloadWithMD5() throws NoSuchAlgorithmException, IOException, SecurityException,
         NoSuchMethodException {
      Payload payload = newStringPayload("whoops");
      calculateMD5(payload, crypto.md5());
      Method method = TestTransformers.class.getMethod("put", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, payload);
      assertRequestLineEquals(request, "PUT http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "whoops", "application/unknown", true);
   }

   public void testInputStream() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("inputStream");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(factory(TestTransformers.class), method);
      assertEquals(transformer, ReturnInputStream.class);
   }

   public void testInputStreamListenableFuture() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("futureInputStream");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(factory(TestTransformers.class), method);
      assertEquals(transformer, ReturnInputStream.class);
   }

   @SuppressWarnings("unchecked")
   public static <T> Class<? extends Function<HttpResponse, ?>> unwrap(RestAnnotationProcessor<T> processor,
         Method method) {
      return (Class<? extends Function<HttpResponse, ?>>) RestAnnotationProcessor.getParserOrThrowException(method)
            .getTypeLiteral().getRawType();
   }

   public void testURI() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("uri");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(factory(TestTransformers.class), method);
      assertEquals(transformer, ParseURIFromListOrLocationHeaderIf20x.class);
   }

   public void testURIListenableFuture() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("futureUri");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(factory(TestTransformers.class), method);
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

   @SuppressWarnings("static-access")
   @Test(expectedExceptions = { RuntimeException.class })
   public void testNoTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("noTransformer");
      factory(TestTransformers.class).getParserOrThrowException(method);
   }

   public void oneTransformerWithContext() throws SecurityException, NoSuchMethodException {
      RestAnnotationProcessor<TestTransformers> processor = factory(TestTransformers.class);
      Method method = TestTransformers.class.getMethod("oneTransformerWithContext");
      GeneratedHttpRequest request = GeneratedHttpRequest.builder()
            .method("GET").endpoint("http://localhost").declaring(TestTransformers.class)
            .javaMethod(method).args(new Object[] {}).build();
      Function<HttpResponse, ?> transformer = processor.createResponseParser(method, request);
      assertEquals(transformer.getClass(), ReturnStringIf200Context.class);
      assertEquals(((ReturnStringIf200Context) transformer).request, request);
   }

   public void testOneTransformer() throws SecurityException, NoSuchMethodException {
      Method method = TestTransformers.class.getMethod("oneTransformer");
      Class<? extends Function<HttpResponse, ?>> transformer = unwrap(factory(TestTransformers.class), method);
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
      Method method = TestRequest.class.getMethod("get", String.class, HttpRequestOptions[].class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1", options });
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
      Method method = TestRequest.class.getMethod("get", String.class, HttpRequestOptions.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1", options });
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
      Method method = TestRequest.class.getMethod("get", String.class, HttpRequestOptions.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1", options });
      assertRequestLineEquals(request, "GET http://localhost:9999/1?prefix=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: localhost:9999\n");
      assertPayloadEquals(request, null, null, false);
   }

   public void testCreateGetQuery() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQuery", String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1" });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getEndpoint().getQuery(), "max-keys=0");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   public void testCreateGetQueryNull() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQueryNull", String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1" });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getEndpoint().getQuery(), "acl");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   public void testCreateGetQueryEmpty() throws SecurityException, NoSuchMethodException {
      Method method = TestRequest.class.getMethod("getQueryEmpty", String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1" });
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
      Method method = TestRequest.class.getMethod("putOptions", String.class, HttpRequestOptions.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, "1", options);

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
      Method method = TestRequest.class.getMethod("get", String.class, String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { key, "localhost" });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      String expectedPath = "/" + URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");
      assertEquals(request.getEndpoint().getRawPath(), expectedPath);
      assertEquals(request.getEndpoint().getPath(), "/" + key);
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost"));
   }

   public void testCreatePutRequest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestRequest.class.getMethod("put", String.class, String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, "111", "data");

      assertRequestLineEquals(request, "PUT http://localhost:9999/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePutHeader() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestRequest.class.getMethod("putHeader", String.class, String.class);
      HttpRequest request = factory(TestRequest.class).createRequest(method, "1", "data");

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
      Method method = TestVirtualHostMethod.class.getMethod("get", String.class, String.class);
      HttpRequest request = factory(TestVirtualHostMethod.class).createRequest(method,
            new Object[] { "1", "localhost" });
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
      Method method = TestVirtualHost.class.getMethod("get", String.class, String.class);
      HttpRequest request = factory(TestVirtualHost.class).createRequest(method, new Object[] { "1", "localhost" });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), ImmutableList.of("localhost:9999"));
   }

   @Test
   public void testHostPrefix() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefix", String.class, String.class);
      HttpRequest request = factory(TestVirtualHost.class).createRequest(method, new Object[] { "1", "holy" });
      assertEquals(request.getEndpoint().getHost(), "holy.localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testHostPrefixEmpty() throws SecurityException, NoSuchMethodException {
      Method method = TestVirtualHost.class.getMethod("getPrefix", String.class, String.class);
      factory(TestVirtualHost.class).createRequest(method, "1", "");
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
      Method method = TestHeaders.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
            ImmutableMultimap.<String, String> of().entries(), method, "robot");
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), ImmutableList.of("robot"));
   }

   @Test
   public void testOneIntHeader() throws SecurityException, NoSuchMethodException, ExecutionException {
      Method method = TestHeaders.class.getMethod("oneIntHeader", int.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
            ImmutableMultimap.<String, String> of().entries(), method, 1);
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), ImmutableList.of("1"));
   }

   @Test
   public void testTwoDifferentHeaders() throws SecurityException, NoSuchMethodException, ExecutionException {
      Method method = TestHeaders.class.getMethod("twoDifferentHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
            ImmutableMultimap.<String, String> of().entries(), method, "robot", "egg");
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("header1"), ImmutableList.of("robot"));
      assertEquals(headers.get("header2"), ImmutableList.of("egg"));
   }

   @Test
   public void testTwoSameHeaders() throws SecurityException, NoSuchMethodException, ExecutionException {
      Method method = TestHeaders.class.getMethod("twoSameHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
            ImmutableMultimap.<String, String> of().entries(), method, "robot", "egg");
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

   @SuppressWarnings("static-access")
   @Test
   public void testOneEndpointParam() throws SecurityException, NoSuchMethodException, ExecutionException {
      Method method = TestEndpointParams.class.getMethod("oneEndpointParam", String.class);
      URI uri = factory(TestEndpointParams.class).getEndpointInParametersOrNull(method, new Object[] { "robot" },
            injector);
      assertEquals(uri, URI.create("robot"));

   }

   @SuppressWarnings("static-access")
   @Test
   public void testTwoDifferentEndpointParams() throws SecurityException, NoSuchMethodException, ExecutionException {
      Method method = TestEndpointParams.class.getMethod("twoEndpointParams", String.class, String.class);
      URI uri = factory(TestEndpointParams.class).getEndpointInParametersOrNull(method,
            new Object[] { "robot", "egg" }, injector);
      assertEquals(uri, URI.create("robot/egg"));
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
      RestAnnotationProcessor<TestPayload> processor = factory(TestPayload.class);
      Method method = TestPayload.class.getMethod("put", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "test");

      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "test", "application/unknown", false);
   }

   @Test
   public void putWithPath() throws SecurityException, NoSuchMethodException, IOException {
      RestAnnotationProcessor<TestPayload> processor = factory(TestPayload.class);
      Method method = TestPayload.class.getMethod("putWithPath", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "rabble", "test");

      assertRequestLineEquals(request, "PUT http://localhost:9999/rabble HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "test", "application/unknown", false);
   }

   public class TestReplaceFormOptions extends BaseHttpRequestOptions {
      public TestReplaceFormOptions() {
         this.formParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @SkipEncoding('/')
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
      Method oneForm = TestFormReplace.class.getMethod("twoForm", String.class);
      Object form = factory(TestFormReplace.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "slash=/robot&hyphen=-robot");
   }

   @FormParams(keys = "x-amz-copy-source", values = "/{bucket}")
   @SkipEncoding('/')
   public interface TestClassForm {
      @Provides
      Set<String> set();

      @Named("bar")
      @Provides
      Set<String> foo();

      @Named("exception")
      @Provides
      Set<String> exception();

      @Named("NoSuchElementException")
      @Provides
      Set<String> noSuchElementException();
      
      @POST
      @Path("/")
      void oneForm(@PathParam("bucket") String path);
   }

   @Test
   public void testProvidesWithGeneric() throws SecurityException, NoSuchMethodException {
      Set<String> set = injector.getInstance(AsyncClientFactory.class).create(TestClassForm.class).set();
      assertEquals(set, ImmutableSet.of("foo"));
   }

   @Test
   public void testProvidesWithGenericQualified() throws SecurityException, NoSuchMethodException {
      Set<String> set = injector.getInstance(AsyncClientFactory.class).create(TestClassForm.class).foo();
      assertEquals(set, ImmutableSet.of("bar"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testProvidesWithGenericQualifiedAuthorizationException() throws SecurityException, NoSuchMethodException {
      injector.getInstance(AsyncClientFactory.class).create(TestClassForm.class).exception();
   }
   
   @Test(expectedExceptions = NoSuchElementException.class)
   public void testProvidesWithGenericQualifiedNoSuchElementException() throws SecurityException, NoSuchMethodException {
      injector.getInstance(AsyncClientFactory.class).create(TestClassForm.class).noSuchElementException();
   }
   
   @Test
   public void testBuildOneClassForm() throws SecurityException, NoSuchMethodException {
      Method oneForm = TestClassForm.class.getMethod("oneForm", String.class);
      Object form = factory(TestClassForm.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneForm() throws SecurityException, NoSuchMethodException {
      Method oneForm = TestFormReplace.class.getMethod("oneForm", String.class);
      Object form = factory(TestFormReplace.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoForms() throws SecurityException, NoSuchMethodException {
      Method twoForms = TestFormReplace.class.getMethod("twoForms", String.class, String.class);
      Object form = factory(TestFormReplace.class).createRequest(twoForms, "robot", "eggs").getPayload()
            .getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoFormsOutOfOrder() throws SecurityException, NoSuchMethodException {
      Method twoFormsOutOfOrder = TestFormReplace.class.getMethod("twoFormsOutOfOrder", String.class, String.class);
      Object form = factory(TestFormReplace.class).createRequest(twoFormsOutOfOrder, "robot", "eggs").getPayload()
            .getRawContent();
      assertEquals(form, "x-amz-copy-source=/eggs/robot");
   }

   public interface TestJAXBResponseParser {
      @GET
      @Path("/jaxb/annotation")
      @JAXBResponseParser
      public ListenableFuture<TestJAXBDomain> jaxbGetWithAnnotation();

      @GET
      @Path("/jaxb/header")
      @Consumes(MediaType.APPLICATION_XML)
      public ListenableFuture<TestJAXBDomain> jaxbGetWithAcceptHeader();
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

   @Test
   public void testCreateJAXBResponseParserWithAnnotation() throws SecurityException, NoSuchMethodException {
      RestAnnotationProcessor<TestJAXBResponseParser> processor = factory(TestJAXBResponseParser.class);
      Method method = TestJAXBResponseParser.class.getMethod("jaxbGetWithAnnotation");
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
            .declaring(TestJAXBResponseParser.class).javaMethod(method).args(new Object[] {}).build();
      Function<HttpResponse, ?> transformer = processor.createResponseParser(method, request);
      assertEquals(transformer.getClass(), ParseXMLWithJAXB.class);
   }

   @Test
   public void testCreateJAXBResponseParserWithAcceptHeader() throws SecurityException, NoSuchMethodException {
      RestAnnotationProcessor<TestJAXBResponseParser> processor = factory(TestJAXBResponseParser.class);
      Method method = TestJAXBResponseParser.class.getMethod("jaxbGetWithAcceptHeader");
      GeneratedHttpRequest request = GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
            .declaring(TestJAXBResponseParser.class).javaMethod(method).args(new Object[] {}).build();
      Function<HttpResponse, ?> transformer = processor.createResponseParser(method, request);
      assertEquals(transformer.getClass(), ParseXMLWithJAXB.class);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testJAXBResponseParserWithAnnotation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestJAXBResponseParser.class.getMethod("jaxbGetWithAnnotation");
      HttpRequest request = factory(TestJAXBResponseParser.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      // now test that it works!

      Function<HttpResponse, TestJAXBDomain> parser = (Function<HttpResponse, TestJAXBDomain>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      StringBuilder payload = new StringBuilder(XMLParser.DEFAULT_XML_HEADER);
      payload.append("<test><elem>Hello World</elem></test>");
      TestJAXBDomain domain = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(payload.toString()).build());
      assertEquals(domain.getElem(), "Hello World");
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testJAXBResponseParserWithAcceptHeader() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestJAXBResponseParser.class.getMethod("jaxbGetWithAcceptHeader");
      HttpRequest request = factory(TestJAXBResponseParser.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      // now test that it works!

      Function<HttpResponse, TestJAXBDomain> parser = (Function<HttpResponse, TestJAXBDomain>) RestAnnotationProcessor
            .createResponseParser(parserFactory, injector, method, request);

      StringBuilder payload = new StringBuilder(XMLParser.DEFAULT_XML_HEADER);
      payload.append("<test><elem>Hello World</elem></test>");
      TestJAXBDomain domain = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(payload.toString()).build());
      assertEquals(domain.getElem(), "Hello World");
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

   @BeforeClass
   void setupFactory() {
      injector =  ContextBuilder
            .newBuilder(
                  AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(Callee.class, AsyncCallee.class,
                        "http://localhost:9999"))
            .modules(ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(), new AbstractModule() {

               @Override
               protected void configure() {
                  bind(new TypeLiteral<Set<String>>() {
                  }).toInstance(ImmutableSet.of("foo"));
                  bind(new TypeLiteral<Set<String>>() {
                  }).annotatedWith(Names.named("bar")).toInstance(ImmutableSet.of("bar"));
                  bind(new TypeLiteral<Supplier<URI>>() {
                  }).annotatedWith(Localhost2.class).toInstance(
                        Suppliers.ofInstance(URI.create("http://localhost:1111")));
               }

               @Provides
               @Named("exception")
               Set<String> exception() {
                  throw new AuthorizationException();
               }
               
               @Provides
               @Named("NoSuchElementException")
               Set<String> noSuchElementException() {
                  throw new NoSuchElementException();
               }
               
            })).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

}
