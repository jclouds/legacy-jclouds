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
package org.jclouds.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Supplier;

/**
 * Works in google app engine since it doesn't use threads.
 * 
 * @author Adrian Cole
 */
public class ExpirableSupplier<V> implements Supplier<V> {
   private final Supplier<V> supplier;
   private final AtomicReference<V> currentValue;
   private final AtomicLong trigger;
   private final long expirationNanos;

   public ExpirableSupplier(Supplier<V> supplier, long duration, TimeUnit unit) {
      this.supplier = supplier;
      this.expirationNanos = unit.toNanos(duration);
      this.currentValue = new AtomicReference<V>(null);
      trigger = new AtomicLong(System.nanoTime() + expirationNanos);
   }

   void updateIfExpired() {
      V current = currentValue.get();
      if (current == null || trigger.get() - System.nanoTime() <= 0) {
         trigger.set(System.nanoTime() + expirationNanos);
         // we always want the last one to win. think login session
         currentValue.set(supplier.get());
      }
   }

   public V get() {
      updateIfExpired();
      return currentValue.get();
   }

}