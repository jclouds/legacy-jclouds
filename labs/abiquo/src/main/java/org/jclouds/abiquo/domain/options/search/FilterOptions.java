/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.options.search;

import org.jclouds.abiquo.domain.options.search.reference.OrderBy;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Available options to filter and pagination methods.
 * 
 * @author Francesc Montserrat
 */
public class FilterOptions extends BaseHttpRequestOptions {
   @Override
   protected Object clone() throws CloneNotSupportedException {
      FilterOptions options = new FilterOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static FilterOptionsBuilder builder() {
      return new FilterOptionsBuilder();
   }

   public static class FilterOptionsBuilder extends BaseFilterOptionsBuilder<FilterOptionsBuilder> {
      public FilterOptions build() {
         FilterOptions options = new FilterOptions();
         return super.addFilterOptions(options);
      }
   }

   @SuppressWarnings("unchecked")
   public static class BaseFilterOptionsBuilder<T extends BaseFilterOptionsBuilder<T>> {
      protected Integer startWith;

      protected Integer limit;

      protected OrderBy by;

      protected String has;

      protected Boolean asc;

      public T startWith(final int startWith) {
         this.startWith = startWith;
         return (T) this;
      }

      public T has(final String has) {
         this.has = has;
         return (T) this;
      }

      public T limit(final int limit) {
         this.limit = limit;
         return (T) this;
      }

      public T orderBy(final OrderBy by) {
         this.by = by;
         return (T) this;
      }

      public T ascendant(final boolean asc) {
         this.asc = asc;
         return (T) this;
      }

      public T descendant(final boolean desc) {
         this.asc = !desc;
         return (T) this;
      }

      public T disablePagination() {
         this.limit = 0;
         return (T) this;
      }

      protected <O extends BaseHttpRequestOptions> O addFilterOptions(final O options) {
         Multimap<String, String> queryParameters = options.buildQueryParameters();

         if (startWith != null) {
            queryParameters.put("startwith", startWith.toString());
         }

         if (limit != null) {
            queryParameters.put("limit", limit.toString());
         }

         if (has != null) {
            queryParameters.put("has", has);
         }

         if (by != null) {
            queryParameters.put("by", by.getValue());
         }

         if (asc != null) {
            queryParameters.put("asc", asc.toString());
         }

         return options;
      }
   }
}
