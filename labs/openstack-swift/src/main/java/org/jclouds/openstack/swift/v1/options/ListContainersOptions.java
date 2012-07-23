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
package org.jclouds.openstack.swift.v1.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the GET container operation. <h2>
 */
public class ListContainersOptions extends BaseHttpRequestOptions {
   public static final ListContainersOptions NONE = new ListContainersOptions();

   /**
    * Given a string value x, return object names greater in value than the specified marker.
    */
   public ListContainersOptions marker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   public String getMarker() {
      return getFirstQueryOrNull("marker");
   }

   /**
    * For an integer value n, limits the number of results to n values.
    */
   public ListContainersOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public int getLimit() {
      String val = getFirstQueryOrNull("limit");
      return val != null ? Integer.valueOf(val) : 10000;
   }


   public static class Builder {

      /**
       * @see ListContainersOptions#marker(String)
       */
      public static ListContainersOptions marker(String marker) {
         ListContainersOptions options = new ListContainersOptions();
         return options.marker(marker);
      }

      /**
       * @see ListContainersOptions#limit(int)
       */
      public static ListContainersOptions limit(int limit) {
         ListContainersOptions options = new ListContainersOptions();
         return options.limit(limit);
      }

   }
}
