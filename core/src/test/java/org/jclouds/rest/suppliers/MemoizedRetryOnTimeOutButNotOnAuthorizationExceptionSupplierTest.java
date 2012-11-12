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
package org.jclouds.rest.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.SetAndThrowAuthorizationExceptionSupplierBackedLoader;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.Atomics;

/**
 * 
 * @author Adrian Cole
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Test(groups = "unit", testName = "MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplierTest")
public class MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplierTest {
   @Test
   public void testLoaderNormal() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      assertEquals(new SetAndThrowAuthorizationExceptionSupplierBackedLoader<String>(Suppliers.ofInstance("foo"),
               authException).load("KEY"), "foo");
      assertEquals(authException.get(), null);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testLoaderThrowsAuthorizationExceptionAndAlsoSetsExceptionType() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplierBackedLoader<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new AuthorizationException();
            }
         }, authException).load("KEY");
      } finally {
         assertEquals(authException.get().getClass(), AuthorizationException.class);
      }
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testLoaderThrowsAuthorizationExceptionAndAlsoSetsExceptionTypeWhenNested() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplierBackedLoader<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new RuntimeException(new ExecutionException(new AuthorizationException()));
            }
         }, authException).load("KEY");
      } finally {
         assertEquals(authException.get().getClass(), AuthorizationException.class);
      }
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testLoaderThrowsOriginalExceptionAndAlsoSetsExceptionTypeWhenNestedAndNotAuthorizationException() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplierBackedLoader<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new RuntimeException(new IllegalArgumentException("foo"));
            }
         }, authException).load("KEY");
      } finally {
         assertEquals(authException.get().getClass(), RuntimeException.class);
      }
   }

   @Test
   public void testMemoizeKeepsValueForFullDurationWhenDelegateCallIsSlow() {
      final long SLEEP_TIME = 250;
      final long EXPIRATION_TIME = 200;

      Supplier<Integer> slowSupplier = new CountingSupplier() {
         private static final long serialVersionUID = 1L;

         @Override
         public Integer get() {
            try {
               Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            }
            return super.get();
         }
      };

      Supplier<Integer> memoizedSupplier = new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(
               new AtomicReference<AuthorizationException>(), slowSupplier, EXPIRATION_TIME, TimeUnit.MILLISECONDS);

      assertEquals(memoizedSupplier.get(), (Integer) 10);
      assertEquals(memoizedSupplier.get(), (Integer) 10);
   }

   // =================================
   //
   // TODO Everything below this point is taken from SuppliersTest, to test our version of the
   // Suppliers2.memoizeWithExpiration
   // It should be deleted when we can switch back to using the google
   // Supplier.memoizeWithExpiration.

   private static class CountingSupplier implements Supplier<Integer>, Serializable {
      private static final long serialVersionUID = 0L;
      transient int calls = 0;

      @Override
      public Integer get() {
         calls++;
         return calls * 10;
      }
   }

   @Test
   public void testMemoizeWithExpiration() throws InterruptedException {
      CountingSupplier countingSupplier = new CountingSupplier();

      Supplier<Integer> memoizedSupplier = new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(
               new AtomicReference<AuthorizationException>(), countingSupplier, 75, TimeUnit.MILLISECONDS);

      checkExpiration(countingSupplier, memoizedSupplier);
   }

   @Test
   public void testMemoizeWithExpirationSerialized() throws InterruptedException {
      CountingSupplier countingSupplier = new CountingSupplier();

      Supplier<Integer> memoizedSupplier = new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(
               new AtomicReference<AuthorizationException>(), countingSupplier, 75, TimeUnit.MILLISECONDS);
      // Calls to the original memoized supplier shouldn't affect its copy.
      memoizedSupplier.get();

      Supplier<Integer> copy = reserialize(memoizedSupplier);
      memoizedSupplier.get();

      CountingSupplier countingCopy = (CountingSupplier) ((MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier) copy)
               .delegate();
      checkExpiration(countingCopy, copy);
   }

   private void checkExpiration(CountingSupplier countingSupplier, Supplier<Integer> memoizedSupplier)
            throws InterruptedException {
      // the underlying supplier hasn't executed yet
      assertEquals(0, countingSupplier.calls);

      assertEquals(10, (int) memoizedSupplier.get());
      // now it has
      assertEquals(1, countingSupplier.calls);

      assertEquals(10, (int) memoizedSupplier.get());
      // it still should only have executed once due to memoization
      assertEquals(1, countingSupplier.calls);

      Thread.sleep(150);

      assertEquals(20, (int) memoizedSupplier.get());
      // old value expired
      assertEquals(2, countingSupplier.calls);

      assertEquals(20, (int) memoizedSupplier.get());
      // it still should only have executed twice due to memoization
      assertEquals(2, countingSupplier.calls);
   }

   @Test
   public void testExpiringMemoizedSupplierThreadSafe() throws Throwable {
      Function<Supplier<Boolean>, Supplier<Boolean>> memoizer = new Function<Supplier<Boolean>, Supplier<Boolean>>() {
         @Override
         public Supplier<Boolean> apply(Supplier<Boolean> supplier) {
            return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(
                     new AtomicReference<AuthorizationException>(), supplier, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
         }
      };
      supplierThreadSafe(memoizer);
   }

   private void supplierThreadSafe(Function<Supplier<Boolean>, Supplier<Boolean>> memoizer) throws Throwable {
      final AtomicInteger count = new AtomicInteger(0);
      final AtomicReference<Throwable> thrown = Atomics.newReference(null);
      final int numThreads = 3;
      final Thread[] threads = new Thread[numThreads];
      final long timeout = TimeUnit.SECONDS.toNanos(60);

      final Supplier<Boolean> supplier = new Supplier<Boolean>() {
         boolean isWaiting(Thread thread) {
            switch (thread.getState()) {
               case BLOCKED:
               case WAITING:
               case TIMED_WAITING:
                  return true;
               default:
                  return false;
            }
         }

         int waitingThreads() {
            int waitingThreads = 0;
            for (Thread thread : threads) {
               if (isWaiting(thread)) {
                  waitingThreads++;
               }
            }
            return waitingThreads;
         }

         @Override
         public Boolean get() {
            // Check that this method is called exactly once, by the first
            // thread to synchronize.
            long t0 = System.nanoTime();
            while (waitingThreads() != numThreads - 1) {
               if (System.nanoTime() - t0 > timeout) {
                  thrown.set(new TimeoutException("timed out waiting for other threads to block"
                           + " synchronizing on supplier"));
                  break;
               }
               Thread.yield();
            }
            count.getAndIncrement();
            return Boolean.TRUE;
         }
      };

      final Supplier<Boolean> memoizedSupplier = memoizer.apply(supplier);

      for (int i = 0; i < numThreads; i++) {
         threads[i] = new Thread() {
            @Override
            public void run() {
               assertSame(Boolean.TRUE, memoizedSupplier.get());
            }
         };
      }
      for (Thread t : threads) {
         t.start();
      }
      for (Thread t : threads) {
         t.join();
      }

      if (thrown.get() != null) {
         throw thrown.get();
      }
      assertEquals(1, count.get());
   }

   // Taken from com.google.common.testing.SerializableTester
   private static <T> T reserialize(T object) {
      checkNotNull(object);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try {
         ObjectOutputStream out = new ObjectOutputStream(bytes);
         out.writeObject(object);
         ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
         return (T) in.readObject();
      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}
