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
package org.jclouds.route53.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

/**
 * Options used to list available zones.
 * 
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_ListHostedZones.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class ListZonesOptions extends BaseHttpRequestOptions implements Cloneable {

   private Integer maxItems;
   private Object afterMarker;

   /**
    * corresponds to {@link org.jclouds.collect.IterableWithMarker#nextMarker}
    */
   public ListZonesOptions afterMarker(Object afterMarker) {
      this.afterMarker = afterMarker;
      return this;
   }

   /**
    * Use this parameter only when paginating results to indicate the maximum
    * number of zone names you want in the response. If there are
    * additional zone names beyond the maximum you specify, the
    * IsTruncated response element is true.
    */
   public ListZonesOptions maxItems(Integer maxItems) {
      this.maxItems = maxItems;
      return this;
   }

   public static class Builder {

      /**
       * @see ListZonesOptions#afterMarker
       */
      public static ListZonesOptions afterMarker(Object afterMarker) {
         return new ListZonesOptions().afterMarker(afterMarker);
      }

      /**
       * @see ListZonesOptions#maxItems
       */
      public static ListZonesOptions maxItems(Integer maxItems) {
         return new ListZonesOptions().maxItems(maxItems);
      }
   }

   @Override
   public Multimap<String, String> buildQueryParameters() {
      Multimap<String, String> params = super.buildQueryParameters();
      if (afterMarker != null)
         params.put("marker", afterMarker.toString());
      if (maxItems != null)
         params.put("maxitems", maxItems.toString());
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(afterMarker, maxItems);
   }

   @Override
   public ListZonesOptions clone() {
      return new ListZonesOptions().afterMarker(afterMarker).maxItems(maxItems);
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
      ListZonesOptions other = ListZonesOptions.class.cast(obj);
      return Objects.equal(this.afterMarker, other.afterMarker) && Objects.equal(this.maxItems, other.maxItems);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("afterMarker", afterMarker).add("maxItems", maxItems).toString();
   }
}
