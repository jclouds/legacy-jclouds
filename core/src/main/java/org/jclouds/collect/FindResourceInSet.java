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
package org.jclouds.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public abstract class FindResourceInSet<F, T> implements Function<F, T> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Supplier<Set<? extends T>> set;

   @Inject
   public FindResourceInSet(@Memoized Supplier<Set<? extends T>> set) {
      this.set = checkNotNull(set, "set");
   }

   public abstract boolean matches(F from, T input);

   public T apply(final F from) {
      try {
         return Iterables.find(set.get(), new Predicate<T>() {

            @Override
            public boolean apply(T input) {
               return matches(from, input);
            }

         });
      } catch (NoSuchElementException e) {
         logger.trace("could not find a match in set for %s", from);
      }
      return null;
   }
}
