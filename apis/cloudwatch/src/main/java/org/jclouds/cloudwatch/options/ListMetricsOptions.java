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
package org.jclouds.cloudwatch.options;

import java.util.Set;

import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Options used to list available metrics.
 *
 * @see <a href="http://docs.amazonwebservices.com/AmazonCloudWatch/latest/APIReference/API_ListMetrics.html" />
 *
 * @author Jeremy Whitlock
 */
public class ListMetricsOptions extends BaseHttpRequestOptions implements Cloneable {

   private Set<Dimension> dimensions = Sets.newLinkedHashSet();
   private String metricName;
   private String namespace;
   private Object afterMarker;

   /**
    * The namespace to filter against.
    *
    * @param namespace the namespace to filter against
    *
    * @return this {@code Builder} object
    */
   public ListMetricsOptions namespace(String namespace) {
      this.namespace = namespace;
      return this;
   }

   /**
    * The name of the metric to filter against.
    *
    * @param metricName the metric name to filter against
    *
    * @return this {@code Builder} object
    */
   public ListMetricsOptions metricName(String metricName) {
      this.metricName = metricName;
      return this;
   }

   /**
    * A list of dimensions to filter against.
    *
    * @param dimensions the dimensions to filter against
    *
    * @return this {@code Builder} object
    */
   public ListMetricsOptions dimensions(Iterable<Dimension> dimensions) {
      Iterables.addAll(this.dimensions, dimensions);
      return this;
   }

   /**
    * A dimension to filter the available metrics by.
    *
    * @param dimension a dimension to filter the returned metrics by
    *
    * @return this {@code Builder} object
    */
   public ListMetricsOptions dimension(Dimension dimension) {
      this.dimensions.add(dimension);
      return this;
   }

   /**
    * The token returned by a previous call to indicate that there is more data available.
    *
    * @param afterMarker the next token indicating that there is more data available
    *
    * @return this {@code Builder} object
    */
   public ListMetricsOptions afterMarker(Object afterMarker) {
      this.afterMarker = afterMarker;
      return this;
   }

   /**
    * Returns a newly-created {@code ListMetricsOptions} based on the contents of
    * the {@code Builder}.
    */
   @Override
   public Multimap<String, String> buildFormParameters() {
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.<String, String>builder();
      int dimensionIndex = 1;

      // If namespace isn't specified, don't include it
      if (namespace != null) {
         formParameters.put("Namespace", namespace);
      }
      // If metricName isn't specified, don't include it
      if (metricName != null) {
         formParameters.put("MetricName", metricName);
      }

      // If dimensions isn't specified, don't include it
      if (dimensions != null) {
         for (Dimension dimension : dimensions) {
            formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
            formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
            dimensionIndex++;
         }
      }

      // If afterMarker isn't specified, don't include it
      if (afterMarker != null) {
         formParameters.put("NextToken", afterMarker.toString());
      }

      return formParameters.build();
   }
   
   @Override
   public ListMetricsOptions clone() {
      return Builder.namespace(namespace).metricName(metricName).dimensions(dimensions).afterMarker(afterMarker);
   }
   
   public static class Builder {

      /**
       * @see ListMetricsOptions#namespace(String)
       */
      public static ListMetricsOptions namespace(String namespace) {
         return new ListMetricsOptions().namespace(namespace);
      }

      /**
       * @see ListMetricsOptions#metricName(String)
       */
      public static ListMetricsOptions metricName(String metricName) {
         return new ListMetricsOptions().metricName(metricName);
      }

      /**
       * @see ListMetricsOptions#dimensions(Iterable)
       */
      public static ListMetricsOptions dimensions(Iterable<Dimension> dimensions) {
         return new ListMetricsOptions().dimensions(dimensions);
      }

      /**
       * @see ListMetricsOptions#dimension(Dimension)
       */
      public static ListMetricsOptions dimension(Dimension dimension) {
         return new ListMetricsOptions().dimension(dimension);
      }

      /**
       * @see ListMetricsOptions#afterMarker(Object)
       */
      public static ListMetricsOptions afterMarker(Object afterMarker) {
         return new ListMetricsOptions().afterMarker(afterMarker);
      }
   }

}
