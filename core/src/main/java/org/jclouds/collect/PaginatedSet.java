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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

/**
 * An {@code Set} that can be continued
 * 
 * @author Adrian Cole
 */
@Beta
public class PaginatedSet<T> extends ForwardingSet<T> {

   public static <T> PaginatedSet<T> copyOf(Iterable<T> contents) {
      return new PaginatedSet<T>(contents, null);
   }

   public static <T> PaginatedSet<T> copyOfWithMarker(Iterable<T> contents, String marker) {
      return new PaginatedSet<T>(contents, marker);
   }

   private final Set<T> contents;
   private final String marker;

   protected PaginatedSet(Iterable<T> contents, @Nullable String marker) {
      this.contents = ImmutableSet.<T> copyOf(checkNotNull(contents, "contents"));
      this.marker = marker;
   }

   /**
    * If there is a next marker, then the set is incomplete and you should issue another command to
    * retrieve the rest, setting the option {@code marker} to this value
    * 
    * @return next marker, or null if list is complete
    */
   @Nullable
   public String getNextMarker() {
      return marker;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(contents, marker);
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
      PaginatedSet<?> other = PaginatedSet.class.cast(obj);
      return Objects.equal(this.contents, other.contents) && Objects.equal(this.marker, other.marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("contents", contents).add("marker", marker).toString();
   }

   @Override
   protected Set<T> delegate() {
      return contents;
   }

}
