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
package org.jclouds.azure.storage.options;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control paginated results (aka list commands).
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class ListOptions extends BaseHttpRequestOptions {
   public static final ListOptions NONE = new ListOptions();

   /**
    * Include this parameter to specify that the container's metadata be returned as part of the
    * response body.
    * 
    * Note that metadata requested with this parameter must be stored in accordance with the naming
    * restrictions imposed by the 2009-09-19 version of the Blob service. Beginning with this
    * version, all metadata names must adhere to the naming conventions for C# identifiers.
    */
   public ListOptions includeMetadata() {
      this.queryParameters.replaceValues("include", ImmutableSet.of("metadata"));
      return this;
   }

   public boolean getIncludeMetadata() {
      return getFirstQueryOrNull("include").equals("metadata");
   }

   /**
    * Filters the results to return only objects whose name begins with the specified prefix.
    */
   public ListOptions prefix(String prefix) {
      this.queryParameters.put("prefix", checkNotNull(prefix, "prefix"));
      return this;
   }

   public String getPrefix() {
      return getFirstQueryOrNull("prefix");
   }

   /**
    * A string value that identifies the portion of the list to be returned with the next list
    * operation. The operation returns a marker value within the response body if the list returned
    * was not complete. The marker value may then be used in a subsequent call to request the next
    * set of list items.
    * <p/>
    * The marker value is opaque to the client.
    */
   public ListOptions marker(String marker) {
      this.queryParameters.put("marker", checkNotNull(marker, "marker"));
      return this;
   }

   public String getMarker() {
      return getFirstQueryOrNull("marker");
   }

   /**
    * Specifies the maximum number of containers to return. If maxresults is not specified, the
    * server will return up to 5,000 items. If the parameter is set to a value greater than 5,000,
    * the server will return a Bad Request (400) error
    */
   public ListOptions maxResults(int maxresults) {
      checkState(maxresults >= 0, "maxresults must be >= 0");
      checkState(maxresults <= 10000, "maxresults must be <= 5000");
      queryParameters.put("maxresults", Integer.toString(maxresults));
      return this;
   }

   public Integer getMaxResults() {
      String maxresults = getFirstQueryOrNull("maxresults");
      return (maxresults != null) ? Integer.valueOf(maxresults) : null;
   }

   public static class Builder {
      /**
       * @see ListOptions#includeMetadata()
       */
      public static ListOptions includeMetadata() {
         ListOptions options = new ListOptions();
         return options.includeMetadata();
      }

      /**
       * @see ListOptions#prefix(String)
       */
      public static ListOptions prefix(String prefix) {
         ListOptions options = new ListOptions();
         return options.prefix(prefix);
      }

      /**
       * @see ListOptions#marker(String)
       */
      public static ListOptions marker(String marker) {
         ListOptions options = new ListOptions();
         return options.marker(marker);
      }

      /**
       * @see ListOptions#maxResults(long)
       */
      public static ListOptions maxResults(int maxKeys) {
         ListOptions options = new ListOptions();
         return options.maxResults(maxKeys);
      }

   }
}
