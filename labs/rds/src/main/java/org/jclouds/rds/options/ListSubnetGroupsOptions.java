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
package org.jclouds.rds.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

/**
 * Options used to list available instances.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_DescribeSubnetGroups.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class ListSubnetGroupsOptions extends BaseHttpRequestOptions implements Cloneable {

   private Object marker;

   /**
    * Use this parameter only when paginating results, and only in a subsequent request after you've
    * received a response where the results are truncated. Set it to the value of the Marker element
    * in the response you just received.
    */
   public ListSubnetGroupsOptions afterMarker(Object marker) {
      this.marker = marker;
      return this;
   }

   public static class Builder {

      /**
       * @see ListSubnetGroupsOptions#getMarker()
       */
      public static ListSubnetGroupsOptions afterMarker(Object marker) {
         return new ListSubnetGroupsOptions().afterMarker(marker);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (marker != null)
         params.put("Marker", marker.toString());
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(marker);
   }

   @Override
   public ListSubnetGroupsOptions clone() {
      return new ListSubnetGroupsOptions().afterMarker(marker);
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
      ListSubnetGroupsOptions other = ListSubnetGroupsOptions.class.cast(obj);
      return Objects.equal(this.marker, other.marker);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("marker", marker).toString();
   }
}
