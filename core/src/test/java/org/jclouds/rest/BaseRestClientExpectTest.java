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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContext;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * Allows us to test a client via its side effects.
 * 
 * <p/>
 * Example usage:
 * 
 * <pre>
 * 
 * HttpRequest bucketFooExists = HttpRequest.builder().method(&quot;HEAD&quot;).endpoint(
 *          URI.create(&quot;https://foo.s3.amazonaws.com/?max-keys=0&quot;)).headers(
 *          ImmutableMultimap.&lt;String, String&gt; builder().put(&quot;Host&quot;, &quot;foo.s3.amazonaws.com&quot;).put(&quot;Date&quot;, CONSTANT_DATE)
 *                   .put(&quot;Authorization&quot;, &quot;AWS identity:86P4BBb7xT+gBqq7jxM8Tc28ktY=&quot;).build()).build();
 * 
 * S3Client clientWhenBucketExists = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(200).build());
 * assert clientWhenBucketExists.bucketExists(&quot;foo&quot;);
 * 
 * S3Client clientWhenBucketDoesntExist = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(404)
 *          .build());
 * assert !clientWhenBucketDoesntExist.bucketExists(&quot;foo&quot;);
 * </pre>
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
@Beta
public abstract class BaseRestClientExpectTest<S> {
   /**
    * only needed when the client is simple and not registered fn rest.properties
    */
   @Target(TYPE)
   @Retention(RUNTIME)
   public static @interface RegisterContext {
      Class<?> sync();

      Class<?> async();
   }

   protected String provider = "mock";

   /**
    * Override this to supply alternative bindings for use in the test. This is commonly used to
    * override suppliers of dates so that the test results are predicatable.
    * 
    * @return optional guice module which can override bindings
    */
   protected Module createModule() {
      return new Module() {

         @Override
         public void configure(Binder binder) {

         }

      };
   }

   /**
    * Convenience method used when creating a response that includes an http payload.
    * 
    * <p/>
    * ex.
    * 
    * <pre>
    * HttpResponse.builder().statusCode(200).payload(payloadFromResource(&quot;/ip_get_details.json&quot;)).build()
    * </pre>
    * 
    * @param resource
    *           resource file such as {@code /serverlist.json}
    * @return payload for use in http responses.
    */
   public Payload payloadFromResource(String resource) {
      try {
         return payloadFromString(Strings2.toStringAndClose(getClass().getResourceAsStream(resource)));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   public Payload payloadFromResourceWithContentType(String resource, String contentType) {
      try {
         return payloadFromStringWithContentType(Strings2.toStringAndClose(getClass().getResourceAsStream(resource)),
                  contentType);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }

   }

   public static Payload payloadFromString(String payload) {
      return Payloads.newStringPayload(payload);
   }

   public static Payload payloadFromStringWithContentType(String payload, String contentType) {
      Payload p = Payloads.newStringPayload(payload);
      p.getContentMetadata().setContentType(contentType);
      return p;
   }

   /**
    * Mock executor service which uses the supplied function to return http responses.
    */
   @SingleThreaded
   @Singleton
   public static class ExpectHttpCommandExecutorService extends BaseHttpCommandExecutorService<HttpRequest> {

      private final Function<HttpRequest, HttpResponse> fn;

      @Inject
      public ExpectHttpCommandExecutorService(Function<HttpRequest, HttpResponse> fn, HttpUtils utils,
               @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioExecutor,
               IOExceptionRetryHandler ioRetryHandler, DelegatingRetryHandler retryHandler,
               DelegatingErrorHandler errorHandler, HttpWire wire) {
         super(utils, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
         this.fn = checkNotNull(fn, "fn");
      }

      @Override
      public void cleanup(HttpRequest nativeResponse) {
         if (nativeResponse.getPayload() != null)
            nativeResponse.getPayload().release();
      }

      @Override
      public HttpRequest convert(HttpRequest request) throws IOException, InterruptedException {
         return request;
      }

      @Override
      public HttpResponse invoke(HttpRequest nativeRequest) throws IOException, InterruptedException {
         return fn.apply(nativeRequest);
      }
   }

   @ConfiguresHttpCommandExecutorService
   @ConfiguresExecutorService
   public static class ExpectModule extends AbstractModule {
      private final Function<HttpRequest, HttpResponse> fn;

      public ExpectModule(Function<HttpRequest, HttpResponse> fn) {
         this.fn = checkNotNull(fn, "fn");
      }

      @Override
      public void configure() {
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_USER_THREADS)).toInstance(
                  MoreExecutors.sameThreadExecutor());
         bind(ExecutorService.class).annotatedWith(Names.named(Constants.PROPERTY_IO_WORKER_THREADS)).toInstance(
                  MoreExecutors.sameThreadExecutor());
         bind(new TypeLiteral<Function<HttpRequest, HttpResponse>>() {
         }).toInstance(fn);
         bind(HttpCommandExecutorService.class).to(ExpectHttpCommandExecutorService.class);
      }
   }

   /**
    * creates a client for a mock server which only responds to a single http request
    * 
    * @param request
    *           the http request the mock server responds to
    * @param response
    *           the response the mock server returns for the request
    * @return a client configured with this behavior
    */
   public S requestSendsResponse(HttpRequest request, HttpResponse response) {
      return requestSendsResponse(request, response, createModule());
   }

   public S requestSendsResponse(HttpRequest request, HttpResponse response, Module module) {
      return requestsSendResponses(ImmutableMap.of(request, response), module);
   }

   /**
    * creates a client for a mock server which only responds to two types of requests
    * 
    * @param requestA
    *           an http request the mock server responds to
    * @param responseA
    *           the response for {@code requestA}
    * @param requestB
    *           another http request the mock server responds to
    * @param responseB
    *           the response for {@code requestB}
    * @return a client configured with this behavior
    */
   public S requestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB) {
      return requestsSendResponses(requestA, responseA, requestB, responseB, createModule());
   }

   public S requestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB, Module module) {
      return requestsSendResponses(ImmutableMap.of(requestA, responseA, requestB, responseB), module);
   }

   /**
    * creates a client for a mock server which only responds to three types of requests
    * 
    * @param requestA
    *           an http request the mock server responds to
    * @param responseA
    *           the response for {@code requestA}
    * @param requestB
    *           another http request the mock server responds to
    * @param responseB
    *           the response for {@code requestB}
    * @param requestC
    *           another http request the mock server responds to
    * @param responseC
    *           the response for {@code requestC}
    * @return a client configured with this behavior
    */
   public S requestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB, HttpRequest requestC, HttpResponse responseC) {
      return requestsSendResponses(requestA, responseA, requestB, responseB, requestC, responseC, createModule());
   }

   public S requestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB, HttpRequest requestC, HttpResponse responseC, Module module) {
      return requestsSendResponses(ImmutableMap.of(requestA, responseA, requestB, responseB, requestC, responseC),
               module);
   }

   public S orderedRequestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB) {
      return orderedRequestsSendResponses(ImmutableList.of(requestA, requestB), ImmutableList.of(responseA, responseB));
   }

   public S orderedRequestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB, HttpRequest requestC, HttpResponse responseC) {
      return orderedRequestsSendResponses(ImmutableList.of(requestA, requestB, requestC), ImmutableList.of(responseA,
               responseB, responseC));
   }

   public S orderedRequestsSendResponses(HttpRequest requestA, HttpResponse responseA, HttpRequest requestB,
            HttpResponse responseB, HttpRequest requestC, HttpResponse responseC, HttpRequest requestD,
            HttpResponse responseD) {
      return orderedRequestsSendResponses(ImmutableList.of(requestA, requestB, requestC, requestD), ImmutableList.of(
               responseA, responseB, responseC, responseD));
   }

   public S orderedRequestsSendResponses(final List<HttpRequest> requests, final List<HttpResponse> responses) {
      final AtomicInteger counter = new AtomicInteger(0);

      return createClient(new Function<HttpRequest, HttpResponse>() {
         @Override
         public HttpResponse apply(HttpRequest input) {
            int index = counter.getAndIncrement();
            if (index >= requests.size())
               return HttpResponse.builder().statusCode(500).message(
                        String.format("request %s is out of range (%s)", index, requests.size())).payload(
                        Payloads.newStringPayload(renderRequest(input))).build();
            assertEquals(renderRequest(input), renderRequest(requests.get(index)));
            return responses.get(index);
         }
      });
   }

   /**
    * creates a client for a mock server which returns responses for requests based on the supplied
    * Map parameter.
    * 
    * @param requestToResponse
    *           valid requests and responses for the mock to respond to
    * @return a client configured with this behavior
    */
   public S requestsSendResponses(Map<HttpRequest, HttpResponse> requestToResponse) {
      return requestsSendResponses(requestToResponse, createModule());
   }

   public S requestsSendResponses(final Map<HttpRequest, HttpResponse> requestToResponse, Module module) {
      return createClient(new Function<HttpRequest, HttpResponse>() {
         ImmutableBiMap<HttpRequest, HttpResponse> bimap = ImmutableBiMap.copyOf(requestToResponse);

         @Override
         public HttpResponse apply(HttpRequest input) {
            if (!(requestToResponse.containsKey(input))) {

               StringBuilder payload = new StringBuilder("\n");
               payload.append("the following request is not configured:\n");
               payload.append("----------------------------------------\n");
               payload.append(renderRequest(input));
               payload.append("----------------------------------------\n");
               payload.append("configured requests:\n");
               for (HttpRequest request : requestToResponse.keySet()) {
                  payload.append("----------------------------------------\n");
                  payload.append(renderRequest(request));
               }
               return HttpResponse.builder().statusCode(500).message("no response configured for request").payload(
                        Payloads.newStringPayload(payload.toString())).build();

            }
            HttpResponse response = requestToResponse.get(input);
            // in case hashCode/equals doesn't do a full content check
            assertEquals(renderRequest(input), renderRequest(bimap.inverse().get(response)));
            return response;
         }
      }, module);
   }

   public String renderRequest(HttpRequest request) {
      StringBuilder builder = new StringBuilder().append(request.getRequestLine()).append('\n');
      for (Entry<String, String> header : request.getHeaders().entries()) {
         builder.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
      }
      if (request.getPayload() != null) {
         for (Entry<String, String> header : HttpUtils.getContentHeadersFromMetadata(
                  request.getPayload().getContentMetadata()).entries()) {
            builder.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
         }
         try {
            builder.append('\n').append(Strings2.toStringAndClose(request.getPayload().getInput()));
         } catch (IOException e) {
            throw Throwables.propagate(e);
         }

      } else {
         builder.append('\n');
      }
      return builder.toString();
   }

   public S createClient(Function<HttpRequest, HttpResponse> fn) {
      return createClient(fn, createModule(), setupProperties());
   }

   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module) {
      return createClient(fn, module, setupProperties());

   }

   public S createClient(Function<HttpRequest, HttpResponse> fn, Properties props) {
      return createClient(fn, createModule(), props);

   }

   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      RestContextSpec<S, ?> contextSpec = makeContextSpec();

      return createContext(contextSpec,
               ImmutableSet.<Module> of(new ExpectModule(fn), new NullLoggingModule(), module), props).getApi();
   }

   protected String identity = "identity";
   protected String credential = "credential";

   private RestContextSpec<S, ?> makeContextSpec() {
      if (getClass().isAnnotationPresent(RegisterContext.class))
         return (RestContextSpec<S, ?>) contextSpec(provider, "http://mock", "1", "", "", "userfoo", null, getClass()
                  .getAnnotation(RegisterContext.class).sync(),
                  getClass().getAnnotation(RegisterContext.class).async(), ImmutableSet.<Module> of());
      else
         return new RestContextFactory(setupRestProperties()).createContextSpec(provider, identity, credential,
                  new Properties());
   }

   /**
    * override this when the provider or api is not located in rest.properties and you are not using
    * the {@link RegisterContext} annotation on your tests.
    */
   protected Properties setupRestProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   /**
    * override this to supply context-specific parameters during tests.
    */
   protected Properties setupProperties() {
      return new Properties();
   }
}