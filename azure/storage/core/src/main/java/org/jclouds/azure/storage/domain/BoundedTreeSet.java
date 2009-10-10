/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.azure.storage.domain;

import java.util.SortedSet;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BoundedTreeSet<T> extends org.jclouds.rest.internal.BoundedTreeSet<T> implements
         BoundedSortedSet<T> {
   /** The serialVersionUID */
   private static final long serialVersionUID = -4475709781001190244L;

   protected final String nextMarker;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((nextMarker == null) ? 0 : nextMarker.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      BoundedTreeSet<?> other = (BoundedTreeSet<?>) obj;
      if (nextMarker == null) {
         if (other.nextMarker != null)
            return false;
      } else if (!nextMarker.equals(other.nextMarker))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ArrayBoundedList [nextMarker=" + nextMarker + ", marker=" + marker + ", maxResults="
               + maxResults + ", prefix=" + prefix + "]";
   }

   public BoundedTreeSet(SortedSet<T> contents, String prefix, String marker, int maxResults,
            String nextMarker) {
      super(contents, prefix, marker, maxResults);
      this.nextMarker = nextMarker;
   }

   public String getNextMarker() {
      return nextMarker;
   }

}
