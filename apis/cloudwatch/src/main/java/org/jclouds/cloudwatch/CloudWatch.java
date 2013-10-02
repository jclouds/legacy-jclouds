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
package org.jclouds.cloudwatch;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterables;

/**
 * Utilities for using CloudWatch.
 *
 * @author Jeremy Whitlock
 */
public class CloudWatch {

   /**
    * List metrics based on the criteria in the {@link ListMetricsOptions} passed in.
    *
    * @param metricApi the {@link MetricApi} to use for the request
    * @param options the {@link ListMetricsOptions} describing the ListMetrics request
    *
    * @return iterable of metrics fitting the criteria
    */
   public static Iterable<Metric> listMetrics(final MetricApi metricApi, final ListMetricsOptions options) {
      return Iterables.concat(PagedIterables.advance(metricApi.list(options),
               new Function<Object, IterableWithMarker<Metric>>() {

                  @Override
                  public IterableWithMarker<Metric> apply(Object input) {
                     return metricApi.list(options.clone().afterMarker(input));
                  }

                  @Override
                  public String toString() {
                     return "listMetrics(" + options + ")";
                  }
               }));
   }

   /**
    * List metrics based on the criteria in the {@link ListMetricsOptions} passed in.
    *
    * @param cloudWatchApi the {@link CloudWatchApi} to use for the request
    * @param region the region to list metrics in
    * @param options the options describing the ListMetrics request
    *
    * @return iterable of metrics fitting the criteria
    */
   public static Iterable<Metric> listMetrics(CloudWatchApi cloudWatchApi, String region,
            final ListMetricsOptions options) {
      return listMetrics(cloudWatchApi.getMetricApiForRegion(region), options);
   }

   /**
    * Pushes metrics to CloudWatch.
    *
    * @param cloudWatchApi the {@link CloudWatchApi} to use for the request
    * @param region the region to put the metrics in
    * @param metrics the metrics to publish
    * @param namespace the namespace to publish the metrics in
    */
   public static void putMetricData(CloudWatchApi cloudWatchApi, String region, Iterable<MetricDatum> metrics,
            String namespace) {
      MetricApi metricApi = cloudWatchApi.getMetricApiForRegion(region);

      for (List<MetricDatum> slice : Iterables.partition(metrics, 10)) {
         metricApi.putMetricsInNamespace(slice, namespace);
      }
   }

}
