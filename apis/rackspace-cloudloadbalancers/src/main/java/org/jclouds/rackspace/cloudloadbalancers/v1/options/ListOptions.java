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
package org.jclouds.rackspace.cloudloadbalancers.v1.options;

import static com.google.common.base.Preconditions.checkArgument;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * To reduce load on the service, list operations will return a maximum of 100 items at a time. To
 * navigate the collection, the limit and marker parameters (for example, ?limit=50&marker=1 ) can
 * be set in the URI. If a marker beyond the end of a list is given, an empty list is returned. Note
 * that list operations never return 404 (itemNotFound) faults.
 * 
 * @see <a
 *      href="http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch03s06.html"
 *      />
 * @author Adrian Cole
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final ListOptions NONE = new ListOptions();

   /**
    * Indicates where to begin listing, if the previous list was larger than the limit.
    */
   public ListOptions marker(String marker) {
      checkArgument(marker != null, "marker cannot be null");
      queryParameters.replaceValues("marker", ImmutableSet.of(marker));
      return this;
   }

   /**
    * To reduce load on the service, list operations will return a maximum of 100 items at a time.
    * <p/>
    * Note that list operations never return itemNotFound (404) faults.
    */
   public ListOptions limit(int limit) {
      checkArgument(limit >= 0, "limit must be >= 0");
      checkArgument(limit <= 10000, "limit must be <= 10000");
      queryParameters.replaceValues("limit", ImmutableSet.of(limit + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListOptions#marker(marker)
       */
      public static ListOptions marker(String marker) {
         ListOptions options = new ListOptions();
         return options.marker(marker);
      }

      /**
       * @see ListOptions#limit(long)
       */
      public static ListOptions limit(int limit) {
         ListOptions options = new ListOptions();
         return options.limit(limit);
      }

   }
}
