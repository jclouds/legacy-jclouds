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
package org.jclouds.atmos.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @author Adrian Cole
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final ListOptions NONE = new ListOptions();

   /**
    * specifies the position to resume listing
    * <p/>
    * note this is an opaque value and should not be interpreted.
    */
   public ListOptions token(String token) {
      this.headers.put("x-emc-token", checkNotNull(token, "x-emc-token"));
      return this;
   }

   public String getToken() {
      return getFirstHeaderOrNull("x-emc-token");
   }

   /**
    * the maximum number of items that should be returned. If this is not specified, there is no
    * limit.
    */
   public ListOptions limit(int maxresults) {
      checkState(maxresults >= 0, "maxresults must be >= 0");
      checkState(maxresults <= 10000, "maxresults must be <= 10000");
      headers.put("x-emc-limit", Integer.toString(maxresults));
      return this;
   }

   /**
    * the maximum number of items that should be returned. If this is not specified, there is no
    * limit.
    */
   public ListOptions includeMeta() {
      headers.put("x-emc-include-meta", Integer.toString(1));
      return this;
   }

   public boolean metaIncluded() {
      String meta = getFirstHeaderOrNull("x-emc-include-meta");
      return (meta != null) ? meta.equals("1") : false;
   }

   public Integer getLimit() {
      String maxresults = getFirstHeaderOrNull("x-emc-limit");
      return (maxresults != null) ? Integer.valueOf(maxresults) : null;
   }

   public static class Builder {

      /**
       * @see ListOptions#token(String)
       */
      public static ListOptions token(String token) {
         ListOptions options = new ListOptions();
         return options.token(token);
      }

      /**
       * @see ListOptions#includeMeta()
       */
      public static ListOptions includeMeta() {
         ListOptions options = new ListOptions();
         return options.includeMeta();
      }

      /**
       * @see ListOptions#limit(int)
       */
      public static ListOptions limit(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.limit(maxKeys);
      }

   }
}
