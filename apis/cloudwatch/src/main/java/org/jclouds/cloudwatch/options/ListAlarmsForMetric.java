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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.annotations.Beta;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.Statistics;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options used to describe alarms for metric.
 *
 * @see <a href="http://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_DescribeAlarmsForMetric.html" />
 *
 * @author Jeremy Whitlock
 */
@Beta
public class ListAlarmsForMetric extends BaseHttpRequestOptions {

   int dimensionIndex = 1;

   /**
    * The list of dimensions associated with the metric.
    *
    * @param dimensions the list of dimensions associated with the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric dimensions(Set<Dimension> dimensions) {
      for (Dimension dimension : checkNotNull(dimensions, "dimensions")) {
         dimension(dimension);
      }
      return this;
   }

   /**
    * The dimension associated with the metric.
    *
    * @param dimension the dimension associated with the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric dimension(Dimension dimension) {
      checkNotNull(dimension, "dimension");
      checkArgument(dimensionIndex <= 10, "maximum number of dimensions is 10");
      formParameters.put("Dimensions.member." + dimensionIndex + ".Name", dimension.getName());
      formParameters.put("Dimensions.member." + dimensionIndex + ".Value", dimension.getValue());
      dimensionIndex++;
      return this;
   }

   /**
    * The name of the metric.
    *
    * @param metricName the name of the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric metricName(String metricName) {
      checkNotNull(metricName, "metricName");
      checkArgument(metricName.length() <= 255, "metricName must be between 1 and 255 characters in length");
      formParameters.put("MetricName", metricName);
      return this;
   }

   /**
    * The namespace of the metric.
    *
    * @param namespace namespace of the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric namespace(String namespace) {
      checkNotNull(namespace, "namespace");
      checkArgument(namespace.length() <= 255, "namespace must be between 1 and 255 characters in length");
      formParameters.put("Namespace", namespace);
      return this;
   }

   /**
    * The period in seconds over which the statistic is applied.
    *
    * @param period period in seconds over which the statistic is applied
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric period(int period) {
      formParameters.put("Period", checkNotNull(period, "period").toString());
      return this;
   }

   /**
    * The statistic for the metric.
    *
    * @param statistic statistic for the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric statistic(Statistics statistic) {
      checkNotNull(statistic, "statistic");
      checkArgument(statistic != Statistics.UNRECOGNIZED, "statistic unrecognized");
      formParameters.put("Statistic", statistic.toString());
      return this;
   }

   /**
    * The unit for the metric.
    *
    * @param unit unit for the metric
    *
    * @return this {@code ListAlarmsForMetric} object
    */
   public ListAlarmsForMetric unit(Unit unit) {
      checkNotNull(unit, "unit");
      checkArgument(unit != Unit.UNRECOGNIZED, "unit unrecognized");
      formParameters.put("Unit", unit.toString());
      return this;
   }

}
