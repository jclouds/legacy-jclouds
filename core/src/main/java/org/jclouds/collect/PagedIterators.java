/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
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

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Utilities for using {@link PagedIterator}s.
 * 
 * @author Adrian Cole
 */
@Beta
public class PagedIterators {

   private static class AdvancingPagedIterator<T> extends PagedIterator<T> {

      private final Function<Object, IterableWithMarker<T>> markerToNext;
      private transient IterableWithMarker<T> current;
      private transient boolean unread = true;

      private AdvancingPagedIterator(IterableWithMarker<T> initial, Function<Object, IterableWithMarker<T>> markerToNext) {
         this.current = checkNotNull(initial, "initial iterable");
         this.markerToNext = checkNotNull(markerToNext, "marker to next iterable");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected IterableWithMarker<T> computeNext() {
         if (unread)
            try {
               return current;
            } finally {
               unread = false;
            }
         else if (nextMarker().isPresent())
            return current = markerToNext.apply(nextMarker().get());
         else
            return endOfData();
      }

      @Override
      public Optional<Object> nextMarker() {
         return current.nextMarker();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(current, unread);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         AdvancingPagedIterator<?> other = AdvancingPagedIterator.class.cast(obj);
         return Objects.equal(this.current, other.current) && Objects.equal(this.unread, other.unread);
      }

      /**
       * {@inheritDoc}
       */
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
    * @return iterable current data which continues if the user iterates beyond the first page
    */
   public static <T> PagedIterator<T> advancing(IterableWithMarker<T> initial,
            Function<Object, IterableWithMarker<T>> markerToNext) {
      if (!initial.nextMarker().isPresent()) {
         return of(initial);
      }
      return new AdvancingPagedIterator<T>(initial, markerToNext);
   }

   /**
    * 
    * @param initial
    *           the initial set current data
    * @return iterable current data which only contains the single element
    */
   public static <T> PagedIterator<T> of(IterableWithMarker<T> initial) {
      return new OnlyElementIterator<T>(initial);
   }

   private static class OnlyElementIterator<T> extends PagedIterator<T> {

      private transient IterableWithMarker<T> onlyElement;
      private transient boolean unread = true;

      private OnlyElementIterator(IterableWithMarker<T> onlyElement) {
         this.onlyElement = checkNotNull(onlyElement, "onlyElement");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected IterableWithMarker<T> computeNext() {
         if (unread)
            try {
               return onlyElement;
            } finally {
               unread = false;
            }
         else
            return endOfData();
      }

      @Override
      public Optional<Object> nextMarker() {
         return onlyElement.nextMarker();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(onlyElement);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         OnlyElementIterator<?> other = OnlyElementIterator.class.cast(obj);
         return Objects.equal(this.onlyElement, other.onlyElement) && Objects.equal(this.unread, other.unread);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return Objects.toStringHelper("").add("onlyElement", onlyElement).add("unread", unread).toString();
      }
   }
}
