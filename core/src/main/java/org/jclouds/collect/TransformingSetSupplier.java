/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.collect;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class TransformingSetSupplier<F, T> implements Supplier<Set<? extends T>> {
   private final Supplier<Iterable<F>> backingSupplier;
   private final Function<F, T> converter;

   public TransformingSetSupplier(Supplier<Iterable<F>> backingSupplier, Function<F, T> converter) {
      this.backingSupplier = checkNotNull(backingSupplier, "backingSupplier");
      this.converter = checkNotNull(converter, "converter");
   }

   @Override
   public Set<? extends T> get() {
      return newLinkedHashSet(transform(backingSupplier.get(), converter));
   }

}
