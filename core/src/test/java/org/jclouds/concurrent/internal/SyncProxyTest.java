/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.concurrent.internal;

import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Futures;
import org.jclouds.concurrent.Timeout;
import org.jclouds.internal.ClassMethodArgs;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Tests behavior of ListenableFutureExceptionParser
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "concurrent.ListenableFutureExceptionParserTest")
public class SyncProxyTest {

   @Test
   void testConvertNanos() {
      assertEquals(SyncProxy.convertToNanos(Sync.class.getAnnotation(Timeout.class)), 30000000);
   }

   @Timeout(duration = 30, timeUnit = TimeUnit.MILLISECONDS)
   private static interface Sync {
      String getString();

      String newString();

      String getRuntimeException();

      String getTypedException() throws FileNotFoundException;

      String take20Milliseconds();

      String take100MillisecondsAndTimeout();

      @Timeout(duration = 150, timeUnit = TimeUnit.MILLISECONDS)
      String take100MillisecondsAndOverride();

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

      public ListenableFuture<String> take100MillisecondsAndTimeout() {
         return Futures.makeListenable(executorService.submit(new Callable<String>() {

            public String call() {
               try {
                  Thread.sleep(100);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               return "foo";
            }

         }), executorService);
      }

      public ListenableFuture<String> take100MillisecondsAndOverride() {
         return take100MillisecondsAndTimeout();
      }

   }

   private Sync sync;

   @BeforeTest
   public void setUp() throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      sync = SyncProxy.proxy(Sync.class, new SyncProxy(Sync.class, new Async(),
            new ConcurrentHashMap<ClassMethodArgs, Object>(), ImmutableMap.<Class<?>, Class<?>> of()));
   }

   @Test
   public void testUnwrapListenableFuture() {
      assertEquals(sync.getString(), "foo");
   }

   @Test
   public void testPassSync() {
      assertEquals(sync.newString(), "new");
   }

   @Test
   public void testTake20Milliseconds() {
      assertEquals(sync.take20Milliseconds(), "foo");

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testTake100MillisecondsAndTimeout() {
      assertEquals(sync.take100MillisecondsAndTimeout(), "foo");

   }

   @Test
   public void testTake100MillisecondsAndOverride() {
      assertEquals(sync.take100MillisecondsAndOverride(), "foo");
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
      SyncProxy.proxy(SyncWrongException.class, new SyncProxy(SyncWrongException.class, new Async(),
               new ConcurrentHashMap<ClassMethodArgs, Object>(), ImmutableMap.<Class<?>, Class<?>> of()));
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
      SyncProxy.proxy(SyncNoTimeOut.class, new SyncProxy(SyncNoTimeOut.class, new Async(),
               new ConcurrentHashMap<ClassMethodArgs, Object>(), ImmutableMap.<Class<?>, Class<?>> of()));
   }

}
