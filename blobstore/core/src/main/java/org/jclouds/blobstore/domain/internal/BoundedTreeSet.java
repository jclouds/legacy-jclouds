/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.domain.internal;

import java.util.TreeSet;

import org.jclouds.blobstore.domain.BoundedSortedSet;

import com.google.common.collect.Iterables;

public class BoundedTreeSet<T> extends TreeSet<T> implements BoundedSortedSet<T> {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7133632087734650835L;
   protected final String path;
   protected final String marker;
   protected final Integer maxResults;
   protected final boolean truncated;

   public BoundedTreeSet(Iterable<T> contents, String path, String marker, Integer maxResults,
            boolean isTruncated) {
      Iterables.addAll(this, contents);
      this.path = path;
      this.marker = marker;
      this.maxResults = maxResults;
      this.truncated = isTruncated;
   }

   public String getPath() {
      return path;
   }

   public String getMarker() {
      return marker;
   }

   public int getMaxResults() {
      return maxResults;
   }

   public boolean isTruncated() {
      return truncated;
   }

}