/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author Adrian Cole
 */
public class Lists2 {

   /**
    * Like Ordering, but handle the case where there are multiple valid maximums
    */
   @SuppressWarnings("unchecked")
   public static <T, E extends T> List<E> multiMax(Comparator<T> ordering, Iterable<E> iterable) {
      Iterator<E> iterator = iterable.iterator();
      List<E> maxes = newArrayList(iterator.next());
      E maxSoFar = maxes.get(0);
      while (iterator.hasNext()) {
         E current = iterator.next();
         int comparison = ordering.compare(maxSoFar, current);
         if (comparison == 0) {
            maxes.add(current);
         } else if (comparison < 0) {
            maxes = newArrayList(current);
            maxSoFar = current;
         }
      }
      return maxes;
   }
 
}
