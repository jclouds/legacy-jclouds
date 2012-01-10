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
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.io.OutputSupplier;

/**
 * @author Adrian Cole
 */
public class Suppliers2 {
   /**
    * converts an {@link OutputStream} to an {@link OutputSupplier}
    */
   public static OutputSupplier<OutputStream> newOutputStreamSupplier(final OutputStream output) {
      checkNotNull(output, "output");
      return new OutputSupplier<OutputStream>() {
         public OutputStream getOutput() throws IOException {
            return output;
         }
      };
   }

   /**
    * Returns a supplier that caches the instance supplied by the delegate and
    * removes the cached value after the specified time has passed or when the
    * {@link org.jclouds.util.Suppliers2.InvalidatableExpiringMemoizingSupplier#invalidate()}
    * method is called.
    *
    * @see com.google.common.base.Suppliers.ExpiringMemoizingSupplier
    */
   public static <T> Supplier<T> memoizeWithExpirationAndInvalidation(
         Supplier<T> delegate, long duration, TimeUnit unit) {
      return new InvalidatableExpiringMemoizingSupplier<T>(delegate, duration, unit);
   }

   @VisibleForTesting
   public static class InvalidatableExpiringMemoizingSupplier<T>
         implements Supplier<T>, Serializable {
      final Supplier<T> delegate;
      final long durationNanos;
      transient volatile T value;
      // The special value 0 means "not yet initialized".
      transient volatile long expirationNanos;
      // Default to true for initial load
      boolean invalid = true;

      InvalidatableExpiringMemoizingSupplier(
            Supplier<T> delegate, long duration, TimeUnit unit) {
         this.delegate = Preconditions.checkNotNull(delegate);
         this.durationNanos = unit.toNanos(duration);
         Preconditions.checkArgument(duration > 0);
      }

      @Override
      public T get() {
         // First invalidation check
         synchronized (this) {
            if (invalid) {
               T t = delegate.get();
               value = t;
               invalid = false;
               return t;
            }
         }

         // Another variant of Double Checked Locking.
         //
         // We use two volatile reads.  We could reduce this to one by
         // putting our fields into a holder class, but (at least on x86)
         // the extra memory consumption and indirection are more
         // expensive than the extra volatile reads.
         long nanos = expirationNanos;
         long now = System.nanoTime();
         if (nanos == 0 || now - nanos >= 0) {
            synchronized (this) {
               if (nanos == expirationNanos) {  // recheck for lost race
                  T t = delegate.get();
                  value = t;
                  nanos = now + durationNanos;
                  // In the very unlikely event that nanos is 0, set it to 1;
                  // no one will notice 1 ns of tardiness.
                  expirationNanos = (nanos == 0) ? 1 : nanos;
                  return t;
               }
            }
         }

         return value;
      }

      public synchronized void invalidate() { invalid = true; }

      private static final long serialVersionUID = 0;
   }
}
