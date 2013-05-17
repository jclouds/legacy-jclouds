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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.name.Names.named;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.custommonkey.xmlunit.XMLUnit;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.date.internal.DateServiceDateCodecFactory;
import org.jclouds.date.internal.SimpleDateFormatDateService;
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
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.io.CopyInputStreamInputSupplierMap;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.HttpApiMetadata;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.util.Strings2;
import org.w3c.dom.Node;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.InputSupplier;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

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
@Beta
public abstract class BaseRestApiExpectTest<S> {

   protected String provider = "mock";

   protected ContentMetadataCodec contentMetadataCodec = new DefaultContentMetadataCodec(
            new DateServiceDateCodecFactory(new SimpleDateFormatDateService()));
   
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
               ContentMetadataCodec contentMetadataCodec,
               @Named(PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
               IOExceptionRetryHandler ioRetryHandler, DelegatingRetryHandler retryHandler,
               DelegatingErrorHandler errorHandler, HttpWire wire) {
         super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
         this.fn = checkNotNull(fn, "fn");
      }

      @Override
      public void cleanup(HttpRequest nativeResponse) {
         if (nativeResponse != null && nativeResponse.getPayload() != null)
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
         bind(ListeningExecutorService.class).annotatedWith(named(PROPERTY_USER_THREADS)).toInstance(sameThreadExecutor());
         bind(ListeningExecutorService.class).annotatedWith(named(PROPERTY_IO_WORKER_THREADS)).toInstance(sameThreadExecutor());
         bind(new TypeLiteral<Function<HttpRequest, HttpResponse>>() {
         }).toInstance(fn);
         bind(HttpCommandExecutorService.class).to(ExpectHttpCommandExecutorService.class);
      }

      @Provides
      @Singleton
      TimeLimiter timeLimiter(@Named(PROPERTY_USER_THREADS) ListeningExecutorService userExecutor){
         return new SimpleTimeLimiter(userExecutor);
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
            if (!httpRequestsAreEqual(input, requests.get(index))) {
               assertEquals(renderRequest(input), renderRequest(requests.get(index)));
            }
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

   protected enum HttpRequestComparisonType {
      XML, JSON, DEFAULT;
   }

   /**
    * How should this HttpRequest be compared with others?
    */
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return HttpRequestComparisonType.DEFAULT;
   }

   /**
    * Compare two requests as instructed by {@link #compareHttpRequestAsType(HttpRequest)} - default
    * is to compare using Objects.equal
    */
   public boolean httpRequestsAreEqual(HttpRequest a, HttpRequest b) {
      try {
         if (a == null || b == null || !Objects.equal(a.getRequestLine(), b.getRequestLine())
               || !Objects.equal(a.getHeaders(), b.getHeaders())) {
            return false;
         }
         if (a.getPayload() == null || b.getPayload() == null) {
            return Objects.equal(a, b);
         }
 
         switch (compareHttpRequestAsType(a)) {
            case XML: {
               Diff diff = XMLUnit.compareXML(Strings2.toString(a.getPayload()), Strings2
                        .toString(b.getPayload()));

               // Ignoring whitespace in elements that have other children, xsi:schemaLocation and
               // differences in namespace prefixes
               diff.overrideDifferenceListener(new DifferenceListener() {
                  @Override
                  public int differenceFound(Difference diff) {
                     if (diff.getId() == DifferenceConstants.SCHEMA_LOCATION_ID
                              || diff.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID) {
                        return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                     }
                     if (diff.getId() == DifferenceConstants.TEXT_VALUE_ID) {
                        for (NodeDetail detail : ImmutableSet.of(diff.getControlNodeDetail(), diff.getTestNodeDetail())) {
                           if (detail.getNode().getParentNode().getChildNodes().getLength() < 2
                                    || !detail.getValue().trim().isEmpty()) {
                              return RETURN_ACCEPT_DIFFERENCE;
                           }
                        }
                        return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                     }
                     return RETURN_ACCEPT_DIFFERENCE;
                  }

                  @Override
                  public void skippedComparison(Node node, Node node1) {
                  }
               });

               return diff.identical();
            }
            case JSON: {               
               JsonParser parser = new JsonParser();
               JsonElement payloadA = parser.parse(Strings2.toString(a.getPayload()));
               JsonElement payloadB = parser.parse(Strings2.toString(b.getPayload()));
               return Objects.equal(payloadA, payloadB);
            }
            default: {
               return Objects.equal(a, b);
            }
         }
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   public S requestsSendResponses(final Map<HttpRequest, HttpResponse> requestToResponse, Module module) {
      return requestsSendResponses(requestToResponse, module, setupProperties());
   }

   public S requestsSendResponses(final Map<HttpRequest, HttpResponse> requestToResponse, Module module,
            Properties props) {
      return createClient(new Function<HttpRequest, HttpResponse>() {
         
         @Override
         public HttpResponse apply(HttpRequest input) {
            HttpRequest matchedRequest = null;
            HttpResponse response = null;
            for (Map.Entry<HttpRequest, HttpResponse> entry : requestToResponse.entrySet()) {
               HttpRequest request = entry.getKey();
               if (httpRequestsAreEqual(input, request)) {
                  matchedRequest = request;
                  response = entry.getValue();
               }
            }

            if (response == null) {
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
               response = HttpResponse.builder().statusCode(500).message("no response configured for request").payload(
                        Payloads.newStringPayload(payload.toString())).build();

            } else if (compareHttpRequestAsType(input) == HttpRequestComparisonType.DEFAULT) {
               // in case hashCode/equals doesn't do a full content check
               assertEquals(renderRequest(input), renderRequest(matchedRequest));
            }

            return response;
         }
      }, module, props);
   }

   public String renderRequest(HttpRequest request) {
      StringBuilder builder = new StringBuilder().append(request.getRequestLine()).append('\n');
      for (Entry<String, String> header : request.getHeaders().entries()) {
         builder.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
      }
      if (request.getPayload() != null) {
         for (Entry<String, String> header : contentMetadataCodec.toHeaders(
                  request.getPayload().getContentMetadata()).entries()) {
            builder.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
         }
         try {
            builder.append('\n').append(Strings2.toString(request.getPayload()));
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

   @SuppressWarnings("unchecked")
   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return (S) createInjector(fn, module, props).getInstance(api);
   }

   protected String identity = "identity";
   protected String credential = "credential";
   protected Class<?> api;

   /**
    * @see org.jclouds.providers.Providers#withId
    */
   protected ProviderMetadata createProviderMetadata() {
      return null;
   }

   /**
    * @see org.jclouds.apis.Apis#withId
    */
   protected ApiMetadata createApiMetadata() {
      return null;
   }

   protected Injector createInjector(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      ContextBuilder builder = null;
      if (provider != null)
         try {
            builder = ContextBuilder.newBuilder(provider).credentials(identity, credential);
         } catch (NoSuchElementException e) {
            Logger
                     .getAnonymousLogger()
                     .warning(
                              "provider ["
                                       + provider
                                       + "] is not setup as META-INF/services/org.jclouds.apis.ApiMetadata or META-INF/services/org.jclouds.providers.ProviderMetadata");
         }
      if (builder == null) {
         ProviderMetadata pm = createProviderMetadata();
         ApiMetadata am = (pm != null) ? pm.getApiMetadata() : checkNotNull(createApiMetadata(),
                  "either createApiMetadata or createProviderMetadata must be overridden");

         builder = pm != null ? ContextBuilder.newBuilder(pm) : ContextBuilder.newBuilder(am);
      }
      ApiMetadata am = builder.getApiMetadata();
      if (am instanceof HttpApiMetadata) {
         this.api = HttpApiMetadata.class.cast(am).getApi();
      } else if (am instanceof org.jclouds.rest.RestApiMetadata) {
         this.api = org.jclouds.rest.RestApiMetadata.class.cast(am).getApi();
      } else {
         throw new UnsupportedOperationException("unsupported base type: " + am);
      }
      // isolate tests from eachother, as default credentialStore is static
      return builder.credentials(identity, credential).modules(
               ImmutableSet.of(new ExpectModule(fn), new NullLoggingModule(), new CredentialStoreModule(new CopyInputStreamInputSupplierMap(
                     new ConcurrentHashMap<String, InputSupplier<InputStream>>())), module)).overrides(props)
               .buildInjector();
   }
   
   /**
    * override this to supply context-specific parameters during tests.
    */
   protected Properties setupProperties() {
      Properties props = new Properties();
      props.put(PROPERTY_MAX_RETRIES, 1);
      return props;
   }
}
