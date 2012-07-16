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

import com.google.common.annotations.Beta;

/**
 * Utilities for using {@link PagedIterable}s.
 * 
 * @author Adrian Cole
 */
@Beta
public class PagedIterables {

   /**
    * 
    * @param iterator
    *           how to advance pages
    * 
    * @return iterable current data which continues if the user iterates beyond the first page
    */
   public static <T> PagedIterable<T> create(final PagedIterator<T> iterator) {
      return new PagedIterable<T>() {

         @Override
         public PagedIterator<T> iterator() {
            return iterator;
         }

      };
   }

}
