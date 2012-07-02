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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Iterables;
import com.google.common.io.OutputSupplier;

/**
 * 
 * @author Adrian Cole
 */
public class Suppliers2 {

   public static <K, V> Supplier<V> getLastValueInMap(Supplier<Map<K, Supplier<V>>> input) {
      return Suppliers.compose(new Function<Map<K, Supplier<V>>, V>() {

         @Override
         public V apply(Map<K, Supplier<V>> input) {
            return Iterables.getLast(input.values()).get();
         }

         @Override
         public String toString() {
            return "getLastValueInMap()";
         }
      }, input);
   }

   public static <X> Function<X, Supplier<X>> ofInstanceFunction() {
      return new Function<X, Supplier<X>>() {

         @Override
         public Supplier<X> apply(X arg0) {
            return Suppliers.ofInstance(arg0);
         }

         @Override
         public String toString() {
            return "Suppliers.ofInstance()";
         }
      };
   }

   /**
    * converts an {@link OutputStream} to an {@link OutputSupplier}
    * 
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
    * same as {@link Supplier.memoizeWithExpiration} except that the expiration ticker starts after
    * write vs after call to {@code get}.
    * 
    * @see Supplier.memoizeWithExpiration
    */
   public static <T> Supplier<T> memoizeWithExpirationAfterWrite(Supplier<T> delegate, long duration, TimeUnit unit) {
      return new ExpireAfterWriteSupplier<T>(delegate, duration, unit);
   }

   static class ExpireAfterWriteSupplier<T> extends ForwardingObject implements Supplier<T>, Serializable {
      private final Supplier<T> delegate;
      private final long duration;
      private final TimeUnit unit;
      private final LoadingCache<Object, T> cache;

      public ExpireAfterWriteSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
         this.delegate = delegate;
         this.duration = duration;
         this.unit = unit;
         cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit).build(CacheLoader.from(delegate));
      }

      @Override
      protected Supplier<T> delegate() {
         return delegate;
      }

      @Override
      public T get() {
         return cache.getUnchecked("FOO");
      }

      private static final long serialVersionUID = 0;

      @Override
      public int hashCode() {
         return Objects.hashCode(delegate, duration, unit);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ExpireAfterWriteSupplier<?> that = ExpireAfterWriteSupplier.class.cast(obj);
         return Objects.equal(delegate, that.delegate) && Objects.equal(duration, that.duration);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("delegate", delegate).add("duration", duration).add("unit", unit)
                  .toString();
      }

   }
}
