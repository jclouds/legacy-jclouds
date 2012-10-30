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
package org.jclouds.cache.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.jclouds.cache.ForwardingCacheLoader;
import org.jclouds.util.Throwables2;

import com.google.common.annotations.Beta;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Exponentially backs off, if we encounter an exception of the given type during any of the
 * following methods:
 * <ul>
 * <li>load
 * <li>reload
 * <li>loadAll
 * </ul>
 * 
 * @param <K>
 *           the key type of the cache loader
 * @param <V>
 *           the value type of the cache loader
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public class BackoffExponentiallyAndRetryOnThrowableCacheLoader<K, V> extends ForwardingCacheLoader<K, V> {
   private final Class<? extends Throwable> retryableThrowable;
   private final long periodMs;
   private final long maxPeriodMs;
   private final int maxTries;
   private final CacheLoader<K, V> loader;

   /**
    * 
    * @param retryableThrowable
    *           the exception which we can retry
    * @param periodMs
    *           initial period, which exponentially increases with each try, specified in
    *           milliseconds
    * @param maxPeriodMs
    *           maximum period duration, specified in milliseconds
    * @param maxTries
    *           maximum amount of tries
    * @param loader
    *           the loader we are able to retry
    */
   public BackoffExponentiallyAndRetryOnThrowableCacheLoader(Class<? extends Throwable> retryableThrowable, long periodMs,
            long maxPeriodMs, int maxTries, CacheLoader<K, V> loader) {
      this.retryableThrowable = checkNotNull(retryableThrowable, "retryableThrowable");
      checkArgument(maxTries > 1, "maxTries must be more than one: %d", maxTries);
      this.maxTries = maxTries;
      checkArgument(periodMs > 0, "periodMs must be positive: %d", periodMs);
      this.periodMs = periodMs;
      checkArgument(maxPeriodMs > periodMs, "maxPeriodMs must be equal to or greater than periodMs: %d %d",
               maxPeriodMs, periodMs);
      this.maxPeriodMs = maxPeriodMs;
      this.loader = checkNotNull(loader, "loader");
   }

   @Override
   protected CacheLoader<K, V> delegate() {
      return loader;
   }

   // TODO: refactor into a better closure in java pattern, if one exists
   @Override
   public V load(final K key) throws Exception {
      return backoffExponentiallyAndRetryOnThrowable(new Callable<V>() {

         @Override
         public V call() throws Exception {
            try {
               return BackoffExponentiallyAndRetryOnThrowableCacheLoader.super.load(key);
            } catch (Exception e) {
               TimeoutException te = Throwables2.getFirstThrowableOfType(e,
                  TimeoutException.class);
               if (te != null) {
                  throw te;
               }
               throw e;
            }
         }
      });
   }

   @Override
   public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception {
      return backoffExponentiallyAndRetryOnThrowable(new Callable<ListenableFuture<V>>() {

         @Override
         public ListenableFuture<V> call() throws Exception {
            return BackoffExponentiallyAndRetryOnThrowableCacheLoader.super.reload(key, oldValue);
         }
      });
   }

   @Override
   public Map<K, V> loadAll(final Iterable<? extends K> keys) throws Exception {
      return backoffExponentiallyAndRetryOnThrowable(new Callable<Map<K, V>>() {

         @Override
         public Map<K, V> call() throws Exception {
            return BackoffExponentiallyAndRetryOnThrowableCacheLoader.super.loadAll(keys);
         }
      });
   }

   private <T> T backoffExponentiallyAndRetryOnThrowable(Callable<T> callable) throws Exception {
      return new BackoffExponentiallyAndRetryOnThrowableCallable<T>(retryableThrowable, periodMs, maxPeriodMs,
               maxTries, callable).call();
   }

}
