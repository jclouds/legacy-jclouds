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

import static org.easymock.EasyMock.createMock;
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
import org.jclouds.rest.internal.InvokeHttpMethod.InvokeAndTransform;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.TimeLimiter;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class InvokeHttpMethodTest {

   public static interface ThingApi {
      @Named("ns:get")
      HttpResponse get();
   }

   private Invocation get;
   private HttpRequest getRequest = HttpRequest.builder().method("GET").endpoint("http://get").build();
   private HttpCommand getCommand = new HttpCommand(getRequest);
   private Function<Invocation, HttpRequest> toRequest;

   @BeforeClass
   void setupInvocations() throws SecurityException, NoSuchMethodException {
      get = Invocation.create(method(ThingApi.class, "get"), ImmutableList.of());
      toRequest = Functions.forMap(ImmutableMap.of(get, getRequest));
   }

   @SuppressWarnings("unchecked")
   private Function<HttpRequest, Function<HttpResponse, ?>> transformerForRequest = Function.class.cast(Functions
         .constant(Functions.identity()));

   private HttpResponse response = HttpResponse.builder().statusCode(200).payload("foo").build();
   private HttpCommandExecutorService http;
   private TimeLimiter timeLimiter;
   @SuppressWarnings("rawtypes")
   private org.jclouds.Fallback fallback;
   private InvocationConfig config;
   private InvokeHttpMethod invokeHttpMethod;

   private ListenableFuture<HttpResponse> future;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   void createMocks() {
      http = createMock(HttpCommandExecutorService.class);
      timeLimiter = createMock(TimeLimiter.class);
      fallback = createMock(org.jclouds.Fallback.class);
      config = createMock(InvocationConfig.class);
      future = createMock(ListenableFuture.class);
      invokeHttpMethod = new InvokeHttpMethod(toRequest, http, transformerForRequest, timeLimiter, config);
      expect(config.getCommandName(get)).andReturn("ns:get");
      expect(config.getFallback(get)).andReturn(fallback);
   }

   @AfterMethod
   void verifyMocks() {
      verify(http, timeLimiter, fallback, config, future);
   }

   public void testMethodWithTimeoutRunsTimeLimiter() throws Exception {
      expect(config.getTimeoutNanos(get)).andReturn(Optional.of(250000000l));
      InvokeAndTransform invoke = invokeHttpMethod.new InvokeAndTransform("ns:get", getCommand);
      expect(timeLimiter.callWithTimeout(invoke, 250000000, TimeUnit.NANOSECONDS, true)).andReturn(response);
      replay(http, timeLimiter, fallback, config, future);
      invokeHttpMethod.apply(get);
   }

   public void testMethodWithNoTimeoutCallGetDirectly() throws Exception {
      expect(config.getTimeoutNanos(get)).andReturn(Optional.<Long> absent());
      expect(http.invoke(new HttpCommand(getRequest))).andReturn(response);
      replay(http, timeLimiter, fallback, config, future);
      invokeHttpMethod.apply(get);
   }

   private HttpResponse fallbackResponse = HttpResponse.builder().statusCode(200).payload("bar").build();

   public void testDirectCallRunsFallbackCreateOrPropagate() throws Exception {
      IllegalStateException exception = new IllegalStateException();
      expect(config.getTimeoutNanos(get)).andReturn(Optional.<Long> absent());
      expect(http.invoke(new HttpCommand(getRequest))).andThrow(exception);
      expect(fallback.createOrPropagate(exception)).andReturn(fallbackResponse);
      replay(http, timeLimiter, fallback, config, future);
      assertEquals(invokeHttpMethod.apply(get), fallbackResponse);
   }

   public void testTimeLimitedRunsFallbackCreateOrPropagate() throws Exception {
      IllegalStateException exception = new IllegalStateException();
      expect(config.getTimeoutNanos(get)).andReturn(Optional.of(250000000l));
      InvokeAndTransform invoke = invokeHttpMethod.new InvokeAndTransform("ns:get", getCommand);
      expect(timeLimiter.callWithTimeout(invoke, 250000000, TimeUnit.NANOSECONDS, true)).andThrow(exception);
      expect(fallback.createOrPropagate(exception)).andReturn(fallbackResponse);
      replay(http, timeLimiter, fallback, config, future);
      assertEquals(invokeHttpMethod.apply(get), fallbackResponse);
   }
}
