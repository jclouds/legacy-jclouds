/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.domain;

import java.util.LinkedHashSet;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class SearchResult<T> extends LinkedHashSet<T> {
   private long start;

   SearchResult() {
   }

   public SearchResult(long start, Iterable<T> results) {
      this.start = start;
      Iterables.addAll(this, results);
   }

   private static final long serialVersionUID = 4000610660948065287L;

   /**
    * 
    * @return the result position this started from from
    */
   long getStart() {
      return start;
   }

}