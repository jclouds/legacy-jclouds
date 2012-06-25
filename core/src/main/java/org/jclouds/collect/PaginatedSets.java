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

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.AbstractIterator;

/**
 * Utilities for using {@link PaginatedSet}s.
 * 
 * @author Adrian Cole, Jeremy Whitlock
 */
@Beta
public class PaginatedSets {

   /**
    * 
    * @param initial
    *           the initial set of data
    * @param markerToNext
    *           produces the next set based on the marker
    * 
    * @return iterable of users fitting the criteria
    */
   public static <T> Iterable<T> lazyContinue(final PaginatedSet<T> initial,
            final Function<String, PaginatedSet<T>> markerToNext) {
      if (initial.getNextMarker() == null)
         return initial;
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return new AbstractIterator<T>() {

               private PaginatedSet<T> response = initial;
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
            return "lazyContinue(" + markerToNext.toString() + ")";
         }
      };
   }

}
