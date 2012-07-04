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

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.io.OutputSupplier;

/**
 * 
 * @author Adrian Cole
 */
public class Suppliers2 {

   public static <K, V> Supplier<V> getLastValueInMap(Supplier<Map<K, Supplier<V>>> input) {
      return Suppliers2.compose(new Function<Map<K, Supplier<V>>, V>() {

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

   // only here until guava compose gives a toString!
   // http://code.google.com/p/guava-libraries/issues/detail?id=1052
   public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
      Preconditions.checkNotNull(function);
      Preconditions.checkNotNull(supplier);
      return new SupplierComposition<F, T>(function, supplier);
   }

   private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
      /** The serialVersionUID */
      private static final long serialVersionUID = 1023509665531743802L;

      final Function<? super F, T> function;
      final Supplier<F> supplier;

      SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
         this.function = function;
         this.supplier = supplier;
      }

      @Override
      public T get() {
         return function.apply(supplier.get());
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("function", function).add("supplier", supplier).toString();
      }
   }

}
