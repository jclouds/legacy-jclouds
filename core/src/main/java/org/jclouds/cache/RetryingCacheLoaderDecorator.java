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

package org.jclouds.cache;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cache.internal.BackoffExponentiallyAndRetryOnThrowableCacheLoader;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.cache.CacheLoader;

/**
 * <p>
 * A decorator of {@link CacheLoader} instances having any combination of the following features:
 * 
 * <ul>
 * <li>exponential backoff based on a particular Throwable type
 * </ul>
 * 
 * These features are all optional; cache loaders can be created using all or none of them. By
 * default, the input cache loader is returned unaffected.
 * 
 * <p>
 * Usage example:
 * 
 * <pre>
 * @code
 * 
 *   CacheLoader<Key, Graph> loader = RetryingCacheLoaderDecorator.newDecorator()
 *       .on(ResourceNotFoundException.class).exponentiallyBackoff()
 *       .decorate(
 *           new CacheLoader<Key, Graph>() {
 *             public Graph load(Key key) throws AnyException {
 *               return createOnFlakeyConnection(key);
 *             }
 *           });}
 * </pre>
 * 
 * @param <K>
 *           the base key type for all cache loaders created by this decorator
 * @param <V>
 *           the base value type for all cache loaders created by this decorator
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public class RetryingCacheLoaderDecorator<K, V> {
   protected RetryingCacheLoaderDecorator() {
   }

   /**
    * Constructs a new {@code RetryingCacheLoaderDecorator} instance with default settings, and no
    * retrying any kind.
    */
   // we can resolve generic type during decorate, as opposed to here
   public static RetryingCacheLoaderDecorator<Object, Object> newDecorator() {
      return new RetryingCacheLoaderDecorator<Object, Object>();
   }

   /**
    * Determines the action to carry out on a particular throwable.
    * 
    */
   public OnThrowableBuilder<K, V> on(Class<? extends Throwable> retryableThrowable) {
      return new OnThrowableBuilder<K, V>(retryableThrowable);
   }

   public static class OnThrowableBuilder<K, V> {
      Class<? extends Throwable> retryableThrowable;

      protected OnThrowableBuilder(Class<? extends Throwable> retryableThrowable) {
         this.retryableThrowable = checkNotNull(retryableThrowable, "retryableThrowable");
      }

      /**
       * For each attempt, exponentially backoff
       */
      public BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V> exponentiallyBackoff() {
         return new BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V>(retryableThrowable);
      }

   }

   public static class BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V> extends
            RetryingCacheLoaderDecorator<K, V> {
      private long periodMs = 100l;
      private long maxPeriodMs = 200l;
      private int maxTries = 5;
      private final Class<? extends Throwable> retryableThrowable;

      protected BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator(Class<? extends Throwable> retryableThrowable) {
         this.retryableThrowable = checkNotNull(retryableThrowable, "retryableThrowable");
      }

      /**
       * The initial period in milliseconds to delay between tries. with each try this period will
       * increase exponentially.
       * <p/>
       * default: {@code 100}
       * 
       */
      public BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V> periodMs(long periodMs) {
         checkArgument(periodMs > 1, "maxTries must be positive: %d", periodMs);
         this.periodMs = periodMs;
         return this;
      }

      /**
       * The initial period in milliseconds to delay between tries. with each try this period will
       * increase exponentially.
       * <p/>
       * default: {@code 200}
       * 
       */
      public BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V> maxPeriodMs(long maxPeriodMs) {
         checkArgument(maxPeriodMs > periodMs, "maxPeriodMs must be equal to or greater than periodMs: %d %d",
                  maxPeriodMs, periodMs);
         this.maxPeriodMs = maxPeriodMs;
         return this;
      }

      /**
       * The maximum attempts to try on the given exception type
       * <p/>
       * default: {@code 5}
       * 
       */
      public BackoffExponentiallyAndRetryOnThrowableCacheLoaderDecorator<K, V> maxTries(int maxTries) {
         checkArgument(maxTries > 1, "maxTries must be more than one: %d", maxTries);
         this.maxTries = maxTries;
         return this;
      }

      @Override
      public <K1 extends K, V1 extends V> CacheLoader<K1, V1> decorate(CacheLoader<K1, V1> loader) {
         return new BackoffExponentiallyAndRetryOnThrowableCacheLoader<K1, V1>(retryableThrowable, periodMs,
                  maxPeriodMs, maxTries, super.decorate(loader));
      }

      @Override
      protected Objects.ToStringHelper string() {
         return Objects.toStringHelper(this).add("retryableThrowable", retryableThrowable).add("periodMs", periodMs).add("maxPeriodMs",
                  maxPeriodMs).add("maxTries", maxTries);
      }
   }

   /**
    * Decorates a cacheloader, or returns the same value, if no retrying features were requested.
    * 
    * <p>
    * This method does not alter the state of this {@code RetryingCacheLoaderDecorator} instance, so
    * it can be invoked again to create multiple independent cache loaders.
    * 
    * @param loader
    *           the cache loader used to obtain new values
    * @return a cache loader having the requested features
    */
   public <K1 extends K, V1 extends V> CacheLoader<K1, V1> decorate(CacheLoader<K1, V1> loader) {
      return (CacheLoader<K1, V1>) loader;
   }

   /**
    * Returns a string representation for this RetryingCacheLoaderDecorator instance. The exact form
    * of the returned string is not specified.
    */
   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * append any state that should be considered in {@link #toString} here.
    */
   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this);
   }
}
