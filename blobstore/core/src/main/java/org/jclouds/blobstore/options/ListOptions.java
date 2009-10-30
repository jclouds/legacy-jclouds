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
package org.jclouds.blobstore.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains options supported in the list container operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListOptions object is to statically import
 * ListOptions.* and invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.blobstore.options.ListOptions.Builder.*
 * <p/>
 * BlobStore connection = // get connection
 * Future<SortedSet<ResourceMetadata>> list = connection.list("container",underPath("home/users").maxResults(1000));
 * <code>
 * 
 * @author Adrian Cole
 */
public class ListOptions {

   private Integer maxKeys;
   private String path;
   private String marker;
   private boolean recursive;

   public Integer getMaxResults() {
      return maxKeys;
   }

   public String getPath() {
      return path;
   }

   public String getMarker() {
      return marker;
   }

   public boolean isRecursive() {
      return recursive;
   }

   /**
    * Returns a pseudo-directory listing.
    * 
    */
   public ListOptions underPath(String path) {
      checkArgument(!recursive, "path and recursive combination currently not supported");
      this.path = checkNotNull(path, "path");
      return this;
   }

   /**
    * Place to continue a listing at. This must be the value returned from the last list object, as
    * not all blobstores use lexigraphic lists.
    */
   public ListOptions afterMarker(String marker) {
      this.marker = checkNotNull(marker, "marker");
      return this;
   }

   /**
    * The maximum number of values you'd like to see in the response body. The server might return
    * fewer than this many values, but will not return more.
    */
   public ListOptions maxResults(int maxKeys) {
      checkArgument(maxKeys >= 0, "maxKeys must be >= 0");
      this.maxKeys = maxKeys;
      return this;
   }

   /**
    * return a listing of all objects inside the store, recursively.
    */
   public ListOptions recursive() {
//      checkArgument(path == null, "path and recursive combination currently not supported");
      this.recursive = true;
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#underPath(String)
       */
      public static ListOptions underPath(String path) {
         ListOptions options = new ListOptions();
         return options.underPath(path);
      }

      /**
       * @see ListOptions#afterMarker(String)
       */
      public static ListOptions afterMarker(String marker) {
         ListOptions options = new ListOptions();
         return options.afterMarker(marker);
      }

      /**
       * @see ListOptions#maxResults(int)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.maxResults(maxKeys);
      }

      /**
       * @see ListOptions#recursive()
       */
      public static ListOptions recursive() {
         ListOptions options = new ListOptions();
         return options.recursive();
      }

   }
}
