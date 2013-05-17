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
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

/**
 * continues to supply iterables until the last was empty
 * 
 * @param <E>
 */
@Beta
public class AdvanceUntilEmptyIterable<E> extends FluentIterable<FluentIterable<E>> {

   public static <E> AdvanceUntilEmptyIterable<E> create(Supplier<FluentIterable<E>> nextIterable){
      return new AdvanceUntilEmptyIterable<E>(nextIterable);
   }
   
   private final AdvanceUntilEmptyIterator<E> iterator;

   protected AdvanceUntilEmptyIterable(Supplier<FluentIterable<E>> nextIterable) {
      this.iterator = new AdvanceUntilEmptyIterator<E>(checkNotNull(nextIterable, "next iterable"));
   }

   @Override
   public Iterator<FluentIterable<E>> iterator() {
      return iterator;
   }

   private static class AdvanceUntilEmptyIterator<E> extends AbstractIterator<FluentIterable<E>> {

      private final Supplier<FluentIterable<E>> nextIterable;
      private transient FluentIterable<E> current;
      private transient boolean unread = true;

      private AdvanceUntilEmptyIterator(Supplier<FluentIterable<E>> nextIterable) {
         this.nextIterable = checkNotNull(nextIterable, "next iterable");
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected FluentIterable<E> computeNext() {
         if (unread)
            try {
               return current = nextIterable.get();
            } finally {
               unread = false;
            }
         else if (current.size() > 0)
            return current = nextIterable.get();
         else
            return endOfData();
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
         AdvanceUntilEmptyIterator<?> other = AdvanceUntilEmptyIterator.class.cast(obj);
         return Objects.equal(this.current, other.current) && Objects.equal(this.unread, other.unread);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return Objects.toStringHelper("").omitNullValues().add("current", current).add("unread", unread).toString();
      }
   }
   
   /**
    * Combines all the pages into a single unmodifiable iterable. ex.
    * 
    * <pre>
    * FluentIterable<StorageMetadata> blobs = blobstore.list(...).concat();
    * for (StorageMetadata blob : blobs) {
    *     process(blob);
    * }
    * </pre>
    * 
    * @see Iterators#concat
    */
   public FluentIterable<E> concat() {
      final Iterator<FluentIterable<E>> iterator = iterator();
      final UnmodifiableIterator<Iterator<E>> unmodifiable = new UnmodifiableIterator<Iterator<E>>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public Iterator<E> next() {
            return iterator.next().iterator();
         }
      };
      return new FluentIterable<E>() {
         @Override
         public Iterator<E> iterator() {
            return Iterators.concat(unmodifiable);
         }
      };
   }

}
