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
package org.jclouds.iam.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

/**
 * Options used to list available users.
 * 
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_ListUsers.html" />
 * 
 * @author Adrian Cole
 */
public class ListUsersOptions extends BaseHttpRequestOptions implements Cloneable {

   private Integer maxItems;
   private String pathPrefix;
   private Object afterMarker;

   /**
    * Use this parameter only when paginating results, and only in a subsequent request after you've
    * received a response where the results are truncated. Set it to the value of the Marker element
    * in the response you just received.
    */
   public ListUsersOptions afterMarker(Object afterMarker) {
      this.afterMarker = afterMarker;
      return this;
   }

   /**
    * Use this parameter only when paginating results to indicate the maximum number of user names
    * you want in the response. If there are additional user names beyond the maximum you specify,
    * the IsTruncated response element is true.
    */
   public ListUsersOptions maxItems(Integer maxItems) {
      this.maxItems = maxItems;
      return this;
   }

   /**
    * The path prefix for filtering the results. For example: /division_abc/subdivision_xyz/, which
    * would get all user names whose path starts with /division_abc/subdivision_xyz/.
    * <p/>
    * This parameter is optional. If it is not included, it defaults to a slash (/), listing all
    * user names.
    */
   public ListUsersOptions pathPrefix(String pathPrefix) {
      this.pathPrefix = pathPrefix;
      return this;
   }

   public static class Builder {

      /**
       * @see ListUsersOptions#afterMarker
       */
      public static ListUsersOptions afterMarker(Object afterMarker) {
         return new ListUsersOptions().afterMarker(afterMarker);
      }

      /**
       * @see ListUsersOptions#maxItems
       */
      public static ListUsersOptions maxItems(Integer maxItems) {
         return new ListUsersOptions().maxItems(maxItems);
      }

      /**
       * @see ListUsersOptions#pathPrefix
       */
      public static ListUsersOptions pathPrefix(String pathPrefix) {
         return new ListUsersOptions().pathPrefix(pathPrefix);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (afterMarker != null)
         params.put("Marker", afterMarker.toString());
      if (maxItems != null)
         params.put("MaxItems", maxItems.toString());
      if (pathPrefix != null)
         params.put("PathPrefix", pathPrefix);
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(afterMarker, maxItems, pathPrefix);
   }

   @Override
   public ListUsersOptions clone() {
      return new ListUsersOptions().afterMarker(afterMarker).maxItems(maxItems).pathPrefix(pathPrefix);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ListUsersOptions other = ListUsersOptions.class.cast(obj);
      return Objects.equal(this.afterMarker, other.afterMarker) && Objects.equal(this.maxItems, other.maxItems)
               && Objects.equal(this.pathPrefix, other.pathPrefix);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("afterMarker", afterMarker).add("maxItems", maxItems).add(
               "pathPrefix", pathPrefix).toString();
   }
}
