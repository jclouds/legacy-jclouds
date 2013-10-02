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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Utilities for using {@link IterableWithMarker}s.
 * 
 * @author Adrian Cole, Jeremy Whitlock
 */
@Beta
public class IterableWithMarkers {
   
   @SuppressWarnings("rawtypes")
   public static final IterableWithMarker EMPTY = from(ImmutableSet.of());

   /**
    * Returns a paginated iterable containing the given elements and null marker.
    * 
    * 
    * @throws NullPointerException
    *            if {@code elements} are null
    */
   public static <T> IterableWithMarker<T> from(Iterable<T> elements) {
      return from(elements, null);
   }

   /**
    * Returns a paginated iterable containing the given elements and marker.
    * 
    * 
    * @throws NullPointerException
    *            if {@code elements} are null
    */
   public static <T> IterableWithMarker<T> from(final Iterable<T> elements, @Nullable final Object marker) {
      return new ForwardingIterableWithMarker<T>() {

         @Override
         protected IterableWithMarker<T> delegate() {
            return new ForwardedIterableWithMarker<T>(elements, marker);
         }

      };
   }

   private static final class ForwardedIterableWithMarker<T> extends IterableWithMarker<T> {
      private final Iterable<T> elements;
      private final Optional<Object> marker;

      private ForwardedIterableWithMarker(Iterable<T> elements, @Nullable Object marker) {
         this.elements = checkNotNull(elements, "elements");
         this.marker = Optional.fromNullable(marker);
      }

      @Override
      public Iterator<T> iterator() {
         return elements.iterator();
      }

      @Override
      public Optional<Object> nextMarker() {
         return marker;
      }
   }

   private abstract static class ForwardingIterableWithMarker<T> extends IterableWithMarker<T> {

      protected abstract IterableWithMarker<T> delegate();

      @Override
      public Iterator<T> iterator() {
         return delegate().iterator();
      }

      @Override
      public Optional<Object> nextMarker() {
         return delegate().nextMarker();
      }
   }
}
