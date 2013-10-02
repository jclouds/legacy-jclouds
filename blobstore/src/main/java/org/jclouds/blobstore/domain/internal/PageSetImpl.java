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
package org.jclouds.blobstore.domain.internal;

import java.util.LinkedHashSet;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Iterables;

public class PageSetImpl<T> extends LinkedHashSet<T> implements PageSet<T> {

   protected final String marker;

   public PageSetImpl(Iterable<? extends T> contents, @Nullable String nextMarker) {
      Iterables.addAll(this, contents);
      this.marker = nextMarker;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getNextMarker() {
      return marker;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((marker == null) ? 0 : marker.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      PageSetImpl<?> other = (PageSetImpl<?>) obj;
      if (marker == null) {
         if (other.marker != null)
            return false;
      } else if (!marker.equals(other.marker))
         return false;
      return true;
   }

}
