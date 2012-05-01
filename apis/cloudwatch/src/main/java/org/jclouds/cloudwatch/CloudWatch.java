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
import org.jclouds.cloudwatch.domain.ListMetricsResponse;
import org.jclouds.cloudwatch.domain.Metric;
import org.jclouds.cloudwatch.options.ListMetricsOptions;

import java.util.Iterator;

/**
 * Utilities for using CloudWatch.
 *
 * @author Jeremy Whitlock
 */
public class CloudWatch {

   /**
    * List metrics based on the criteria in the {@link ListMetricsOptions} passed in.
    *
    * @param cloudWatchClient the CloudWatch client
    * @param region the region to list metrics in
    * @param options the options describing the ListMetrics request
    *
    * @return iterable of metrics fitting the criteria
    */
   public static Iterable<Metric> listMetrics(final CloudWatchClient cloudWatchClient,
                                              final String region, final ListMetricsOptions options) {
      return new Iterable<Metric>() {
         public Iterator<Metric> iterator() {
            return new AbstractIterator<Metric>() {

               private ListMetricsOptions lastOptions = options;
               private ListMetricsResponse response = cloudWatchClient.listMetrics(region, lastOptions);
               private Iterator<Metric> iterator = response.getMetrics().iterator();

               /**
                * {@inheritDoc}
                */
               @Override
               protected Metric computeNext() {
                  while (true) {
                     if (iterator == null) {
                        lastOptions = ListMetricsOptions.builder()
                                                        .dimensions(lastOptions.getDimensions())
                                                        .metricName(lastOptions.getMetricName())
                                                        .namespace(lastOptions.getNamespace())
                                                        .nextToken(response.getNextToken())
                                                        .build();
                        response = cloudWatchClient.listMetrics(region, lastOptions);
                        iterator = response.getMetrics().iterator();
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

}
