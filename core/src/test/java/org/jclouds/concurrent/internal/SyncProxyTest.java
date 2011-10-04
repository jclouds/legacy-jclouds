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

import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Futures;
import org.jclouds.concurrent.Timeout;
import org.jclouds.internal.ClassMethodArgs;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Tests behavior of ListenableFutureExceptionParser
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class SyncProxyTest {

   @Test
   void testConvertNanos() {
      assertEquals(SyncProxy.convertToNanos(Sync.class.getAnnotation(Timeout.class)), 40000000);
   }

   @Timeout(duration = 40, timeUnit = TimeUnit.MILLISECONDS)
   private static interface Sync {
      String getString();

      String newString();

      @Provides
      Set<String> string();

      String getRuntimeException();

      String getTypedException() throws FileNotFoundException;

      String take20Milliseconds();

      String take200MillisecondsAndTimeout();

      @Timeout(duration = 300, timeUnit = TimeUnit.MILLISECONDS)
      String take200MillisecondsAndOverride();

      String takeXMillisecondsPropOverride(long ms);
   }

   static ExecutorService executorService = Executors.newCachedThreadPool();

   public static class Async {
      public String toString() {
         return "async";
      }

      public ListenableFuture<String> getString() {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() throws Exception {
               return "foo";
            }

         }), executorService);
      }

      public ListenableFuture<String> getRuntimeException() {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() throws Exception {
               throw new RuntimeException();
            }

         }), executorService);
      }

      public ListenableFuture<String> getTypedException() throws FileNotFoundException {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() throws FileNotFoundException {
               throw new FileNotFoundException();
            }

         }), executorService);
      }

      public String newString() {
         return "new";
      }

      @Provides
      public Set<String> string() {
         return ImmutableSet.of("new");
      }
      
      public ListenableFuture<String> take20Milliseconds() {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() {
               try {
                  Thread.sleep(20);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               return "foo";
            }

         }), executorService);
      }

      public ListenableFuture<String> take200MillisecondsAndTimeout() {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() {
               try {
                  Thread.sleep(200);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               return "foo";
            }

         }), executorService);
      }

      public ListenableFuture<String> take200MillisecondsAndOverride() {
         return take200MillisecondsAndTimeout();
      }

      public ListenableFuture<String> takeXMillisecondsPropOverride(final long ms) {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() {
               try {
                  Thread.sleep(ms);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               return "foo";
            }

         }), executorService);
      }

   }

   private Sync sync;

   @BeforeTest
   public void setUp() throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      Cache<ClassMethodArgs, Object> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.<Object>constant(null)));
      sync = SyncProxy.proxy(Sync.class, new Async(),cache, ImmutableMap.<Class<?>, Class<?>> of(),
              ImmutableMap.of("Sync.takeXMillisecondsPropOverride", 250L));
      // just to warm up
      sync.string();
   }

   @Test
   public void testUnwrapListenableFuture() {
      assertEquals(sync.getString(), "foo");
   }

   @Test
   public void testPassSync() {
      assertEquals(sync.newString(), "new");
      assertEquals(sync.string(), ImmutableSet.of("new"));
   }

   @Test
   public void testTake20Milliseconds() {
      assertEquals(sync.take20Milliseconds(), "foo");

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testTake200MillisecondsAndTimeout() {
      assertEquals(sync.take200MillisecondsAndTimeout(), "foo");
   }

   @Test
   public void testTake200MillisecondsAndOverride() {
      assertEquals(sync.take200MillisecondsAndOverride(), "foo");
   }

   @Test
   public void testTake200MillisecondsPropOverride() {
      assertEquals(sync.takeXMillisecondsPropOverride(200), "foo");
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testTake300MillisecondsPropTimeout() {
      assertEquals(sync.takeXMillisecondsPropOverride(300), "foo");
   }

   @Test
   public void testToString() {
      assertEquals(sync.toString(), "Sync Proxy for: Async");
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testUnwrapRuntimeException() {
      sync.getRuntimeException();
   }

   @Test(expectedExceptions = FileNotFoundException.class)
   public void testUnwrapTypedException() throws FileNotFoundException {
      sync.getTypedException();
   }

   @Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
   private static interface SyncWrongException {
      String getString();

      String newString();

      String getRuntimeException();

      String getTypedException() throws UnsupportedEncodingException;

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testWrongTypedException() throws IllegalArgumentException, SecurityException, NoSuchMethodException,
            IOException {
      Cache<ClassMethodArgs, Object> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.<Object>constant(null)));
      SyncProxy.proxy(SyncWrongException.class,  new Async(), cache, ImmutableMap.<Class<?>, Class<?>> of(),
              ImmutableMap.<String, Long>of());
   }

   private static interface SyncNoTimeOut {
      String getString();

      String newString();

      String getRuntimeException();

      String getTypedException() throws UnsupportedEncodingException;

   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNoTimeOutException() throws IllegalArgumentException, SecurityException, NoSuchMethodException,
            IOException {
      Cache<ClassMethodArgs, Object> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.<Object>constant(null)));
      SyncProxy.proxy(SyncNoTimeOut.class, new Async(),
            cache, ImmutableMap.<Class<?>, Class<?>> of(), ImmutableMap.<String, Long>of());
   }


   @Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
   private static interface SyncClassOverride {
      String getString();

      String newString();

      String getRuntimeException();

      @Timeout(duration = 300, timeUnit = TimeUnit.MILLISECONDS)
      String takeXMillisecondsPropOverride(long ms);

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testClassOverridePropTimeout() throws Exception {
      Cache<ClassMethodArgs, Object> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.<Object>constant(null)));
      final SyncClassOverride sync2 = SyncProxy.proxy(SyncClassOverride.class, new Async(),
            cache, ImmutableMap.<Class<?>, Class<?>> of(), ImmutableMap.<String, Long>of("SyncClassOverride", 100L));

      assertEquals(sync2.takeXMillisecondsPropOverride(200), "foo");
   }
}
