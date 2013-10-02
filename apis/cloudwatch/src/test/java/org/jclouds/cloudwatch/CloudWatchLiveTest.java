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

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.jclouds.util.Predicates2.retry;

import java.util.Date;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.cloudwatch.domain.Dimension;
import org.jclouds.cloudwatch.domain.MetricDatum;
import org.jclouds.cloudwatch.domain.Unit;
import org.jclouds.cloudwatch.internal.BaseCloudWatchApiLiveTest;
import org.jclouds.cloudwatch.options.ListMetricsOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CloudWatch}.
 *
 * @author Jeremy Whitlock
 */
public class CloudWatchLiveTest extends BaseCloudWatchApiLiveTest {

   public CloudWatchLiveTest() {
      provider = "cloudwatch";
   }

   @Test
   protected void testCloudWatch() {
      String metricName = "TestMetricName" + System.currentTimeMillis();
      String namespace = "JCLOUDS/Test";
      Date metricTimestamp = new Date();
      Set<MetricDatum> metrics = Sets.newLinkedHashSet();

      for (int i = 0; i < 11; i++) {
         metrics.add(MetricDatum.builder()
                                .metricName(metricName + "_" + i)
                                .dimension(new Dimension("BaseMetricName", metricName))
                                .unit(Unit.COUNT)
                                .timestamp(metricTimestamp)
                                .value((double) i)
                                .build());
      }

      CloudWatch.putMetricData(api, null, metrics, namespace);

      ListMetricsOptions lmo = ListMetricsOptions.Builder.namespace(namespace)
                                                 .dimension(new Dimension("BaseMetricName", metricName));
      boolean success = retry(new Predicate<ListMetricsOptions>() {
         public boolean apply(ListMetricsOptions options) {
            return Iterables.size(CloudWatch.listMetrics(api, null, options)) == 11;
         }
      }, 20, 1, MINUTES).apply(lmo);

      if (!success) {
         Assert.fail("Unable to gather the created CloudWatch data within the time (20m) allotted.");
      }
   }

}
