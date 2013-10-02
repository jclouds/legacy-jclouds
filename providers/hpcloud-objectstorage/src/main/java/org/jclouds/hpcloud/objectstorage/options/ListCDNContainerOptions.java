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
package org.jclouds.hpcloud.objectstorage.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;


/**
 * Contains options supported in the REST API for the GET CDN containers operation.
 */
public class ListCDNContainerOptions extends BaseHttpRequestOptions {
   public static final ListCDNContainerOptions NONE = new ListCDNContainerOptions();

   public ListCDNContainerOptions enabledOnly() {
      queryParameters.put("enabled_only", "true");
      return this;
   }

   /**
    * Indicates where to begin listing the identity's containers. The list will only include 
    * containers whose names occur lexicographically after the marker. This is convenient for 
    * pagination: To get the next page of results use the last container name of the current 
    * page as the marker.
    */
   public ListCDNContainerOptions afterMarker(String marker) {
      queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }


   /**
    * The maximum number of containers that will be included in the response body. 
    * The server might return fewer than this many containers, but will not return more.
    */
   public ListCDNContainerOptions maxResults(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }

   public static class Builder {
      
      public static ListCDNContainerOptions enabledOnly() {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.enabledOnly();
      }

      /**
       * @see org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions#afterMarker(String)
       */
      public static ListCDNContainerOptions afterMarker(String marker) {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.afterMarker(marker);
      }

      /**
       * @see org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions#limit(int)
       */
      public static ListCDNContainerOptions maxResults(int limit) {
         ListCDNContainerOptions options = new ListCDNContainerOptions();
         return options.maxResults(limit);
      }

   }

}
