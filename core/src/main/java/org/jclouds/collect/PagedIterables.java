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
package org.jclouds.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;

/**
 * Utilities for using {@link PagedIterable}s.
 * 
 * @author Adrian Cole
 */
@Beta
public class PagedIterables {
   /**
    * @param only
    *           the only page of data
    * 
    * @return iterable with only the one page
    */
   public static <T> PagedIterable<T> onlyPage(final IterableWithMarker<T> only) {
      return new PagedIterable<T>() {
         public Iterator<IterableWithMarker<T>> iterator() {
            return ImmutableSet.of(only).iterator();
         }
      };
   }

   /**
    * @see #onlyPage(IterableWithMarker)
    */
   @Deprecated
   public static <T> PagedIterable<T> of(IterableWithMarker<T> only) {
      return onlyPage(only);
   }

   /**
    * 
    * 
    * @param initial
    *           the initial set current data
    * @param markerToNext
    *           produces the next set based on the marker
    * 
    * @return iterable current data which continues if the user iterates beyond
    *         the first page
    */
   public static <T> PagedIterable<T> advance(final IterableWithMarker<T> initial,
         final Function<Object, IterableWithMarker<T>> markerToNext) {
      return new PagedIterable<T>() {
         public Iterator<IterableWithMarker<T>> iterator() {
            return advancingIterator(initial, markerToNext);
         }
      };
   }

   private static class AdvancingIterator<T> extends AbstractIterator<IterableWithMarker<T>> {

      private final Function<Object, IterableWithMarker<T>> markerToNext;
      private transient IterableWithMarker<T> current;
      private transient boolean unread = true;

      private AdvancingIterator(IterableWithMarker<T> initial, Function<Object, IterableWithMarker<T>> markerToNext) {
         this.current = checkNotNull(initial, "initial iterable");
         this.markerToNext = checkNotNull(markerToNext, "marker to next iterable");
      }

      @Override
      protected IterableWithMarker<T> computeNext() {
         if (unread)
            try {
               return current;
            } finally {
               unread = false;
            }
         else if (current.nextMarker().isPresent())
            return current = markerToNext.apply(current.nextMarker().get());
         else
            return endOfData();
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(current, unread);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         AdvancingIterator<?> other = AdvancingIterator.class.cast(obj);
         return Objects.equal(this.current, other.current) && Objects.equal(this.unread, other.unread);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper("").add("current", current).add("unread", unread).toString();
      }
   }

   /**
    * 
    * @param initial
    *           the initial set current data
    * @param markerToNext
    *           produces the next set based on the marker
    * 
    * @return iterable current data which continues if the user iterates beyond
    *         the first page
    */
   private static <T> Iterator<IterableWithMarker<T>> advancingIterator(IterableWithMarker<T> initial,
         Function<Object, IterableWithMarker<T>> markerToNext) {
      if (!initial.nextMarker().isPresent()) {
         return ImmutableSet.of(initial).iterator();
      }
      return new AdvancingIterator<T>(initial, markerToNext);
   }
}
