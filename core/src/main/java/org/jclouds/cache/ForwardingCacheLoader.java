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

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A {@link CacheLoader} which forwards all its method calls to another {@link CacheLoader}.
 * Subclasses should override one or more methods to modify the behavior of the backing cache as
 * desired per the <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public abstract class ForwardingCacheLoader<K, V> extends CacheLoader<K, V> {

   /** Constructor for use by subclasses. */
   protected ForwardingCacheLoader() {
   }

   protected abstract CacheLoader<K, V> delegate();

   @Override
   public V load(K key) throws Exception {
      return delegate().load(key);
   }

   @Override
   public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
      return delegate().reload(key, oldValue);
   }

   @Override
   public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
      return delegate().loadAll(keys);
   }

   /**
    * A simplified version of {@link ForwardingCacheLoader} where subclasses can pass in an already
    * constructed {@link CacheLoader} as the delegate.
    * 
    */
   @Beta
   public static class SimpleForwardingCacheLoader<K, V> extends ForwardingCacheLoader<K, V> {
      private final CacheLoader<K, V> delegate;

      protected SimpleForwardingCacheLoader(CacheLoader<K, V> delegate) {
         this.delegate = Preconditions.checkNotNull(delegate);
      }

      @Override
      protected final CacheLoader<K, V> delegate() {
         return delegate;
      }

   }
}
