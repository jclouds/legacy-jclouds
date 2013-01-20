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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;

import org.jclouds.reflect.Invocation;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class BlockOnFutureTest {

   static ListenableFuture<String> future;

   public static class ThingAsyncApi {
      public ListenableFuture<String> get() {
         return future;
      }

      @Named("ns:get")
      public ListenableFuture<String> namedGet() {
         return future;
      }
   }

   private Invocation get;
   private Invocation namedGet;

   @BeforeClass
   void setupInvocations() throws SecurityException, NoSuchMethodException {
      get = Invocation.create(method(ThingAsyncApi.class, "get"), ImmutableList.of());
      namedGet = Invocation.create(method(ThingAsyncApi.class, "namedGet"), ImmutableList.of());
   }

   @SuppressWarnings("unchecked")
   @BeforeMethod
   void createMockedFuture() throws InterruptedException, ExecutionException, TimeoutException {
      future = createMock(ListenableFuture.class);
      expect(future.get(250000000, TimeUnit.NANOSECONDS)).andReturn("foo");
      replay(future);
   }

   public void testUnnamedMethodWithDefaultPropTimeout() throws Exception {
      Function<ListenableFuture<?>, Object> withOverride = new BlockOnFuture(ImmutableMap.of("default", 250L), get);
      assertEquals(withOverride.apply(future), "foo");
      verify(future);
   }

   public void testUnnamedMethodWithClassPropTimeout() throws Exception {
      Function<ListenableFuture<?>, Object> withOverride = new BlockOnFuture(ImmutableMap.of("default", 50L,
            "ThingApi", 250L), get);
      assertEquals(withOverride.apply(future), "foo");
      verify(future);
   }

   public void testUnnamedMethodWithMethodPropTimeout() throws Exception {
      Function<ListenableFuture<?>, Object> withOverride = new BlockOnFuture(ImmutableMap.of("default", 50L,
            "ThingApi", 100L, "ThingApi.get", 250L), get);
      assertEquals(withOverride.apply(future), "foo");
      verify(future);
   }

   @SuppressWarnings("unchecked")
   public void testUnnamedMethodWithNoTimeoutsCallGetDirectly() throws Exception {
      future = createMock(ListenableFuture.class);
      expect(future.get()).andReturn("foo");
      replay(future);

      Function<ListenableFuture<?>, Object> noOverrides = new BlockOnFuture(ImmutableMap.<String, Long> of(), get);

      assertEquals(noOverrides.apply(future), "foo");
      verify(future);
   }

   public void testNamedMethodWithDefaultPropTimeout() throws Exception {
      Function<ListenableFuture<?>, Object> withOverride = new BlockOnFuture(ImmutableMap.of("default", 250L), namedGet);
      assertEquals(withOverride.apply(future), "foo");
      verify(future);
   }

   public void testNamedMethodWithMethodPropTimeout() throws Exception {
      Function<ListenableFuture<?>, Object> withOverride = new BlockOnFuture(ImmutableMap.of("default", 50L,
            "ThingApi", 100L, "ns:get", 250L), namedGet);
      assertEquals(withOverride.apply(future), "foo");
      verify(future);
   }

   @SuppressWarnings("unchecked")
   public void testNamedMethodWithNoTimeoutsCallGetDirectly() throws Exception {
      future = createMock(ListenableFuture.class);
      expect(future.get()).andReturn("foo");
      replay(future);

      Function<ListenableFuture<?>, Object> noOverrides = new BlockOnFuture(ImmutableMap.<String, Long> of(), namedGet);

      assertEquals(noOverrides.apply(future), "foo");
      verify(future);
   }

}
