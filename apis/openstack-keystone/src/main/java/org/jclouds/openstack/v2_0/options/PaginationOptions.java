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
package org.jclouds.openstack.v2_0.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/Paginated_Collections-d1e664.html"
 *      />
 * @author Adrian Cole
 */
public class PaginationOptions extends BaseHttpRequestOptions {
   /**
    * Only return objects changed since this time.
    */
   public PaginationOptions changesSince(Date ifModifiedSince) {
      this.queryParameters.put("changes-since", checkNotNull(ifModifiedSince, "ifModifiedSince").getTime() / 1000 + "");
      return this;
   }

   /**
    * The marker parameter is the ID of the last item in the previous list. Items are sorted by
    * create time in descending order. When a create time is not available they are sorted by ID.
    */
   public PaginationOptions marker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   /**
    * To reduce load on the service, list operations will return a maximum of 1,000 items at a time.
    * To navigate the collection, the parameters limit and offset can be set in the URI
    * (e.g.?limit=0&offset=0). If an offset is given beyond the end of a list an empty list will be
    * returned.
    * <p/>
    * Note that list operations never return itemNotFound (404) faults.
    */
   public PaginationOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public static class Builder {

      /**
       * @see PaginationOptions#marker(String)
       */
      public static PaginationOptions marker(String marker) {
         PaginationOptions options = new PaginationOptions();
         return options.marker(marker);
      }

      /**
       * @see PaginationOptions#limit
       */
      public static PaginationOptions limit(int limit) {
         PaginationOptions options = new PaginationOptions();
         return options.limit(limit);
      }

      /**
       * @see PaginationOptions#changesSince(Date)
       */
      public static PaginationOptions changesSince(Date since) {
         PaginationOptions options = new PaginationOptions();
         return options.changesSince(since);
      }

   }
}
