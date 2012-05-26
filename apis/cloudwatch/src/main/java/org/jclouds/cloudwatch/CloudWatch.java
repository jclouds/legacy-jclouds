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
package org.jclouds.cloudwatch;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Sets;
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.features.MetricClient;
import org.jclouds.cloudwatch.options.ListMetricsOptions;

import java.util.Iterator;
import java.util.Set;

/**
 * Utilities for using CloudWatch.
 *
 * @author Jeremy Whitlock
 */
public class CloudWatch {

   /**
    * List metrics based on the criteria in the {@link ListMetricsOptions} passed in.
    *
    * @param metricClient the {@link MetricClient} to use for the request
    * @param options the {@link ListMetricsOptions} describing the ListMetrics request
    *
    * @return iterable of metrics fitting the criteria
    */
   public static Iterable<Metric> listMetrics(final MetricClient metricClient, final ListMetricsOptions options) {
      return new Iterable<Metric>() {
         public Iterator<Metric> iterator() {
            return new AbstractIterator<Metric>() {

               private ListMetricsOptions lastOptions = options;
               private ListMetricsResponse response = metricClient.listMetrics(lastOptions);
               private Iterator<Metric> iterator = response.iterator();

               /**
                * {@inheritDoc}
                */
               @Override
               protected Metric computeNext() {
                  while (true) {
                     if (iterator == null) {
                        lastOptions = ListMetricsOptions.builder()
                                                        .namespace(lastOptions.getNamespace())
                                                        .metricName(lastOptions.getMetricName())
                                                        .dimensions(lastOptions.getDimensions())
                                                        .nextToken(lastOptions.getNextToken())
                                                        .build();
                        response = metricClient.listMetrics(lastOptions);
                        iterator = response.iterator();
                     }
                     if (iterator.hasNext()) {
                        return iterator.next();
                     }
                     if (response.getNextToken() == null) {
                        return endOfData();
                     }
                     iterator = null;
                  }
               }

            };
         }
      };
   }

   /**
    * List metrics based on the criteria in the {@link ListMetricsOptions} passed in.
    *
    * @param cloudWatchClient the {@link CloudWatchClient} to use for the request
    * @param region the region to list metrics in
    * @param options the options describing the ListMetrics request
    *
    * @return iterable of metrics fitting the criteria
    */
   public static Iterable<Metric> listMetrics(CloudWatchClient cloudWatchClient, String region,
            final ListMetricsOptions options) {
      return listMetrics(cloudWatchClient.getMetricClientForRegion(region), options);
   }

   /**
    * Pushes metrics to CloudWatch.
    *
    * @param cloudWatchClient the {@link CloudWatchClient} to use for the request
    * @param region the region to put the metrics in
    * @param metrics the metrics to publish
    * @param namespace the namespace to publish the metrics in
    */
   public static void putMetricData(CloudWatchClient cloudWatchClient, String region, Iterable<MetricDatum> metrics,
                                    String namespace) {
      MetricClient metricClient = cloudWatchClient.getMetricClientForRegion(region);
      Iterator<MetricDatum> mIterator = metrics.iterator();
      Set<MetricDatum> metricsData = Sets.newLinkedHashSet();

      while (mIterator.hasNext()) {
         metricsData.add(mIterator.next());
         if (metricsData.size() == 10 || !mIterator.hasNext()) {
            // Make the call
            metricClient.putMetricData(metrics, namespace);

            // Reset the list for subsequent call if necessary
            if (mIterator.hasNext()) {
               metricsData = Sets.newLinkedHashSet();
            }
         }
      }
   }

}
