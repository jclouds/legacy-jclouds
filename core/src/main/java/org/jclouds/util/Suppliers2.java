/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.util;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class Suppliers2 {

   public static <K, V> Supplier<V> getLastValueInMap(final Supplier<Map<K, Supplier<V>>> input) {
      return new Supplier<V>() {
         @Override
         public V get() {
            Supplier<V> last = Iterables.getLast(input.get().values());
            return last != null ? last.get() : null;
         }

         @Override
         public String toString() {
            return "getLastValueInMap()";
         }
      };
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
    * returns the value of the first supplier, or the value of the fallback, if the unlessNull is
    * null.
    */
   @Beta
   public static <T> Supplier<T> or(final Supplier<T> unlessNull, final Supplier<T> fallback) {
      return new Supplier<T>() {

         @Override
         public T get() {
            T val = unlessNull.get();
            if (val != null)
               return val;
            return fallback.get();
         }

         @Override
         public String toString() {
            return Objects.toStringHelper(this).add("unlessNull", unlessNull).add("fallback", fallback).toString();
         }
      };
   }

   /**
    * if a throwable of certain type is encountered on getting the first value, use the fallback.
    */
   @Beta
   public static <T, X extends Throwable> Supplier<T> onThrowable(final Supplier<T> unlessThrowable,
            final Class<X> throwable, final Supplier<T> fallback) {
      return new Supplier<T>() {

         @Override
         public T get() {
            try {
               return unlessThrowable.get();
            } catch (Throwable t) {
               if (Throwables2.getFirstThrowableOfType(t, throwable) != null)
                  return fallback.get();
               throw Throwables.propagate(t);
            }
         }

         @Override
         public String toString() {
            return Objects.toStringHelper(this).add("unlessThrowable", unlessThrowable)
                     .add("throwable", throwable.getSimpleName()).add("fallback", fallback).toString();
         }
      };
   }

}
