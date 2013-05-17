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
package org.jclouds.rackspace.cloudidentity.v2_0.options;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @author Everett Toews
 */
public class PaginationOptions extends BaseHttpRequestOptions {
   public PaginationOptions queryParameters(Multimap<String, String> queryParams) {
      checkNotNull(queryParams, "queryParams");
      queryParameters.putAll(queryParams);
      return this;
   }
   
   /**
    * Offset is the starting point for the return data. Offset must be a multiple of the limit (or zero).
    */
   public PaginationOptions offset(int offset) {
      checkState(offset >= 0, "offset must be >= 0");
      queryParameters.put("offset", String.valueOf(offset));
      return this;
   }

   /**
    * Limit is the restriction on the maximum number of items for that type that can be returned.
    * <p/>
    * Note that list operations never return itemNotFound (404) faults.
    */
   public PaginationOptions limit(int limit) {
      checkState(limit >= 0, "limit must be >= 0");
      checkState(limit <= 10000, "limit must be <= 10000");
      queryParameters.put("limit", Integer.toString(limit));
      return this;
   }
   
   /**
    * Name is a filter on the result set.
    */
   public PaginationOptions name(String nameFilter) {
      queryParameters.put("name", nameFilter);
      return this;
   }

   public static class Builder {
      public static PaginationOptions queryParameters(Multimap<String, String> queryParams) {
         PaginationOptions options = new PaginationOptions();
         return options.queryParameters(queryParams);
      }
      
      /**
       * @see PaginationOptions#offset(int)
       */
      public static PaginationOptions offset(int offset) {
         PaginationOptions options = new PaginationOptions();
         return options.offset(offset);
      }

      /**
       * @see PaginationOptions#limit(int)
       */
      public static PaginationOptions limit(int limit) {
         PaginationOptions options = new PaginationOptions();
         return options.limit(limit);
      }

      /**
       * @see PaginationOptions#name(String)
       */
      public static PaginationOptions name(String name) {
         PaginationOptions options = new PaginationOptions();
         return options.name(name);
      }
   }
}
