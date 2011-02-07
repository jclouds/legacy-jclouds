/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rest.internal;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.util.Types.newParameterizedType;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.io.Payloads.calculateMD5;
import static org.jclouds.io.Payloads.newInputStreamPayload;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;

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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
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

import org.easymock.IArgumentMatcher;
import org.jclouds.concurrent.Timeout;
import org.jclouds.crypto.Crypto;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseURIFromListOrLocationHeaderIf20x;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnInputStream;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.http.internal.PayloadEnclosingImpl;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;
import org.jclouds.io.PayloadEnclosing;
import org.jclouds.io.Payloads;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientTest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.MapPayloadParams;
import org.jclouds.rest.annotations.MatrixParams;
import org.jclouds.rest.annotations.OverrideRequestFilters;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PartParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.binders.BindAsHostPrefix;
import org.jclouds.rest.binders.BindMapToMatrixParams;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.util.Strings2;
import org.mortbay.jetty.HttpHeaders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * Tests behavior of {@code RestAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RestAnnotationProcessorTest")
public class RestAnnotationProcessorTest extends BaseRestClientTest {

   @RequiresHttp
   @ConfiguresRestClient
   protected static class CallerCalleeModule extends RestClientModule<Caller, AsyncCaller> {
      CallerCalleeModule() {
         super(Caller.class, AsyncCaller.class, ImmutableMap.<Class<?>, Class<?>> of(Callee.class, AsyncCallee.class));
      }

      @Override
      protected void configure() {
         super.configure();
         bind(URI.class).annotatedWith(Localhost2.class).toInstance(URI.create("http://localhost:1111"));
         bind(IOExceptionRetryHandler.class).toInstance(IOExceptionRetryHandler.NEVER_RETRY);
      }

   }

   @Path("/client/{jclouds.api-version}")
   public static interface AsyncCallee {
      @GET
      @Path("/{path}")
      ListenableFuture<Void> onePath(@PathParam("path") String path);
   }

   @Endpoint(Localhost2.class)
   @Timeout(duration = 10, timeUnit = TimeUnit.NANOSECONDS)
   public static interface Caller {

      @Delegate
      public Callee getCallee();
   }

   @Timeout(duration = 10, timeUnit = TimeUnit.NANOSECONDS)
   public static interface Callee {

      void onePath(String path);
   }

   public static interface AsyncCaller {

      @Delegate
      public AsyncCallee getCallee();
   }

   @SuppressWarnings("unchecked")
   public void testDelegateAsyncIncludesVersion() throws SecurityException, NoSuchMethodException,
            InterruptedException, ExecutionException {
      Injector child = injectorForClient();
      TransformingHttpCommandExecutorService mock = child.getInstance(TransformingHttpCommandExecutorService.class);

      ReleasePayloadAndReturn function = child.getInstance(ReleasePayloadAndReturn.class);

      try {
         child.getInstance(AsyncCallee.class);
         assert false : "Callee shouldn't be bound yet";
      } catch (ConfigurationException e) {

      }

      AsyncCaller caller = child.getInstance(AsyncCaller.class);
      expect(mock.submit(requestLineEquals("GET http://localhost:9999/client/1/foo HTTP/1.1"), eq(function)))
               .andReturn(createNiceMock(ListenableFuture.class)).atLeastOnce();
      replay(mock);

      caller.getCallee().onePath("foo");

      verify(mock);

   }

   public static HttpCommand requestLineEquals(final String requestLine) {
      reportMatcher(new IArgumentMatcher() {

         @Override
         public void appendTo(StringBuffer buffer) {
            buffer.append("requestLineEquals(");
            buffer.append(requestLine);
            buffer.append(")");
         }

         @Override
         public boolean matches(Object arg) {
            return ((HttpCommand) arg).getCurrentRequest().getRequestLine().equals(requestLine);
         }

      });
      return null;
   }

   public void testDelegateWithOverridingEndpoint() throws SecurityException, NoSuchMethodException,
            InterruptedException, ExecutionException {
      Injector child = injectorForClient();
      TransformingHttpCommandExecutorService mock = child.getInstance(TransformingHttpCommandExecutorService.class);

      ReleasePayloadAndReturn function = child.getInstance(ReleasePayloadAndReturn.class);

      try {
         child.getInstance(Callee.class);
         assert false : "Callee shouldn't be bound yet";
      } catch (ConfigurationException e) {

      }

      Caller caller = child.getInstance(Caller.class);
      expect(mock.submit(requestLineEquals("GET http://localhost:1111/client/1/foo HTTP/1.1"), eq(function)))
               .andReturn(Futures.<Void> immediateFuture(null)).atLeastOnce();
      replay(mock);

      caller.getCallee().onePath("foo");

      verify(mock);

   }

   private Injector injectorForClient() {

      RestContextSpec<Caller, AsyncCaller> contextSpec = contextSpec("test", "http://localhost:9999", "1", "",
               "userfoo", null, Caller.class, AsyncCaller.class, ImmutableSet.<Module> of(new MockModule(),
                        new NullLoggingModule(), new CallerCalleeModule()));

      return createContextBuilder(contextSpec).buildInjector();

   }

   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   @Target( { ElementType.METHOD })
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

   public interface TestPayloadParamVarargs {
      @POST
      public void varargs(HttpRequestOptions... options);

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

   public void testHttpRequestOptionsPayloadParam() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("post", Payload.class);
      HttpRequest request = factory(TestQuery.class).createRequest(method, Payloads.newStringPayload("foo"));
      assertRequestLineEquals(request, "POST http://localhost:9999?x-ms-version=2009-07-17 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "foo", "application/octet-stream", false);
   }

   public void testHttpRequestWithOnlyContentType() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("post", HttpRequestOptions.class);
      verifyTestPostOptions(method);
   }

   public void testPayloadParamVarargs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPayloadParamVarargs.class.getMethod("varargs", Array.newInstance(HttpRequestOptions.class, 0)
               .getClass());
      verifyTestPostOptions(method);
   }

   private void verifyTestPostOptions(Method method) throws IOException {
      HttpRequest request = factory(TestPayloadParamVarargs.class).createRequest(method, new HttpRequestOptions() {

         public Multimap<String, String> buildMatrixParameters() {
            return LinkedHashMultimap.create();
         }

         public String buildPathSuffix() {
            return null;
         }

         public Multimap<String, String> buildQueryParameters() {
            return LinkedHashMultimap.create();
         }

         public Multimap<String, String> buildFormParameters() {
            return LinkedHashMultimap.create();
         }

         public Multimap<String, String> buildRequestHeaders() {
            return LinkedHashMultimap.create();
         }

         public String buildStringPayload() {
            return "fooya";
         }

      });
      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "fooya", "application/unknown", false);
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
      public void postAsJson(@BinderParam(BindToJsonPayload.class) String content);

      @POST
      @Path("/{foo}")
      public void postWithPath(@PathParam("foo") @MapPayloadParam("fooble") String path, MapBinder content);

      @POST
      @Path("/{foo}")
      @MapBinder(BindToJsonPayload.class)
      public void postWithMethodBinder(@PathParam("foo") @MapPayloadParam("fooble") String path);

      @POST
      @Path("/{foo}")
      @MapPayloadParams(keys = "rat", values = "atat")
      @MapBinder(BindToJsonPayload.class)
      public void postWithMethodBinderAndDefaults(@PathParam("foo") @MapPayloadParam("fooble") String path);
   }

   public void testCreatePostRequest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method, "data");

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "data", "application/unknown", false);
   }

   public void testCreatePostRequestNullOk() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPost.class.getMethod("post", String.class);
      HttpRequest request = factory(TestPost.class).createRequest(method);

      assertRequestLineEquals(request, "POST http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, "application/unknown", false);
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
         public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
            request.setPayload(postParams.get("fooble"));
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
      GeneratedHttpRequest<TestMultipartForm> httpRequest = factory(TestMultipartForm.class).createRequest(method,
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

   public void testMultipartWithParamStringPart() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withParamStringPart", String.class, String.class);
      GeneratedHttpRequest<TestMultipartForm> httpRequest = factory(TestMultipartForm.class).createRequest(method,
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

   public void testMultipartWithParamFilePart() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestMultipartForm.class.getMethod("withParamFilePart", String.class, File.class);
      File file = File.createTempFile("foo", "bar");
      Files.append("foobledata", file, UTF_8);
      file.deleteOnExit();

      GeneratedHttpRequest<TestMultipartForm> httpRequest = factory(TestMultipartForm.class).createRequest(method,
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
      GeneratedHttpRequest<TestMultipartForm> httpRequest = factory(TestMultipartForm.class).createRequest(method,
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

      GeneratedHttpRequest<TestMultipartForm> httpRequest = factory(TestMultipartForm.class).createRequest(method,
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
      void putWithMethodBinder(@PathParam("foo") @MapPayloadParam("fooble") String path);

      @PUT
      @Path("/{foo}")
      @Produces(MediaType.TEXT_PLAIN)
      void putWithMethodBinderProduces(@PathParam("foo") @BinderParam(BindToStringPayload.class) String path);

      @PUT
      @Path("/{foo}")
      @MapBinder(BindToJsonPayload.class)
      @Consumes(MediaType.APPLICATION_JSON)
      Wrapper putWithMethodBinderConsumes(@PathParam("foo") @MapPayloadParam("fooble") String path);

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
      @Unwrap(depth = 2)
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<? extends Set<String>> testUnwrapDepth2();

      @GET
      @Path("/")
      @Unwrap(depth = 3, edgeCollection = Set.class)
      @Consumes(MediaType.APPLICATION_JSON)
      ListenableFuture<String> testUnwrapDepth3();

      @Target( { ElementType.METHOD })
      @Retention(RetentionPolicy.RUNTIME)
      @HttpMethod("ROWDY")
      public @interface ROWDY {
      }

      @ROWDY
      @Path("/objects/{id}")
      ListenableFuture<Boolean> rowdy(@PathParam("id") String path);
   }

   static class Wrapper {
      String foo;
   }

   public void testAlternateHttpMethod() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("rowdy", String.class);
      HttpRequest request = factory(TestPut.class).createRequest(method, "data");

      assertRequestLineEquals(request, "ROWDY http://localhost:9999/objects/data HTTP/1.1");
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

      Function<HttpResponse, Wrapper> parser = (Function<HttpResponse, Wrapper>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))).foo, "bar");

   }

   @SuppressWarnings("unchecked")
   public void testGeneric1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))), ImmutableMap.of(
               "foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testGeneric2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric2");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))), ImmutableMap.of(
               "foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testGeneric3() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testGeneric3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, ParseJson.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))), ImmutableMap.of(
               "foo", "bar"));

   }

   @SuppressWarnings("unchecked")
   public void testUnwrap1() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))), "bar");

   }

   @SuppressWarnings("unchecked")
   public void testUnwrap2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap2");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{ foo:\"bar\"}"))), "bar");

   }

   @SuppressWarnings("unchecked")
   public void testUnwrap3() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrap3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}"))),
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

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{\"runit\":[\"0.7.0\",\"0.7.1\"]}"))),
               ImmutableSet.of("0.7.0", "0.7.1"));
   }

   @SuppressWarnings("unchecked")
   public void testUnwrapDepth2() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrapDepth2");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyNestedJsonValue.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok",
               newStringPayload("{\"runit\":{\"runit\":[\"0.7.0\",\"0.7.1\"]}}"))), ImmutableSet.of("0.7.0", "0.7.1"));
   }

   @SuppressWarnings("unchecked")
   public void testUnwrapDepth3() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrapDepth3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyNestedJsonValueInSet.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{\"runit\":{\"runit\":[\"0.7.0\"]}}"))),
               "0.7.0");
   }

   @SuppressWarnings("unchecked")
   public void testUnwrapDepth3None() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrapDepth3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyNestedJsonValueInSet.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      assertEquals(parser.apply(new HttpResponse(200, "ok", newStringPayload("{\"runit\":{\"runit\":[]}}"))), null);
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testUnwrapDepth3TooMany() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPut.class.getMethod("testUnwrapDepth3");
      HttpRequest request = factory(TestPut.class).createRequest(method);

      assertResponseParserClassEquals(method, request, UnwrapOnlyNestedJsonValueInSet.class);
      // now test that it works!

      Function<HttpResponse, Map<String, String>> parser = (Function<HttpResponse, Map<String, String>>) RestAnnotationProcessor
               .createResponseParser(parserFactory, injector, method, request);

      parser.apply(new HttpResponse(200, "ok", newStringPayload("{\"runit\":{\"runit\":[\"0.7.0\",\"0.7.1\"]}}")));
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
               HttpRequest.builder().method("GET").endpoint(URI.create("http://localhost")).headers(
                        ImmutableMultimap.of("foo", "bar")).build());
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
   public void testFormParamExtractor() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TestPath.class.getMethod("oneFormParamExtractor", String.class);
      HttpRequest request = factory(TestPath.class).createRequest(method, new Object[] { "localhost" });
      assertRequestLineEquals(request, "POST http://localhost:9999/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "one=l", "application/x-www-form-urlencoded", false);
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
   public void testBuildTwoHeader() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneHeader = TestHeader.class.getMethod("twoHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader, new Object[] { "robot" })
               .getHeaders();
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("slash"), Collections.singletonList("/robot"));
      assertEquals(headers.get("hyphen"), Collections.singletonList("-robot"));
   }

   @Headers(keys = "x-amz-copy-source", values = "/{bucket}")
   public class TestClassHeader {
      @GET
      @Path("/")
      public void oneHeader(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassHeader() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneHeader = TestClassHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestClassHeader.class).createRequest(oneHeader,
               new Object[] { "robot" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot"));
   }

   @Test
   public void testBuildOneHeader() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneHeader = TestHeader.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(oneHeader, new Object[] { "robot" })
               .getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot"));
   }

   @Test
   public void testBuildTwoHeaders() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method twoHeaders = TestHeader.class.getMethod("twoHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(twoHeaders,
               new Object[] { "robot", "eggs" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/robot/eggs"));
   }

   @Test
   public void testBuildTwoHeadersOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoHeadersOutOfOrder = TestHeader.class.getMethod("twoHeadersOutOfOrder", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeader.class).createRequest(twoHeadersOutOfOrder,
               new Object[] { "robot", "eggs" }).getHeaders();
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("x-amz-copy-source"), Collections.singletonList("/eggs/robot"));
   }

   public class TestReplaceQueryOptions extends BaseHttpRequestOptions {
      public TestReplaceQueryOptions() {
         this.queryParameters.put("x-amz-copy-source", "/{bucket}");
      }
   }

   @Test
   public void testQueryInOptions() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneQuery = TestQueryReplace.class.getMethod("queryInOptions", String.class, TestReplaceQueryOptions.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery,
               new Object[] { "robot", new TestReplaceQueryOptions() }).getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   private interface TestMapMatrixParams {
      @POST
      @Path("/objects/{id}/action/{action}")
      ListenableFuture<String> action(@PathParam("id") String id, @PathParam("action") String action,
               @BinderParam(BindMapToMatrixParams.class) Map<String, String> options);
   }

   public void testTestMapMatrixParams() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method method = TestMapMatrixParams.class.getMethod("action", String.class, String.class, Map.class);
      HttpRequest request = factory(TestMapMatrixParams.class).createRequest(method,
               new Object[] { "robot", "kill", ImmutableMap.of("death", "slow") });
      assertRequestLineEquals(request, "POST http://localhost:9999/objects/robot/action/kill;death=slow HTTP/1.1");
      assertEquals(request.getHeaders().size(), 0);
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
   public void testBuildTwoQuery() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
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
   public void testBuildOneClassQuery() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneQuery = TestClassQuery.class.getMethod("oneQuery", String.class);
      String query = factory(TestClassQuery.class).createRequest(oneQuery, new Object[] { "robot" }).getEndpoint()
               .getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneQuery() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneQuery = TestQueryReplace.class.getMethod("oneQuery", String.class);
      String query = factory(TestQueryReplace.class).createRequest(oneQuery, new Object[] { "robot" }).getEndpoint()
               .getQuery();
      assertEquals(query, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoQuerys() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method twoQuerys = TestQueryReplace.class.getMethod("twoQuerys", String.class, String.class);
      String query = factory(TestQueryReplace.class).createRequest(twoQuerys, new Object[] { "robot", "eggs" })
               .getEndpoint().getQuery();
      assertEquals(query, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoQuerysOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoQuerysOutOfOrder = TestQueryReplace.class.getMethod("twoQuerysOutOfOrder", String.class, String.class);
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
   public void testMatrixInOptions() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("matrixInOptions", String.class,
               TestReplaceMatrixOptions.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix,
               new Object[] { "robot", new TestReplaceMatrixOptions() }).getEndpoint().getPath();
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
   public void testBuildTwoMatrix() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
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
   public void testBuildOneClassMatrix() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneMatrix = TestClassMatrix.class.getMethod("oneMatrix", String.class);
      String path = factory(TestClassMatrix.class).createRequest(oneMatrix, new Object[] { "robot" }).getEndpoint()
               .getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneMatrix() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneMatrix = TestMatrixReplace.class.getMethod("oneMatrix", String.class);
      String path = factory(TestMatrixReplace.class).createRequest(oneMatrix, new Object[] { "robot" }).getEndpoint()
               .getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoMatrixs() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method twoMatrixs = TestMatrixReplace.class.getMethod("twoMatrixs", String.class, String.class);
      String path = factory(TestMatrixReplace.class).createRequest(twoMatrixs, new Object[] { "robot", "eggs" })
               .getEndpoint().getPath();
      assertEquals(path, "/;x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoMatrixsOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoMatrixsOutOfOrder = TestMatrixReplace.class.getMethod("twoMatrixsOutOfOrder", String.class,
               String.class);
      String path = factory(TestMatrixReplace.class).createRequest(twoMatrixsOutOfOrder,
               new Object[] { "robot", "eggs" }).getEndpoint().getPath();
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
      PayloadEnclosing payloadEnclosing = new PayloadEnclosingImpl(newInputStreamPayload(Strings2
               .toInputStream("whoops")));

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
      GeneratedHttpRequest<TestTransformers> request = GeneratedHttpRequest.<TestTransformers> builder().method("GET")
               .endpoint(URI.create("http://localhost")).declaring(TestTransformers.class).javaMethod(method).args(
                        new Object[] {}).build();
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
      HttpRequestOptions[] optionsHolder = new HttpRequestOptions[] {};
      Method method = TestRequest.class.getMethod("get", String.class, optionsHolder.getClass());
      HttpRequest request = factory(TestRequest.class).createRequest(method, new Object[] { "1", options });
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/1");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), Collections.singletonList("localhost"));
      assertEquals(request.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE), Collections.singletonList(dateService
               .rfc822DateFormat(date)));
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
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), Collections.singletonList("localhost"));
      assertEquals(request.getHeaders().get(HttpHeaders.IF_MODIFIED_SINCE), Collections.singletonList(dateService
               .rfc822DateFormat(date)));
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
      assertNonPayloadHeadersEqual(request, "Host: localhost\n");
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
      assertNonPayloadHeadersEqual(request, "Host: localhost\n");
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
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), Collections.singletonList("localhost"));
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
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), Collections.singletonList("localhost"));
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
      assertEquals(request.getHeaders().get(HttpHeaders.HOST), Collections.singletonList("localhost"));
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
   public void testOneHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("oneHeader", String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
               ImmutableMultimap.<String, String> of().entries(), method, "robot");
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("robot"));
   }

   @Test
   public void testOneIntHeader() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("oneIntHeader", int.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
               ImmutableMultimap.<String, String> of().entries(), method, 1);
      assertEquals(headers.size(), 1);
      assertEquals(headers.get("header"), Collections.singletonList("1"));
   }

   @Test
   public void testTwoDifferentHeaders() throws SecurityException, NoSuchMethodException {
      Method method = TestHeaders.class.getMethod("twoDifferentHeaders", String.class, String.class);
      Multimap<String, String> headers = factory(TestHeaders.class).buildHeaders(
               ImmutableMultimap.<String, String> of().entries(), method, "robot", "egg");
      assertEquals(headers.size(), 2);
      assertEquals(headers.get("header1"), Collections.singletonList("robot"));
      assertEquals(headers.get("header2"), Collections.singletonList("egg"));
   }

   @Test
   public void testTwoSameHeaders() throws SecurityException, NoSuchMethodException {
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
   public void testOneEndpointParam() throws SecurityException, NoSuchMethodException {
      Method method = TestEndpointParams.class.getMethod("oneEndpointParam", String.class);
      URI uri = factory(TestEndpointParams.class).getEndpointInParametersOrNull(method, new Object[] { "robot" },
               injector);
      assertEquals(uri, URI.create("robot"));

   }

   @SuppressWarnings("static-access")
   @Test
   public void testTwoDifferentEndpointParams() throws SecurityException, NoSuchMethodException {
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
      GeneratedHttpRequest<TestPayload> request = processor.createRequest(method, "test");

      assertRequestLineEquals(request, "PUT http://localhost:9999 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, "test", "application/unknown", false);
   }

   @Test
   public void putWithPath() throws SecurityException, NoSuchMethodException, IOException {
      RestAnnotationProcessor<TestPayload> processor = factory(TestPayload.class);
      Method method = TestPayload.class.getMethod("putWithPath", String.class, String.class);
      GeneratedHttpRequest<TestPayload> request = processor.createRequest(method, "rabble", "test");

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
   public void testBuildTwoForm() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneForm = TestFormReplace.class.getMethod("twoForm", String.class);
      Object form = factory(TestFormReplace.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "slash=/robot&hyphen=-robot");
   }

   @FormParams(keys = "x-amz-copy-source", values = "/{bucket}")
   @SkipEncoding('/')
   public class TestClassForm {
      @POST
      @Path("/")
      public void oneForm(@PathParam("bucket") String path) {
      }
   }

   @Test
   public void testBuildOneClassForm() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneForm = TestClassForm.class.getMethod("oneForm", String.class);
      Object form = factory(TestClassForm.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildOneForm() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method oneForm = TestFormReplace.class.getMethod("oneForm", String.class);
      Object form = factory(TestFormReplace.class).createRequest(oneForm, "robot").getPayload().getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot");
   }

   @Test
   public void testBuildTwoForms() throws SecurityException, NoSuchMethodException, UnsupportedEncodingException {
      Method twoForms = TestFormReplace.class.getMethod("twoForms", String.class, String.class);
      Object form = factory(TestFormReplace.class).createRequest(twoForms, "robot", "eggs").getPayload()
               .getRawContent();
      assertEquals(form, "x-amz-copy-source=/robot/eggs");
   }

   @Test
   public void testBuildTwoFormsOutOfOrder() throws SecurityException, NoSuchMethodException,
            UnsupportedEncodingException {
      Method twoFormsOutOfOrder = TestFormReplace.class.getMethod("twoFormsOutOfOrder", String.class, String.class);
      Object form = factory(TestFormReplace.class).createRequest(twoFormsOutOfOrder, "robot", "eggs").getPayload()
               .getRawContent();
      assertEquals(form, "x-amz-copy-source=/eggs/robot");
   }

   @SuppressWarnings("unchecked")
   private <T> RestAnnotationProcessor<T> factory(Class<T> clazz) {
      return ((RestAnnotationProcessor<T>) injector.getInstance(Key.get(newParameterizedType(
               RestAnnotationProcessor.class, clazz))));
   }

   DateService dateService = new SimpleDateFormatDateService();

   @BeforeClass
   void setupFactory() {
      RestContextSpec<String, Integer> contextSpec = contextSpec("test", "http://localhost:9999", "1", "", "userfoo",
               null, String.class, Integer.class, ImmutableSet.<Module> of(new MockModule(), new NullLoggingModule(),
                        new AbstractModule() {

                           @Override
                           protected void configure() {
                              bind(URI.class).annotatedWith(Localhost2.class).toInstance(
                                       URI.create("http://localhost:1111"));
                           }

                        }));

      injector = createContextBuilder(contextSpec).buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      crypto = injector.getInstance(Crypto.class);
   }

}
