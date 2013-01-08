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
package org.jclouds.concurrent.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests behavior of ListenableFutureExceptionParser
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", enabled = false, singleThreaded = true)
public class SyncProxyTest {

   static ListenableFuture<String> future;

   @SuppressWarnings("unchecked")
   @BeforeMethod
   void createMockedFuture() throws InterruptedException, ExecutionException, TimeoutException {
      future = createMock(ListenableFuture.class);
      expect(future.get(250000000, TimeUnit.NANOSECONDS)).andReturn("foo");
      replay(future);
   }

   public static class Async {
      public ListenableFuture<String> get() {
         return future;
      }
   }

   private static interface Sync {
      String get();
   }

   public void testWithDefaultPropTimeout() throws Exception {
      Sync withOverride = syncProxyForTimeouts(ImmutableMap.of("default", 250L));
      assertEquals(withOverride.get(), "foo");
      verify(future);
   }

   public void testWithClassPropTimeout() throws Exception {
      Sync withOverride = syncProxyForTimeouts(ImmutableMap.of("default", 50L, "Sync", 250L));
      assertEquals(withOverride.get(), "foo");
      verify(future);
   }

   public void testWithMethodPropTimeout() throws Exception {
      Sync withOverride = syncProxyForTimeouts(ImmutableMap.of("default", 50L, "Sync", 100L, "Sync.get", 250L));
      assertEquals(withOverride.get(), "foo");
      verify(future);
   }

   @SuppressWarnings("unchecked")
   public void testWithMethodWithNoTimeoutsCallGetDirectly() throws Exception {
      future = createMock(ListenableFuture.class);
      expect(future.get()).andReturn("foo");
      replay(future);
      
      Sync noOverrides = syncProxyForTimeouts(ImmutableMap.<String, Long> of());

      assertEquals(noOverrides.get(), "foo");
      verify(future);
   }

   private Sync syncProxyForTimeouts(ImmutableMap<String, Long> timeouts) throws NoSuchMethodException {
//      LoadingCache<ForwardInvocationToInterface, Object> cache = CacheBuilder.newBuilder().build(
//            CacheLoader.from(Functions.<Object> constant(null)));
//      return FunctionalReflection.newProxy(Sync.class, new SyncProxy(new AlwaysPresentImplicitOptionalConverter(),
//            cache, ImmutableMap.<Class<?>, Class<?>> of(Sync.class, Async.class), timeouts, Sync.class, new Async()));
////      
//      Function<InvocationSuccess, Optional<Object>> optionalConverter, SyncProxy.Factory factory,
//      AsyncRestClientProxy.Caller.Factory asyncFactory, Map<Class<?>, Class<?>> sync2Async,
//      @Named("TIMEOUTS") Map<String, Long> timeouts, @Assisted Class<?> declaring, @Assisted Object async
      return null;
   }

}
