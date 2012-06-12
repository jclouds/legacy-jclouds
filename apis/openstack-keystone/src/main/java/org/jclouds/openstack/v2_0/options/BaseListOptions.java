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
package org.jclouds.openstack.v2_0.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
public class BaseListOptions extends BaseHttpRequestOptions {
   public static final BaseListOptions NONE = new BaseListOptions();

   /**
    * Only return objects changed since this time.
    */
   public BaseListOptions changesSince(Date ifModifiedSince) {
      this.queryParameters.put("changes-since", checkNotNull(ifModifiedSince, "ifModifiedSince")
               .getTime()
               / 1000 + "");
      return this;
   }

   /**
    * Indicates where to begin listing. The list will only include objects that occur after the
    * offset. This is convenient for pagination: To get the next page of results use the last result
    * number of the current page + current page offset as the offset.
    */
   public BaseListOptions startAt(long offset) {
      checkState(offset >= 0, "offset must be >= 0");
      queryParameters.put("offset", Long.toString(checkNotNull(offset, "offset")));
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
   public BaseListOptions maxResults(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public static class Builder {

      /**
       * @see BaseListOptions#startAt(long)
       */
      public static BaseListOptions startAt(long prefix) {
         BaseListOptions options = new BaseListOptions();
         return options.startAt(prefix);
      }

      /**
       * @see BaseListOptions#maxResults
       */
      public static BaseListOptions maxResults(int maxKeys) {
         BaseListOptions options = new BaseListOptions();
         return options.maxResults(maxKeys);
      }

      /**
       * @see BaseListOptions#changesSince(Date)
       */
      public static BaseListOptions changesSince(Date since) {
         BaseListOptions options = new BaseListOptions();
         return options.changesSince(since);
      }

   }
}
