/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy elements the License at
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

import java.util.Iterator;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ForwardingObject;

/**
 * Utilities for using {@link PaginatedIterable}s.
 * 
 * @author Adrian Cole, Jeremy Whitlock
 */
@Beta
public class PaginatedIterables {

   /**
    * 
    * @param initial
    *           the initial set elements data
    * @param markerToNext
    *           produces the next set based on the marker
    * 
    * @return iterable elements data which continues if the user iterates beyond the first page
    */
   public static <T> Iterable<T> lazyContinue(final PaginatedIterable<T> initial,
            final Function<Object, PaginatedIterable<T>> markerToNext) {
      if (initial.getNextMarker() == null)
         return initial;
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return new AbstractIterator<T>() {

               private PaginatedIterable<T> response = initial;
               private Iterator<T> iterator = response.iterator();

               /**
                * {@inheritDoc}
                */
               @Override
               protected T computeNext() {
                  while (true) {
                     if (iterator == null) {
                        response = markerToNext.apply(response.getNextMarker());
                        iterator = response.iterator();
                     }
                     if (iterator.hasNext()) {
                        return iterator.next();
                     }
                     if (response.getNextMarker() == null) {
                        return endOfData();
                     }
                     iterator = null;
                  }
               }

            };
         }

         @Override
         public String toString() {
            return "lazyContinue(" + markerToNext + ")";
         }
      };
   }

   /**
    * Returns a paginated iterable containing the given elements and null marker.
    * 
    * 
    * @throws NullPointerException
    *            if {@code elements} are null
    */
   public static <T> PaginatedIterable<T> forward(Iterable<T> elements) {
      return forwardWithMarker(elements, null);
   }

   /**
    * Returns a paginated iterable containing the given elements and marker.
    * 
    * 
    * @throws NullPointerException
    *            if {@code elements} are null
    */
   public static <T> PaginatedIterable<T> forwardWithMarker(final Iterable<T> elements, @Nullable final Object marker) {
      return new ForwardingPaginatedIterable<T>() {

         @Override
         protected PaginatedIterable<T> delegate() {
            return new ForwardedPaginatedIterable<T>(elements, marker);
         }

      };
   }

   private static final class ForwardedPaginatedIterable<T> extends ForwardingObject implements PaginatedIterable<T> {
      private final Iterable<T> elements;
      private final Object marker;

      @Override
      protected Iterable<T> delegate() {
         return elements;
      }

      private ForwardedPaginatedIterable(Iterable<T> elements, @Nullable Object marker) {
         this.elements = checkNotNull(elements, "elements");
         ;
         this.marker = marker;
      }

      @Override
      public Iterator<T> iterator() {
         return delegate().iterator();
      }

      @Override
      public Object getNextMarker() {
         return marker;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(elements, marker);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ForwardedPaginatedIterable<?> other = ForwardedPaginatedIterable.class.cast(obj);
         return Objects.equal(this.elements, other.elements) && Objects.equal(this.marker, other.marker);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return Objects.toStringHelper("").add("elements", elements).add("marker", marker).toString();
      }

   }

}
