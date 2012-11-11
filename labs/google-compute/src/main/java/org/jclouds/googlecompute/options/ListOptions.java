/*
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

package org.jclouds.googlecompute.options;

import com.google.common.base.Objects;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.javax.annotation.Nullable;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

/**
 * Allows to optionally specify a filter, max results and a page token for <code>list()</code> REST methods.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations/list"/>
 */
public class ListOptions extends BaseHttpRequestOptions {

   public static final ListOptions NONE = builder().build();

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromListOptions(this);
   }

   public static class Builder {

      private String filter;
      private int maxResults;
      private String nextPageToken;

      /**
       * @see ListOptions#getFilter()
       */
      public Builder filter(String filter) {
         this.filter = filter;
         return this;
      }

      /**
       * @see ListOptions#getMaxResults()
       */
      public Builder maxResults(int maxResults) {
         this.maxResults = maxResults;
         return this;
      }

      /**
       * @see ListOptions#getNextPageToken()
       */
      public Builder nextPageToken(String nextPageToken) {
         this.nextPageToken = nextPageToken;
         return this;
      }

      public ListOptions build() {
         return new ListOptions(filter, maxResults, nextPageToken);
      }

      public Builder fromListOptions(ListOptions in) {
         return this.filter(in.getFilter()).maxResults(in.getMaxResults()).nextPageToken(in.getNextPageToken());
      }
   }

   private final String filter;
   private final int maxResults;
   private final String nextPageToken;

   public ListOptions(String filter, int maxResults, String nextPageToken) {
      this.filter = filter;
      this.maxResults = maxResults;
      this.nextPageToken = nextPageToken;
      if (filter != null)
         super.queryParameters.put("filter", filter);
      if (maxResults != 0)
         super.queryParameters.put("maxResults", maxResults + "");
      if (nextPageToken != null)
         super.queryParameters.put("pageToken", nextPageToken);
   }

   /**
    * Optional. Filter expression for filtering listed resources, in the form filter={expression}. Your {expression}
    * must contain the following:
    * <p/>
    * {@code <field_name> <comparison_string> <literal_string>}
    * <ul>
    * <li>{@code <field_name>}:  The name of the field you want to compare. The field name must be valid for the
    * type of resource being filtered. Only atomic field types are supported (string, number,
    * boolean). Array and object fields are not currently supported.</li>
    * <li>{@code <comparison_string>}: The comparison string, either eq (equals) or ne (not equals).</li>
    * <li>{@code <literal_string>}: The literal string value to filter to. The literal value must be valid
    * for the type of field (string, number, boolean). For string fields, the literal value is interpreted as a
    * regular expression using RE2 syntax. The literal value must match the entire field. For example,
    * when filtering instances, name eq my_instance won't work, but name eq .*my_instance will work.</li>
    * </ul>
    * <p/>
    * For example:
    * <p/>
    * {@code filter=status ne RUNNING}
    * <p/>
    * The above filter returns only results whose status field does not equal RUNNING. You can also enclose your
    * literal string in single, double, or no quotes. For example, all three of the following would be valid
    * expressions:
    * <p/>
    * {@code filter=status ne "RUNNING"}<br/>
    * {@code filter=status ne 'RUNNING'}<br/>
    * {@code filter=status ne RUNNING}<br/>
    * <p/>
    * Complex regular expressions can also be used, like the following:
    * {@code name eq '."my_instance_[0-9]+'}
    *
    * @return the filter
    */
   @Nullable
   public String getFilter() {
      return filter;
   }

   /**
    * @return Maximum count of results to be returned. Maximum and default value is 100. Acceptable values are 0 to
    *         100, inclusive. (Default: 100)
    */
   @Nullable
   public int getMaxResults() {
      return maxResults;
   }

   /**
    * @return Tag returned by a previous list request truncated by maxResults. Used to continue a previous list request.
    */
   @Nullable
   public String getNextPageToken() {
      return nextPageToken;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(filter, maxResults, nextPageToken);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ListOptions that = ListOptions.class.cast(obj);
      return equal(this.filter, that.filter)
              && equal(this.maxResults, that.maxResults)
              && equal(this.nextPageToken, that.nextPageToken);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("filter", filter).add("maxResults", maxResults).add("nextPageToken", nextPageToken);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }


}
