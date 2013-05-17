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

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Extends {@link FluentIterable} allowing you to lazily advance through
 * sequence of pages in a result set. Typically used in APIs that return only a
 * certain number of records at a time.
 * </p>
 * Simplest usage is to employ the {@link #concat} convenience function, and iterate.
 * </p>
 * <pre>
 * FluentIterable<? extends Image> images = imageApi.listInDetail().concat();
 *
 * for (Image image: images) {
 *    System.out.println(image);
 * }
 * </pre>
 * </p> 
 * Another usage is to employ the {@link #concat} convenience function, and one
 * of the methods from {@link FluentIterable}.
 * </p>
 * <pre>
 *    Optional<? extends Image> image = imageApi.listInDetail().concat().firstMatch(isInterestingImage());
 *    System.out.println(image.orNull());
 * ...
 * private static Predicate<Image> isInterestingImage() {
 *    return new Predicate<Image>() {
 *       {@literal @}Override
 *       public boolean apply(Image image) {
 *          return image.getName().startsWith("Arch");
 *       }
 *    };
 * }
 * </pre>
 * 
 * @author Adrian Cole
 */
@Beta
public abstract class PagedIterable<E> extends FluentIterable<IterableWithMarker<E>> {

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
      final Iterator<IterableWithMarker<E>> iterator = iterator();
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
