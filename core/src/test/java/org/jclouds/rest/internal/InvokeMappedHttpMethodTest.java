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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.config.InvocationConfig;
import org.jclouds.rest.internal.InvokeSyncToAsyncHttpMethod.InvokeAndTransform;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class InvokeMappedHttpMethodTest {

   public static interface ThingApi {
      HttpResponse get();
   }

   public static interface ThingAsyncApi {
      @Named("ns:get")
      ListenableFuture<HttpResponse> get();
   }

   private Invocation get;
   private Invocation asyncGet;
   private Function<Invocation, Invocation> sync2async;
   private HttpRequest getRequest = HttpRequest.builder().method("GET").endpoint("http://get").build();
   private HttpCommand getCommand = new HttpCommand(getRequest);
   private Function<Invocation, HttpRequest> toRequest;

   @BeforeClass
   void setupInvocations() throws SecurityException, NoSuchMethodException {
      get = Invocation.create(method(ThingApi.class, "get"), ImmutableList.of());
      asyncGet = Invocation.create(method(ThingAsyncApi.class, "get"), ImmutableList.of());
      sync2async = Functions.forMap(ImmutableMap.of(get, asyncGet));
      toRequest = Functions.forMap(ImmutableMap.of(asyncGet, getRequest));
   }

   @SuppressWarnings("unchecked")
   private Function<HttpRequest, Function<HttpResponse, ?>> transformerForRequest = Function.class.cast(Functions
         .constant(Functions.identity()));
   private ListeningExecutorService userThreads = MoreExecutors.sameThreadExecutor();

   private HttpResponse response = HttpResponse.builder().statusCode(200).payload("foo").build();
   private HttpCommandExecutorService http;
   private TimeLimiter timeLimiter;
   @SuppressWarnings("rawtypes")
   private org.jclouds.Fallback fallback;
   private InvocationConfig config;
   private InvokeSyncToAsyncHttpMethod invokeHttpMethod;

   private ListenableFuture<HttpResponse> future;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   void createMocks() {
      http = createMock(HttpCommandExecutorService.class);
      timeLimiter = createMock(TimeLimiter.class);
      fallback = createMock(org.jclouds.Fallback.class);
      config = createMock(InvocationConfig.class);
      future = createMock(ListenableFuture.class);
      invokeHttpMethod = new InvokeSyncToAsyncHttpMethod(sync2async, toRequest, http, transformerForRequest, timeLimiter, config,
            userThreads);
      expect(config.getCommandName(asyncGet)).andReturn("ns:get");
      expect(config.getFallback(asyncGet)).andReturn(fallback);
   }

   @AfterMethod
   void verifyMocks() {
      verify(http, timeLimiter, fallback, config, future);
   }

   public void testMethodWithTimeoutRunsTimeLimiter() throws Exception {
      expect(config.getTimeoutNanos(asyncGet)).andReturn(Optional.of(250000000l));
      InvokeAndTransform invoke = invokeHttpMethod.new InvokeAndTransform("ns:get", getCommand);
      expect(timeLimiter.callWithTimeout(invoke, 250000000, TimeUnit.NANOSECONDS, true)).andReturn(response);
      replay(http, timeLimiter, fallback, config, future);
      invokeHttpMethod.apply(get);
   }

   public void testMethodWithNoTimeoutCallGetDirectly() throws Exception {
      expect(config.getTimeoutNanos(asyncGet)).andReturn(Optional.<Long> absent());
      expect(http.invoke(new HttpCommand(getRequest))).andReturn(response);
      replay(http, timeLimiter, fallback, config, future);
      invokeHttpMethod.apply(get);
   }

   public void testAsyncMethodSubmitsRequest() throws Exception {
      expect(http.submit(new HttpCommand(getRequest))).andReturn(future);
      future.addListener(anyObject(Runnable.class), eq(userThreads));
      replay(http, timeLimiter, fallback, config, future);
      invokeHttpMethod.apply(asyncGet);
   }

   private HttpResponse fallbackResponse = HttpResponse.builder().statusCode(200).payload("bar").build();

   public void testDirectCallRunsFallbackCreateOrPropagate() throws Exception {
      IllegalStateException exception = new IllegalStateException();
      expect(config.getTimeoutNanos(asyncGet)).andReturn(Optional.<Long> absent());
      expect(http.invoke(new HttpCommand(getRequest))).andThrow(exception);
      expect(fallback.createOrPropagate(exception)).andReturn(fallbackResponse);
      replay(http, timeLimiter, fallback, config, future);
      assertEquals(invokeHttpMethod.apply(get), fallbackResponse);
   }

   public void testTimeLimitedRunsFallbackCreateOrPropagate() throws Exception {
      IllegalStateException exception = new IllegalStateException();
      expect(config.getTimeoutNanos(asyncGet)).andReturn(Optional.of(250000000l));
      InvokeAndTransform invoke = invokeHttpMethod.new InvokeAndTransform("ns:get", getCommand);
      expect(timeLimiter.callWithTimeout(invoke, 250000000, TimeUnit.NANOSECONDS, true)).andThrow(exception);
      expect(fallback.createOrPropagate(exception)).andReturn(fallbackResponse);
      replay(http, timeLimiter, fallback, config, future);
      assertEquals(invokeHttpMethod.apply(get), fallbackResponse);
   }

   @SuppressWarnings("unchecked")
   public void testSubmitRunsFallbackCreateOnGet() throws Exception {
      IllegalStateException exception = new IllegalStateException();
      expect(http.submit(new HttpCommand(getRequest))).andReturn(
            Futures.<HttpResponse> immediateFailedFuture(exception));
      expect(fallback.create(exception)).andReturn(Futures.<HttpResponse> immediateFuture(fallbackResponse));
      // not using the field, as you can see above we are making an immediate
      // failed future instead.
      future = createMock(ListenableFuture.class);
      replay(http, timeLimiter, fallback, config, future);
      assertEquals(ListenableFuture.class.cast(invokeHttpMethod.apply(asyncGet)).get(), fallbackResponse);
   }
}
