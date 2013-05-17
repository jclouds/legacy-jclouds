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
package org.jclouds.openstack.swift.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the GET container operation. <h2>
 */
public class ListContainerOptions extends BaseHttpRequestOptions {
   public static final ListContainerOptions NONE = new ListContainerOptions();

   /**
    * Indicates where to begin listing the account's containers. The list will only include
    * containers whose names occur lexicographically after the marker. This is convenient for
    * pagination: To get the next page of results use the last container name of the current page as
    * the marker.
    */
   public ListContainerOptions afterMarker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   public String getMarker() {
      return getFirstQueryOrNull("marker");
   }

   /**
    * The maximum number of containers that will be included in the response body. The server might
    * return fewer than this many containers, but will not return more.
    */
   public ListContainerOptions maxResults(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public int getMaxResults() {
      String val = getFirstQueryOrNull("limit");
      return val != null ? Integer.valueOf(val) : 10000;
   }

   /**
    * For a string value X, causes the results to be limited to Object names beginning with the
    * substring X.
    * 
    */
   public ListContainerOptions withPrefix(String prefix) {
      queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
      return this;
   }

   public String getPrefix() {
      return getFirstQueryOrNull("prefix");
   }

   /**
    * For a string value X, return the Object names nested in the pseudo path.
    * <p/>
    * Users will be able to simulate a hierarchical structure in Cloud Files by following a few
    * guidelines. Object names must contain the forward slash character / as a path element
    * separator and also create directory marker Objects, then they will be able to traverse this
    * nested structure with the new path query parameter.
    * <p/>
    * To take advantage of this feature, the directory marker Objects must also be created to
    * represent the appropriate directories. The following additional Objects need to be created. A
    * good convention would be to create these as zero or one byte files with a Content-Type of
    * application/directory
    */
   public ListContainerOptions underPath(String path) {
      queryParameters.put("path", checkNotNull(path, "path"));
      return this;
   }

   public String getPath() {
      return getFirstQueryOrNull("path");
   }

   public static class Builder {

      /**
       * @see ListContainerOptions#afterMarker(String)
       */
      public static ListContainerOptions afterMarker(String marker) {
         ListContainerOptions options = new ListContainerOptions();
         return options.afterMarker(marker);
      }

      /**
       * @see ListContainerOptions#limit(int)
       */
      public static ListContainerOptions maxResults(int limit) {
         ListContainerOptions options = new ListContainerOptions();
         return options.maxResults(limit);
      }

      /**
       * @see ListContainerOptions#withPrefix(String)
       */
      public static ListContainerOptions withPrefix(String prefix) {
         ListContainerOptions options = new ListContainerOptions();
         return options.withPrefix(prefix);
      }

      /**
       * @see ListContainerOptions#withPath(String)
       */
      public static ListContainerOptions underPath(String path) {
         ListContainerOptions options = new ListContainerOptions();
         return options.underPath(path);
      }

   }
}
