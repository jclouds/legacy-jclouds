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
package org.jclouds.cloudwatch.domain;

import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;

import java.util.Iterator;
import java.util.Set;

/**
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_ListMetrics.html" />
 *
 * @author Jeremy Whitlock
 */
public class ListMetricsResponse {

   private final Set<Metric> metrics;
   private final String nextToken;

   public ListMetricsResponse(Set<Metric> metrics, String nextToken) {
      // Default to an empty set
      if (metrics == null) {
         this.metrics = Sets.newLinkedHashSet();
      } else {
         this.metrics = metrics;
      }
      this.nextToken = nextToken;
   }

   /**
    * return the list of {@link Metric}
    */
   public Set<Metric> getMetrics() {
      return metrics;
   }

   /**
    * return the next token or null if there is none.
    */
   @Nullable
   public String getNextToken() {
      return nextToken;
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
      ListMetricsResponse other = (ListMetricsResponse)obj;
      if (metrics == null) {
         if (other.metrics != null)
            return false;
      } else if (!metrics.equals(other.metrics))
         return false;
      if (nextToken == null) {
         if (other.nextToken != null)
            return false;
      } else if (!nextToken.equals(other.nextToken))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append("[metrics=[");

      Iterator<Metric> iterator = metrics.iterator();

      while (iterator.hasNext()) {
         builder.append(iterator.next());

         if (iterator.hasNext()) {
            builder.append(", ");
         }
      }

      builder.append("]");

      builder.append(", nextToken=")
             .append(nextToken)
             .append("]");

      return builder.toString();
   }

}
